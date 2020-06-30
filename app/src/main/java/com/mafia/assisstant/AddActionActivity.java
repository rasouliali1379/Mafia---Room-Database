package com.mafia.assisstant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mafia.assisstant.Adapters.AbilityListAdapter;
import com.mafia.assisstant.DataHolders.ActionTypesDataHolder;
import com.mafia.assisstant.Room.DataHolder.AbilityDataHolder;
import com.mafia.assisstant.Room.DataHolder.ActionDataHolder;
import com.mafia.assisstant.Room.DataHolder.KindDataHolder;
import com.mafia.assisstant.Room.DataHolder.PowerDataHolder;
import com.mafia.assisstant.Room.DataHolder.RoleDataHolder;
import com.mafia.assisstant.Room.ViewModel.AbilityViewModel;
import com.mafia.assisstant.Room.ViewModel.ActionViewModel;
import com.mafia.assisstant.Room.ViewModel.KindViewModel;
import com.mafia.assisstant.Room.ViewModel.PowerViewModel;
import com.mafia.assisstant.Room.ViewModel.RoleViewModel;
import com.mafia.assisstant.Utils.ActionTypes;
import com.mafia.assisstant.ViewModels.AddAbilityViewModel;
import com.mafia.assisstant.ViewModels.AddActionViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddActionActivity extends AppCompatActivity {

    @BindView(R.id.add_action_change_role_layout) LinearLayout changeRoleLayout;
    @BindView(R.id.add_action_target_power_layout) LinearLayout targetAbilityLayout;
    @BindView(R.id.add_action_change_power_layout)LinearLayout changeAbilityLayout;
    @BindView(R.id.add_action_reward_layout)LinearLayout rewardLayout;
    @BindView(R.id.add_action_change_role_type_layout)LinearLayout changeRoleTypeLayout;

    @BindView(R.id.add_action_change_power) FrameLayout changeAbilityBtn;
    @BindView(R.id.add_action_target_power_btn) FrameLayout targetAbilityBtn;
    @BindView(R.id.add_action_change_role_btn)FrameLayout changeRoleBtn;

    @BindView(R.id.add_action_change_role_btn_tv) TextView changeRoleBtnTv;
    @BindView(R.id.add_action_target_power_btn_tv) TextView targetAbilityBtnTv;
    @BindView(R.id.add_action_change_power_btn_tv) TextView changeAbilityBtnTv;
    @BindView(R.id.add_action_change_role_error_txt)TextView changeRoleErrorTxt;
    @BindView(R.id.add_action_target_ability_error_txt)TextView targetAbilityErrorTxt;
    @BindView(R.id.add_action_change_ability_to_error_txt)TextView changeAbilityToErrorTxt;
    @BindView(R.id.add_action_reward_txt) TextView rewardTxt;
    @BindView(R.id.add_action_round_txt)TextView roundTxt;

    @BindView(R.id.add_action_change_role_type_spinner)Spinner roleTypeSpinner;

    @BindView(R.id.add_action_reward_et) EditText rewardEt;


    private static final int TARGET_POWER = 1;
    private static final int CHANGE_POWER_TO = 2;
    private static final int POWER_LIST_ADAPTER = 1;
    private static final int ROLE_LIST_ADAPTER = 2;
    private static final int CHANGE_ROLE_TO = 2;
    private static final int RELATED_TO_ROLE_POWERS = 1;
    private static final int ALL_POWERS = 2;
    private static final int DECREASE_POWER = 1;
    private static final int INCREASE_POWER = 2;
    private static final int SHIELD = 3;
    private static final int SELF_SAFE = 4;


    boolean decreasePower = false;
    int mode;
    boolean editMode = false;
    ActionDataHolder ActiveAction;

    PowerViewModel powerViewModel;
    RoleViewModel roleViewModel;
    ActionViewModel actionViewModel;
    AddActionViewModel addActionViewModel;
    AbilityViewModel abilityViewModel;
    KindViewModel kindViewModel;

    private int AbilityId = 0, ConditionId = 0;

    private List<AbilityDataHolder> abilities;
    private List<PowerDataHolder> powers;
    private List<KindDataHolder> kinds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_action);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        setTitle(getResources().getString(R.string.add_action));
        initViewModels();
    }

    private void initViewModels() {
        powerViewModel = new ViewModelProvider(this).get(PowerViewModel.class);
        roleViewModel = new ViewModelProvider(this).get(RoleViewModel.class);
        actionViewModel = new ViewModelProvider(this).get(ActionViewModel.class);
        addActionViewModel = new ViewModelProvider(this).get(AddActionViewModel.class);
        abilityViewModel = new ViewModelProvider(this).get(AbilityViewModel.class);
        kindViewModel = new ViewModelProvider(this).get(KindViewModel.class);
        initVars();
    }

    private void initVars() {
        abilityViewModel.getAll().observe(this, abilities-> {
            if(abilities != null){
                this.abilities = abilities;
                powerViewModel.getAll().observe(this, powers-> {
                    if(powers != null){
                        this.powers = powers;
                        defineMode();
                    }
                });
            }
        });

    }

    private void defineMode() {
        Intent intent = getIntent();
        if (intent.hasExtra("action_id")){
            actionViewModel.getById(intent.getIntExtra("action_id", 0)).observe(this, action -> editMode(action));
            setTitle(getResources().getString(R.string.edit_action_title));
        } else if (intent.hasExtra("action_type") && intent.hasExtra("ability_id") && intent.hasExtra("condition_id")) {
            mode = intent.getIntExtra("action_type", 0);
            ConditionId = intent.getIntExtra("condition_id", 0);
            AbilityId = intent.getIntExtra("ability_id", 0);
            setTitle(getResources().getString(R.string.add_action));
            defineType(mode);
        }

    }

    private void editMode(ActionDataHolder action) {
        ActiveAction = action;
        editMode = true;
        mode = ActiveAction.getActionTypeId() - 1;
        defineType(mode);
    }

    private void fillFields(int mode) {

        switch (mode){
            case 2:
                roleViewModel.getById(ActiveAction.getToRoleId()).observe(this, role -> addActionViewModel.setChangeRole(role));
                break;
            case 3:
                abilityViewModel.getById(ActiveAction.getAbilityFromId()).observe(this, ability -> addActionViewModel.setTargetAbility(ability));
                abilityViewModel.getById(ActiveAction.getAbilityToId()).observe(this, ability -> addActionViewModel.setChangeAbility(ability));
                break;
            case 4:
                rewardEt.setText(String.valueOf(ActiveAction.getAbilityReward()));
                decreasePower = false;
                break;
            case 5:
                rewardEt.setText(String.valueOf(ActiveAction.getAbilityReward()));
                decreasePower = true;
                break;
            case 9:
            case 10:
                rewardEt.setText(String.valueOf(ActiveAction.getAbilityReward()));
                break;
            case 11:
                roleTypeSpinner.setSelection(ActiveAction.getToRoleTypeId() - 1);
                break;
        }
    }

    private void defineType(int mode) {
        switch (mode){
            case 2:
                changeRoleEnviroment();
                break;
            case 3:
                changePowerEnviroment();
                break;
            case 4:
                rewardPowerEnviroment(INCREASE_POWER);
                decreasePower = false;
                break;
            case 5:
                //Todo 7- removePower increasePower decreasePower need to have define ability
                rewardPowerEnviroment(DECREASE_POWER);
                decreasePower = true;
                break;
            case 9:
                rewardPowerEnviroment(SHIELD);
                break;
            case 10:
                rewardPowerEnviroment(SELF_SAFE);
                break;
            case 11:
                changeRoleTypeEnviroment();
                break;
        }
    }

    private void changeRoleTypeEnviroment() {
        changeRoleTypeLayout.setVisibility(View.VISIBLE);
        kindViewModel.getAll().observe(this, kinds -> {
            if(kinds != null){
                if (kinds.size() > 0){
                    this.kinds = kinds;
                    setSpinnerData(kinds);
                    if (editMode){
                        fillFields(mode);
                    }
                }
            }
        });
    }

    private void setSpinnerData (List<KindDataHolder> kinds) {
        List<String> list = new ArrayList<>();
        for (int i = 0 ; i < kinds.size(); i++){
            list.add(kinds.get(i).getKindName());
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item, list);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleTypeSpinner.setAdapter(spinnerArrayAdapter);
    }

    private void rewardPowerEnviroment(int which) {
        //Todo 6- seprate decrease power from increase power
        rewardLayout.setVisibility(View.VISIBLE);
        switch (which){
            case 1:
                rewardTxt.setText(getResources().getString(R.string.add_action_reduction_rate));
                break;
            case 2:
                rewardTxt.setText(getResources().getString(R.string.add_action_increase_rate));
                break;
            case 3:
                rewardTxt.setText(getResources().getString(R.string.add_action_shield_label));
                break;
            case 4:
                rewardTxt.setText(getResources().getString(R.string.add_action_self_safe_label));
                break;
            case 5:
                rewardTxt.setText(getResources().getString(R.string.add_action_change_role_type));
                break;
        }

        if (editMode){
            fillFields(mode);
        }
    }

    private void changePowerEnviroment() {
        targetAbilityLayout.setVisibility(View.VISIBLE);
        changeAbilityLayout.setVisibility(View.VISIBLE);
        targetAbilityBtn.setOnClickListener(v->recyclerViewDialogInflater(TARGET_POWER, POWER_LIST_ADAPTER, RELATED_TO_ROLE_POWERS));
        changeAbilityBtn.setOnClickListener(v->recyclerViewDialogInflater(CHANGE_POWER_TO, POWER_LIST_ADAPTER,ALL_POWERS));
        addActionViewModel.getTargetAbility().observe(this, ability -> {
            if(ability.getRoleId() == -1){
                targetAbilityBtnTv.setText(getResources().getString(R.string.choose_power));
            } else {
                targetAbilityBtnTv.setText(getSingleAbility(ability.getPowerId()).getPowerName());
            }
        });
        addActionViewModel.getChangeAbility().observe(this, ability -> {
            if (ability.getRoleId() == -1){
                changeAbilityBtnTv.setText(getResources().getString(R.string.choose_power));
            } else {
                changeAbilityBtnTv.setText(getSingleAbility(ability.getPowerId()).getPowerName());
            }
        });

        if (editMode){
            fillFields(mode);
        }
    }

    private PowerDataHolder getSingleAbility(int powerId) {
        for (int i = 0; i < powers.size(); i++){
            if (powers.get(i).getId() == powerId){
                return powers.get(i);
            }
        }
        return null;
    }

    private void changeRoleEnviroment() {
        changeRoleLayout.setVisibility(View.VISIBLE);
        changeRoleBtn.setOnClickListener(v -> recyclerViewDialogInflater(CHANGE_ROLE_TO, ROLE_LIST_ADAPTER, -1));
        addActionViewModel.getChangeRole().observe(this, role -> {
            if (role.getId() == -1){
                changeRoleBtnTv.setText(getResources().getString(R.string.choose_role));
            } else {
                changeRoleBtnTv.setText(role.getRoleName());
            }
        });
        if (editMode){
            fillFields(mode);
        }
    }

    private void recyclerViewDialogInflater(int wichInterface , int wichAdapter, int wichAbilities) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.recyclerview_dialog, viewGroup, false);
        DialogVH holder = new DialogVH(dialogView);
        holder.addBtnRoot.setVisibility(View.GONE);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();



        RoleListAdapter roleListAdapter = new RoleListAdapter(this, role -> {
            addActionViewModel.setChangeRole(role);

            alertDialog.dismiss();
        });

        holder.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Todo  4- Show powers that related to the role
        switch (wichAdapter){
            case POWER_LIST_ADAPTER:
                AbilityListAdapter abilityListAdapter;
                switch (wichAbilities){
                    case 1:
                        List<AbilityDataHolder> relatedAbilities = getNotDraftedAbilities(getRelatedAbilities(getAbility(AbilityId).getRoleId()));
                        if(relatedAbilities.size() > 0 ){
                            abilityListAdapter = new AbilityListAdapter(this, relatedAbilities, getRelatedPowers(relatedAbilities), false ,ability -> {
                                switch (wichInterface){
                                    case TARGET_POWER:
                                        addActionViewModel.setTargetAbility(ability);
                                        break;
                                    case CHANGE_POWER_TO:
                                        addActionViewModel.setChangeAbility(ability);
                                        break;
                                }
                                alertDialog.dismiss();
                            });
                            holder.recyclerView.setAdapter(abilityListAdapter);
                        } else {
                            holder.nothingFoundTxt.setVisibility(View.VISIBLE);
                            holder.nothingFoundTxt.setText(getResources().getString(R.string.no_abilities_found));
                        }
                        break;
                    case 2:
                        List<AbilityDataHolder> allAbilities = getNotDraftedAbilities(this.abilities);
                        if(allAbilities.size() > 0){
                            abilityListAdapter = new AbilityListAdapter(this, allAbilities, getRelatedPowers(allAbilities), false,  ability -> {
                                switch (wichInterface){
                                    case TARGET_POWER:
                                        addActionViewModel.setTargetAbility(ability);
                                        break;
                                    case CHANGE_POWER_TO:
                                        addActionViewModel.setChangeAbility(ability);
                                        break;
                                }
                                alertDialog.dismiss();
                            });
                            holder.recyclerView.setAdapter(abilityListAdapter);
                        } else {
                            holder.nothingFoundTxt.setVisibility(View.VISIBLE);
                            holder.nothingFoundTxt.setText(getResources().getString(R.string.no_abilities_found));
                        }
                        break;
                }
                break;
            case ROLE_LIST_ADAPTER:
                holder.recyclerView.setAdapter(roleListAdapter);
                roleViewModel.getAll().observe(this, roles -> roleListAdapter.setData(getNotDraftedRoles(roles)));
                break;
        }


        alertDialog.show();
    }

    private List<AbilityDataHolder> getNotDraftedAbilities(List<AbilityDataHolder> abilities) {
        List<AbilityDataHolder> temp_abilities = new ArrayList<>();
        for (int i = 0; i < abilities.size(); i++){
            if (!abilities.get(i).isDraft()){
                temp_abilities.add(abilities.get(i));
            }
        }
        return temp_abilities;
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

    private AbilityDataHolder getAbility(int abilityToId) {
        for (int i = 0 ; i < abilities.size(); i++){
            if (abilityToId == abilities.get(i).getId()){
                return abilities.get(i);
            }
        }
        return null;
    }

    private List<RoleDataHolder> getNotDraftedRoles(List<RoleDataHolder> roles) {
        List<RoleDataHolder> temp_roles = new ArrayList<>();
        for (int i = 0; i < roles.size(); i++){
            if (!roles.get(i).isDraft())
                temp_roles.add(roles.get(i));
        }
        return temp_roles;
    }

    @OnClick(R.id.submit_action_fab) void submitAction (){
        ActionDataHolder action = new ActionDataHolder();

        switch (mode){
            case 2:
                action.setActionTypeId(3);
                if (addActionViewModel.getChangeRole().getValue().getId() != -1){
                    action.setToRoleId(addActionViewModel.getChangeRole().getValue().getId());
                } else {
                    changeRoleErrorTxt.setVisibility(View.VISIBLE);
                    return;
                }
                break;
            case 3:
                action.setActionTypeId(4);
                if (addActionViewModel.getTargetAbility().getValue().getRoleId() != -1){
                    action.setAbilityFromId(addActionViewModel.getTargetAbility().getValue().getId());
                } else {
                    targetAbilityErrorTxt.setVisibility(View.VISIBLE);
                    return;
                }

                if (addActionViewModel.getChangeAbility().getValue().getRoleId() != -1){
                    action.setAbilityToId(addActionViewModel.getTargetAbility().getValue().getId());
                } else {
                    changeAbilityToErrorTxt.setVisibility(View.VISIBLE);
                    return;
                }

                break;
            case 4:
                if(rewardEt.getText() != null){
                    if (rewardEt.getText().toString().length() > 0) {
                        action.setActionTypeId(5);
                        action.setAbilityReward(Integer.parseInt(rewardEt.getText().toString()));
                    } else {
                        rewardEt.setError(getResources().getString(R.string.fill_manual_et_error));
                        return;
                    }
                } else {
                    rewardEt.setError(getResources().getString(R.string.fill_manual_et_error));
                    return;
                }
                break;
            case 5:
                if(rewardEt.getText() != null){
                    if (rewardEt.getText().toString().length() > 0){
                        action.setActionTypeId(6);
                        action.setAbilityReward(Integer.parseInt(rewardEt.getText().toString()));
                    } else {
                        rewardEt.setError(getResources().getString(R.string.fill_manual_et_error));
                        return;
                    }
                } else {
                    rewardEt.setError(getResources().getString(R.string.fill_manual_et_error));
                    return;
                }
                break;
            case 9:
                if(rewardEt.getText() != null){
                    if (rewardEt.getText().toString().length() > 0){
                        action.setActionTypeId(10);
                        action.setAbilityReward(Integer.parseInt(rewardEt.getText().toString()));
                    } else {
                        rewardEt.setError(getResources().getString(R.string.fill_manual_et_error));
                        return;
                    }
                } else {
                    rewardEt.setError(getResources().getString(R.string.fill_manual_et_error));
                    return;
                }
                break;
            case 10:
                if(rewardEt.getText() != null){
                    if (rewardEt.getText().toString().length() > 0){
                        action.setActionTypeId(11);
                        action.setAbilityReward(Integer.parseInt(rewardEt.getText().toString()));
                    } else {
                        rewardEt.setError(getResources().getString(R.string.fill_manual_et_error));
                        return;
                    }
                } else {
                    rewardEt.setError(getResources().getString(R.string.fill_manual_et_error));
                    return;
                }
                break;
            case 11:
                int kindPostion = roleTypeSpinner.getSelectedItemPosition();
                action.setActionTypeId(12);
                action.setToRoleTypeId(kinds.get(kindPostion).getId());
                break;
        }

        if(editMode){
            action.setId(ActiveAction.getId());
            action.setConditionId(ActiveAction.getConditionId());
            action.setAbilityId(ActiveAction.getAbilityId());
            actionViewModel.update(action);
        }else {
            action.setConditionId(ConditionId);
            action.setAbilityId(AbilityId);
            actionViewModel.insert(action);
        }

        finish();
    }

    static class DialogVH {
        @BindView(R.id.dialog_recyclerview)RecyclerView recyclerView;
        @BindView(R.id.add_button_root_layout)LinearLayout addBtnRoot;
        @BindView(R.id.recyclerview_dialog_nothing_found)TextView nothingFoundTxt;
        DialogVH(View rootView) {
            ButterKnife.bind(this , rootView);
        }
    }

    static class PowerListAdapter extends RecyclerView.Adapter<PowerListAdapter.ViewHolder>{
        Context context;
        List<PowerDataHolder> powers;
        private final OnItemClickListener listener;

        public PowerListAdapter(Context context, OnItemClickListener listener) {
            this.context = context;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.role_presentation_item,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.textView.setText(powers.get(position).getPowerName());
            holder.bind(powers.get(position), listener);
        }

        public void setData(List<PowerDataHolder> powers) {
            this.powers = powers;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            if (powers != null){
                return powers.size();
            }
            return 0;
        }

        public interface OnItemClickListener {
            void onItemClick(PowerDataHolder power);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.role_presentation_item_txt)TextView textView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this , itemView);
            }

            public void bind(PowerDataHolder power, final OnItemClickListener listener) {
                itemView.setOnClickListener((View view)-> listener.onItemClick(power));
            }
        }
    }

    static class RoleListAdapter extends RecyclerView.Adapter<RoleListAdapter.ViewHolder>{

        Context context;
        List<RoleDataHolder> roles;
        private final OnItemClickListener listener;

        public RoleListAdapter(Context context, OnItemClickListener listener) {
            this.context = context;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.role_presentation_item,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.textView.setText(roles.get(position).getRoleName());
            holder.bind(roles.get(position), listener);
        }

        public void setData(List<RoleDataHolder> roles) {
            this.roles = roles;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            if (roles != null){
                return roles.size();
            }
            return 0;
        }

        public interface OnItemClickListener {
            void onItemClick(RoleDataHolder role);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.role_presentation_item_txt)TextView textView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this , itemView);
            }

            public void bind(RoleDataHolder role, final OnItemClickListener listener) {
                itemView.setOnClickListener((View view)-> listener.onItemClick(role));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
