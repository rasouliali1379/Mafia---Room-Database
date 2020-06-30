package com.mafia.assisstant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import com.mafia.assisstant.Room.ViewModel.PlayerViewModel;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class StartGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);
        setTitle(getResources().getString(R.string.start_game));
        ButterKnife.bind(this);
        PlayerViewModel playerViewModel = new ViewModelProvider(this).get(PlayerViewModel.class);
        playerViewModel.deleteAll();
    }

    @OnClick(R.id.skip_button) void skipActivity (){
        startActivity(new Intent(StartGameActivity.this , AddPlayerActivity.class));
        finish();
    }
}