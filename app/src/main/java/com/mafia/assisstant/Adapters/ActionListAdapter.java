package com.mafia.assisstant.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mafia.assisstant.AddActionActivity;
import com.mafia.assisstant.DataHolders.ActionTypesDataHolder;
import com.mafia.assisstant.R;
import com.mafia.assisstant.Room.DataHolder.ActionDataHolder;
import com.mafia.assisstant.Room.DataHolder.ConditionDataholder;
import com.mafia.assisstant.Room.DataHolder.RoleDataHolder;
import com.mafia.assisstant.Room.ViewModel.ActionViewModel;
import com.mafia.assisstant.Utils.ActionTypes;

import java.util.List;

import butterknife.Action;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ActionListAdapter extends RecyclerView.Adapter<ActionListAdapter.ViewHolder>{

    private Context context;
    private List<ActionDataHolder> actions;
    private ActionViewModel actionViewModel;

    public ActionListAdapter(Context context, ActionViewModel actionViewModel) {
        this.context = context;
        this.actionViewModel = actionViewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dropdown_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.label.setText(getRelatedAction(actions.get(position).getActionTypeId()));
        if (actions.get(position).getActionTypeId() == 1 || actions.get(position).getActionTypeId() == 2 || actions.get(position).getActionTypeId() == 7 || actions.get(position).getActionTypeId() == 8 || actions.get(position).getActionTypeId() == 9 || actions.get(position).getActionTypeId() == 13){
            holder.editBtn.setVisibility(View.INVISIBLE);
        } else {
            holder.editBtn.setOnClickListener(v -> editAction(position));
            holder.editBtn.setVisibility(View.VISIBLE);
        }
        holder.earaseBtn.setOnClickListener(v -> earaseAction(position));
    }

    private String getRelatedAction(int actionTypeId) {
        List<ActionTypesDataHolder> actionTypes= new ActionTypes(context).getActionTypes();

        for (int i = 0; i < actionTypes.size(); i++){
            if (actionTypes.get(i).getId() == actionTypeId){
                return actionTypes.get(i).getName();
            }
        }
        return "";
    }

    private void earaseAction(int position) {
        actionViewModel.delete(actions.get(position));
    }

    private void editAction(int position) {
        Intent intent = new Intent(context, AddActionActivity.class);
        intent.putExtra("action_id", actions.get(position).getId());
        context.startActivity(intent);
    }

    public void setData(List<ActionDataHolder> actions) {
        this.actions = actions;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (actions != null){
            return actions.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.dropdown_item_edit)ImageView editBtn;
        @BindView(R.id.dropdown_item_earase)ImageView earaseBtn;
        @BindView(R.id.dropdown_item_label)TextView label;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this , itemView);
        }
    }
}