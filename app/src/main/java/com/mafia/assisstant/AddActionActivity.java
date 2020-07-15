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
            actionViewModel.getById(intent.getIntExtra("action_id", 0)).observe(this, this::editMode);
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

    private void defineType(int mode) {
        switch (mode){
            case 0:

                break;
            case 1:

                break;
            case 2:

                break;
            case 3:

                break;
            case 4:

                break;
            case 5:

                break;
            case 6:

                break;
            case 7:

                break;
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
