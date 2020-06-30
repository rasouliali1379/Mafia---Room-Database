package com.mafia.assisstant.Room.Repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.mafia.assisstant.Room.Dao.PlayerDao;
import com.mafia.assisstant.Room.Dao.RoleDao;
import com.mafia.assisstant.Room.DataHolder.PlayerDataHolder;
import com.mafia.assisstant.Room.DataHolder.RoleDataHolder;
import com.mafia.assisstant.Room.MafiaRoomDatabase;

import java.util.List;

public class PlayerRepository {
    private PlayerDao playerDao;
    private LiveData<List<PlayerDataHolder>> players;

    public PlayerRepository(Application application) {
        MafiaRoomDatabase db = MafiaRoomDatabase.getDatabase(application);
        playerDao = db.playerDao();
        players = playerDao.getAll();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<PlayerDataHolder>> getAll() {
        return players;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insert(PlayerDataHolder player) {
        MafiaRoomDatabase.databaseWriteExecutor.execute(() -> {
            playerDao.insert(player);
        });
    }

    public void delete(PlayerDataHolder player) {
        MafiaRoomDatabase.databaseWriteExecutor.execute(() -> {
            playerDao.delete(player);
        });
    }

    public void update(PlayerDataHolder player) {
        MafiaRoomDatabase.databaseWriteExecutor.execute(() -> {
            playerDao.update(player);
        });
    }

    public void deleteAll() {
        MafiaRoomDatabase.databaseWriteExecutor.execute(() -> {
            playerDao.deleteAll();
        });
    }

    public void insertMultiple(List<PlayerDataHolder> players) {
        MafiaRoomDatabase.databaseWriteExecutor.execute(() -> {
            playerDao.insertMultiple(players);
        });
    }

    public void updateMultiple(List<PlayerDataHolder> players) {
        MafiaRoomDatabase.databaseWriteExecutor.execute(() -> {
            playerDao.updateMultiple(players);
        });
    }
}
