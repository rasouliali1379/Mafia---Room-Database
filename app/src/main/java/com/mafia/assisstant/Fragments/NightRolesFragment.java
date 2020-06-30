package com.mafia.assisstant.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mafia.assisstant.Helpers.ItemTouchHelperAdapter;
import com.mafia.assisstant.Helpers.NightRolesTouchHelperCallback;
import com.mafia.assisstant.R;
import com.mafia.assisstant.RoleAssignmentActivity;
import com.mafia.assisstant.Room.DataHolder.RoleDataHolder;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NightRolesFragment extends Fragment {

    @BindView(R.id.night_roles_recyclerview) RecyclerView recyclerView;

    Context context;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_night_roles, container, false);
        ButterKnife.bind(this , view);
        NightListAdapter nightListAdapter = new NightListAdapter(context, RoleAssignmentActivity.nightRoles);
        ItemTouchHelper.Callback nightCallback = new NightRolesTouchHelperCallback(nightListAdapter);
        ItemTouchHelper nightTouchHelper = new ItemTouchHelper(nightCallback);
        nightTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(nightListAdapter);
        nightListAdapter.changePriority();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    static class NightListAdapter extends RecyclerView.Adapter<NightListAdapter.ViewHolder> implements ItemTouchHelperAdapter {

        private Context context;
        List<RoleDataHolder> roles;

        public NightListAdapter(Context context, List<RoleDataHolder> roles) {
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
            @BindView(R.id.role_assignment_role_name)
            TextView roleNameTxt;
            @BindView(R.id.role_assignment_priority) TextView priorityTxt;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this , itemView);
            }

        }
    }
}
