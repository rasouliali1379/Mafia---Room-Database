package com.mafia.assisstant.Fragments;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mafia.assisstant.Adapters.AbilityListAdapter;
import com.mafia.assisstant.Adapters.AvailablePlayerListAdapter;
import com.mafia.assisstant.DataHolders.AbilityCountDataHolder;
import com.mafia.assisstant.DataHolders.ChangeAbilityDataHolder;

import com.mafia.assisstant.R;
import com.mafia.assisstant.Room.DataHolder.AbilityDataHolder;
import com.mafia.assisstant.Room.DataHolder.EventDataHolder;
import com.mafia.assisstant.Room.DataHolder.PlayerDataHolder;
import com.mafia.assisstant.Room.DataHolder.PowerDataHolder;
import com.mafia.assisstant.Room.DataHolder.RoleDataHolder;
import com.mafia.assisstant.Room.ViewModel.EventViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class UsePowerFragment extends DialogFragment {

    @BindView(R.id.abilities_recyclerview)RecyclerView abilitiesRecyclerView;
    @BindView(R.id.players_recyclerview)RecyclerView playersRecyclerView;
    @BindView(R.id.use_power_back_btn)ImageView backBtn;
    @BindView(R.id.use_power_submit_btn)ImageView submitBtn;
    @BindView(R.id.use_power_layout)RelativeLayout rootLayout;
    @BindView(R.id.use_power_btn_box)RelativeLayout btnBox;
    @BindView(R.id.use_power_title)TextView title;

    private boolean isDay;
    private Context context;
    private PlayerDataHolder player;
    private RoleDataHolder role;
    private List<PowerDataHolder> powers;
    private List<AbilityDataHolder> abilities;
    private List<PlayerDataHolder> players;
    private List<Integer> removedPowers;
    private List<ChangeAbilityDataHolder> changedAbilities;
    private List<AbilityCountDataHolder> abilityCounts;

    private  AbilityListAdapter abilityListAdapter;
    private AvailablePlayerListAdapter avaiblePlayerListAdapter;

    private EventViewModel eventViewModel;

    private AbilityDataHolder ability;
    private boolean isAbilities = true;
    private int playerRequired = 0;
    int playerChecked = 0;

    public UsePowerFragment(Context context,
                            PlayerDataHolder player,
                            RoleDataHolder role,
                            List<PowerDataHolder> powers,
                            List<AbilityDataHolder> abilities,
                            List<PlayerDataHolder> players,
                            List<Integer> removedPowers,
                            List<ChangeAbilityDataHolder> changedAbilities,
                            List<AbilityCountDataHolder> abilityCounts, boolean isDay) {
        this.context = context;
        this.player = player;
        this.role = role;
        this.powers = powers;
        this.abilities = abilities;
        this.players = players;
        this.removedPowers = removedPowers;
        this.changedAbilities = changedAbilities;
        this.abilityCounts = abilityCounts;
        this.isDay = isDay;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_use_power, container, true);
        ButterKnife.bind(this, view);
        defineTheme();
        initViewModels();
        recyclerViewSetup();
        return view;
    }

    private void defineTheme() {

        if(isDay){
            rootLayout.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            btnBox.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            title.setTextColor(context.getResources().getColor(R.color.colorAccent));
            submitBtn.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent), android.graphics.PorterDuff.Mode.SRC_IN);
            backBtn.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            btnBox.setBackgroundColor(context.getResources().getColor(R.color.darkThemeColorPrimary));
            rootLayout.setBackgroundColor(context.getResources().getColor(R.color.darkThemeColorSecondary));
            title.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            submitBtn.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
            backBtn.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
        }

    }

    private void recyclerViewSetup() {
        List<AbilityDataHolder> abilities = checkChangedAbilities(getRelatedAbilities(role.getId()));
        List<PowerDataHolder> powers = getRelatedPowers(abilities);

        abilityListAdapter = new AbilityListAdapter(context, abilities, powers, true, ability -> {
            this.ability = ability;
            switchRecyclerView();
        });
        abilitiesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        abilitiesRecyclerView.setAdapter(abilityListAdapter);

        avaiblePlayerListAdapter = new AvailablePlayerListAdapter(context, players,false, (holder, position) -> checkItem(holder, position));
        playersRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        playersRecyclerView.setAdapter(avaiblePlayerListAdapter);

        switchRecyclerView();
    }



    private void checkItem(AvailablePlayerListAdapter.ViewHolder holder, int position) {
        if (holder.checkBox.isChecked()){
            if(playerChecked < playerRequired){
                players.get(position).setChecked(true);
                playerChecked++;
            }else {
                holder.checkBox.setChecked(false);
                Toast.makeText(context, context.getResources().getString(R.string.can_not_check_more)
                        + playerRequired
                        + context.getResources().getString(R.string.players_lowercase), Toast.LENGTH_LONG).show();
            }
        } else {
            players.get(position).setChecked(false);
            playerChecked--;
        }
    }

    private void switchRecyclerView() {
        if(isAbilities){
            abilitiesRecyclerView.setVisibility(View.VISIBLE);
            playersRecyclerView.setVisibility(View.GONE);
            submitBtn.setVisibility(View.INVISIBLE);
            backBtn.setVisibility(View.INVISIBLE);
            backBtn.setOnClickListener(null);
            title.setText(context.getResources().getString(R.string.choose_ability));
            isAbilities = false;
        } else {
            playersRecyclerView.setVisibility(View.VISIBLE);
            abilitiesRecyclerView.setVisibility(View.GONE);
            backBtn.setVisibility(View.VISIBLE);
            backBtn.setOnClickListener(v->switchRecyclerView());
            unCheckAll(players);
            playerChecked = 0;
            initObservers();
            title.setText(context.getResources().getString(R.string.choose_players) + playerRequired);
            isAbilities = true;
        }
    }

    private void unCheckAll(List<PlayerDataHolder> players) {
        for(int i = 0; i < players.size(); i++){
            players.get(i).setChecked(false);
        }
        avaiblePlayerListAdapter.notifyDataSetChanged();
    }

    private List<AbilityDataHolder> checkChangedAbilities(List<AbilityDataHolder> relatedAbilities) {
        List<AbilityDataHolder> abilities = findPlayerIdInChangedAbilities(player.getPlayerId());
        return replaceAbilities(relatedAbilities, abilities);
    }

    private List<AbilityDataHolder> replaceAbilities(List<AbilityDataHolder> relatedAbilities, List<AbilityDataHolder> abilities) {
        for (int i = 0; i < relatedAbilities.size(); i++) {
            for (int i2 = 0; i2 < abilities.size(); i2++) {
                if (abilities.get(i2).getId() == relatedAbilities.get(i).getId()){
                    relatedAbilities.set(i, abilities.get(i2));
                }
            }
        }
        return relatedAbilities;
    }

    private List<AbilityDataHolder> findPlayerIdInChangedAbilities(int playerId) {
        List<AbilityDataHolder> abilities_temp = new ArrayList<>();
        for (int i = 0; i < changedAbilities.size(); i++){
            if (changedAbilities.get(i).getPlayerId() == playerId){
                AbilityDataHolder ability = getAbility(changedAbilities.get(i).getAbilityToId());
                if (ability != null){
                    abilities_temp.add(ability);
                }
            }
        }
        return abilities_temp;
    }

    private AbilityDataHolder getAbility(int abilityToId) {
        for (int i = 0 ; i < abilities.size(); i++){
            if (abilityToId == abilities.get(i).getId()){
                return abilities.get(i);
            }
        }
        return null;
    }

    private List<AbilityDataHolder> getRelatedAbilities(int id) {
        List<AbilityDataHolder> abilities = new ArrayList<>();
        for(int i=0; i < this.abilities.size(); i++){
            if (this.abilities.get(i).getRoleId() == id){
                abilities.add(this.abilities.get(i));
            }
        }
        return abilities;
    }

    private List<PowerDataHolder> getRelatedPowers(List<AbilityDataHolder> abilities) {
        List<PowerDataHolder> powers = new ArrayList<>();
        for (int i = 0; i < abilities.size(); i++){
            powers.add(getSinglePower(abilities.get(i).getPowerId()));
        }
        return powers;
    }

    private PowerDataHolder getSinglePower(int powerId) {
        for (int i = 0; i < powers.size(); i++){
            if (powerId == powers.get(i).getId()){
                return powers.get(i);
            }
        }
        return null;
    }

    //available players

    private void initViewModels() {
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);
    }

    private void initObservers() {
        initVar(getAbilityCounts(ability.getId()));
        submitBtn.setOnClickListener(v-> submitPlayers());
        submitBtn.setVisibility(View.VISIBLE);
    }

    private List<AbilityCountDataHolder> getAbilityCounts(int id) {
        List<AbilityCountDataHolder> abilityCounts = new ArrayList<>();
        for (int i = 0; i < this.abilityCounts.size(); i++){
            if (this.abilityCounts.get(i).getAbilityId() == id){
                abilityCounts.add(this.abilityCounts.get(i));
            }
        }

        return abilityCounts;
    }

    private void initVar(List<AbilityCountDataHolder> abilityCounts) {
        if (abilityCounts.size() > 0){
            playerRequired = abilityCounts.get(abilityCounts.size() - 1).getAbilityCount();
        } else {
            playerRequired = ability.getPowerCount();
        }
    }



    private void submitPlayers(){
        List<PlayerDataHolder> chosenPlayers = getChoosenPlayers();
        if (chosenPlayers.size() > 0){
            List<EventDataHolder> events = new ArrayList<>();
            for(int i = 0; i < chosenPlayers.size(); i++){
                events.add(new EventDataHolder(chosenPlayers.get(i).getPlayerId(), player.getPlayerId(), ability.getId(), isDay , true));
            }
            eventViewModel.insert(events);
            dismiss();
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.choose_some_player), Toast.LENGTH_SHORT).show();
        }
    }

    private List<PlayerDataHolder> getChoosenPlayers() {
        List<PlayerDataHolder> players_temp = new ArrayList<>();
        for (int i = 0; i < players.size(); i++){
            if (players.get(i).isChecked()){
                players_temp.add(players.get(i));
            }
        }
        return players_temp;
    }
}
