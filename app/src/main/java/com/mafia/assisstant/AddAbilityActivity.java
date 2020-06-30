package com.mafia.assisstant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mafia.assisstant.DataHolders.ActionTypesDataHolder;
import com.mafia.assisstant.Room.DataHolder.AbilityDataHolder;
import com.mafia.assisstant.Room.DataHolder.ActionDataHolder;
import com.mafia.assisstant.Room.DataHolder.PowerDataHolder;
import com.mafia.assisstant.Room.ViewModel.AbilityViewModel;
import com.mafia.assisstant.Room.ViewModel.ActionViewModel;
import com.mafia.assisstant.Room.ViewModel.PowerViewModel;
import com.mafia.assisstant.Utils.ActionTypes;
import com.mafia.assisstant.ViewModels.AddAbilityViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddAbilityActivity extends AppCompatActivity {

    @BindView(R.id.power_selector_layout) LinearLayout powerSelectorLayout;
    @BindView(R.id.power_action_btn) LinearLayout ActionsBtn;
    @BindView(R.id.power_action_tv) TextView powerActionTv;
    @BindView(R.id.power_per_day_et) EditText powerPerDayEt;
    @BindView(R.id.power_scale_et) EditText powerScaleEt;
    @BindView(R.id.power_desc_et)EditText powerDescEt;
    @BindView(R.id.power_select_error)TextView powerSelectError;
    @BindView(R.id.power_selector_tv) TextView powerSelectorTv;
    @BindView(R.id.add_ability_day_or_night_cb) CheckBox dayOrNight;
    @BindView(R.id.power_against_selector_tv)TextView powerAgainstSelectTv;
    @BindView(R.id.power_against_selector_layout) LinearLayout powerAgainstLayout;
    @BindView(R.id.power_health_et)EditText powerHealthEt;
    @BindView(R.id.power_day_coefficient_et)EditText dayCoefficientEt;
    @BindView(R.id.power_execution_delay_et)EditText executionDelayEt;
    @BindView(R.id.add_ability_power_against_remove_selection)ImageView powerAgainstRs;
    @BindView(R.id.add_ability_power_remove_selection)ImageView powerRs;
    @BindView(R.id.add_ability_by_default_cv)CardView byDefaultCv;
    @BindView(R.id.add_ability_by_default_cb)CheckBox byDefaultCb;

    AddAbilityViewModel addAbilityViewModel;
    PowerViewModel powerViewModel;
    AbilityViewModel abilityViewModel;
    ActionViewModel actionViewModel;

    AbilityDataHolder ability;
    int PowerID = -1 , RoleId = 0 , PowerAgainstID = 0;
    boolean editMode;
    int AbilityId;

    private static final int POWER = 1;
    private static final int POWER_AGAINST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ability);
        setTitle(getResources().getString(R.string.add_ability_activity_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        initViewModels();
        defineMode();
        powerPerDayEt.requestFocus();

        if (MainActivity.progressDialog.isShowing()){
            MainActivity.progressDialog.dismiss();
        }

        //Todo 20- remove edit and erase for power against items
        //Todo 21- remove selection btn for power against
        //Todo 24- add by default
    }

    private void defineMode() {
        Intent intent = getIntent();
        if (intent.hasExtra("ability_id")){
            AbilityId = intent.getIntExtra("ability_id" , 0);
            editMode(AbilityId);
            initObservers();
            setTitle(getResources().getString(R.string.edit_ability_title));
            editMode = true;
        } else if(intent.hasExtra("role_id")){
            RoleId = intent.getIntExtra("role_id" , 0);
            setTitle(getResources().getString(R.string.add_ability_activity_title));
            addNewAbility();
            powerPerDayListener();
            editMode = false;
        }
        initListeners();
    }

    private void initListeners() {
        byDefaultCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                byDefaultCv.setVisibility(View.GONE);
            } else {
                byDefaultCv.setVisibility(View.VISIBLE);
            }
        });
        powerAgainstLayout.setOnClickListener(v->selectPowerDialogInflater(POWER_AGAINST));
        powerSelectorLayout.setOnClickListener(v->selectPowerDialogInflater(POWER));
        powerRs.setOnClickListener(v-> {
            PowerID = -1;
            addAbilityViewModel.setSelectedPower(new PowerDataHolder(-1, getResources().getString(R.string.choose_power)));
        });
        powerAgainstRs.setOnClickListener(v-> {
            PowerAgainstID = -1;
            addAbilityViewModel.setSelectedPowerAgainst(new PowerDataHolder(-1, getResources().getString(R.string.choose_power)));
        });
        ActionsBtn.setOnClickListener(v->{
            MainActivity.progressDialog = new ProgressDialog(this);
            MainActivity.progressDialog.setCancelable(false);
            MainActivity.progressDialog.setMessage(getResources().getString(R.string.please_wait));
            MainActivity.progressDialog.show();
            Intent intent2 = new Intent(this, ActionActivity.class);
            intent2.putExtra("ability_id", AbilityId);
            startActivity(intent2);
        });

    }

    private void addNewAbility() {
        abilityViewModel.insert(new AbilityDataHolder(RoleId, true));
        new Handler().postDelayed(() -> abilityViewModel.getAll().observe(this, abilities -> {
            AbilityId = abilities.get(abilities.size()-1).getId();
            initObservers();
        }), 100);
    }

    private void initViewModels() {
        addAbilityViewModel = new ViewModelProvider(this).get(AddAbilityViewModel.class);
        powerViewModel = new ViewModelProvider(this).get(PowerViewModel.class);
        abilityViewModel = new ViewModelProvider(this).get(AbilityViewModel.class);
        actionViewModel = new ViewModelProvider(this).get(ActionViewModel.class);
    }

    private void initObservers() {
        addAbilityViewModel.getSelectedPower().observe(this , power -> {
            if (power.getId() == -1){
                powerSelectorTv.setText(getResources().getString(R.string.choose_power));
            } else {
                powerSelectorTv.setText(power.getPowerName());
                PowerID = power.getId();
            }

        });

        addAbilityViewModel.getSelectedPowerAgainst().observe(this, power -> {
            if(power.getId() == -1){
                powerAgainstSelectTv.setText(getResources().getString(R.string.choose_power));
            } else {
                powerAgainstSelectTv.setText(power.getPowerName());
                PowerAgainstID = power.getId();
            }
        });

        addAbilityViewModel.getActionSlector().observe(this, title -> powerActionTv.setText(title));

        actionViewModel.getByAbilityId(AbilityId).observe(this, actions ->{
            if (actions.size() > 0){
                setActionsLive(actions);
            } else {
                powerActionTv.setText(getResources().getString(R.string.power_action_define_tv));
            }
        });
    }

    private void setActionsLive(List<ActionDataHolder> actions) {
        StringBuilder sb = new StringBuilder();

        sb.append(actions.size());
        if (actions.size() == 1){
            sb.append(getResources().getString(R.string.action_defined));
        } else {
            sb.append(getResources().getString(R.string.actions_defined));
        }
        addAbilityViewModel.setActionSlector(sb.toString());
    }

    private void editMode(int id) {
        abilityViewModel.getById(id).observe(this, ability -> {
            this.ability = ability;
            powerViewModel.getById(ability.getPowerId()).observe(this, power -> {

                PowerID = power.getId();
                powerSelectorTv.setText(power.getPowerName());
                if(!ability.isRatio()){
                    if (ability.getPowerCount() > 0){
                        powerPerDayEt.setText(String.valueOf(ability.getPowerCount()));
                        powerScaleListener();
                    }
                } else {
                    if (ability.getPowerCount() > 0){
                        powerScaleEt.setText(String.valueOf(ability.getPowerCount()));
                        powerPerDayListener();
                    }
                }

                if(ability.getAbilityDesc() != null){
                    if (ability.getAbilityDesc().length() > 0){
                        powerDescEt.setText(ability.getAbilityDesc());
                    }
                }

                dayOrNight.setChecked(ability.isDay());
                byDefaultCb.setChecked(ability.isByDefault());
                powerSelectorLayout.setOnClickListener(v->selectPowerDialogInflater(POWER));
                powerAgainstLayout.setOnClickListener(v->selectPowerDialogInflater(POWER_AGAINST));
                ActionsBtn.setOnClickListener(v->{
                    Intent intent = new Intent(this, ActionActivity.class);
                    intent.putExtra("ability_id", AbilityId);
                    startActivity(intent);
                });
            });
        });
    }

    //Todo 10- fix error
    private TextWatcher powerPerDayTw = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (start  == 0 && powerScaleEt.getText().toString().length() > 0){
                powerScaleEt.setText("");
                powerScaleEt.setError(null);
                powerPerDayEt.setError(getResources().getString(R.string.power_scale_error_txt));
            }

            powerPerDayEt.removeTextChangedListener(powerPerDayTw);
            powerScaleListener();

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private TextWatcher powerScaleTw = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (start  == 0 && powerPerDayEt.getText().toString().length() > 0){
                powerPerDayEt.setText("");
                powerPerDayEt.setError(null);
                powerScaleEt.setError(getResources().getString(R.string.power_scale_error_txt));
            }

            powerScaleEt.removeTextChangedListener(powerScaleTw);
            powerPerDayListener();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void powerPerDayListener() {
        powerPerDayEt.addTextChangedListener( powerPerDayTw );
    }

    private void powerScaleListener() {
        powerScaleEt.addTextChangedListener(powerScaleTw);
    }

    @OnClick(R.id.submit_ability_fab) void submitAbilityFunc() {
        AbilityDataHolder ability = new AbilityDataHolder();
        ability.setId(AbilityId);

        if (PowerID > -1){
            ability.setPowerId(PowerID);
        }else{
            powerSelectError.setVisibility(View.VISIBLE);
            return;
        }

        if (PowerAgainstID > 0) {
            ability.setPowerAgainst(PowerAgainstID);
        }

        ability.setDay(dayOrNight.isChecked());

        if (byDefaultCb.isChecked()){
            ability.setByDefault(true);
        } else {
            ability.setByDefault(false);
            switch(filledEtDetector()){
                case 0:
                    powerPerDayEt.setError(getResources().getString(R.string.power_pay_day_error_title));
                    powerScaleEt.setError(getResources().getString(R.string.power_pay_day_error_title));
                    return;
                case 1:
                    ability.setPowerCount(Integer.parseInt(powerPerDayEt.getText().toString()));
                    ability.setRatio(false);
                    break;

                case 2:
                    ability.setPowerCount(Integer.parseInt(powerScaleEt.getText().toString()));
                    ability.setRatio(true);
                    break;
            }


            if (powerDescEt.getText() != null){
                ability.setAbilityDesc(powerDescEt.getText().toString());
            }



            if (powerHealthEt.getText() != null){
                if (powerHealthEt.getText().toString().length() > 0){
                    if (Integer.parseInt(powerHealthEt.getText().toString()) > 0){
                        ability.setHealth(Integer.parseInt(powerHealthEt.getText().toString()));
                    } else {
                        powerHealthEt.setError(getResources().getString(R.string.power_health_et_error));
                        return;
                    }
                }

            }

            if (dayCoefficientEt.getText() != null){
                if (dayCoefficientEt.getText().toString().length() > 0){
                    if (Integer.parseInt(dayCoefficientEt.getText().toString()) > 0){
                        ability.setHealth(Integer.parseInt(dayCoefficientEt.getText().toString()));
                    } else {
                        dayCoefficientEt.setError(getResources().getString(R.string.power_health_et_error));
                        return;
                    }
                }
            }

            if (executionDelayEt.getText() != null){
                if (executionDelayEt.getText().toString().length() > 0){
                    if (Integer.parseInt(executionDelayEt.getText().toString()) > 0){
                        ability.setHealth(Integer.parseInt(executionDelayEt.getText().toString()));
                    } else {
                        executionDelayEt.setError(getResources().getString(R.string.power_health_et_error));
                        return;
                    }
                }
            }
        }


        if (editMode){
            ability.setRoleId(this.ability.getRoleId());
        } else {
            ability.setDraft(false);
            ability.setRoleId(RoleId);
        }

        abilityViewModel.update(ability);
        finish();
    }

    private int filledEtDetector() {
        if (powerPerDayEt.getText() != null){
            if (powerPerDayEt.getText().toString().length() > 0)
                return 1;
        }

        if (powerScaleEt.getText() != null){
            if (powerScaleEt.getText().toString().length() > 0)
                return 2;
        }

        return 0;
    }

    private  void selectPowerDialogInflater(int which){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.recyclerview_dialog, viewGroup, false);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();

        DialogVH2 holder = new DialogVH2(dialogView);

        holder.addPowerBtn.setOnClickListener(v -> addPowerDialogInflater());

        CustomListAdapter customListAdapter = new CustomListAdapter(this, which,  power -> {
            switch (which){
                case 1:
                    addAbilityViewModel.setSelectedPower(power);
                    break;
                case 2:
                    addAbilityViewModel.setSelectedPowerAgainst(power);
                    break;
            }
            alertDialog.dismiss();
        }, addAbilityViewModel, abilityViewModel, powerViewModel);

        holder.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        holder.recyclerView.setAdapter(customListAdapter);
        powerViewModel.getAll().observe(this, powers -> customListAdapter.setData(powers));
        alertDialog.show();

    }
    private void addPowerDialogInflater() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.add_power_dialog, viewGroup, false);
        DialogVH holder = new DialogVH(dialogView);

        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        holder.submitBtn.setOnClickListener(v-> {
            if (holder.powerNameEt.getText() != null){
                if (holder.powerNameEt.getText().toString().length() > 0){
                    submitPower(holder);
                    alertDialog.dismiss();
                }else {
                    holder.powerNameEt.setError(getResources().getString(R.string.name_required));
                    return;
                }
            } else {
                holder.powerNameEt.setError(getResources().getString(R.string.name_required));
                return;
            }

        });
        holder.cancelBtn.setOnClickListener((View view)-> alertDialog.dismiss());

    }

    private void submitPower(DialogVH holder) {
        PowerDataHolder power = new PowerDataHolder();

        power.setPowerName(holder.powerNameEt.getText().toString());

        powerViewModel.insert(power);
    }

    static class DialogVH {

        @BindView(R.id.add_power_name_tv)EditText powerNameEt;
        @BindView(R.id.add_power_submit_btn)TextView submitBtn;
        @BindView(R.id.add_power_cancel_btn)TextView cancelBtn;

        DialogVH(View rootView) {
            ButterKnife.bind(this , rootView);

        }
    }

    static class DialogVH2 {

        @BindView(R.id.dialog_recyclerview) RecyclerView recyclerView;
        @BindView(R.id.add_power_btn) FrameLayout addPowerBtn;
        DialogVH2(View rootView) {
            ButterKnife.bind(this , rootView);

        }
    }

    static class CustomListAdapter extends RecyclerView.Adapter<CustomListAdapter.ViewHolder>{

        private Context context;
        private List<PowerDataHolder> powers;
        private final OnItemClickListener listener;
        private AddAbilityViewModel addAbilityViewModel;
        private AbilityViewModel abilityViewModel;
        private PowerViewModel powerViewModel;
        int which;
        public CustomListAdapter(Context context, int which,
                                 OnItemClickListener listener,
                                 AddAbilityViewModel addAbilityViewModel,
                                 AbilityViewModel abilityViewModel,
                                 PowerViewModel powerViewModel) {

            this.context = context;
            this.which = which;
            this.listener = listener;
            this.addAbilityViewModel = addAbilityViewModel;
            this.abilityViewModel = abilityViewModel;
            this.powerViewModel = powerViewModel;
        }

        @NonNull
        @Override
        public CustomListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.dropdown_item,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CustomListAdapter.ViewHolder holder, int position) {
            if (which == 1){
                holder.editBtn.setVisibility(View.VISIBLE);
                holder.earaseBtn.setVisibility(View.VISIBLE);
                holder.editBtn.setOnClickListener(v->editPowerDialog(powers.get(position)));
                holder.earaseBtn.setOnClickListener(v->earasePower(powers.get(position)));
            } else {
                holder.editBtn.setVisibility(View.INVISIBLE);
                holder.earaseBtn.setVisibility(View.INVISIBLE);
                holder.editBtn.setOnClickListener(null);
                holder.earaseBtn.setOnClickListener(null);
            }


            holder.label.setText(powers.get(position).getPowerName());
            holder.label.setTextDirection(View.TEXT_DIRECTION_LOCALE);
            holder.label.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

            holder.bind(powers.get(position), listener);
        }

        private void earasePower(PowerDataHolder power) {
            abilityViewModel.getByPowerId(power.getId()).observe((LifecycleOwner) context, abilities -> {
                if(abilities.size() > 0){
                    Toast.makeText(context , context.getResources().getString(R.string.relation_error), Toast.LENGTH_LONG).show();
                } else {
                    powerViewModel.delete(power);
                }

                if (addAbilityViewModel.getSelectedPower().getValue().getId() == power.getId()){
                    addAbilityViewModel.setSelectedPower(new PowerDataHolder(0 ,context.getResources().getString(R.string.choose_power)));
                }
            });


        }


        private void editPowerDialog(PowerDataHolder power) {
            Activity activity = (Activity) context;
            activity.runOnUiThread(() ->{
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                ViewGroup viewGroup = activity.findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(context).inflate(R.layout.add_power_dialog, viewGroup, false);
                DialogVH holder = new DialogVH(dialogView);
                builder.setView(dialogView);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                holder.powerNameEt.setText(power.getPowerName());
                holder.submitBtn.setOnClickListener(v-> {
                    if (holder.powerNameEt.getText() != null){
                        if (holder.powerNameEt.getText().toString().length() > 0){
                            updatePower(holder , power);
                            alertDialog.dismiss();
                        }else {
                            holder.powerNameEt.setError(context.getResources().getString(R.string.name_required));
                            return;
                        }
                    } else {
                        holder.powerNameEt.setError(context.getResources().getString(R.string.name_required));
                        return;
                    }

                });
                holder.cancelBtn.setOnClickListener((View view)-> alertDialog.dismiss());
            });

        }

        private void updatePower(DialogVH holder , PowerDataHolder power) {
            power.setPowerName(holder.powerNameEt.getText().toString());

            if (addAbilityViewModel.getSelectedPower().getValue().getId() == power.getId()){
                addAbilityViewModel.setSelectedPower(power);
            }

            powerViewModel.update(power);
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
            @BindView(R.id.dropdown_item_label) TextView label;
            @BindView(R.id.dropdown_item_edit) ImageView editBtn;
            @BindView(R.id.dropdown_item_earase) ImageView earaseBtn;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this , itemView);
            }

            public void bind(final PowerDataHolder power, final OnItemClickListener listener) {
                itemView.setOnClickListener((View view)-> listener.onItemClick(power));
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
