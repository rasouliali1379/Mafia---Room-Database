package com.mafia.assisstant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.mafia.assisstant.Adapters.ConditionListAdapter;

import com.mafia.assisstant.Fragments.ActionFragment;
import com.mafia.assisstant.Helpers.TabAdapter;
import com.mafia.assisstant.Room.DataHolder.ActionDataHolder;
import com.mafia.assisstant.Room.DataHolder.ConditionDataholder;
import com.mafia.assisstant.Room.DataHolder.KindDataHolder;
import com.mafia.assisstant.Room.DataHolder.RoleDataHolder;
import com.mafia.assisstant.Room.ViewModel.ActionViewModel;
import com.mafia.assisstant.Room.ViewModel.ConditionViewModel;
import com.mafia.assisstant.Room.ViewModel.KindViewModel;
import com.mafia.assisstant.Room.ViewModel.RoleViewModel;
import com.mafia.assisstant.Utils.Consts;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActionActivity extends AppCompatActivity {

    ActionViewModel actionViewModel;
    RoleViewModel roleViewModel;
    ConditionViewModel conditionViewModel;
    KindViewModel kindViewModel;

    private int AbilityId;
    private List<RoleDataHolder> roles;
    ConditionListAdapter conditionListAdapter;
    private List<KindDataHolder> kinds;

    @BindView(R.id.actions_activity_tablayout) TabLayout tabLayout;
    @BindView(R.id.actions_activity_tablayout) ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getResources().getString(R.string.action_activity_title));
        ButterKnife.bind(this);
        viewModelsSetup();

        Intent intent = getIntent();
        if (intent.hasExtra("ability_id")){
            AbilityId = intent.getIntExtra("ability_id", 0);
            roleViewModel.getAll().observe(this, roles -> {
                if (roles != null){
                    this.roles = roles;
                    kindViewModel.getAll().observe(this, kinds -> {
                        if(kinds != null){
                            this.kinds = kinds;
                            tabLayoutSetup();
                        }
                    });
                }
            });
        }


        if(MainActivity.progressDialog.isShowing()){
            MainActivity.progressDialog.dismiss();
        }
    }

    private void tabLayoutSetup() {
        TabAdapter adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new ActionFragment(this, kinds , roles, conditionViewModel, AbilityId, Consts.TARGET_ACTION_GROUP), getResources().getString(R.string.action_on_target));
        adapter.addFragment(new ActionFragment(this, kinds , roles, conditionViewModel, AbilityId, Consts.SELF_ACTION_GROUP), getResources().getString(R.string.action_on_self));
        adapter.addFragment(new ActionFragment(this, kinds , roles, conditionViewModel, AbilityId, Consts.OTHERS_ACTION_GROUP), getResources().getString(R.string.action_on_others));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void viewModelsSetup() {
        conditionViewModel = new ViewModelProvider(this).get(ConditionViewModel.class);
        roleViewModel = new ViewModelProvider(this).get(RoleViewModel.class);
        actionViewModel = new ViewModelProvider(this).get(ActionViewModel.class);
        kindViewModel = new ViewModelProvider(this).get(KindViewModel.class);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            detectEmptyConditions();
        }
        return super.onOptionsItemSelected(item);
    }

    private void detectEmptyConditions(){
        actionViewModel.getAll().observe(this, actions->{
            if (actions != null){
                conditionViewModel.getAll().observe(this, conditions-> {
                    if(conditions != null){
                        deleteEmptyConditions(conditions, actions);
                        finish();
                    }
                });
            }
        });
    }

    private void deleteEmptyConditions(List<ConditionDataholder> conditions, List<ActionDataHolder> actions) {
        if (actions.size() < 1){
            conditionViewModel.deleteAll();
        } else {
            List<ConditionDataholder> empty_conditions = new ArrayList<>();
            for (int i = 0; i < conditions.size(); i++){
                if (!hasAction(conditions.get(i).getId(), actions)){
                    empty_conditions.add(conditions.get(i));
                }
            }

            if (empty_conditions.size() > 0){
                conditionViewModel.deleteMultiple(empty_conditions);
            }
        }

    }

    private boolean hasAction(int id, List<ActionDataHolder> actions) {
        for (int i = 0; i < actions.size(); i++){
            if (id == actions.get(i).getConditionId()){
                return true;
            }
        }

        return false;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        detectEmptyConditions();
    }

    @Override
    protected void onStop() {
        super.onStop();
        conditionListAdapter.savePriorities();
    }
}
