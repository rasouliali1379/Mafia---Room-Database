package com.mafia.assisstant.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mafia.assisstant.Adapters.EventsListAdapter;
import com.mafia.assisstant.R;
import com.mafia.assisstant.Room.DataHolder.AbilityDataHolder;
import com.mafia.assisstant.Room.DataHolder.ActionDataHolder;
import com.mafia.assisstant.Room.DataHolder.ConditionDataholder;
import com.mafia.assisstant.Room.DataHolder.EventDataHolder;
import com.mafia.assisstant.Room.DataHolder.PlayerDataHolder;
import com.mafia.assisstant.Room.DataHolder.PowerDataHolder;
import com.mafia.assisstant.Room.DataHolder.RoleDataHolder;
import com.mafia.assisstant.Room.ViewModel.AbilityViewModel;
import com.mafia.assisstant.Room.ViewModel.ActionViewModel;
import com.mafia.assisstant.Room.ViewModel.ConditionViewModel;
import com.mafia.assisstant.Room.ViewModel.EventViewModel;
import com.mafia.assisstant.Room.ViewModel.PlayerViewModel;
import com.mafia.assisstant.Room.ViewModel.PowerViewModel;
import com.mafia.assisstant.Room.ViewModel.RoleViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;


public class EventsFragment extends Fragment {
    Context context;
    boolean isDay, darkTheme;

    PlayerViewModel playerViewModel;
    AbilityViewModel abilityViewModel;
    PowerViewModel powerViewModel;
    RoleViewModel roleViewModel;
    EventViewModel eventViewModel;
    ActionViewModel actionViewModel;
    ConditionViewModel conditionViewModel;

    private List<EventDataHolder> events;
    private List<PlayerDataHolder> players;
    private List<RoleDataHolder> roles;
    private List<AbilityDataHolder> abilities;
    private List<PowerDataHolder> powers;
    private List<ActionDataHolder> actions;
    private List<ConditionDataholder> conditions;

    LiveData<List<EventDataHolder>> eventLiveData;
    LiveData<List<PlayerDataHolder>> playerLiveData;
    LiveData<List<RoleDataHolder>> roleLiveData;
    LiveData<List<AbilityDataHolder>> abilityLiveData;
    LiveData<List<PowerDataHolder>> powerLiveData;
    LiveData<List<ActionDataHolder>> actionLiveData;
    LiveData<List<ConditionDataholder>> conditionLiveData;

    @BindView(R.id.events_recyclerview)RecyclerView recyclerView;
    @BindView(R.id.no_events_message)TextView noEvents;
    @BindView(R.id.events_root_layout)RelativeLayout rootLayout;

    EventsListAdapter eventsListAdapter;
    Dialog dialog;

    public EventsFragment(Context context, Dialog dialog, boolean isDay, boolean darkTheme) {
        this.context = context;
        this.isDay = isDay;
        this.darkTheme = darkTheme;
        this.dialog = dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        ButterKnife.bind(this,view);
        defineTheme();
        viewModelInit();
        dismissListenerSetup();
        return view;
    }

    private void defineTheme() {
        if (darkTheme){
            noEvents.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            rootLayout.setBackgroundColor(context.getResources().getColor(R.color.darkThemeColorSecondary));
        } else {
            noEvents.setTextColor(context.getResources().getColor(R.color.colorAccent));
            rootLayout.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        }

    }

    private void dismissListenerSetup() {
        dialog.setOnDismissListener(dialog -> {
            playerLiveData.removeObservers(getViewLifecycleOwner());
            roleLiveData.removeObservers(getViewLifecycleOwner());
            abilityLiveData.removeObservers(getViewLifecycleOwner());
            powerLiveData.removeObservers(getViewLifecycleOwner());
            actionLiveData.removeObservers(getViewLifecycleOwner());
            conditionLiveData.removeObservers(getViewLifecycleOwner());
            eventLiveData.removeObservers(getViewLifecycleOwner());
            Log.e("observers", "dismissed");
        });
    }

    private void viewModelInit() {
        abilityViewModel = new ViewModelProvider(this).get(AbilityViewModel.class);
        powerViewModel = new ViewModelProvider(this).get(PowerViewModel.class);
        roleViewModel = new ViewModelProvider(this).get(RoleViewModel.class);
        playerViewModel = new ViewModelProvider(this).get(PlayerViewModel.class);
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);
        conditionViewModel = new ViewModelProvider(this).get(ConditionViewModel.class);
        actionViewModel = new ViewModelProvider(this).get(ActionViewModel.class);
        initLiveDataObjects();
        listsInit();
    }

    private void initLiveDataObjects() {
        playerLiveData = playerViewModel.getAll();
        eventLiveData = eventViewModel.getAll();
        conditionLiveData = conditionViewModel.getAll();
        actionLiveData = actionViewModel.getAll();
        roleLiveData = roleViewModel.getAll();
        powerLiveData = powerViewModel.getAll();
        abilityLiveData = abilityViewModel.getAll();
    }

    private void listsInit() {
        playerLiveData.observe(getViewLifecycleOwner(), players -> {
            this.players = players;
            roleLiveData.observe(getViewLifecycleOwner(), roles ->{
                this.roles = roles;
                abilityLiveData.observe(getViewLifecycleOwner(), abilities -> {
                    this.abilities = abilities;
                    powerLiveData.observe(getViewLifecycleOwner(), powers -> {
                        this.powers = powers;
                        actionLiveData.observe(getViewLifecycleOwner(), actions -> {
                            this.actions = actions;
                            conditionLiveData.observe(getViewLifecycleOwner(), conditions -> {
                                this.conditions = conditions;
                                eventLiveData.observe(getViewLifecycleOwner(), events -> {
                                    recyclerviewSetup();
                                    setDataToRecyclerview(events);
                                });
                            });
                        });
                    });
                });
            });
        });
    }

    private void recyclerviewSetup() {
        eventsListAdapter = new EventsListAdapter(context, players, roles, abilities, powers, actions, conditions, isDay,darkTheme);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(eventsListAdapter);
    }

    private void setDataToRecyclerview (List<EventDataHolder> events){
        List<EventDataHolder> currentEvents = getCurrentEvents(events);
        List<EventDataHolder> timeSeparatedEvents = separateByTimeSet(currentEvents);
        if(timeSeparatedEvents.size() > 0){
            List<List<EventDataHolder>> subDividedEvents = eventsSubdivision(timeSeparatedEvents);
            eventsListAdapter.setData(subDividedEvents);
            noEvents.setVisibility(View.INVISIBLE);
        } else {
            noEvents.setVisibility(View.VISIBLE);
        }
    }

    private List<EventDataHolder> separateByTimeSet(List<EventDataHolder> events) {
        List<EventDataHolder> events_temp = new ArrayList<>();
        for(int i = 0; i < events.size(); i++){
            if (isDay == events.get(i).isDay()){
                events_temp.add(events.get(i));
            }
        }
        return events_temp;
    }

    private List<EventDataHolder> getCurrentEvents(List<EventDataHolder> events) {
        List<EventDataHolder> events_temp = new ArrayList<>();
        for(int i = 0; i < events.size(); i++){
            if (events.get(i).isCurrent()){
                events_temp.add(events.get(i));
            }
        }
        return events_temp;
    }

    private List<List<EventDataHolder>> eventsSubdivision(List<EventDataHolder> commonEvents) {
        List<List<EventDataHolder>> events = new ArrayList<>();
        Iterator<Integer> iterator = getIds(commonEvents).iterator();

        while(iterator.hasNext()){
            int id = iterator.next();
            List<EventDataHolder> subEvents = new ArrayList<>();
            for (int i2 = 0; i2 < commonEvents.size(); i2++){
                if (commonEvents.get(i2).getPlayerId() == id){
                    subEvents.add(commonEvents.get(i2));
                }
            }
            events.add(subEvents);
        }

        return events;
    }

    private Set<Integer> getIds(List<EventDataHolder> commonEvents) {
        Set<Integer> idsSet = new HashSet<>();
        for (int i = 0; i < commonEvents.size(); i++){
            idsSet.add(commonEvents.get(i).getPlayerId());
        }
        return idsSet;
    }
}
