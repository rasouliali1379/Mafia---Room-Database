package com.mafia.assisstant.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mafia.assisstant.Adapters.ConditionListAdapter;
import com.mafia.assisstant.AddActionActivity;
import com.mafia.assisstant.Helpers.ConditionsTouchHelperCallBack;
import com.mafia.assisstant.R;
import com.mafia.assisstant.Room.DataHolder.ActionDataHolder;
import com.mafia.assisstant.Room.DataHolder.ConditionDataholder;
import com.mafia.assisstant.Room.DataHolder.KindDataHolder;
import com.mafia.assisstant.Room.DataHolder.RoleDataHolder;
import com.mafia.assisstant.Room.ViewModel.ActionViewModel;
import com.mafia.assisstant.Room.ViewModel.ConditionViewModel;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActionFragment extends Fragment {

    @BindView(R.id.action_fragment_recyclerview)RecyclerView recyclerView;

    ConditionListAdapter conditionListAdapter;

    private Context context;
    private List<KindDataHolder> kinds;
    private List<RoleDataHolder> roles;
    private List<ConditionDataholder> conditions;
    private int AbilityId, ActionGroup, conditionsSize, selectedConditionId;
    private ConditionViewModel conditionViewModel;
    private ActionViewModel actionViewModel;


    public ActionFragment(Context context, List<KindDataHolder> kinds, List<RoleDataHolder> roles, ConditionViewModel conditionViewModel, ActionViewModel actionViewModel, int AbilityId, int ActionGroup) {
        this.context = context;
        this.kinds = kinds;
        this.roles = roles;
        this.conditionViewModel = conditionViewModel;
        this.AbilityId = AbilityId;
        this.ActionGroup = ActionGroup;
        this.actionViewModel = actionViewModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_action, container, false);
        ButterKnife.bind(this, view);
        setupRecyclerview();
        return view;
    }

    private void setupRecyclerview() {
        conditionListAdapter = new ConditionListAdapter(context, AbilityId, roles, kinds, actionViewModel, conditionViewModel, position -> setItemSelected(position));
        ItemTouchHelper.Callback dayCallback = new ConditionsTouchHelperCallBack(conditionListAdapter);
        ItemTouchHelper dayTouchHelper = new ItemTouchHelper(dayCallback);
        dayTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(conditionListAdapter);
        conditionViewModel.getByAbilityId(AbilityId).observe((LifecycleOwner) context, conditions -> {
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

    @OnClick(R.id.action_fragment_add_condition) void addCondition (){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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

    @OnClick(R.id.action_fragment_add_action)void addAction(){
        if(selectedConditionId > 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(getResources().getString(R.string.action_types_title));
            builder.setItems(R.array.action_types , (DialogInterface dialog, int which) -> {
                if (which == 0 || which == 1 || which == 6 || which == 7 || which == 8 || which == 12){
                    ActionDataHolder action = new ActionDataHolder();
                    action.setAbilityId(AbilityId);
                    action.setConditionId(selectedConditionId);
                    action.setActionTypeId(which + 1);
                    actionViewModel.insert(action);
                } else {
                    Intent intent = new Intent(context , AddActionActivity.class);
                    intent.putExtra("action_type", which);
                    intent.putExtra("ability_id", AbilityId);
                    intent.putExtra("condition_id", selectedConditionId);
                    startActivity(intent);
                }
            });
            builder.create();
            builder.show();
        } else if (conditionsSize < 1){
            Toast.makeText(context, getResources().getString(R.string.add_condition_warning),Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, getResources().getString(R.string.select_condition_warning),Toast.LENGTH_LONG).show();
        }

    }
}