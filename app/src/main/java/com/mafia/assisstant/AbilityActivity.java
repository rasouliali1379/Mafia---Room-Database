package com.mafia.assisstant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Application;
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
import com.mafia.assisstant.Room.DataHolder.AbilityDataHolder;
import com.mafia.assisstant.Room.DataHolder.PowerDataHolder;
import com.mafia.assisstant.Room.Repository.AbilityRepository;
import com.mafia.assisstant.Room.Repository.PowerRepository;
import com.mafia.assisstant.Room.ViewModel.AbilityViewModel;
import com.mafia.assisstant.Room.ViewModel.PowerViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AbilityActivity extends AppCompatActivity {
    int RoleId;
    AbilityViewModel abilityViewModel;
    PowerViewModel powerViewModel;
    @BindView(R.id.ability_recyclerview) RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ability);
        setTitle(getResources().getString(R.string.ability_activity_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        if (intent.hasExtra("id")){
            RoleId = intent.getIntExtra("id" , -1);
        }
        initViewModels();
        recyclerViewSetup();

        if (MainActivity.progressDialog.isShowing()){
            MainActivity.progressDialog.dismiss();
        }
    }

    private void initViewModels() {
        abilityViewModel = new ViewModelProvider(this).get(AbilityViewModel.class);
        powerViewModel = new ViewModelProvider(this).get(PowerViewModel.class);
    }


    private void recyclerViewSetup() {
        //Todo 16- fix the drafts showing up in list
        CustomAdapter customAdapter = new CustomAdapter(this, ability -> showOptionsDialog(ability), powerViewModel);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(customAdapter);
        abilityViewModel.getByRoleId(RoleId).observe(this, abilities -> customAdapter.setData(getNoDraftAbilities(abilities)));
    }

    private List<AbilityDataHolder> getNoDraftAbilities(List<AbilityDataHolder> abilities) {
        List<AbilityDataHolder> temp_abilities = new ArrayList<>();
        for (int i = 0; i < abilities.size(); i++){
            if (!abilities.get(i).isDraft()){
                temp_abilities.add(abilities.get(i));
            }
        }
        return temp_abilities;
    }

    private void showOptionsDialog(AbilityDataHolder ability) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.option_dialog_title));
        builder.setItems(R.array.options , (DialogInterface dialog, int which) -> {
            switch (which){
                case 0:
                    Intent intent = new Intent(this , AddAbilityActivity.class);
                    intent.putExtra("ability_id" , ability.getId());
                    startActivity(intent);
                    break;

                case 1:
                    new AbilityRepository(getApplication()).delete(ability);
                    break;
            }
        });
        builder.create();
        builder.show();
    }

    @OnClick(R.id.add_power_fab) void addAbility (){
        MainActivity.progressDialog = new ProgressDialog(this);
        MainActivity.progressDialog.setCancelable(false);
        MainActivity.progressDialog.setMessage(getResources().getString(R.string.please_wait));
        MainActivity.progressDialog.show();
        Intent intent = new Intent(this , AddAbilityActivity.class);
        intent.putExtra("role_id" , RoleId);
        startActivity(intent);
    }
    @OnClick(R.id.submit_ability_fab) void submitAbility(){
        abilityViewModel.deleteDrafts();
        finish();
    }

    static class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder>{

        private Context context;
        private List<AbilityDataHolder> abilities;
        private final OnItemClickListener listener;
        private PowerViewModel powerViewModel;

        public CustomAdapter(Context context, OnItemClickListener listener, PowerViewModel powerViewModel) {
            this.context = context;
            this.listener = listener;
            this.powerViewModel = powerViewModel;
        }

        @NonNull
        @Override
        public CustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.ability_item,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CustomAdapter.ViewHolder holder, int position) {
            powerViewModel.getById(abilities.get(position).getPowerId()).observe((LifecycleOwner) context, power ->{
                if (power != null){
                    holder.abilityNameTxt.setText(power.getPowerName());
                }
            });
            if (abilities.get(position).isByDefault()){
                holder.abilityRatioTxt.setText(context.getResources().getString(R.string.default_uppercase));
            } else if (abilities.get(position).isRatio()){
                holder.abilityRatioTxt.setText("1/" + abilities.get(position).getPowerCount());
            } else {
                holder.abilityRatioTxt.setText(String.valueOf(abilities.get(position).getPowerCount()));
            }

            holder.bind(abilities.get(position), listener);
        }

        public void setData(List<AbilityDataHolder> abilities) {
            this.abilities = abilities;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            if (abilities != null){
                return abilities.size();
            }
            return 0;
        }

        public interface OnItemClickListener {
            void onItemClick(AbilityDataHolder abilityDataHolder);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.ability_name_txt)TextView abilityNameTxt;
            @BindView(R.id.ability_ratio_txt)TextView abilityRatioTxt;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this , itemView);
            }

            public void bind(AbilityDataHolder abilityDataHolder , final OnItemClickListener listener) {
                itemView.setOnClickListener(v-> listener.onItemClick(abilityDataHolder));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            abilityViewModel.deleteDrafts();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        abilityViewModel.deleteDrafts();
        finish();
    }
}
