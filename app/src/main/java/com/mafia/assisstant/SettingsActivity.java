package com.mafia.assisstant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.mafia.assisstant.Room.ViewModel.AbilityViewModel;
import com.mafia.assisstant.Room.ViewModel.PowerViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends AppCompatActivity {
    @BindView(R.id.starter_player_manually_layout) RelativeLayout relativeLayout;
    @BindView(R.id.starter_player_et) EditText manualStarterEt;
    @BindView(R.id.starter_player_random_switch) Switch randomStarter;
    @BindView(R.id.define_round_day_et)EditText dayEt;
    @BindView(R.id.define_round_night_et)EditText nightEt;
    @BindView(R.id.settings_language_spinner)Spinner languagesSpinner;
    @BindView(R.id.setting_round_start_spinner) Spinner roundStartSpinner;
    @BindView(R.id.settings_god_ability_btn)FrameLayout godAbilityBtn;
    @BindView(R.id.settings_god_ability_txt)TextView godAbilityTxt;
    @BindView(R.id.settings_common_ability_btn)FrameLayout commonAbilityBtn;
    @BindView(R.id.settings_common_ability_txt)TextView commonAbilityTxt;
    SharedPreferences.Editor editor;
    SharedPreferences prefs;

    Locale myLocale;
    String currentLanguage , currentLang;
    int AbilityNum;

    AbilityViewModel abilityViewModel;
    PowerViewModel powerViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(getResources().getString(R.string.settings_activity_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        prefs = getSharedPreferences("Mafia" , MODE_PRIVATE);
        editor = prefs.edit();
        currentLanguage = prefs.getString("language", "fa");
        fillSettings();
        clickListeners();
        initViewModels();
        godAbilitySetup();
    }

    private void initViewModels() {
        abilityViewModel = new ViewModelProvider(this).get(AbilityViewModel.class);
        powerViewModel = new ViewModelProvider(this).get(PowerViewModel.class);
    }

    private void fillSettings() {
        nightEt.setText(String.valueOf(prefs.getInt("round_night_num", 1)));
        dayEt.setText(String.valueOf(prefs.getInt("round_day_num", 1)));
        switch (currentLanguage){
            case "en":
                languagesSpinner.setSelection(0);
                break;
            case "fa":
                languagesSpinner.setSelection(1);
                break;
        }

        if (prefs.getBoolean("randomStarter", true)){
            randomStarter.setChecked(true);
            relativeLayout.setVisibility(View.GONE);
        } else {
            if (prefs.getInt("manualStarter", 0) > 0){
                manualStarterEt.setText(String.valueOf(prefs.getInt("manualStarter", 0)));
            }
        }
        if (prefs.getString("timeset", getResources().getString(R.string.day)).equals(getResources().getString(R.string.day))){
            roundStartSpinner.setSelection(0);
        } else {
            roundStartSpinner.setSelection(1);
        }


    }

    private void clickListeners (){
        randomStarter.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                relativeLayout.setVisibility(View.GONE);
            } else {
                relativeLayout.setVisibility(View.VISIBLE);
            }
        });

        languagesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 0:
                        setLocale("en");
                        break;
                    case 1:
                        setLocale("fa");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        godAbilityBtn.setOnClickListener(v->{
            MainActivity.progressDialog = new ProgressDialog(this);
            MainActivity.progressDialog.setCancelable(false);
            MainActivity.progressDialog.setMessage(getResources().getString(R.string.please_wait));
            MainActivity.progressDialog.show();
            Intent intent = new Intent(this, AbilityActivity.class);
            intent.putExtra("id", -1);
            startActivity(intent);
        });

        commonAbilityBtn.setOnClickListener(v->{
            MainActivity.progressDialog = new ProgressDialog(this);
            MainActivity.progressDialog.setCancelable(false);
            MainActivity.progressDialog.setMessage(getResources().getString(R.string.please_wait));
            MainActivity.progressDialog.show();
            Intent intent = new Intent(this, AbilityActivity.class);
            intent.putExtra("id", 0);
            startActivity(intent);
        });

    }

    private void godAbilitySetup() {
        abilityViewModel.getByRoleId(-1).observe(this, abilities -> {
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
                    godAbilityTxt.setText(sb.toString());
                } else {
                    godAbilityTxt.setText(getResources().getString(R.string.add_role_ability_picker));
                }
            });
        });

    }

    private void commonAbilitySetup() {
        abilityViewModel.getByRoleId(-1).observe(this, abilities -> {
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
                    commonAbilityTxt.setText(sb.toString());
                } else {
                    commonAbilityTxt.setText(getResources().getString(R.string.add_role_ability_picker));
                }
            });
        });

    }

    public void setLocale(String localeName) {
        if (!localeName.equals(currentLanguage)) {
            editor.putString("language", localeName).commit();
            myLocale = new Locale(localeName);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            conf.setLayoutDirection(myLocale);
            res.updateConfiguration(conf, dm);
            Intent refresh = new Intent(this, SettingsActivity.class);
            refresh.putExtra(currentLang, localeName);
            startActivity(refresh);
            finish();
        }
    }

    @OnClick(R.id.submit_settings) void submitSettings(){
        if (randomStarter.isChecked()){
            editor.putBoolean("randomStarter", true);
        } else {
            editor.putBoolean("randomStarter", false);
            try {
                if (manualStarterEt.getText() != null){
                    if (Integer.parseInt(manualStarterEt.getText().toString()) > 0 ){
                        editor.putInt("manualStarter", Integer.parseInt(manualStarterEt.getText().toString()));
                    } else {
                        editor.putInt("manualStarter", 0);
                    }
                } else {
                    manualStarterEt.setError(getResources().getString(R.string.fill_manual_et_error));
                    return;
                }
            }catch (NumberFormatException e){
                manualStarterEt.setError(getResources().getString(R.string.fill_manual_et_error));
                return;
            }
        }



        try{
            if (dayEt.getText().toString() != null){
                if (Integer.parseInt(dayEt.getText().toString()) > 0 ){
                    editor.putInt("round_day_num", Integer.parseInt(dayEt.getText().toString()));
                } else {
                    dayEt.setError(getResources().getString(R.string.define_round_day_error));
                    return;
                }
            }
        } catch(NumberFormatException ex){
            dayEt.setError(getResources().getString(R.string.define_round_day_error));
            return;
        }

        try {
            if (nightEt.getText() != null){
                if (Integer.parseInt(nightEt.getText().toString()) > 0 ){
                    editor.putInt("round_night_num", Integer.parseInt(nightEt.getText().toString()));
                } else {
                    nightEt.setError(getResources().getString(R.string.define_round_day_error));
                    return;
                }
            }
        } catch (NumberFormatException ex){
            nightEt.setError(getResources().getString(R.string.define_round_day_error));
            return;
        }

        editor.putString("timeset", roundStartSpinner.getSelectedItem().toString());

        editor.commit();
        startActivity(new Intent(SettingsActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(SettingsActivity.this, MainActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SettingsActivity.this, MainActivity.class));
        finish();
    }
}
