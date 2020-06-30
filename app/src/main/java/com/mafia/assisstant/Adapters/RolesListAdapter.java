package com.mafia.assisstant.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.mafia.assisstant.AddRoleActivity;
import com.mafia.assisstant.R;
import com.mafia.assisstant.Room.DataHolder.AbilityDataHolder;
import com.mafia.assisstant.Room.DataHolder.KindDataHolder;
import com.mafia.assisstant.Room.DataHolder.RoleDataHolder;
import com.mafia.assisstant.Room.ViewModel.AbilityViewModel;
import com.mafia.assisstant.Room.ViewModel.PowerViewModel;
import com.mafia.assisstant.Room.ViewModel.RoleViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RolesListAdapter extends RecyclerView.Adapter<RolesListAdapter.ViewHolder>{

    private Context context;
    private List<RoleDataHolder> roles;
    private List<KindDataHolder> kinds;
    private AbilityViewModel abilityViewModel;
    private PowerViewModel powerViewModel;
    private RoleViewModel roleViewModel;

    public RolesListAdapter(Context context,
                           List<KindDataHolder> kinds,
                            AbilityViewModel abilityViewModel,
                            PowerViewModel powerViewModel,
                            RoleViewModel roleViewModel) {
        this.context = context;
        this.kinds = kinds;
        this.abilityViewModel = abilityViewModel;
        this.powerViewModel = powerViewModel;
        this.roleViewModel = roleViewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.role_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            RoleDataHolder role = roles.get(position);
            if(role.getId() == 1){
                holder.roleNameTxt.setText(context.getResources().getString(R.string.mafia_uppercase));
            } else if (role.getId() == 2){
                holder.roleNameTxt.setText(context.getResources().getString(R.string.civil_uppercase));
            } else {
                holder.roleNameTxt.setText(role.getRoleName());
            }
            holder.roleTypeTxt.setText(getKind(role.getRoleKindId()));
            abilityViewModel.getByRoleId(role.getId()).observe((LifecycleOwner) context, abilities -> {
                if (abilities.size() > 0){
                    getWorkTimeAndPowers(holder, abilities);
                }
            });
            holder.editBtn.setOnClickListener(v-> editRole(role));
            holder.deleteBtn.setOnClickListener(v-> deleteRole(role));

    }

    private void editRole(RoleDataHolder role) {
        Intent intent = new Intent(context , AddRoleActivity.class);
        intent.putExtra("id" , role.getId());
        context.startActivity(intent);
    }


    private void deleteRole(RoleDataHolder role) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.attention);
        builder.setMessage(R.string.delete_role_warning);
        builder.setNegativeButton(R.string.no_btn_label, (dialog, id) -> dialog.dismiss());
        builder.setPositiveButton(R.string.yes, (dialog, id) -> {
            abilityViewModel.getByRoleId(role.getId()).observe((LifecycleOwner) context, abilities-> {
                if (abilities != null){
                    abilityViewModel.deleteMultiple(abilities);
                    roleViewModel.delete(role);
                    abilityViewModel.getByRoleId(role.getId()).removeObservers((LifecycleOwner) context);
                }
            });
        });

        AlertDialog alertDialog = builder.create();
        builder.setMessage(context.getResources().getString(R.string.game_in_progress_roles));
        alertDialog.show();
    }

    private void getWorkTimeAndPowers(ViewHolder holder, List<AbilityDataHolder> abilities) {
        if (abilities.size() == 0){
            holder.powersTxt.setVisibility(View.GONE);
            holder.workTimeTxt.setVisibility(View.GONE);
            holder.abilityLayout.setVisibility(View.GONE);
            holder.workTimeLayout.setVisibility(View.GONE);
        } else {
            holder.powersTxt.setVisibility(View.VISIBLE);
            holder.workTimeTxt.setVisibility(View.VISIBLE);
            holder.abilityLayout.setVisibility(View.VISIBLE);
            holder.workTimeLayout.setVisibility(View.VISIBLE);
            List<Integer> powerIds = new ArrayList<>();
            for (int i =0; i < abilities.size(); i++){
                powerIds.add(abilities.get(i).getPowerId());
            }

            powerViewModel.getByIDMultiple(powerIds).observe((LifecycleOwner) context, powers -> {
                StringBuilder sb = new StringBuilder();
                for (int i =0 ; i < powers.size() ; i++ ){
                    if (i == powers.size() - 1){
                        sb.append(powers.get(i).getPowerName());

                    } else {
                        sb.append(powers.get(i).getPowerName());
                        sb.append(context.getResources().getString(R.string.comma));
                    }
                }
                holder.powersTxt.setText(sb.toString());
            });

            boolean [] workTimes = checkWorkTimes(abilities);

            if (workTimes[0] && workTimes [1]){
                holder.workTimeTxt.setText(context.getResources().getString(R.string.power_detail_work_time_24));
            } else if (workTimes[0] && !workTimes[1]){
                holder.workTimeTxt.setText(context.getResources().getString(R.string.power_detail_work_time_days));
            } else {
                holder.workTimeTxt.setText(context.getResources().getString(R.string.power_detail_work_time_nights));
            }
        }
    }

    private boolean[] checkWorkTimes(List<AbilityDataHolder> abilities) {
        boolean [] checkList = new boolean[3];

        for (int i = 0; i < abilities.size() ; i++){

            if (!checkList[2]){
              if (abilities.get(i).isByDefault()){
                  checkList[2] = true;
              }
            }

            if (abilities.get(i).isDay()){
                checkList[0] = true;
            } else{
                checkList[1] = true;
            }

            if (checkList[0] && checkList [1]){
                return checkList;
            }
        }
        return checkList;
    }

    private String getKind(int roleKindId) {

        for (int i = 0 ; i < kinds.size(); i++){
            if (kinds.get(i).getId() == roleKindId){
                return kinds.get(i).getKindName();
            }
        }
        return "";
    }

    @Override
    public int getItemCount() {
        if (roles != null)
            return roles.size();
        return 0;
    }

    public void setData(List<RoleDataHolder> roles) {
        List<RoleDataHolder> mainRoles = new ArrayList<>();
        for(int i =0; i < roles.size(); i++){
            if (!roles.get(i).isDraft()){
                mainRoles.add(roles.get(i));
            }
        }
        this.roles = mainRoles;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.role_item_role_name)
        TextView roleNameTxt;
        @BindView(R.id.role_item_role_type) TextView roleTypeTxt;
        @BindView(R.id.role_item_work_time) TextView workTimeTxt;
        @BindView(R.id.role_item_powers) TextView powersTxt;
        @BindView(R.id.role_item_work_time_layout)LinearLayout workTimeLayout;
        @BindView(R.id.role_item_ability_layout)LinearLayout abilityLayout;
        @BindView(R.id.role_item_delete)ImageView deleteBtn;
        @BindView(R.id.role_item_edit)ImageView editBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this , itemView);
        }
    }
}