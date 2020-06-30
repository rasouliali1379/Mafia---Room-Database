package com.mafia.assisstant.Adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.widget.CompoundButtonCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mafia.assisstant.R;
import com.mafia.assisstant.Room.DataHolder.AbilityDataHolder;
import com.mafia.assisstant.Room.DataHolder.PlayerDataHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AvailablePlayerListAdapter  extends RecyclerView.Adapter<AvailablePlayerListAdapter.ViewHolder> {

    Context context;
    List<PlayerDataHolder> players;
    boolean isDay;
    private final OnItemClickListener listener;

    public AvailablePlayerListAdapter(Context context, List<PlayerDataHolder> players, boolean isDay, OnItemClickListener listener) {
        this.context = context;
        this.players = players;
        this.isDay = isDay;
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
        if (isDay){
            holder.checkBox.setTextColor(context.getResources().getColor(R.color.colorAccent));
            int states[][] = {{android.R.attr.state_checked}, {}};
            int colors[] = {context.getResources().getColor(R.color.colorAccent), context.getResources().getColor(R.color.colorAccent)};
            CompoundButtonCompat.setButtonTintList(holder.checkBox, new ColorStateList(states, colors));
        } else {
            holder.checkBox.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            int states[][] = {{android.R.attr.state_checked}, {}};
            int colors[] = {context.getResources().getColor(R.color.colorPrimary), context.getResources().getColor(R.color.colorPrimary)};
            CompoundButtonCompat.setButtonTintList(holder.checkBox, new ColorStateList(states, colors));
        }
        holder.checkBox.setText(players.get(position).getPlayerName());
        holder.bind(holder, position, listener);

    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public interface OnItemClickListener {
        void onItemClick(ViewHolder holder, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.checkbox_item_cb) public CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this , itemView);
        }

        public void bind(ViewHolder holder, final int position, final OnItemClickListener listener) {
            itemView.setOnClickListener((View view)-> listener.onItemClick(holder, position));
        }
    }
}