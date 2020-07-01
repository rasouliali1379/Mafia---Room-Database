package com.mafia.assisstant.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mafia.assisstant.Helpers.DayRolesTouchHelperCallback;
import com.mafia.assisstant.Helpers.ItemTouchHelperAdapter;
import com.mafia.assisstant.Helpers.NightRolesTouchHelperCallback;
import com.mafia.assisstant.R;
import com.mafia.assisstant.RoleAssignmentActivity;
import com.mafia.assisstant.Room.DataHolder.RoleDataHolder;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DayRolesFragment extends Fragment {
    @BindView(R.id.day_roles_recyclerview)RecyclerView recyclerView;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day_roles, container, false);
        ButterKnife.bind(this, view);
        DayListAdapter dayListAdapter = new DayListAdapter(context, RoleAssignmentActivity.dayRoles);
        ItemTouchHelper.Callback dayCallback = new DayRolesTouchHelperCallback(dayListAdapter);
        ItemTouchHelper dayTouchHelper = new ItemTouchHelper(dayCallback);
        dayTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(dayListAdapter);
        dayListAdapter.changePriority();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    static class DayListAdapter extends RecyclerView.Adapter<DayListAdapter.ViewHolder> implements ItemTouchHelperAdapter {

        private Context context;
        List<RoleDataHolder> roles;

        public DayListAdapter(Context context, List<RoleDataHolder> roles) {
            this.context = context;
            this.roles = roles;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.role_assignment_item,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.roleNameTxt.setText(roles.get(position).getRoleName());
            holder.priorityTxt.setText(String.valueOf(roles.get(position).getPriority()));
        }

        @Override
        public int getItemCount() {
            if (roles != null){
                return roles.size();
            }
            return 0;
        }


        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            Collections.swap(roles, fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onItemDismiss(int position) {
            roles.remove(position);
            notifyItemRemoved(position);
            changePriority();
        }

        @Override
        public void onItemMoveFinished() {
            changePriority();
        }

        public void changePriority() {
            for(int i = 0; i < roles.size(); i++){
                roles.get(i).setPriority(i + 1);
                notifyItemChanged(i);
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.role_assignment_role_name) TextView roleNameTxt;
            @BindView(R.id.role_assignment_priority) TextView priorityTxt;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this , itemView);
            }

        }
    }
}
