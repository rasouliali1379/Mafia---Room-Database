package com.mafia.assisstant.Room.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mafia.assisstant.Room.DataHolder.PlayerDataHolder;
import com.mafia.assisstant.Room.Repository.PlayerRepository;

import java.util.List;

public class PlayerViewModel extends AndroidViewModel {

    private PlayerRepository mRepository;

    private LiveData<List<PlayerDataHolder>> players;

    public PlayerViewModel (Application application) {
        super(application);
        mRepository = new PlayerRepository(application);
        players = mRepository.getAll();
    }

    public LiveData<List<PlayerDataHolder>> getAll() { return players; }

    public void insert(PlayerDataHolder playerDataHolder) { mRepository.insert(playerDataHolder); }

    public void deleteAll(){
        mRepository.deleteAll();
    }

    public void insertMultiple(List<PlayerDataHolder> players) {
        mRepository.insertMultiple(players);
    }

    public void updateMultiple(List<PlayerDataHolder> players) {
        mRepository.updateMultiple(players);
    }
}