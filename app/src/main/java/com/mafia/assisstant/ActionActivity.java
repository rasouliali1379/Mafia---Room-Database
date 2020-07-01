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
