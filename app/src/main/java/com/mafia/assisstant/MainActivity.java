package com.mafia.assisstant;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ProgressBar;


import com.mafia.assisstant.Room.DataHolder.KindDataHolder;
import com.mafia.assisstant.Room.DataHolder.RoleDataHolder;
import com.mafia.assisstant.Room.ViewModel.EventViewModel;
import com.mafia.assisstant.Room.ViewModel.KindViewModel;
import com.mafia.assisstant.Room.ViewModel.PlayerViewModel;
import com.mafia.assisstant.Room.ViewModel.RoleViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    public static ProgressDialog progressDialog;
    SharedPreferences.Editor editor;
    SharedPreferences prefs;
    PlayerViewModel playerViewModel;
    EventViewModel eventViewModel;
    RoleViewModel roleViewModel;

    Locale myLocale;
    String appLanguage;

    private static final int OPEN_ROLES_LIST = 1, START_GAME = 2;
    int rolesCount = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getResources().getString(R.string.app_name));
        ButterKnife.bind(this);
        prefs = getSharedPreferences("Mafia", MODE_PRIVATE);
        editor = prefs.edit();
        MainActivity.progressDialog = new ProgressDialog(this);
        MainActivity.progressDialog.setCancelable(false);
        MainActivity.progressDialog.setMessage(getResources().getString(R.string.please_wait));
        if (!getIntent().hasExtra("current_lang")){
            appLanguage = prefs.getString("language", "fa");
            setLocale(appLanguage);
        }


        initViewModels();
        if (prefs.getBoolean("first_time", true)){
            addKinds();
            addDeafaultRoles();
            editor.putBoolean("first_time", false);
            editor.commit();
        }
        initObserver();
    }

    private void initObserver() {
        roleViewModel.getAll().observe(this, roles -> {
            rolesCount = getNotDraftedRoles(roles).size();
            Log.e("role count", String.valueOf(rolesCount));
        });
    }

    private void initViewModels() {
        playerViewModel = new ViewModelProvider(this).get(PlayerViewModel.class);
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);
        roleViewModel = new ViewModelProvider(this).get(RoleViewModel.class);
    }

    private void addDeafaultRoles() {
        List<RoleDataHolder> roles = new ArrayList<>();
        roles.add(new RoleDataHolder(1, 1,"Mafia", false));
        roles.add(new RoleDataHolder(2, 2,"Civil", false));

        RoleViewModel roleViewModel = new ViewModelProvider(this).get(RoleViewModel.class);
        roleViewModel.insertMultiple(roles);
    }

    private void addKinds() {
        KindViewModel kindViewModel = new ViewModelProvider(this).get(KindViewModel.class);
        List<KindDataHolder> kinds = new ArrayList<>();
        kinds.add(new KindDataHolder("مافیا", "Role"));
        kinds.add(new KindDataHolder("شهروند", "Role"));
        kinds.add(new KindDataHolder("مستقل", "Role"));
        kindViewModel.insertMultiple(kinds);

        kindViewModel.getAll().observe(this, kindDataHolders -> {
            for (int i =0; i < kinds.size(); i++){
                Log.e(String.valueOf(kinds.get(i).getId()), kinds.get(i).getKindName());
            }
        });
    }

    @OnClick(R.id.roles_btn_framelayout) void openRoles(){
        if (prefs.getBoolean("game_in_progress", false)){
            showOptionsDialog(OPEN_ROLES_LIST);
        } else {
            MainActivity.progressDialog = new ProgressDialog(this);
            MainActivity.progressDialog.setCancelable(false);
            MainActivity.progressDialog.setMessage(getResources().getString(R.string.please_wait));
            MainActivity.progressDialog.show();
            startActivity(new Intent(this, RolesActivity.class));
        }
    }



    @OnClick(R.id.start_game_btn_framelayout) void startGame(){
        if (prefs.getBoolean("game_in_progress", false)){
            showOptionsDialog(START_GAME);
        } else {
            if (rolesCount > 2){
                MainActivity.progressDialog = new ProgressDialog(this);
                MainActivity.progressDialog.setCancelable(false);
                MainActivity.progressDialog.setMessage(getResources().getString(R.string.please_wait));
                MainActivity.progressDialog.show();
                playerViewModel.deleteAll();
                startActivity(new Intent(this, AddPlayerActivity.class));
            } else {
                showErrorDialog();
            }
        }
    }

    private List<RoleDataHolder> getNotDraftedRoles(List<RoleDataHolder> roles) {
        List<RoleDataHolder> temp_roles = new ArrayList<>();
        for (int i = 0; i < roles.size(); i++){
            if (!roles.get(i).isDraft())
                temp_roles.add(roles.get(i));
        }
        return temp_roles;
    }

    private void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton(R.string.ok, (dialog, id) -> dialog.dismiss());
        builder.setMessage(getResources().getString(R.string.role_deficiency_error));
        builder.setTitle(R.string.attention);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @OnClick(R.id.settings_fab) void openSettings(){
        MainActivity.progressDialog = new ProgressDialog(this);
        MainActivity.progressDialog.setCancelable(false);
        MainActivity.progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.show();
        startActivity(new Intent(MainActivity.this , SettingsActivity.class));
        finish();
    }

    public void setLocale(String localeName) {
        myLocale = new Locale(localeName);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        conf.setLayoutDirection(myLocale);
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, MainActivity.class);
        refresh.putExtra("current_lang", localeName);
        startActivity(refresh);
        finish();
    }
    private void showOptionsDialog(int which) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (which){
            case 1 :
                builder.setPositiveButton(R.string.yes, (dialog, id) -> {
                    stopGame();
                    openRoles();
                });
                builder.setNegativeButton(R.string.no_btn_label, (dialog, id) -> dialog.dismiss());
                builder.setMessage(getResources().getString(R.string.game_in_progress_roles));
                break;
            case 2 :
                builder.setPositiveButton(R.string.yes, (dialog, id) -> continueLastGame());
                builder.setNegativeButton(R.string.no_btn_label, (dialog, id) -> {
                    stopGame();
                    startGame();
                });
                builder.setMessage(getResources().getString(R.string.game_in_progress));
                break;
        }

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void continueLastGame() {
        startActivity(new Intent(this, MainBoardActivity.class));
    }

    private void stopGame(){
        editor.putBoolean("game_in_progress", false);
        editor.commit();
        playerViewModel.deleteAll();
        eventViewModel.deleteAll();
    }

}
