package com.mafia.assisstant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mafia.assisstant.Room.DataHolder.PlayerDataHolder;
import com.mafia.assisstant.Room.ViewModel.KindViewModel;
import com.mafia.assisstant.Room.ViewModel.PlayerViewModel;
import com.mafia.assisstant.Room.ViewModel.RoleViewModel;
import com.mafia.assisstant.ViewModels.RolePresentationViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RolePresentationActivity extends AppCompatActivity {
    PlayerViewModel playerViewModel;
    RoleViewModel roleViewModel;
    KindViewModel kindViewModel;
    RolePresentationViewModel rolePresentationViewModel;
    CustomListAdapter customListAdapter;
    @BindView(R.id.role_presentation_recyclerview) RecyclerView recyclerView;
    @BindView(R.id.role_presentation_cover)TextView playerInfoCover;
    @BindView(R.id.role_presentation_player_name)TextView playerName;
    @BindView(R.id.role_presentation_role_name)TextView roleName;
    @BindView(R.id.role_presentation_role_desc)TextView roleDesc;
    @BindView(R.id.role_presentation_main_container)RelativeLayout mainContainer;
    @BindView(R.id.role_presentation_next_fab) FloatingActionButton nextPlayer;
    @BindView(R.id.role_presentation_previous_fab) FloatingActionButton previousPlayer;

    int position = 0;
    List<PlayerDataHolder> players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_presentation);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        setTitle(getResources().getString(R.string.role_presentation_title));
        viewModelsInit();
        coverSetup();
        fabsSetup();
        recyclerViewSetup();
    }

    private void fabsSetup() {
        nextPlayer.setOnClickListener(v->{
            position++;
            fillContainer();
            coverSetup();
        });

        previousPlayer.setOnClickListener(v->{
            position--;
            fillContainer();
            coverSetup();

        });
    }

    private void viewModelsInit() {
        playerViewModel = new ViewModelProvider(this).get(PlayerViewModel.class);
        roleViewModel = new ViewModelProvider(this).get(RoleViewModel.class);
        rolePresentationViewModel = new ViewModelProvider(this).get(RolePresentationViewModel.class);
        kindViewModel = new ViewModelProvider(this).get(KindViewModel.class);
        observersSetup();
    }

    private void observersSetup() {
        rolePresentationViewModel.getPlayerName().observe(this, name -> playerName.setText(getResources().getString(R.string.main_container_player_name) + name));
        rolePresentationViewModel.getRoleName().observe(this, name -> roleName.setText(getResources().getString(R.string.main_container_role_name) + name));
        rolePresentationViewModel.getRoleDesc().observe(this, desc -> roleDesc.setText(getResources().getString(R.string.main_container_role_type) + desc));
    }

    private void coverSetup() {
        playerInfoCover.setOnClickListener(v->{
            playerInfoCover.setVisibility(View.INVISIBLE);
            mainContainer.setVisibility(View.VISIBLE);
        });
    }


    private void recyclerViewSetup() {
        customListAdapter = new CustomListAdapter(this, position->{
            this.position = position;
            fillContainer();
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(customListAdapter);

        playerViewModel.getAll().observe(this, players ->{
            if(players != null){
                this.players = players;
                customListAdapter.setData(this.players);
                fillContainer();
            }
        });

    }

    private void fillContainer() {
        playerInfoCover.setVisibility(View.VISIBLE);
        mainContainer.setVisibility(View.INVISIBLE);
        rolePresentationViewModel.setPlayerName(players.get(position).getPlayerName());
        roleViewModel.getById(players.get(position).getRoleId()).observe(this, role -> {
            if (role != null) {
                rolePresentationViewModel.setRoleName(role.getRoleName());
                kindViewModel.getAll().observe(this, kinds -> {
                    if (kinds != null){
                        String kind;
                        if (role.getRoleKindId() == 1){
                            kind = kinds.get(0).getKindName();
                        } else if (role.getRoleKindId() == 2) {
                            kind = kinds.get(1).getKindName();
                        } else {
                            kind = kinds.get(2).getKindName();
                        }
                        rolePresentationViewModel.setRoleDesc(kind);
                    }
                });
            }
        });

        if (position == 0) {
            previousPlayer.setVisibility(View.GONE);
            nextPlayer.setVisibility(View.VISIBLE);
        } else if (position == players.size() - 1){
            nextPlayer.setVisibility(View.GONE);
            previousPlayer.setVisibility(View.VISIBLE);
        } else {
            previousPlayer.setVisibility(View.VISIBLE);
            nextPlayer.setVisibility(View.VISIBLE);
        }


        players.get(position).setSeen(true);
        players.get(position).setActive(true);

        for (int i = position; i > 0; i--){
            players.get(i - 1).setActive(false);
        }

        for (int i = position; i < players.size() - 1; i++){
            players.get(i + 1).setActive(false);
        }

        customListAdapter.notifyDataSetChanged();
        recyclerView.post(() -> recyclerView.smoothScrollToPosition(position));
    }


    static class CustomListAdapter extends RecyclerView.Adapter<CustomListAdapter.ViewHolder>{

        private Context context;
        private final OnItemClickListener listener;
        List<PlayerDataHolder> players;

        public CustomListAdapter(Context context, OnItemClickListener listener) {
            this.context = context;
            this.listener = listener;
            hasStableIds();
        }

        @NonNull
        @Override
        public CustomListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.role_presentation_item,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.playerName.setText(position + 1 + "- " +players.get(position).getPlayerName());

            if (players.get(position).isActive()){
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorGreen));
                holder.playerName.setTextColor(Color.WHITE);
            } else if (players.get(position).isSeen()){
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
                holder.playerName.setTextColor(Color.WHITE);
            } else {
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                holder.playerName.setTextColor(context.getResources().getColor(R.color.colorAccent));
            }

            holder.bind(position, listener);

        }

        @Override
        public int getItemCount() {
            if (players != null){
                return players.size();
            }
            return 0;
        }

        public void setData (List<PlayerDataHolder> players){
            this.players = players;
            notifyDataSetChanged();
        }

        public interface OnItemClickListener {
            void onItemClick(int position);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.role_presentation_item_txt)TextView playerName;
            @BindView(R.id.role_presentation_cardview)CardView cardView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this , itemView);
            }

            public void bind(int position, final OnItemClickListener listener) {
                itemView.setOnClickListener((View view)-> listener.onItemClick(position));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.start_game:
                startGame();
                break;
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.role_presentation, menu);
        return true;
    }

    private void startGame() {
        for (int i = 0; i < players.size(); i++){
            if (!players.get(i).isSeen()){
                Toast.makeText(this, getResources().getString(R.string.role_presentation_alert), Toast.LENGTH_LONG).show();
                return;
            }
        }
        startActivity(new Intent(RolePresentationActivity.this, MainBoardActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }



}
