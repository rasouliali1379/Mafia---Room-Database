package com.mafia.assisstant;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mafia.assisstant.Adapters.ConditionListAdapter;

import com.mafia.assisstant.Helpers.ConditionsTouchHelperCallBack;
import com.mafia.assisstant.Helpers.DayRolesTouchHelperCallback;
import com.mafia.assisstant.Room.DataHolder.ActionDataHolder;
import com.mafia.assisstant.Room.DataHolder.ConditionDataholder;
import com.mafia.assisstant.Room.DataHolder.KindDataHolder;
import com.mafia.assisstant.Room.DataHolder.RoleDataHolder;
import com.mafia.assisstant.Room.ViewModel.ActionViewModel;
import com.mafia.assisstant.Room.ViewModel.ConditionViewModel;
import com.mafia.assisstant.Room.ViewModel.KindViewModel;
import com.mafia.assisstant.Room.ViewModel.RoleViewModel;

import java.util.ArrayList;
import java.util.Collection;
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

    @BindView(R.id.conditions_recyclerview)RecyclerView recyclerView;
    @BindView(R.id.add_condtion_btn)LinearLayout addConditionBtn;

    private int AbilityId;
    private List<RoleDataHolder> roles;
    ConditionListAdapter conditionListAdapter;
    int selectedConditionId = -1, conditionsSize = 0;
    private List<ConditionDataholder> conditions;
    private List<KindDataHolder> kinds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getResources().getString(R.string.action_activity_title));
        ButterKnife.bind(this);
        Intent intent = getIntent();
        viewModelsSetup();

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

        //Todo 13- add kind condition
        //Todo 19- change wrong names
        addConditionBtn.setOnClickListener(v-> showOptionsDialog());

        if(MainActivity.progressDialog.isShowing()){
            MainActivity.progressDialog.dismiss();
        }
    }

    private void showOptionsDialog() {

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

                    conditionViewModel.insert(new ConditionDataholder(AbilityId, true, priority));
                    break;
                case 1:
                  conditionViewModel.insert(new ConditionDataholder(AbilityId, false, priority));
                    break;
            }
            selectedConditionId = -1;
        });
        builder.create();
        builder.show();
    }

    private void viewModelsSetup() {
        conditionViewModel = new ViewModelProvider(this).get(ConditionViewModel.class);
        roleViewModel = new ViewModelProvider(this).get(RoleViewModel.class);
        actionViewModel = new ViewModelProvider(this).get(ActionViewModel.class);
        kindViewModel = new ViewModelProvider(this).get(KindViewModel.class);
    }

    private void setupRecyclerview() {
        conditionListAdapter = new ConditionListAdapter(this, AbilityId, roles, kinds, actionViewModel, conditionViewModel, position -> setItemSelected(position));
        ItemTouchHelper.Callback dayCallback = new ConditionsTouchHelperCallBack(conditionListAdapter);
        ItemTouchHelper dayTouchHelper = new ItemTouchHelper(dayCallback);
        dayTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(conditionListAdapter);
        conditionViewModel.getByAbilityId(AbilityId).observe(this, conditions -> {
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

    private void setItemSelected(int position) {
        conditionListAdapter.conditions.get(position).setSelected(true);
        selectedConditionId = conditionListAdapter.conditions.get(position).getId();
        conditionListAdapter.notifyItemChanged(position);

        for (int i = 0; i < conditionListAdapter.conditions.size(); i++){
            if (conditionListAdapter.conditions.get(i).isSelected() && i != position){
                conditionListAdapter.conditions.get(i).setSelected(false);
            }
        }
        conditionListAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.add_action_fab)void addAction(){
        if(selectedConditionId > 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.action_types_title));
            builder.setItems(R.array.action_types , (DialogInterface dialog, int which) -> {
                if (which == 0 || which == 1 || which == 6 || which == 7 || which == 8 || which == 12){
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

    private void deleteEmptyConditions(List<ConditionDataholder> conditions, List<ActionDataHolder> actions) {
        if (actions.size() < 1){
            conditionViewModel.deleteAll();
        } else {
            List<ConditionDataholder> empty_conditions = new ArrayList<>();
            for (int i = 0; i < this.conditions.size(); i++){
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            //Todo 3- delete empty conditions
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
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

    @Override
    protected void onStop() {
        super.onStop();
        conditionListAdapter.savePriorities();
    }
}
