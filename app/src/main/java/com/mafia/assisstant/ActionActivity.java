package com.mafia.assisstant;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.mafia.assisstant.Adapters.ConditionListAdapter;

import com.mafia.assisstant.Helpers.ConditionsTouchHelperCallBack;
import com.mafia.assisstant.Room.DataHolder.ActionDataHolder;
import com.mafia.assisstant.Room.DataHolder.ConditionDataholder;
import com.mafia.assisstant.Room.DataHolder.KindDataHolder;
import com.mafia.assisstant.Room.DataHolder.RoleDataHolder;
import com.mafia.assisstant.Room.ViewModel.ActionViewModel;
import com.mafia.assisstant.Room.ViewModel.ConditionViewModel;
import com.mafia.assisstant.Room.ViewModel.KindViewModel;
import com.mafia.assisstant.Room.ViewModel.RoleViewModel;
import com.mafia.assisstant.Utils.Consts;
import com.mafia.assisstant.ViewModels.ActionActivityViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActionActivity extends AppCompatActivity {

    ActionViewModel actionViewModel;
    RoleViewModel roleViewModel;
    ConditionViewModel conditionViewModel;
    KindViewModel kindViewModel;

    private int AbilityId;
    private List<RoleDataHolder> roles;
    private List<KindDataHolder> kinds;
    private List<ConditionDataholder> conditions;
    private int conditionsSize, selectedConditionId;
    private ActionActivityViewModel actionActivityViewModel;
    ConditionListAdapter conditionListAdapter;

    @BindView(R.id.action_activity_recyclerview)RecyclerView recyclerView;

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
                            setupRecyclerview();
                        }
                    });
                }
            });
        }

        if(MainActivity.progressDialog.isShowing()){
            MainActivity.progressDialog.dismiss();
        }
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

    private void setupRecyclerview() {
        conditionListAdapter = new ConditionListAdapter(this, AbilityId, roles, kinds, actionViewModel, actionActivityViewModel, conditionViewModel, position -> {
            actionActivityViewModel.setSelectedCondition(position);
            selectedConditionId = conditions.get(position).getId();
        });
        ItemTouchHelper.Callback dayCallback = new ConditionsTouchHelperCallBack(conditionListAdapter);
        ItemTouchHelper dayTouchHelper = new ItemTouchHelper(dayCallback);
        dayTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(conditionListAdapter);
        conditionViewModel.getByAbilityIdAndCmd(AbilityId, Consts.CONDITION).observe( this, conditions -> {
            if (conditions != null){
                conditionsSize = conditions.size();
                this.conditions = sortConditionsByPriority(conditions);
                conditionListAdapter.setData(this.conditions);
                conditionListAdapter.changePriority();
            }
        });
    }

    private List<ConditionDataholder> sortConditionsByPriority(List<ConditionDataholder> conditions) {
        for (int i = 0; i < conditions.size(); i++){
            if (i + 1 != conditions.size()){
                if (conditions.get(i).getPriority() > conditions.get(i + 1).getPriority()) {
                    Collections.swap(conditions, i, i + 1);
                    i = 0;
                }
            }
        }
        return conditions;
    }

    @OnClick(R.id.action_activity_add_condition) void addCondition(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.action_types_title));
        builder.setItems(R.array.conditions , (DialogInterface dialog, int which) -> {
            int priority;
            if (conditions.size() > 1){
                priority = conditions.get(conditions.size() - 1).getPriority();
            } else {
                priority = 1;
            }
            switch (which){
                case 0:
                    conditionViewModel.insert(new ConditionDataholder(AbilityId, true, priority, false));
                    break;
                case 1:
                    conditionViewModel.insert(new ConditionDataholder(AbilityId, false, priority, false));
                    break;
                case 2:
                    conditionViewModel.insert(new ConditionDataholder(AbilityId, false, priority, true));
                    break;
            }
            selectedConditionId = -1;
        });
        builder.create();
        builder.show();
    }

    @OnClick(R.id.action_activity_add_action)void addAction(){
        if(selectedConditionId > 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.action_types_title));
            builder.setItems(R.array.action_types , (DialogInterface dialog, int which) -> {
                if (which == 8){
                    ActionDataHolder action = new ActionDataHolder();
                    action.setAbilityId(AbilityId);
                    action.setConditionId(selectedConditionId);
                    action.setActionTypeId(which + 1);
                    actionViewModel.insert(action);
                } else {
                    Intent intent = new Intent(this , AddActionActivity.class);
                    intent.putExtra("action_type", which);
                    intent.putExtra("ability_id", AbilityId);
                    intent.putExtra("condition_id", selectedConditionId);
                    startActivity(intent);
                }
            });
            builder.create();
            builder.show();
        } else if (conditionsSize < 1){
            Toast.makeText(this, getResources().getString(R.string.add_condition_warning),Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, getResources().getString(R.string.select_condition_warning),Toast.LENGTH_LONG).show();
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        detectEmptyConditions();
    }
}
