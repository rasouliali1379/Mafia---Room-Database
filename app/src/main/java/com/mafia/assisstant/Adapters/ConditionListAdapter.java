package com.mafia.assisstant.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;

import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mafia.assisstant.Helpers.ItemTouchHelperAdapter;
import com.mafia.assisstant.R;
import com.mafia.assisstant.Room.DataHolder.ConditionDataholder;
import com.mafia.assisstant.Room.DataHolder.KindDataHolder;
import com.mafia.assisstant.Room.DataHolder.RoleDataHolder;
import com.mafia.assisstant.Room.ViewModel.ActionViewModel;
import com.mafia.assisstant.Room.ViewModel.ConditionViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConditionListAdapter extends RecyclerView.Adapter<ConditionListAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    private Context context;
    private int AbilityId;

    private List<RoleDataHolder> roles;
    private List<KindDataHolder> kinds;
    public List<ConditionDataholder> conditions;

    private ActionViewModel actionViewModel;
    private ConditionViewModel conditionViewModel;
    private final OnItemClickListener listener;
    View view;

    RolesCheckboxListAdapter rolesCheckboxListAdapter;

    public ConditionListAdapter(Context context, int abilityId,
                                List<RoleDataHolder> roles,
                                List<KindDataHolder> kinds,
                                ActionViewModel actionViewModel,
                                ConditionViewModel conditionViewModel,
                                OnItemClickListener listener) {
        this.context = context;
        AbilityId = abilityId;
        this.roles = roles;
        this.kinds = kinds;
        this.actionViewModel = actionViewModel;
        this.conditionViewModel = conditionViewModel;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.condition_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ConditionDataholder condition = conditions.get(position);
        if (condition.isKind()){
            holder.conditionItemTitle.setText(context.getResources().getString(R.string.kind_condition));
            holder.conditionTxt.setOnClickListener(v-> showPopUpMenu(v, position));

        } else {
            holder.conditionItemTitle.setText(context.getResources().getString(R.string.role_condition));
            holder.conditionTxt.setOnClickListener(v-> showCheckBoxDialog(position));
        }

        holder.priority.setText(String.valueOf(condition.getPriority()));
        if(condition.isSelected()){
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorGreen));
            holder.conditionTxt.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            holder.conditionItemTitle.setTextColor(context.getResources().getColor(R.color.colorPrimary));

        } else {
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            holder.conditionTxt.setTextColor(context.getResources().getColor(R.color.colorAccent));
            holder.conditionItemTitle.setTextColor(context.getResources().getColor(R.color.colorAccent));
        }

        if(condition.getIncludeRoles() != null){
            if (condition.isKind()){
                if (condition.getIncludeRoles().length() > 0){
                    holder.conditionTxt.setText(getChoosenKinds(conditions.get(position).getIncludeRoles()));
                } else {
                    holder.conditionTxt.setText(context.getResources().getString(R.string.none));
                }
            } else {
                if (condition.getIncludeRoles().length() > 0){
                    holder.conditionTxt.setText(getIncludedRoles(conditions.get(position).getIncludeRoles()));
                } else {
                    holder.conditionTxt.setText(context.getResources().getString(R.string.none));
                }
            }
        } else {
            holder.conditionTxt.setText(context.getResources().getString(R.string.none));
        }


        ActionListAdapter actionListAdapter = new ActionListAdapter(context, actionViewModel);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerView.setAdapter(actionListAdapter);
        actionViewModel.getByConditionId(condition.getId()).observe((LifecycleOwner) context, actions -> {
            if (actions != null){
                actionListAdapter.setData(actions);
            }
        });

        holder.bind(position, listener);

    }

    private void deleteRelatedActions(ConditionDataholder condition) {
        actionViewModel.getByConditionId(condition.getId()).observe((LifecycleOwner) context, actions ->{
            if (actions != null){
                if (actions.size() > 0){
                    actionViewModel.deleteMultiple(actions);
                }
                conditionViewModel.delete(condition);
            }
        });
    }

    private void showPopUpMenu(View v , int position) {
        PopupMenu popup = new PopupMenu(context, v);
        for (int i = 0; i < kinds.size(); i++){
            popup.getMenu().add(Menu.NONE, i, Menu.NONE, kinds.get(i).getKindName());
        }

        popup.setOnMenuItemClickListener(item -> {
            ConditionDataholder condition = new ConditionDataholder();
            condition.setIncludeRoles(String.valueOf(item.getItemId() + 1));
            condition.setAbilityId(AbilityId);
            condition.setId(conditions.get(position).getId());
            condition.setKind(true);
            conditionViewModel.update(condition);
            return true;
        });

        popup.show();
    }

    private String getChoosenKinds(String kind) {
        StringBuilder sb = new StringBuilder();
        sb.append(context.getResources().getString(R.string.role_type_must_be));
        switch (Integer.parseInt(kind)){
            case 1:
                sb.append(context.getResources().getString(R.string.mafia_lowercase));
                break;
            case 2:
                sb.append(context.getResources().getString(R.string.civil_lowercase));
                break;
            case 3:
                sb.append(context.getResources().getString(R.string.independent_lowercase));
                break;
        }
        if (context.getSharedPreferences("Mafia", Context.MODE_PRIVATE).getString("language", "fa").equals("fa")){
            sb.append(context.getResources().getString(R.string.bashad));
        }
        return sb.toString();
    }

    private void showCheckBoxDialog(int conditionPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        ViewGroup viewGroup = view.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.checkbox_recyclerview_dialog, viewGroup, false);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnDismissListener(dialog -> submitIncludedRoles(alertDialog, conditionPosition));
        DialogVH holder = new DialogVH(dialogView);

        rolesCheckboxListAdapter = new RolesCheckboxListAdapter(context , getNotDraftedRoles(roles) , position -> {
            roles.get(position).setChecked(!roles.get(position).isChecked());
            rolesCheckboxListAdapter.notifyItemChanged(position);
        });

        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerView.setAdapter(rolesCheckboxListAdapter);
        holder.dialogTitle.setText(context.getResources().getString(R.string.roles));
        holder.closeBtn.setOnClickListener(v-> alertDialog.dismiss());
        alertDialog.show();
    }

    private List<RoleDataHolder> getNotDraftedRoles(List<RoleDataHolder> roles) {
        List<RoleDataHolder> temp_roles = new ArrayList<>();
        for (int i = 0; i < roles.size(); i++){
            if (!roles.get(i).isDraft())
                temp_roles.add(roles.get(i));
        }
        return temp_roles;
    }

    private void submitIncludedRoles(AlertDialog alertDialog, int position) {

        List<Integer> ids = new ArrayList<>();

        for (int i = 0; i < roles.size(); i++){
            if (roles.get(i).isChecked()){
                ids.add(roles.get(i).getId());
            }
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ids.size(); i++){
            if (ids.size() - 1 != i ){
                sb.append(ids.get(i));
                sb.append("-");
            } else {
                sb.append(ids.get(i));
            }
        }

        ConditionDataholder condition = new ConditionDataholder();
        condition.setIncludeRoles(sb.toString());
        condition.setAbilityId(AbilityId);
        condition.setId(conditions.get(position).getId());
        conditionViewModel.update(condition);

        alertDialog.dismiss();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(conditions, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        deleteRelatedActions(conditions.get(position));
        changePriority();
    }

    @Override
    public void onItemMoveFinished() {
        changePriority();
    }

    public void changePriority() {
        for(int i = 0; i < conditions.size(); i++){
            conditions.get(i).setPriority(i + 1);
            notifyItemChanged(i);
        }
    }

    public void savePriorities(){
        conditionViewModel.updateMultiple(conditions);
    }

    static class DialogVH {

        @BindView(R.id.checkbox_recyclerview) RecyclerView recyclerView;
        @BindView(R.id.checkbox_dialog_close_btn) ImageView closeBtn;
        @BindView(R.id.checkbox_dialog_title)TextView dialogTitle;

        DialogVH(View rootView) {
            ButterKnife.bind(this , rootView);
        }
    }

    private String getIncludedRoles(String includeRoles) {

        String [] ids = includeRoles.split("-");
        StringBuilder sb = new StringBuilder();
        sb.append(context.getResources().getString(R.string.must_include));
        List<RoleDataHolder> temp_roles = new ArrayList<>();
        for (int i = 0; i < roles.size(); i++){
            if (roleIdEqual(ids, roles.get(i).getId())){
                temp_roles.add(roles.get(i));
            }
        }

        for (int i = 0; i < temp_roles.size(); i++){
            sb.append(temp_roles.get(i).getRoleName());
            if (i + 1 == temp_roles.size() - 1){
                sb.append(context.getResources().getString(R.string.and));
            } else if (i != temp_roles.size() - 1){
                sb.append(context.getResources().getString(R.string.comma));
            }
        }

        return sb.toString();
    }

    private boolean roleIdEqual(String[] ids, int id) {
        for (int i = 0; i < ids.length;  i++){
            if (Integer.parseInt(ids[i]) == id){
                return true;
            }
        }
        return false;
    }

    public void setData(List<ConditionDataholder> conditions) {
        this.conditions = conditions;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    @Override
    public int getItemCount() {
        if (conditions != null){
            return conditions.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.condition_item_cv) CardView cardView;
        @BindView(R.id.actions_recyclerview)RecyclerView recyclerView;
        @BindView(R.id.action_condition_txt)TextView conditionTxt;
        @BindView(R.id.condition_item_title)TextView conditionItemTitle;
        @BindView(R.id.condition_item_priority_txt)TextView priority;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this , itemView);
        }

        public void bind(final int position, final OnItemClickListener listener) {
            itemView.setOnClickListener((View view)-> listener.onItemClick(position));
        }
    }


}