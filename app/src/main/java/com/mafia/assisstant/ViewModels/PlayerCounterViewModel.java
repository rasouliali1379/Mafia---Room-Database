package com.mafia.assisstant.ViewModels;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mafia.assisstant.R;

public class PlayerCounterViewModel extends ViewModel {
    private MutableLiveData<String> playerNumber;
    Context context;

    public PlayerCounterViewModel(Context context) {
        this.context = context;
    }

    public MutableLiveData<String> getPlayerNumber(){
        if (playerNumber == null){
            playerNumber = new MutableLiveData<>();
            setPlayerNumber("9" + context.getResources().getString(R.string.players_required));
        }
        return playerNumber;
    }

    public void setPlayerNumber(String playerNumber){
        this.playerNumber.setValue(playerNumber);
    }
}
