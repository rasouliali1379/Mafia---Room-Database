package com.mafia.assisstant.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mafia.assisstant.R;
import com.mafia.assisstant.Room.DataHolder.AbilityDataHolder;
import com.mafia.assisstant.Room.DataHolder.PowerDataHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AbilityListAdapter extends RecyclerView.Adapter<AbilityListAdapter.ViewHolder> {

    Context context;
    List<AbilityDataHolder> abilities;
    List<PowerDataHolder> powers;
    boolean darkTheme;
    private final OnItemClickListener listener;

    public AbilityListAdapter(Context context, List<AbilityDataHolder> abilities, List<PowerDataHolder> powers, boolean darkTheme, OnItemClickListener listener) {
        this.context = context;
        this.abilities = abilities;
        this.powers = powers;
        this.darkTheme = darkTheme;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mainboard_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.powerName.setText(powers.get(position).getPowerName());
        if (darkTheme){
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.darkThemeColorPrimary));
            holder.powerName.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        } else {
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            holder.powerName.setTextColor(context.getResources().getColor(R.color.colorAccent));
        }
        holder.bind(abilities.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return abilities.size();
    }

    public interface OnItemClickListener {
        void onItemClick(AbilityDataHolder ability);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.main_board_item_player_name) TextView powerName;
        @BindView(R.id.main_board_item_cardview) CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this , itemView);
        }

        public void bind(final AbilityDataHolder ability, final OnItemClickListener listener) {
            itemView.setOnClickListener((View view)-> listener.onItemClick(ability));
        }
    }
}
