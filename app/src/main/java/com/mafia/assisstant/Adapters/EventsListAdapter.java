package com.mafia.assisstant.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mafia.assisstant.DataHolders.ChangeAbilityDataHolder;
import com.mafia.assisstant.DataHolders.RepulsedEventsDataHolder;
import com.mafia.assisstant.R;
import com.mafia.assisstant.Room.DataHolder.AbilityDataHolder;
import com.mafia.assisstant.Room.DataHolder.ActionDataHolder;
import com.mafia.assisstant.Room.DataHolder.ConditionDataholder;
import com.mafia.assisstant.Room.DataHolder.EventDataHolder;
import com.mafia.assisstant.Room.DataHolder.PlayerDataHolder;
import com.mafia.assisstant.Room.DataHolder.PowerDataHolder;
import com.mafia.assisstant.Room.DataHolder.RoleDataHolder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventsListAdapter extends RecyclerView.Adapter<EventsListAdapter.ViewHolder> {

    Context context;
    private List<List<EventDataHolder>> events;
    private List<PlayerDataHolder> players;
    private List<RoleDataHolder> roles;
    private List<AbilityDataHolder> abilities;
    private List<PowerDataHolder> powers;
    private List<ActionDataHolder> actions;
    private List<ConditionDataholder> conditions;
    List<RepulsedEventsDataHolder> repulsedEvents;
    StringBuilder subDividedEventsSb;
    boolean isDay, darkTheme;

    SharedPreferences prefs;

    public EventsListAdapter(Context context,
                                    List<PlayerDataHolder> players,
                                    List<RoleDataHolder> roles,
                                    List<AbilityDataHolder> abilities,
                                    List<PowerDataHolder> powers,
                                    List<ActionDataHolder> actions,
                                    List<ConditionDataholder> conditions, boolean isDay, boolean darkTheme) {
        this.context = context;
        this.players = players;
        this.roles = roles;
        this.abilities = abilities;
        this.powers = powers;
        this.actions = actions;
        this.conditions = conditions;
        this.isDay = isDay;
        this.darkTheme = darkTheme;
        prefs = context.getSharedPreferences("Mafia", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.role_presentation_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        defineTheme(holder);
        holder.eventContent.setText(getEventLog(position));
    }

    private void defineTheme(ViewHolder holder) {
        if (darkTheme){
            holder.eventContent.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.darkThemeColorPrimary));
        } else {
            holder.eventContent.setTextColor(context.getResources().getColor(R.color.colorAccent));
        }
        if (prefs.getString("language", "fa").equals("fa")){
            holder.eventContent.setTextDirection(View.TEXT_DIRECTION_RTL);
            holder.eventContent.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        } else {
            holder.eventContent.setTextDirection(View.TEXT_DIRECTION_LTR);
            holder.eventContent.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        }
        holder.eventContent.setTextSize(15f);
    }

    private String getEventLog(int position) {
        StringBuilder sb = new StringBuilder();
        if (events.get(position).size() < 2){
            sb.append(getUniqueEventLog(events.get(position).get(0)));
        } else {
            repulsedEvents.clear();
            subDividedEventsSb = new StringBuilder();
            sb.append(checkAbilityAgainst(events.get(position)));
        }
        return sb.toString();
    }

    private String checkAbilityAgainst(List<EventDataHolder> events) {
        if ( repulsedEvents == null)
            repulsedEvents = new ArrayList<>();
        if(events.size() > 1){
            for(int i = 0; i < events.size(); i++) {
                for (int i2 = i + 1; i2 < events.size(); i2++) {
                    if (getAbilityAgainst(events.get(i).getAbilityId()) == events.get(i2).getAbilityId() && !checkRepulsedEvents(events.get(i).getId()) && !checkRepulsedEvents(events.get(i2).getId())){
                        repulsedEvents.add(new RepulsedEventsDataHolder(events.get(i2).getAbilityId(), getAbilityAgainst(events.get(i).getAbilityId())));
                        subDividedEventsSb.append(getUniqueEventLog(events.get(i2)));
                        subDividedEventsSb.append(getRepulser(events.get(i)));
                        checkAbilityAgainst(events);
                    }
                }
            }
        } else {
            subDividedEventsSb.append(getUniqueEventLog(events.get(events.size() - 1)));
        }
        return subDividedEventsSb.toString();
    }

    private String getRepulser(EventDataHolder event) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append(context.getResources().getString(R.string.ability_repulsed));
        sb.append(getSingleRole(getPlayer(event.getCausedPlayerId()).getRoleId()).getRoleName());
        return sb.toString();
    }

    private RoleDataHolder getSingleRole(int roleId) {
        for (int i =0; i < roles.size(); i++){
            if (roles.get(i).getId() == roleId){
                return roles.get(i);
            }
        }
        return null;
    }

    private boolean checkRepulsedEvents(int id) {
        if (repulsedEvents.size() > 0 ){
            for (int i = 0; i < repulsedEvents.size(); i++){
                if (repulsedEvents.get(i).getAttackedBy() == id || repulsedEvents.get(i).getRepulsedBy() == id){
                    return true;
                }
            }
        }

        return false;
    }

    private int getAbilityAgainst(int abilityId) {
        AbilityDataHolder ability = getAbility(abilityId);
        return ability.getAbilityAgainstId();
    }

    private AbilityDataHolder getAbility(int abilityId) {
        for (int i = 0; i < abilities.size(); i++){
            if (abilities.get(i).getId() == abilityId){
                return abilities.get(i);
            }
        }
        return null;
    }

    private String getUniqueEventLog(EventDataHolder event) {
        List<ActionDataHolder> actions = getRelatedActions(event.getAbilityId());
        StringBuilder sb = new StringBuilder();
        PlayerDataHolder p = getPlayer(event.getCausedPlayerId());
        if (p != null){
            sb.append(p.getPlayerName());
            sb.append(context.getResources().getString(R.string.used_power));
            PowerDataHolder power = getSinglePower(getAbilityByRoleId(p.getRoleId()).getPowerId());
            if (power != null){
                sb.append(power.getPowerName());
                sb.append(context.getResources().getString(R.string.used_on));
            }
        }

        PlayerDataHolder p1 = getPlayer(event.getPlayerId());
        if (p1 != null){
            sb.append(p1.getPlayerName());
        }

        if(actions.size() > 0 && !checkCondition(actions)){
            sb.append("\n");
            sb.append(context.getResources().getString(R.string.declined_by_conditions));
        }
        return sb.toString();
    }

    private PowerDataHolder getSinglePower(int powerId) {
        for (int i = 0; i < powers.size(); i++){
            if (powerId == powers.get(i).getId()){
                return powers.get(i);
            }
        }
        return null;
    }

    private boolean checkCondition(List<ActionDataHolder> actions) {
        for (int i = 0 ; i < actions.size(); i++){
            if (actions.get(i).getConditionId() > 0){
                ConditionDataholder condition = getActionCondition(actions.get(i).getConditionId());
                if (condition != null){
                    String [] ids = condition.getIncludeRoles().split("-");
                    if (checkSingleCondition(ids)){
                        return true;
                    }
                }
            } else {
                return true;
            }
        }
        return false;
    }

    private ConditionDataholder getActionCondition(int conditionId) {
        for (int i = 0; i < conditions.size(); i++){
            if (conditions.get(i).getId() == conditionId){
                return conditions.get(i);
            }
        }
        return null;
    }

    private boolean checkSingleCondition(String[] ids) {
        for (int i = 0; i < ids.length; i++){
            for (int i2 = 0; i2 < roles.size(); i2++){
                if (Integer.parseInt(ids[i]) == roles.get(i2).getId()){
                    return true;
                }
            }
        }
        return false;
    }

    private List<ActionDataHolder> getRelatedActions(int id) {
        List<ActionDataHolder> actions = new ArrayList<>();
        for (int i = 0; i < actions.size(); i++){
            if (actions.get(i).getAbilityId() == id){
                actions.add(actions.get(i));
            }
        }
        return actions;
    }

    private PlayerDataHolder getPlayer (int playerId){
        for(int i = 0; i < players.size(); i++){
            if (players.get(i).getPlayerId() == playerId){
                return players.get(i);
            }
        }
        return null;
    }

    private AbilityDataHolder getAbilityByRoleId(int roleId) {
        for (int i = 0; i < abilities.size(); i++){
            if (abilities.get(i).getRoleId() == roleId){
                return abilities.get(i);
            }
        }
        return null;
    }

    @Override
    public int getItemCount() {
        if (events != null){
            Log.e("Events Size", String.valueOf(events.size()));
            return events.size();
        }
        return 0;
    }

    public void setData (List<List<EventDataHolder>> events){
        Log.e("data set", "true");
        this.events = events;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.role_presentation_cardview)CardView cardView;
        @BindView(R.id.role_presentation_item_txt)TextView eventContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this , itemView);
        }


    }
}
