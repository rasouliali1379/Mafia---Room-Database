package com.mafia.assisstant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.mafia.assisstant.Adapters.RolesListAdapter;
import com.mafia.assisstant.Room.DataHolder.AbilityDataHolder;
import com.mafia.assisstant.Room.DataHolder.KindDataHolder;
import com.mafia.assisstant.Room.DataHolder.PowerDataHolder;
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

public class RolesActivity extends AppCompatActivity {

    List<KindDataHolder> kinds;
    KindViewModel kindViewModel;
    AbilityViewModel abilityViewModel;
    PowerViewModel powerViewModel;
    RoleViewModel roleViewModel;
    @BindView(R.id.roles_recyclerview) RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roles);
        setTitle(getResources().getString(R.string.role_activity_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        initViewModels();
        if (MainActivity.progressDialog.isShowing()){
            MainActivity.progressDialog.dismiss();
        }
    }

    private void initViewModels() {
        kindViewModel = new ViewModelProvider(this).get(KindViewModel.class);
        kindViewModel.getAll().observe(this, kinds -> {
            this.kinds = kinds;
            recyclerViewSetup();
        });
        abilityViewModel = new ViewModelProvider(this).get(AbilityViewModel.class);
        powerViewModel = new ViewModelProvider(this).get(PowerViewModel.class);
        roleViewModel = new ViewModelProvider(this).get(RoleViewModel.class);
    }

    private void recyclerViewSetup() {
        RolesListAdapter rolesListAdapter = new RolesListAdapter(this, kinds, abilityViewModel, powerViewModel, roleViewModel);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(rolesListAdapter);
        roleViewModel.getAll().observe(this, roles -> rolesListAdapter.setData(excludeDefaultRoles(getNotDraftedRoles(roles))));
    }

    private List<RoleDataHolder> excludeDefaultRoles(List<RoleDataHolder> notDraftedRoles) {
        notDraftedRoles.remove(0);
        notDraftedRoles.remove(0);
        return notDraftedRoles;
    }

    private List<RoleDataHolder> getNotDraftedRoles(List<RoleDataHolder> roles) {
        List<RoleDataHolder> temp_roles = new ArrayList<>();
        for (int i = 0; i < roles.size(); i++){
            if (!roles.get(i).isDraft())
                temp_roles.add(roles.get(i));
        }
        return temp_roles;
    }



    @OnClick(R.id.add_role_fab) void addRole (){
        MainActivity.progressDialog = new ProgressDialog(this);
        MainActivity.progressDialog.setCancelable(false);
        MainActivity.progressDialog.setMessage(getResources().getString(R.string.please_wait));
        MainActivity.progressDialog.show();
        startActivity(new Intent(this , AddRoleActivity.class));
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
