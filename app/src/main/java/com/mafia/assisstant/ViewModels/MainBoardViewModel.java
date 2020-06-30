package com.mafia.assisstant.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.mafia.assisstant.Room.DataHolder.PlayerDataHolder;

import java.util.List;

public class MainBoardViewModel extends AndroidViewModel {

    private MutableLiveData<PlayerDataHolder> starterPlayer;
    private MutableLiveData<PlayerDataHolder> activePlayer;
    private MutableLiveData<Integer> AllPlayers;
    private MutableLiveData<Integer> mafiaCounter;
    private MutableLiveData<Integer> civilCounter;


    public MainBoardViewModel(@NonNull Application application) {
        super(application);

    }

    public void setStarterPlayer(PlayerDataHolder player) {
        if (starterPlayer == null){
            starterPlayer = new MutableLiveData<>();
        }
        this.starterPlayer.setValue(player);
    }

    public MutableLiveData<PlayerDataHolder> getStarterPlayer() {
        return starterPlayer;
    }

    public void setMafiaCounter(int mafiaPlayers) {
        if (mafiaCounter == null){
            mafiaCounter = new MutableLiveData<>();
        }
        this.mafiaCounter.setValue(mafiaPlayers);
    }

    public MutableLiveData<Integer> getMafiaCounter() {
        return mafiaCounter;
    }

    public void setCivilCounter(int civilPlayers) {
        if (civilCounter == null){
            civilCounter = new MutableLiveData<>();
        }
        this.civilCounter.setValue(civilPlayers);
    }

    public MutableLiveData<Integer> getCivilCounter() {
        return civilCounter;
    }

    public void setActivePlayer(PlayerDataHolder player) {
        if (activePlayer == null){
            activePlayer = new MutableLiveData<>();
        }
        this.activePlayer.setValue(player);
    }

    public MutableLiveData<PlayerDataHolder> getActivePlayer() {
        return activePlayer;
    }

    public MutableLiveData<Integer> getAllPlayersCounter() {
        return AllPlayers;
    }

    public void setAllPlayers(int num) {
        if (AllPlayers == null){
            AllPlayers = new MutableLiveData<>();
        }
        this.AllPlayers.setValue(num);
    }
}
