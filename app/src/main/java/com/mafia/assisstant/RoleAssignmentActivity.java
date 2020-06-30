package com.mafia.assisstant;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.mafia.assisstant.Fragments.DayRolesFragment;
import com.mafia.assisstant.Fragments.NightRolesFragment;
import com.mafia.assisstant.Helpers.TabAdapter;
import com.mafia.assisstant.Room.DataHolder.AbilityDataHolder;
import com.mafia.assisstant.Room.DataHolder.PlayerDataHolder;
import com.mafia.assisstant.Room.DataHolder.PowerDataHolder;
import com.mafia.assisstant.Room.DataHolder.RoleDataHolder;
import com.mafia.assisstant.Room.ViewModel.AbilityViewModel;
import com.mafia.assisstant.Room.ViewModel.PlayerViewModel;
import com.mafia.assisstant.Room.ViewModel.PowerViewModel;
import com.mafia.assisstant.Room.ViewModel.RoleViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RoleAssignmentActivity extends AppCompatActivity {
    Context context = this;
    int mafiaNeeded , civilNeeded;

    PlayerViewModel playerViewModel;
    RoleViewModel roleViewModel;
    AbilityViewModel abilityViewModel;
    PowerViewModel powerViewModel;

    List<RoleDataHolder> allRoles;
    List<PlayerDataHolder> players;
    List<AbilityDataHolder> abilities;
    List<PowerDataHolder> powers;

    public static List<RoleDataHolder> dayRoles;
    public static List<RoleDataHolder> nightRoles;

    @BindView(R.id.role_assignment_tab_layout)TabLayout tabLayout;
    @BindView(R.id.role_assignment_view_pager)ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_assignment);
        setTitle(getResources().getString(R.string.role_assignment_activity_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        viewModelsInit();
        if (MainActivity.progressDialog.isShowing()){
            MainActivity.progressDialog.dismiss();
        }
    }

    private void tabLayoutSetup() {
        TabAdapter adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new NightRolesFragment(), getResources().getString(R.string.night_roles));
        adapter.addFragment(new DayRolesFragment(), getResources().getString(R.string.day_roles));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }


    private void viewModelsInit() {
        playerViewModel = new ViewModelProvider(this).get(PlayerViewModel.class);
        roleViewModel = new ViewModelProvider(this).get(RoleViewModel.class);
        abilityViewModel = new ViewModelProvider(this).get(AbilityViewModel.class);
        powerViewModel = new ViewModelProvider(this).get(PowerViewModel.class);
        fillTheLists();
    }

    private void fillTheLists() {
        playerViewModel.getAll().observe(this, players -> {
            this.players = players;
            calculationStuff();
        });

        roleViewModel.getAll().observe(this, roles -> {
            List<RoleDataHolder> mainRoles = new ArrayList<>();
            for(int i =0; i < roles.size(); i++){
                if (!roles.get(i).isDraft() && roles.get(i).getId() != 1 && roles.get(i).getId() != 2){
                    mainRoles.add(roles.get(i));
                }
            }

            allRoles = mainRoles;

            abilityViewModel.getAll().observe(this, abilities -> {

                List<AbilityDataHolder> mainAbilities = new ArrayList<>();
                for(int i =0; i < abilities.size(); i++){
                    if (!abilities.get(i).isDraft()){
                        mainAbilities.add(abilities.get(i));
                    }
                }

                this.abilities = mainAbilities;

                powerViewModel.getAll().observe(this, powers -> {
                    this.powers = powers;
                    splitData();
                });

            });

        });
    }


    private void splitData() {

        nightRoles = new ArrayList<>();
        dayRoles = new ArrayList<>();

        for (int i = 0; i < allRoles.size(); i++){
            List<AbilityDataHolder> abilities_temp = new ArrayList<>();
            for (int i2 = 0; i2 < abilities.size(); i2++){
                if (allRoles.get(i).getId() == abilities.get(i2).getRoleId()){
                    abilities_temp.add(abilities.get(i2));
                }
            }

            boolean [] workTimes = checkWorkTimes(abilities_temp);

            if (workTimes[1]){
                nightRoles.add(allRoles.get(i));
            } else {
                dayRoles.add(allRoles.get(i));
            }
        }
        tabLayoutSetup();

    }

    private boolean[] checkWorkTimes(List<AbilityDataHolder> abilities_temp) {
        boolean [] checkList = new boolean[2];
        for (int i = 0; i < abilities_temp.size() ; i++){
            if (abilities_temp.get(i).isDay()){
                checkList[0] = true;
            } else{
                checkList[1] = true;
            }
            if (checkList[0] && checkList [1]){
                return checkList;
            }
        }
        return checkList;
    }

    private void calculationStuff() {
        int temp = players.size();
        double temp2 = temp/3;
        if (temp2 == (int)temp2){
            mafiaNeeded = (int) temp2;
            civilNeeded = temp - (int) temp2;
        } else {
            if ((int)temp2 < temp2 && (int)temp2 + 1 > temp2){
                mafiaNeeded = (int) temp2;
                double temp3 =  temp2 * 2;
                civilNeeded = (int) temp3 + 1;
            }
        }
    }

    @OnClick(R.id.submit_roles_btn)void submitRoles(){
        int mafiaPopulation = countMafia();
        int civilPopulation = countCivil();

        if (mafiaPopulation > mafiaNeeded){
            Toast.makeText(context , "To many mafias, drop " + (mafiaPopulation - mafiaNeeded),Toast.LENGTH_LONG).show();
            return;
        }
        if (civilPopulation > civilNeeded){
            Toast.makeText(context , "To many civils, drop " + ( civilPopulation - civilNeeded ),Toast.LENGTH_LONG).show();
            return;
        }

        if (mafiaPopulation < mafiaNeeded || civilPopulation < civilNeeded){
            showDialog();
        }

        if (civilPopulation == civilNeeded && mafiaPopulation == mafiaNeeded){
            assignRoles();
        }
    }

    private void showDialog(){
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.add_remain_roles)
                .setPositiveButton(R.string.yes, (dialog, id) -> addRequiredRoles())
                .setNegativeButton(R.string.no_btn_label, (dialog, id) -> dialog.dismiss());
        AlertDialog alert = builder.create();
        alert.setTitle(R.string.attention);
        alert.show();
    }

    private void assignRoles(){
        allRoles.clear();
        allRoles.addAll(nightRoles);
        allRoles.addAll(dayRoles);

        Collections.shuffle(allRoles);

        for (int i = 0; i < players.size(); i++){
            players.get(i).setRoleId(allRoles.get(i).getId());
            players.get(i).setPriority(allRoles.get(i).getPriority());
        }

        playerViewModel.updateMultiple(players);

        startActivity(new Intent(RoleAssignmentActivity.this, RolePresentationActivity.class));
        finish();
    }

    private void addRequiredRoles() {
        int mafiaRequired = mafiaNeeded - countMafia();
        int civilRequired = civilNeeded - countCivil();

        for (int i = 0; i < mafiaRequired; i++){
            nightRoles.add(new RoleDataHolder(1, 1,"Mafia", false));
        }

        for (int i = 0; i < civilRequired; i++){
            dayRoles.add(new RoleDataHolder(2, 2,"Civil", false));
        }

        Log.e("mafiaRequired", String.valueOf(mafiaRequired));
        Log.e("civilRequired", String.valueOf(civilRequired));

        assignRoles();
    }

    private int countMafia() {
        int num = 0;

        List<RoleDataHolder> temp = new ArrayList<>();
        temp.addAll(nightRoles);
        temp.addAll(dayRoles);

        for (int i =0; i < temp.size(); i++){
            if (temp.get(i).getRoleKindId() == 1){
                num++;
            }
        }

        return num;
    }

    private int countCivil() {
        int num = 0;

        List<RoleDataHolder> temp = new ArrayList<>();
        temp.addAll(nightRoles);
        temp.addAll(dayRoles);

        for (int i =0; i < temp.size(); i++){
            if (temp.get(i).getRoleKindId() == 2){
                num++;
            }
        }

        return num;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.set_day_night:
                splitData();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_board, menu);
        MenuItem item = menu.findItem(R.id.set_day_night);
        item.setTitle(getResources().getString(R.string.reset));
        return true;
    }
}
