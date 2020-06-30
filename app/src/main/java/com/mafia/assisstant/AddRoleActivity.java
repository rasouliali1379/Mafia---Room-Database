package com.mafia.assisstant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.mafia.assisstant.Room.DataHolder.AbilityDataHolder;
import com.mafia.assisstant.Room.DataHolder.KindDataHolder;
import com.mafia.assisstant.Room.DataHolder.RoleDataHolder;
import com.mafia.assisstant.Room.ViewModel.AbilityViewModel;
import com.mafia.assisstant.Room.ViewModel.KindViewModel;
import com.mafia.assisstant.Room.ViewModel.PowerViewModel;
import com.mafia.assisstant.Room.ViewModel.RoleViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddRoleActivity extends AppCompatActivity {

    RoleViewModel roleViewModel;
    KindViewModel kindViewModel;
    AbilityViewModel abilityViewModel;
    PowerViewModel powerViewModel;
    List<KindDataHolder> kinds;
    RoleDataHolder ActiveRole;

    @BindView(R.id.add_role_ability_txt) TextView roleAbilityTxt;
    @BindView(R.id.role_name_txt) EditText roleNameTxt;
    @BindView(R.id.role_type_spinner) Spinner roleKindSpinner;
    @BindView(R.id.ability_select_error) TextView abilitySelectError;
    @BindView(R.id.role_ability_layout)LinearLayout roleAbilityLayout;

    boolean newRole = true;
    int AbilityNum = 0;
    int id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_role);
        setTitle(getResources().getString(R.string.add_role_activity_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        viewModelsInit();
        spinnerSetup();
        if (MainActivity.progressDialog.isShowing()){
            MainActivity.progressDialog.dismiss();
        }
    }



    private void viewModelsInit() {
        roleViewModel = new ViewModelProvider(this).get(RoleViewModel.class);
        kindViewModel = new ViewModelProvider(this,new KindViewModelFactory(getApplication())).get(KindViewModel.class);
        abilityViewModel =new ViewModelProvider(this).get(AbilityViewModel.class);
        powerViewModel = new ViewModelProvider(this).get(PowerViewModel.class);
        roleViewModel.deleteDrafts();
        abilityViewModel.deleteDrafts();
    }

    private void defineMode() {
        Intent intent = getIntent();
        if (intent.hasExtra("id")){
            id = intent.getIntExtra("id" , -1);
            editEnviroment(id);
            newRole = false;
        }else{
            newRole = true;
            new Handler().postDelayed(() -> addNewRole(), 100);
        }
    }

    private void addNewRole() {
        RoleDataHolder newRoleDraft = new RoleDataHolder();
        newRoleDraft.setDraft(true);
        newRoleDraft.setRoleKindId(matchType(roleKindSpinner.getSelectedItem().toString()));
        roleViewModel.insert(newRoleDraft);
        new Handler().postDelayed(() -> {
            roleViewModel.getAll().observe(this, roles -> {
                ActiveRole = roles.get(roles.size()-1);
                id = ActiveRole.getId();
                roleAbilityTxtSetup();
            });
        }, 100);
    }

    private void editEnviroment(int id) {
        setTitle(getResources().getString(R.string.edit_role_activity_title));

        if(id == 1 || id == 2){

            roleNameTxt.setFocusable(false);
            roleNameTxt.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            roleNameTxt.setBackground(getResources().getDrawable(R.drawable.bg_gray_border));
            roleKindSpinner.setEnabled(false);
            roleKindSpinner.setBackground(getResources().getDrawable(R.drawable.bg_gray_border));
            roleAbilityLayout.setVisibility(View.GONE);
            roleViewModel.getById(id).observe(this, role -> {
                if (role != null){
                    if (id == 1){
                        roleNameTxt.setText(getResources().getString(R.string.mafia_uppercase));
                    } else {
                        roleNameTxt.setText(getResources().getString(R.string.civil_uppercase));
                    }
                    roleKindSpinner.setSelection(getSelectedKind(role.getRoleKindId()));
                }
            });

        } else {

            roleViewModel.getById(id).observe(this, role -> {
                if (role != null){
                    roleNameTxt.setText(role.getRoleName());
                    roleKindSpinner.setSelection(getSelectedKind(role.getRoleKindId()));
                    roleAbilityTxtSetup();
                }
            });

        }
    }

    private void roleAbilityTxtSetup() {
        abilityViewModel.getByRoleId(id).observe(this, abilities -> {
            List<Integer> powerIds = new ArrayList<>();
            for (int i =0; i < abilities.size(); i++){
                powerIds.add(abilities.get(i).getPowerId());
            }
            powerViewModel.getByIDMultiple(powerIds).observe(this,powers -> {
                StringBuilder sb = new StringBuilder();
                AbilityNum = powers.size();
                for (int i =0; i < AbilityNum; i++) {
                    if (i != powers.size() - 1) {
                        sb.append(powers.get(i).getPowerName());
                        sb.append(" - ");
                    } else {
                        sb.append(powers.get(i).getPowerName());
                    }
                }

                if(AbilityNum > 0){
                    roleAbilityTxt.setText(sb.toString());
                } else {
                    roleAbilityTxt.setText(getResources().getString(R.string.add_role_ability_picker));
                }
            });
        });

    }

    private int getSelectedKind(int roleKindId) {

        for (int i = 0 ; i < kinds.size(); i++){
            if (kinds.get(i).getId() == roleKindId){
                return i;
            }
        }

        return 0;
    }

    private void spinnerSetup() {
        kindViewModel.getAll().observe(this, kinds -> {
            if(kinds != null){
                if (kinds.size() > 0){
                    this.kinds = kinds;
                    setSpinnerData(kinds);
                    defineMode();
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
        roleKindSpinner.setAdapter(spinnerArrayAdapter);
    }


    @OnClick(R.id.btn_select_ability) void openAbilityList (){
        MainActivity.progressDialog = new ProgressDialog(this);
        MainActivity.progressDialog.setCancelable(false);
        MainActivity.progressDialog.setMessage(getResources().getString(R.string.please_wait));
        MainActivity.progressDialog.show();
        Intent intent = new Intent(this , AbilityActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    @OnClick(R.id.submit_role_fab) void submitRole(){
        if (AbilityNum > 0){
            RoleDataHolder role = new RoleDataHolder();
            if (roleNameTxt.getText() != null){
                if (roleNameTxt.getText().toString().length() > 0){
                    role.setRoleName(roleNameTxt.getText().toString());
                } else {
                    roleNameTxt.setError(getResources().getString(R.string.name_required));
                    return;
                }
            }else {
                roleNameTxt.setError(getResources().getString(R.string.name_required));
                return;
            }


            role.setRoleKindId(matchType(roleKindSpinner.getSelectedItem().toString()));
            role.setId(id);
            role.setDraft(false);
            roleViewModel.update(role);

        } else {
            abilitySelectError.setVisibility(View.VISIBLE);
            return;
        }
        finish();

    }

    private int matchType(String roleKind) {
        for (int i = 0 ; i < kinds.size(); i++){
            if (kinds.get(i).getKindName().indexOf(roleKind) > -1){
                return kinds.get(i).getId();
            }
        }
        return 0;
    }

    public class KindViewModelFactory extends ViewModelProvider.NewInstanceFactory {
        private Application application;

        public KindViewModelFactory(@NonNull Application application) {
            this.application = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass == KindViewModel.class)
                return (T) new KindViewModel(application);
            return null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
