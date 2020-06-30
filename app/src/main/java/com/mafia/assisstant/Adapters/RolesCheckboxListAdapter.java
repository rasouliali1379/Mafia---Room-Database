package com.mafia.assisstant.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mafia.assisstant.R;
import com.mafia.assisstant.Room.DataHolder.RoleDataHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RolesCheckboxListAdapter extends RecyclerView.Adapter<RolesCheckboxListAdapter.ViewHolder> {

    Context context;
    public List<RoleDataHolder> roles;
    private final OnItemClickListener listener;

    public RolesCheckboxListAdapter(Context context, List<RoleDataHolder> roles, OnItemClickListener listener) {
        this.context = context;
        this.roles = roles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.checkbox_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.checkBox.setText(roles.get(position).getRoleName());

        if (roles.get(position).isChecked())
            holder.checkBox.setChecked(true);
        else
            holder.checkBox.setChecked(false);

        holder.bind(position, listener);
    }

    @Override
    public int getItemCount() {
        return roles.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.checkbox_item_cb) CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this , itemView);
        }

        public void bind(int position, final OnItemClickListener listener) {
            itemView.setOnClickListener((View view)-> listener.onItemClick(position));
        }
    }
}
