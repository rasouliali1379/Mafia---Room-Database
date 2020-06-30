package com.mafia.assisstant.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.mafia.assisstant.DataHolders.AbilityCountDataHolder;
import com.mafia.assisstant.DataHolders.ChangeAbilityDataHolder;
import com.mafia.assisstant.Helpers.TabAdapter;
import com.mafia.assisstant.R;
import com.mafia.assisstant.Room.DataHolder.AbilityDataHolder;
import com.mafia.assisstant.Room.DataHolder.KindDataHolder;
import com.mafia.assisstant.Room.DataHolder.PlayerDataHolder;
import com.mafia.assisstant.Room.DataHolder.PowerDataHolder;
import com.mafia.assisstant.Room.DataHolder.RoleDataHolder;
import com.mafia.assisstant.ViewModels.MainBoardViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DayFragment extends Fragment {

    @BindView(R.id.day_fragment_filter_btn) LinearLayout filterBtn;
    @BindView(R.id.day_fragment_recyclerview) RecyclerView recyclerView;
    @BindView(R.id.day_fragment_remaining_mafia) TextView remainingMafia;
    @BindView(R.id.day_fragment_remaining_civil) TextView remainingCivil;
    @BindView(R.id.day_fragment_starter_name) TextView starterName;
    @BindView(R.id.day_fragment_all_players)TextView allPlayersCount;

    private Context context;
    private List<PlayerDataHolder> allPlayers;
    private List<RoleDataHolder> roles;
    private List<AbilityDataHolder> abilities;
    private List<KindDataHolder> kinds;
    private List<PowerDataHolder> powers;
    private List<Integer> removedPowers;
    private List<ChangeAbilityDataHolder> changedAbilities;
    private List<AbilityCountDataHolder> abilityCounts;
    private MainBoardViewModel mainBoardViewModel;
    private View view;

    public DayFragment(Context context,
                       List<PlayerDataHolder> allPlayers,
                       List<RoleDataHolder> roles,
                       List<AbilityDataHolder> abilities,
                       List<KindDataHolder> kinds,
                       List<PowerDataHolder> powers,
                       MainBoardViewModel mainBoardViewModel,
                       List<Integer> removedPowers,
                       List<ChangeAbilityDataHolder> changedAbilities,
                       List<AbilityCountDataHolder> abilityCounts) {
        this.context = context;
        this.allPlayers = allPlayers;
        this.roles = roles;
        this.abilities = abilities;
        this.kinds = kinds;
        this.powers = powers;
        this.mainBoardViewModel = mainBoardViewModel;
        this.removedPowers = removedPowers;
        this.changedAbilities = changedAbilities;
        this.abilityCounts = abilityCounts;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_day, container, false);
        ButterKnife.bind(this, view);
        recyclerViewSetup();
        initObservers();
        return view;
    }

    private void initObservers() {
        mainBoardViewModel.getStarterPlayer().observe((LifecycleOwner) context, player -> starterName.setText(context.getResources().getString(R.string.starter_player)+ " : " + player.getPlayerName()));
        mainBoardViewModel.getCivilCounter().observe((LifecycleOwner) context, num -> remainingCivil.setText(context.getResources().getString(R.string.remainig_civil) + num  ));
        mainBoardViewModel.getMafiaCounter().observe((LifecycleOwner) context, num -> remainingMafia.setText(context.getResources().getString(R.string.remainig_mafia) + num  ));
        mainBoardViewModel.getAllPlayersCounter().observe((LifecycleOwner) context, num -> allPlayersCount.setText(context.getResources().getString(R.string.all_players) + num));
    }

    private void recyclerViewSetup() {
        PlayerListAdapter playerListAdapter = new PlayerListAdapter(context, allPlayers, roles, abilities, player -> {
           if (hasDayPower(player)){
               showOptionsMenu(player);
           } else {
               showInfoDialog(player);
           }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(playerListAdapter);
    }

    private void showOptionsMenu(PlayerDataHolder player) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getResources().getString(R.string.option_dialog_title));
        builder.setItems(R.array.day_players_options , (DialogInterface dialog, int which) -> {
            switch (which){
                case 0:
                    usePower(player);
                    break;

                case 1:
                    showInfoDialog(player);
                    break;
            }
        });
        builder.create();
        builder.show();
    }

    private void usePower(PlayerDataHolder player) {
        RoleDataHolder role = getRole(player.getRoleId());
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        ViewGroup viewGroup = view.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.framelayout, viewGroup, false);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        List<PlayerDataHolder> players = getAlivePlayers();
        DialogVH2 holder = new DialogVH2(dialogView);
        holder.frameLayout.setBackground(new ColorDrawable(context.getResources().getColor(R.color.darkThemeColorPrimary)));
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        //transaction.replace(R.id.main_framelayout, new AvaibleAbilitiesFragment(context, player, role, powers, abilities, players, removedPowers, changedAbilities, abilityCounts, false, alertDialog));
        transaction.commit();
        alertDialog.show();
    }

    private List<PlayerDataHolder> getAlivePlayers() {
        List<PlayerDataHolder> players = new ArrayList<>();
        for(int i = 0; i < allPlayers.size(); i++){
            if (!allPlayers.get(i).isDead()){
                players.add(allPlayers.get(i));
            }
        }
        return players;
    }

    private void showInfoDialog(PlayerDataHolder player) {
        RoleDataHolder role = getRole(player.getRoleId());
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        ViewGroup viewGroup = view.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.player_info_dialog, viewGroup, false);
        DialogVH holder = new DialogVH(dialogView);
        holder.playerName.setText(context.getResources().getString(R.string.player_name_1) + player.getPlayerName());
        holder.playerRole.setText(context.getResources().getString(R.string.player_role) + role.getRoleName());

        if (player.isDead())
            holder.playerStatus.setText(context.getResources().getString(R.string.player_status) + context.getResources().getString(R.string.status_dead));
        else
            holder.playerStatus.setText(context.getResources().getString(R.string.player_status) + context.getResources().getString(R.string.status_alive));

        holder.playerPowers.setText(context.getResources().getString(R.string.player_powers) + getRelatedPowers(role.getId()));
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        holder.okBtn.setOnClickListener(v->alertDialog.dismiss());
    }

    private String getRelatedPowers(int id) {
        List<AbilityDataHolder> abilities = getRelatedAbilities(id);
        StringBuilder sb = new StringBuilder();
        if (abilities.size() > 0 ){
            for (int i = 0; i < abilities.size(); i++){
                if (abilities.size() - 1 == i){
                    sb.append(getSinglePower(abilities.get(i).getPowerId()));
                } else {
                    sb.append(getSinglePower(abilities.get(i).getPowerId()));
                    sb.append(" - ");
                }

            }
        } else {
            sb.append(context.getResources().getString(R.string.none));
        }

        return sb.toString();
    }

    private String getSinglePower(int powerId) {
        for (int i = 0; i < powers.size(); i++){
            if (powerId == powers.get(i).getId()){
                return powers.get(i).getPowerName();
            }
        }
        return "";
    }

    private List<AbilityDataHolder> getRelatedAbilities(int id) {
        List<AbilityDataHolder> abilities = new ArrayList<>();
        for(int i=0; i < abilities.size(); i++){
            if (this.abilities.get(i).getRoleId() == id){
                abilities.add(this.abilities.get(i));
            }
        }
        return abilities;
    }

    @OnClick(R.id.night_events_btn) void nightEvents(){
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        Fragment prev = getChildFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment dialogFragment = new  EventsDialogFragment(context, false, false);
        dialogFragment.show(ft, "dialog");
    }

    @OnClick(R.id.day_events_btn) void dayEvents(){
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        Fragment prev = getChildFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment dialogFragment = new  EventsDialogFragment(context, true, false);
        dialogFragment.show(ft, "dialog");
    }

    private boolean hasDayPower(PlayerDataHolder player) {
        RoleDataHolder role = getRole(player.getRoleId());
        for (int i = 0; i < abilities.size(); i++){
            if (role.getId() == abilities.get(i).getRoleId() && abilities.get(i).isDay() && !checkPowerStatus(player.getPlayerId())){
                return true;
            }
        }
        return false;
    }

    private boolean checkPowerStatus(int playerId) {
        for (int i = 0 ; i < removedPowers.size(); i++){
            if (removedPowers.get(i) == playerId){
                return true;
            }
        }
        return false;
    }

    private RoleDataHolder getRole(int roleId) {
        for (int i = 0; i < roles.size(); i++){
            if (roles.get(i).getId() == roleId){
                return roles.get(i);
            }
        }
        return null;
    }

    static class DialogVH {

        @BindView(R.id.player_info_name)TextView playerName;
        @BindView(R.id.player_info_role)TextView playerRole;
        @BindView(R.id.player_info_powers)TextView playerPowers;
        @BindView(R.id.player_info_status)TextView playerStatus;
        @BindView(R.id.player_info_ok_btn) Button okBtn;
        DialogVH(View rootView) {
            ButterKnife.bind(this , rootView);
        }
    }
    static class DialogVH2 {

        @BindView(R.id.main_framelayout) FrameLayout frameLayout;
        DialogVH2(View rootView) {
            ButterKnife.bind(this , rootView);
        }
    }


    static class PlayerListAdapter extends RecyclerView.Adapter<PlayerListAdapter.ViewHolder>{

        private Context context;
        private final OnItemClickListener listener;
        List<PlayerDataHolder> players;
        List<RoleDataHolder> roles;
        List<AbilityDataHolder> abilities;

        public PlayerListAdapter(Context context,
                                 List<PlayerDataHolder> players,
                                 List<RoleDataHolder> roles,
                                 List<AbilityDataHolder> abilities,
                                 OnItemClickListener listener) {
            this.context = context;
            this.listener = listener;
            this.players = players;
            this.roles = roles;
            this.abilities = abilities;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.mainboard_item,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (players.get(position).isDead()){
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
            }

            holder.playerName.setText(players.get(position).getPlayerName());

            if (hasDayPower(players.get(position).getRoleId())){
                holder.powerIdentifier.setVisibility(View.VISIBLE);
            }

            holder.bind(players.get(position), listener);
        }

        private boolean hasDayPower(int roleId) {
            RoleDataHolder role = getRole(roleId);
            for (int i = 0; i < abilities.size(); i++){
                if (role.getId() == abilities.get(i).getRoleId() && abilities.get(i).isDay()){
                    return true;
                }
            }
            return false;
        }

        private RoleDataHolder getRole(int roleId) {
            for (int i = 0; i < roles.size(); i++){
                if (roles.get(i).getId() == roleId){
                    return roles.get(i);
                }
            }
            return null;
        }


        @Override
        public int getItemCount() {
            if (players != null)
                return players.size();
            else return 0;
        }

        public interface OnItemClickListener {
            void onItemClick(PlayerDataHolder player);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.main_board_item_player_name)TextView playerName;
            @BindView(R.id.main_board_item_power_identifier)ImageView powerIdentifier;
            @BindView(R.id.main_board_item_cardview)CardView cardView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this , itemView);
            }

            public void bind(PlayerDataHolder player , final OnItemClickListener listener) {
                itemView.setOnClickListener(v-> listener.onItemClick(player));
            }
        }
    }

}
