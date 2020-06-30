package com.mafia.assisstant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mafia.assisstant.Room.DataHolder.PlayerDataHolder;
import com.mafia.assisstant.Room.Repository.PlayerRepository;
import com.mafia.assisstant.Room.ViewModel.PlayerViewModel;
import com.mafia.assisstant.ViewModels.PlayerCounterViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddPlayerActivity extends AppCompatActivity {

    Context context = this;

    @BindView(R.id.players_recyclerview) RecyclerView recyclerView;
    @BindView(R.id.submit_players_btn) Button submitPlayersBtn;

    PlayerCounterViewModel playerCounterViewModel;
    PlayerViewModel playerViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_player);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        setTitle(getResources().getString(R.string.add_players_title));
        viewModelsInit();
        recyclerViewSetup();
        if (MainActivity.progressDialog.isShowing()){
            MainActivity.progressDialog.dismiss();
        }
    }

    private void viewModelsInit() {
        playerCounterViewModel = new ViewModelProvider(this, new PlayerCounterViewModelFactory(getApplication())).get(PlayerCounterViewModel.class);
        playerCounterViewModel.getPlayerNumber().observe(this , playerNumber -> submitPlayersBtn.setText(playerNumber));
        playerViewModel = new ViewModelProvider(this).get(PlayerViewModel.class);
    }

    @OnClick(R.id.add_player_btn)void addPlayer(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.add_power_dialog, viewGroup, false);
        DialogVH holder = new DialogVH(dialogView);

        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        holder.powerNameEt.setHint(getResources().getString(R.string.player_name));
        holder.submitBtn.setOnClickListener(v-> {
            if (holder.powerNameEt.getText() != null){
                if (holder.powerNameEt.getText().toString().length() > 0){
                    addName(holder.powerNameEt.getText().toString());
                    alertDialog.dismiss();
                }else {
                    holder.powerNameEt.setError(getResources().getString(R.string.name_required));
                    return;
                }
            } else {
                holder.powerNameEt.setError(getResources().getString(R.string.name_required));
                return;
            }

        });
        holder.cancelBtn.setOnClickListener((View view)-> alertDialog.dismiss());
    }

    private void recyclerViewSetup() {
        CustomListAdapter customListAdapter = new CustomListAdapter(this, getApplication());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(customListAdapter);
        playerViewModel.getAll().observe(this, players -> {
            customListAdapter.setData(players);
            countPlayers(context, players);
        });
    }

    private void addName(String playerName) {
        PlayerDataHolder player = new PlayerDataHolder();
        player.setPlayerName(playerName);
        playerViewModel.insert(player);
    }

    static class DialogVH {
        @BindView(R.id.add_power_name_tv) EditText powerNameEt;
        @BindView(R.id.add_power_submit_btn) TextView submitBtn;
        @BindView(R.id.add_power_cancel_btn)TextView cancelBtn;

        DialogVH(View rootView) {
            ButterKnife.bind(this , rootView);
        }
    }

    private void countPlayers(Context context, List<PlayerDataHolder> players) {
        StringBuilder sb = new StringBuilder();
        if (players.size() < 9){
            submitPlayersBtn.setBackground(context.getResources().getDrawable(R.drawable.bg_grey_filled));
            submitPlayersBtn.setTextColor(Color.BLACK);
            submitPlayersBtn.setEnabled(false);
            submitPlayersBtn.setOnClickListener(null);
            sb.append( 9 - players.size());
            sb.append(" ");
            if (9 - players.size() == 1){
                sb.append(context.getResources().getString(R.string.player_required));
            } else {
                sb.append(context.getResources().getString(R.string.players_required));
            }
        } else {
            sb.append(context.getResources().getString(R.string.submit_players_btn_title));
            submitPlayersBtn.setBackground(context.getResources().getDrawable(R.drawable.bg_black_filled));
            submitPlayersBtn.setTextColor(Color.WHITE);
            submitPlayersBtn.setEnabled(true);
            submitPlayersBtn.setOnClickListener(v ->submitPlayers());
        }
        playerCounterViewModel.setPlayerNumber(sb.toString());
    }

    private void submitPlayers() {
        MainActivity.progressDialog = new ProgressDialog(this);
        MainActivity.progressDialog.setCancelable(false);
        MainActivity.progressDialog.setMessage(getResources().getString(R.string.please_wait));
        MainActivity.progressDialog.show();
        startActivity(new Intent(AddPlayerActivity.this , RoleAssignmentActivity.class));
        finish();
    }

    static class CustomListAdapter extends RecyclerView.Adapter<CustomListAdapter.ViewHolder>{

        private Context context;
        List<PlayerDataHolder> players;
        private Application application;

        public CustomListAdapter(Context context, Application application) {
            this.context = context;
            this.application = application;
        }

        @NonNull
        @Override
        public CustomListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.dropdown_item,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.label.setText(players.get(position).getPlayerName());
            holder.editBtn.setOnClickListener(v->editPlayer(players.get(position)));
            holder.earaseBtn.setOnClickListener(v->earasePlayer(players.get(position)));
            holder.label.setTextDirection(View.TEXT_DIRECTION_LOCALE);
            holder.label.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        }

        private void earasePlayer(PlayerDataHolder player) {
           new PlayerRepository(application).delete(player);
        }

        private void editPlayer(PlayerDataHolder player) {
            Activity activity = (Activity) context;
            activity.runOnUiThread(() -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                ViewGroup viewGroup = activity.findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(context).inflate(R.layout.add_power_dialog, viewGroup, false);
                DialogVH holder = new DialogVH(dialogView);
                builder.setView(dialogView);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                holder.powerNameEt.setText(player.getPlayerName());
                holder.submitBtn.setOnClickListener(v-> {
                    if (holder.powerNameEt.getText() != null){
                        if (holder.powerNameEt.getText().toString().length() > 0){
                            player.setPlayerName(holder.powerNameEt.getText().toString());
                            new PlayerRepository(application).update(player);
                            alertDialog.dismiss();
                        }else {
                            holder.powerNameEt.setError(context.getResources().getString(R.string.name_required));
                            return;
                        }
                    } else {
                        holder.powerNameEt.setError(context.getResources().getString(R.string.name_required));
                        return;
                    }

                });
                holder.cancelBtn.setOnClickListener((View view)-> alertDialog.dismiss());
            });

        }

        @Override
        public int getItemCount() {
            if (players != null){
                return players.size();
            }
            return 0;
        }

        public void setData(List<PlayerDataHolder> players) {
            this.players = players;
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.dropdown_item_label) TextView label;
            @BindView(R.id.dropdown_item_edit) ImageView editBtn;
            @BindView(R.id.dropdown_item_earase) ImageView earaseBtn;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this , itemView);
            }
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        playerViewModel.deleteAll();
        finish();
    }

    public class PlayerCounterViewModelFactory extends ViewModelProvider.NewInstanceFactory {
        private Application application;

        public PlayerCounterViewModelFactory(@NonNull Application application) {
            this.application = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass == PlayerCounterViewModel.class)
                return (T) new PlayerCounterViewModel(application);
            return null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.auto_add:
                autoAddPlayers();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void autoAddPlayers() {
        List<PlayerDataHolder> players = new ArrayList<>();
        players.add(new PlayerDataHolder("علی"));
        players.add(new PlayerDataHolder("محمد"));
        players.add(new PlayerDataHolder("جواد"));
        players.add(new PlayerDataHolder("جعفر"));
        players.add(new PlayerDataHolder("ظفر"));
        players.add(new PlayerDataHolder("سهیل"));
        players.add(new PlayerDataHolder("سارا"));
        players.add(new PlayerDataHolder("سیمین"));
        players.add(new PlayerDataHolder("پری"));

        playerViewModel.insertMultiple(players);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_player, menu);
        return true;
    }
}
