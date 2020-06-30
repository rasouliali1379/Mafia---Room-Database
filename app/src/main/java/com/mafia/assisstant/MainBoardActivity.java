package com.mafia.assisstant;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mafia.assisstant.DataHolders.AbilityCountDataHolder;
import com.mafia.assisstant.DataHolders.ChangeAbilityDataHolder;
import com.mafia.assisstant.Fragments.DayFragment;
import com.mafia.assisstant.Fragments.NightFragment;
import com.mafia.assisstant.Room.DataHolder.AbilityDataHolder;
import com.mafia.assisstant.Room.DataHolder.ActionDataHolder;
import com.mafia.assisstant.Room.DataHolder.ConditionDataholder;
import com.mafia.assisstant.Room.DataHolder.EventDataHolder;
import com.mafia.assisstant.Room.DataHolder.KindDataHolder;
import com.mafia.assisstant.Room.DataHolder.PlayerDataHolder;
import com.mafia.assisstant.Room.DataHolder.PowerDataHolder;
import com.mafia.assisstant.Room.DataHolder.RoleDataHolder;
import com.mafia.assisstant.Room.ViewModel.AbilityViewModel;
import com.mafia.assisstant.Room.ViewModel.ActionViewModel;
import com.mafia.assisstant.Room.ViewModel.ConditionViewModel;
import com.mafia.assisstant.Room.ViewModel.EventViewModel;
import com.mafia.assisstant.Room.ViewModel.KindViewModel;
import com.mafia.assisstant.Room.ViewModel.PlayerViewModel;
import com.mafia.assisstant.Room.ViewModel.PowerViewModel;
import com.mafia.assisstant.Room.ViewModel.RoleViewModel;
import com.mafia.assisstant.ViewModels.MainBoardViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MainBoardActivity extends AppCompatActivity {
    Menu menu;

    public List<PlayerDataHolder> allPlayers;

    private List<RoleDataHolder> roles;
    private List<AbilityDataHolder> abilities;
    private List<PowerDataHolder> powers;
    private List<ActionDataHolder> actions;
    private List<ConditionDataholder> conditions;
    private List<KindDataHolder> kinds;
    private List<PlayerDataHolder> playersActioned;
    private List<Integer> removedPowers;
    private List<ChangeAbilityDataHolder> changedAbilities;
    private List<AbilityCountDataHolder> abilityCounts;

    LiveData<List<EventDataHolder>> eventsLiveData;

    PlayerViewModel playerViewModel;
    KindViewModel kindViewModel;
    AbilityViewModel abilityViewModel;
    PowerViewModel powerViewModel;
    RoleViewModel roleViewModel;
    MainBoardViewModel mainBoardViewModel;
    EventViewModel eventViewModel;
    ActionViewModel actionViewModel;
    ConditionViewModel conditionViewModel;

    ActionBar actionBar;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;


    int dayCount = 1;
    int lastStarterPosition;
    int roundDayNum, roundNightNum, nightsPassed = 0, daysPassed = 0;

    private static final int MAFIA_TYPE_ID = 1;
    private static final int CIVIL_TYPE_ID = 2;
    private static final int CIVIL_WON = 1;
    private static final int MAFIA_WON = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_board);

        prefs = getSharedPreferences("Mafia" , MODE_PRIVATE);
        editor = prefs.edit();

        actionBar = getSupportActionBar();
        viewModelInit();

        setTitle(getResources().getString(R.string.day) + " " + dayCount);

        //Todo 17- implement timeSet
        //Todo 22- add all players count
        //Todo 23- fix remaining players

    }

    private void viewModelInit() {
        kindViewModel = new ViewModelProvider(this).get(KindViewModel.class);
        abilityViewModel = new ViewModelProvider(this).get(AbilityViewModel.class);
        powerViewModel = new ViewModelProvider(this).get(PowerViewModel.class);
        roleViewModel = new ViewModelProvider(this).get(RoleViewModel.class);
        playerViewModel = new ViewModelProvider(this).get(PlayerViewModel.class);
        mainBoardViewModel = new ViewModelProvider(this).get(MainBoardViewModel.class);
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);
        conditionViewModel = new ViewModelProvider(this).get(ConditionViewModel.class);
        actionViewModel = new ViewModelProvider(this).get(ActionViewModel.class);
        listsInit();
        initVars();
    }

    private void initVars() {
        removedPowers = new ArrayList<>();
        playersActioned = new ArrayList<>();
        changedAbilities = new ArrayList<>();
        abilityCounts = new ArrayList<>();
        eventsLiveData = eventViewModel.getAll();
    }

    private void listsInit() {
        playerViewModel.getAll().observe(this, players -> {
            if (players != null && players.size() > 0){
                allPlayers = players;
                defineStarterRandomly();
                roleViewModel.getAll().observe(this, roles ->{
                    if (roles != null && roles.size() > 0){
                        this.roles = roles;
                        abilityViewModel.getAll().observe(this, abilities -> {
                            if(abilities != null && abilities.size() > 0){
                                this.abilities = abilities;
                                powerViewModel.getAll().observe(this, powers -> {
                                    if (powers != null && powers.size() > 0){
                                        this.powers = powers;
                                        kindViewModel.getAll().observe(this, kinds->{
                                            if (kinds != null && kinds.size() > 0){
                                                this.kinds = kinds;
                                                countPlayers();
                                                if(prefs.getBoolean("game_in_progress", false)){
                                                    processEvents(true);
                                                } else {
                                                    editor.putBoolean("game_in_progress",true);
                                                    editor.commit();
                                                }

                                                roundDayNum = prefs.getInt("round_day_num", 1);
                                                roundNightNum = prefs.getInt("round_night_num", 1);
                                                switchFragment(new DayFragment(this, this.allPlayers, this.roles, this.abilities, this.kinds, this.powers, mainBoardViewModel, removedPowers, changedAbilities, abilityCounts));
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });

        actionViewModel.getAll().observe(this, actions -> {
            this.actions = actions;
            actionViewModel.getAll().removeObservers(this);
        });
        conditionViewModel.getAll().observe(this, conditions -> {
            this.conditions = conditions;
            conditionViewModel.getAll().removeObservers(this);
        });

    }



    private void switchFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.main_board_framelayout, fragment);
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.set_day_night) {
            toggleDayNight();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_board, menu);
        this.menu = menu;
        return true;
    }

    private void toggleDayNight() {
        MenuItem item = menu.findItem(R.id.set_day_night);

        if (item.getTitle().toString().indexOf(getResources().getString(R.string.announce_night)) > -1) {
            setNight(item);
        } else {
            setDay(item);
        }

    }

    private void setNight(MenuItem item) {
        nightTheme(item);
        checkTimeSet();
        daysPassed++;
        eventsLiveData.removeObservers(this);
        switchFragment(new NightFragment(this,
                this.allPlayers,
                this.roles,
                this.abilities,
                this.kinds,
                this.powers,
                mainBoardViewModel,
                removedPowers,
                changedAbilities,
                abilityCounts,
                dayCount));

    }

    private void nightTheme(MenuItem item) {
        Spannable btnTxt = new SpannableString(getResources().getString(R.string.announce_day));
        btnTxt.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), 0, btnTxt.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        item.setTitle(btnTxt);

        Spannable title = new SpannableString(getResources().getString(R.string.night) + " " + dayCount);
        title.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        setTitle(title);

        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.darkThemeColorPrimary)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.darkThemeColorSecondary));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.darkThemeColorPrimary));
        }
    }

    private void setDay(MenuItem item) {
        if (NightFragment.nightPlayersCount == NightFragment.ActivePosition + 1) {
            dayCount++;
            dayTheme(item);
            if(prefs.getBoolean("randomStarter",true)){
                defineStarterRandomly();
            } else {
                defineStarterManually();
            }
            processEvents(false);
            countPlayers();
            nightsPassed++;
            checkTimeSet();

            switchFragment(new DayFragment(this, this.allPlayers, this.roles, this.abilities, this.kinds, this.powers, mainBoardViewModel, removedPowers, changedAbilities, abilityCounts));
        } else {
            Toast.makeText(this , getResources().getString(R.string.player_remaining), Toast.LENGTH_LONG).show();
        }
    }

    private void checkTimeSet() {
        if (nightsPassed == roundNightNum && daysPassed == roundDayNum){
            nightsPassed = 0;
            daysPassed = 0;
            playersActioned.clear();
            abilityCounts.clear();
        }
    }

    private void countPlayers() {
        List<PlayerDataHolder> alivePlayers = getAlivePlayers();
        int mafiaPlayers = countEachSide(alivePlayers, MAFIA_TYPE_ID);
        int civilPlayers = countEachSide(alivePlayers, CIVIL_TYPE_ID);

        mainBoardViewModel.setMafiaCounter(mafiaPlayers);
        mainBoardViewModel.setCivilCounter(civilPlayers);
        mainBoardViewModel.setAllPlayers(mafiaPlayers + civilPlayers);
        if (mafiaPlayers == civilPlayers){
            showResultDialog(MAFIA_WON);
        } else if (mafiaPlayers == 0) {
            showResultDialog(CIVIL_WON);
        }
    }

    private void showResultDialog(int winnerIs) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(
                getResources().getString(R.string.ok),
                (dialog, id) -> finish());

        switch (winnerIs){
            case 1:
                builder.setMessage(getResources().getString(R.string.mafia_won));
                break;
            case 2:
                builder.setMessage(getResources().getString(R.string.civil_won));
                break;
        }

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        editor.putBoolean("game_in_progress",false);
        editor.commit();
    }

    private int countEachSide(List<PlayerDataHolder> alivePlayers, int typeId) {
        int counter = 0;
        List<RoleDataHolder> roles = getRelatedRroles(alivePlayers);

        for(int i= 0; i < roles.size(); i++){
            if (roles.get(i).getRoleKindId() == typeId){
                counter++;
            }
        }
        return counter;
    }

    private List<RoleDataHolder> getRelatedRroles(List<PlayerDataHolder> alivePlayers) {
        List<RoleDataHolder> roles = new ArrayList<>();
        for (int i = 0; i < alivePlayers.size(); i++){
            roles.add(getSingleRole(alivePlayers.get(i).getRoleId()));
        }
        return roles;
    }

    private RoleDataHolder getSingleRole(int roleId) {
        for (int i =0; i < roles.size(); i++){
            if (roles.get(i).getId() == roleId){
                return roles.get(i);
            }
        }
        return null;
    }


    private void dayTheme(MenuItem item){
        Spannable btnTxt = new SpannableString(getResources().getString(R.string.announce_night));
        btnTxt.setSpan(new ForegroundColorSpan(Color.BLACK), 0, btnTxt.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        item.setTitle(btnTxt);

        Spannable title = new SpannableString(getResources().getString(R.string.day) + " " + dayCount);
        title.setSpan(new ForegroundColorSpan(Color.BLACK), 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        setTitle(title);

        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    private void defineStarterRandomly(){
        List<PlayerDataHolder> alivePlayers = getAlivePlayers();
        Random rand = new Random();
        int randNum = rand.nextInt(alivePlayers.size());
        mainBoardViewModel.setStarterPlayer(alivePlayers.get(randNum));
        lastStarterPosition = randNum;
    }

    private void defineStarterManually() {
        List<PlayerDataHolder> alivePlayers = getAlivePlayers();
        int newPosition = findStarter(alivePlayers, mainBoardViewModel.getStarterPlayer().getValue().getPlayerId());
        if(newPosition < 0){
            newPosition = lastStarterPosition - 1 + prefs.getInt("manualStarter", 0);
            if (newPosition >= alivePlayers.size()){
                newPosition -= alivePlayers.size();
            }
        }
        mainBoardViewModel.setStarterPlayer(alivePlayers.get(newPosition));
    }

    private int findStarter(List<PlayerDataHolder> alivePlayers, int playerId) {
        for (int i = 0; i < alivePlayers.size(); i++){
            if (alivePlayers.get(i).getPlayerId() == playerId){
                return i;
            }
        }
        return -1;
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

    private void processEvents(boolean loadGame){
        eventsLiveData.observe(this, events -> {
            if (events != null){
                if (events.size() > 0){
                    if (loadGame){
                        filterEvents(events,loadGame);
                    }else {
                        List<EventDataHolder> events_temp = getCurrentNightEvents(events);
                        filterEvents(events_temp, loadGame);
                    }
                    playerViewModel.updateMultiple(playersActioned);
                    makeEventsUnCurrent(events);
                    eventsLiveData.removeObservers(this);
                }
            }
        });
    }

    private void makeEventsUnCurrent(List<EventDataHolder> events) {
        List<EventDataHolder> events_temp = getCurrentEvents(events);

        for (int i = 0; i < events_temp.size(); i++) {
            events_temp.get(i).setCurrent(false);
        }

        eventViewModel.updateMultiple(events_temp);
    }

    private List<EventDataHolder> getCurrentEvents(List<EventDataHolder> events) {
        List<EventDataHolder> currentEvents = new ArrayList<>();
        for (int i = 0; i < events.size(); i++){
            if (events.get(i).isCurrent()){
                currentEvents.add(events.get(i));
            }
        }
        return currentEvents;
    }

    private void filterEvents(List<EventDataHolder> events, boolean loadGame) {
        List<EventDataHolder> uniqueEvents = getUniqueEvents(getCurrentEvents(events));
        executeEvents(uniqueEvents, loadGame);
        List<EventDataHolder> commonEvents = excludeUniqueEvents(events, uniqueEvents);
        List<List<EventDataHolder>> subdividedEvents = eventsSubdivision(commonEvents);
        checkEachSubEvent(subdividedEvents,loadGame);
    }

    private void checkEachSubEvent(List<List<EventDataHolder>> subdividedEvents, boolean loadGame) {
        for(int i = 0; i < subdividedEvents.size(); i++) {
            checkAbilityAgaist(subdividedEvents.get(i),loadGame);
        }
    }

    private void checkAbilityAgaist(List<EventDataHolder> events, boolean loadGame) {
        if(events.size() > 1){
            for(int i = 0; i < events.size(); i++) {
                for (int i2 = i + 1; i2 < events.size(); i2++) {
                    if (getAbilityAgainst(events.get(i).getAbilityId()) == events.get(i2).getAbilityId()){
                        events.remove(i);
                        events.remove(i2 - 1);
                        checkAbilityAgaist(events, loadGame);
                    }
                }
            }
        } else {
            executeEvents(events, loadGame);
        }
    }

    private int getAbilityAgainst(int abilityId) {
        AbilityDataHolder ability = getAbility(abilityId);
        return ability.getAbilityAgainstId();
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

    private List<EventDataHolder> excludeUniqueEvents(List<EventDataHolder> events, List<EventDataHolder> uniqueEvents) {
        for (int i = 0; i < uniqueEvents.size(); i++){
            for (int i2 = 0; i2 < events.size(); i2++){
               if (events.get(i2).getId() == uniqueEvents.get(i).getId()){
                   events.remove(i2);
                   i2 = events.size();
               }
            }
        }
        return events;
    }

    private void executeEvents(List<EventDataHolder> events, boolean loadGame) {
        for (int i = 0 ; i < events.size(); i++){
            List<ActionDataHolder> actions = getRelatedActions(events.get(i).getAbilityId());
            checkConditions(actions, events.get(i), loadGame);
        }
    }

    private void checkConditions(List<ActionDataHolder> actions, EventDataHolder event, boolean loadGame) {
        for (int i = 0 ; i < actions.size(); i++){
            if (actions.get(i).getConditionId() > 0){
                ConditionDataholder condition = getActionCondition(actions.get(i).getConditionId());
                if (condition != null){
                    String [] ids = condition.getIncludeRoles().split("-");
                    if (checkSingleCondition(ids)){
                        if (loadGame){
                            if ( actions.get(i).getActionTypeId()  != 2){
                                executeActions(actions.get(i), event);
                            }
                        } else {
                            executeActions(actions.get(i), event);
                        }
                    }
                }
            } else {
                if (loadGame){
                    if ( actions.get(i).getActionTypeId()  != 2){
                        executeActions(actions.get(i), event);
                    }
                } else {
                    executeActions(actions.get(i), event);
                }
            }
        }
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

    private ConditionDataholder getActionCondition(int conditionId) {
        for (int i = 0; i < conditions.size(); i++){
            if (conditions.get(i).getId() == conditionId){
                return conditions.get(i);
            }
        }
        return null;
    }

    private void executeActions(ActionDataHolder action, EventDataHolder event) {
        switch (action.getActionTypeId()){
            case 1:
                deleteAction(event.getPlayerId());
                break;
            case 2:
                suicideAction(event.getCausedPlayerId());
            case 3:
                changeRoleAction(action, event.getPlayerId());
                break;
            case 4:
                changeAbility(action, event.getPlayerId());
                break;
            case 5:
                increaseAbilityAction(action);
                break;
            case 6:
                decreaseAbilityAction(action);
                break;
            case 7:
                removePowerAction(event.getPlayerId());
                break;
        }
    }



    private void removePowerAction(int playerId) {
        removedPowers.add(playerId);
    }

    private void decreaseAbilityAction(ActionDataHolder action) {
        AbilityCountDataHolder abilityCount = new AbilityCountDataHolder();
        abilityCount.setAbilityId(action.getAbilityId());
        abilityCount.setAbilityCount(-action.getAbilityReward());
        abilityCounts.add(abilityCount);
    }

    private void increaseAbilityAction(ActionDataHolder action) {
        AbilityCountDataHolder abilityCount = new AbilityCountDataHolder();
        abilityCount.setAbilityId(action.getAbilityId());
        abilityCount.setAbilityCount(action.getAbilityReward());
        abilityCounts.add(abilityCount);
    }

    private void changeAbility(ActionDataHolder action, int playerId) {
        ChangeAbilityDataHolder changeAbility = new ChangeAbilityDataHolder();
        changeAbility.setPlayerId(playerId);
        changeAbility.setAbilityToId(action.getAbilityToId());
        changedAbilities.add(changeAbility);
    }

    private void changeRoleAction(ActionDataHolder action, int playerId) {
        PlayerDataHolder player = new PlayerDataHolder();
        player.setPlayerId(playerId);
        player.setRoleId(action.getToRoleId());
        playersActioned.add(player);
    }

    private void suicideAction(int id) {
        int position = getPlayer(id);
        if(position > -1)
            allPlayers.get(position).setDead(true);
    }

    private void deleteAction(int playerId) {
        int position = getPlayer(playerId);
        if(position > -1)
            allPlayers.get(position).setDead(true);
    }

    private int getPlayer (int playerId){
        for(int i = 0; i < allPlayers.size(); i++){
            if (allPlayers.get(i).getPlayerId() == playerId){
                return i;
            }
        }
        return -1;
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


    private AbilityDataHolder getAbility(int abilityId) {
        for (int i = 0; i < abilities.size(); i++){
            if (abilities.get(i).getId() == abilityId){
                return abilities.get(i);
            }
        }
        return null;
    }

    private List<EventDataHolder> getUniqueEvents(List<EventDataHolder> events) {
        List<EventDataHolder> events_temp = new ArrayList<>();
        for (int i =0; i < events.size(); i++){
            if (!hasEqual(events, i)){
                events_temp.add(events.get(i));
            }
        }
        return events_temp;
    }

    private boolean hasEqual(List<EventDataHolder> events, int position) {
        for (int i = position; i < events.size(); i++){
            if (events.get(position).getPlayerId() == events.get(i).getPlayerId()){
                return true;
            }
        }
        return false;
    }

    private List<EventDataHolder> getCurrentNightEvents(List<EventDataHolder> events) {
        List<EventDataHolder> events_temp = new ArrayList<>();
        for(int i = 0; i < events.size(); i++){
            if (events.get(i).isCurrent() && !events.get(i).isDay()){
                events_temp.add(events.get(i));
            }
        }
        return events_temp;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(R.string.yes, (dialog, id) -> finish());
        builder.setNegativeButton(R.string.no_btn_label, (dialog, id) -> dialog.dismiss());
        builder.setMessage(getResources().getString(R.string.exit_game_message));
        builder.setTitle(R.string.attention);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
