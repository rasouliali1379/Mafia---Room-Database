package com.mafia.assisstant.Fragments;

import android.content.Context;
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
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.mafia.assisstant.DataHolders.AbilityCountDataHolder;
import com.mafia.assisstant.DataHolders.ChangeAbilityDataHolder;
import com.mafia.assisstant.R;
import com.mafia.assisstant.Room.DataHolder.AbilityDataHolder;
import com.mafia.assisstant.Room.DataHolder.KindDataHolder;
import com.mafia.assisstant.Room.DataHolder.PlayerDataHolder;
import com.mafia.assisstant.Room.DataHolder.PowerDataHolder;
import com.mafia.assisstant.Room.DataHolder.RoleDataHolder;
import com.mafia.assisstant.ViewModels.MainBoardViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NightFragment extends Fragment {

    private Context context;
    private List<PlayerDataHolder> allPlayers;
    private List<PlayerDataHolder> nightPlayers;
    private List<RoleDataHolder> roles;
    private List<AbilityDataHolder> abilities;
    private List<KindDataHolder> kinds;
    private List<PowerDataHolder> powers;
    private MainBoardViewModel mainBoardViewModel;
    private List<Integer> removedPowers;
    private List<ChangeAbilityDataHolder> changedAbilities;
    private List<AbilityCountDataHolder> abilityCounts;

    @BindView(R.id.night_fragment_recyclerview)RecyclerView recyclerView;
    @BindView(R.id.night_fragment_next_btn)FloatingActionButton nextFab;
    @BindView(R.id.night_fragment_player_name)TextView playerName;
    @BindView(R.id.night_fragment_player_status)TextView playerStatus;
    @BindView(R.id.night_fragment_role_name)TextView roleName;
    @BindView(R.id.night_fragment_use_power)FloatingActionButton usePowerFab;

    public static int ActivePosition = 0;
    public static int nightPlayersCount = 0;

    int dayCount;
    NightListAdapter nightListAdapter;
    View view;

    public NightFragment(Context context,
                         List<PlayerDataHolder> allPlayers,
                         List<RoleDataHolder> roles,
                         List<AbilityDataHolder> abilities,
                         List<KindDataHolder> kinds,
                         List<PowerDataHolder> powers,
                         MainBoardViewModel mainBoardViewModel,
                         List<Integer> removedPowers,
                         List<ChangeAbilityDataHolder> changedAbilities,
                         List<AbilityCountDataHolder> abilityCounts, int dayCount) {
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
        this.dayCount = dayCount;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_night, container, false);
        ButterKnife.bind(this, view);
        seprateNightRoles();
        recyclerViewSetup();
        nextPlayer();
        initObserver();
        return view;
    }

    private void initObserver() {
        mainBoardViewModel.getActivePlayer().observe((LifecycleOwner) context, player -> fillPlayerData(player));
    }

    private void fillPlayerData(PlayerDataHolder player) {
        RoleDataHolder role = getRole(player.getRoleId());
        roleName.setText(role.getRoleName());
        playerName.setText(player.getPlayerName());
        if (player.isDead()){
            playerStatus.setText(getResources().getString(R.string.status) + getResources().getString(R.string.status_dead));
            usePowerFab.setVisibility(View.INVISIBLE);
        } else {
            if (checkPowerStatus(player.getPlayerId())){
                usePowerFab.setVisibility(View.INVISIBLE);
            } else {
                if (isAdded()){
                    playerStatus.setText(getResources().getString(R.string.status) + getResources().getString(R.string.status_alive));
                }
                usePowerFab.setVisibility(View.VISIBLE);
                usePowerFab.setOnClickListener(v->usePower(player,role));
            }

        }

    }

    private boolean checkPowerStatus(int playerId) {
        for (int i = 0 ; i < removedPowers.size(); i++){
            if (removedPowers.get(i) == playerId){
                return true;
            }
        }
        return false;
    }

    private void usePower(PlayerDataHolder player,RoleDataHolder role) {
        List<PlayerDataHolder> players = getAlivePlayers();
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        Fragment prev = getChildFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment dialogFragment = new UsePowerFragment(context, player, role, powers, abilities, players, removedPowers, changedAbilities, abilityCounts, false);
        dialogFragment.show(ft, "dialog");
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



    private RoleDataHolder getRole(int roleId) {
        for (int i = 0; i < roles.size(); i++){
            if (roles.get(i).getId() == roleId){
                return roles.get(i);
            }
        }
        return null;
    }

    private void seprateNightRoles() {
        nightPlayers = new ArrayList<>();
        for (int i = 0; i < allPlayers.size(); i++){
            if (getNightPlayersById(allPlayers.get(i))){
                nightPlayers.add(allPlayers.get(i));
            }
        }
        nightPlayersCount = nightPlayers.size();
    }

    private boolean getNightPlayersById(PlayerDataHolder player) {
        for (int i = 0; i < abilities.size(); i++){
            if (abilities.get(i).getRoleId() == player.getRoleId() && !abilities.get(i).isDay()){
                return true;
            }
        }
        return false;
    }

    private void recyclerViewSetup() {

        nightListAdapter = new NightListAdapter(context, sortByPriority(checkDayRatio(nightPlayers)), roles);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(nightListAdapter);
        nextFab.setOnClickListener(v->nextPlayer());
    }

    private List<PlayerDataHolder> checkDayRatio(List<PlayerDataHolder> nightPlayers) {
        for (int i = 0; i < nightPlayers.size(); i++){
            if (checkDayRationForEach(getNightAbilities(nightPlayers.get(i).getRoleId()))){
                nightPlayers.remove(i);
                i--;
            }
        }
        return nightPlayers;
    }

    private boolean checkDayRationForEach(List<AbilityDataHolder> nightAbilities) {
        for (int i = 0; i < nightAbilities.size(); i++){
            if ( nightAbilities.get(i).getTimes() > 0 && dayCount % nightAbilities.get(i).getTimes() != 0){
                return true;
            }
        }
        return false;
    }

    private List<AbilityDataHolder> getNightAbilities(int roleId) {
        List<AbilityDataHolder> abilities = new ArrayList<>();
        for (int i = 0; i < abilities.size(); i++){
            if (abilities.get(i).getRoleId() == roleId && !abilities.get(i).isDay()){
                abilities.add(abilities.get(i));
            }
        }
        return abilities;
    }

    private List<PlayerDataHolder> sortByPriority(List<PlayerDataHolder> nightPlayers) {
        for (int i = 0; i < nightPlayers.size(); i++){
            if (i != nightPlayers.size() - 1){
                if (getRole(nightPlayers.get(i).getRoleId()).getPriority() > getRole(nightPlayers.get(i + 1).getRoleId()).getPriority()){
                    Collections.swap(nightPlayers, i , i + 1);
                    i = 0;
                }
            }

        }
        return nightPlayers;
    }

    private void nextPlayer(){
        mainBoardViewModel.setActivePlayer(nightPlayers.get(ActivePosition));
        if (ActivePosition > 0){
            nightPlayers.get(ActivePosition - 1).setActive(false);
        }

        nightPlayers.get(ActivePosition).setSeen(true);
        nightPlayers.get(ActivePosition).setActive(true);

        nightListAdapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(ActivePosition);

        if (ActivePosition + 1 == nightPlayers.size()){
            nextFab.setVisibility(View.INVISIBLE);
            return;
        }

        ActivePosition++;
    }

    @OnClick(R.id.night_fragment_night_events_btn) void nightEvents(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment dialogFragment = new  EventsDialogFragment(context, false, true);
        dialogFragment.show(ft, "dialog");
    }

    @OnClick(R.id.night_fragment_day_events_btn) void dayEvents(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment dialogFragment = new  EventsDialogFragment(context, true, true);
        dialogFragment.show(ft, "dialog");
    }


    static class DialogVH {

        @BindView(R.id.main_framelayout) FrameLayout frameLayout;

        DialogVH(View rootView) {
            ButterKnife.bind(this , rootView);
        }
    }

    static class DialogVH2 {

        @BindView(R.id.events_layout_tablayout)TabLayout tabLayout;
        @BindView(R.id.events_layout_viewpager)ViewPager viewPager;

        DialogVH2(View rootView) {
            ButterKnife.bind(this , rootView);

        }
    }

    static class NightListAdapter extends RecyclerView.Adapter<NightListAdapter.ViewHolder>{

        private Context context;
        List<PlayerDataHolder> players;
        List<RoleDataHolder> roles;


        public NightListAdapter(Context context, List<PlayerDataHolder> players, List<RoleDataHolder> roles) {
            this.context = context;
            this.players = players;
            this.roles = roles;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.mainboard_item,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            RoleDataHolder role = getRole(players.get(position).getRoleId());
            holder.roleName.setText(role.getRoleName());
            if (players.get(position).isDead()){
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorChrismon));
                holder.roleName.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
            } else if (players.get(position).isActive()){
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorGreen));
                holder.roleName.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            } else if (players.get(position).isSeen()){
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
                holder.roleName.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            } else {
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.darkThemeColorPrimary));
                holder.roleName.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            }
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

        public class ViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.main_board_item_player_name) TextView roleName;
            @BindView(R.id.main_board_item_cardview) CardView cardView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this , itemView);
            }
        }
    }


}
