package com.mafia.assisstant.Room.Repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.mafia.assisstant.Room.Dao.PowerDao;
import com.mafia.assisstant.Room.Dao.RoleDao;
import com.mafia.assisstant.Room.DataHolder.AbilityDataHolder;
import com.mafia.assisstant.Room.DataHolder.PowerDataHolder;
import com.mafia.assisstant.Room.DataHolder.RoleDataHolder;
import com.mafia.assisstant.Room.MafiaRoomDatabase;

import java.util.List;

public class PowerRepository {
    private PowerDao powerDao;
    private LiveData<List<PowerDataHolder>> powers;

    public PowerRepository(Application application) {
        MafiaRoomDatabase db = MafiaRoomDatabase.getDatabase(application);
        powerDao = db.powerDao();
        powers = powerDao.getAll();
    }


    public LiveData<List<PowerDataHolder>> getAll() {
        return powers;
    }


    public LiveData<PowerDataHolder> getById(int id) {
        return powerDao.getByID(id);
    }

    public LiveData<List<PowerDataHolder>> getByIdMultiple(List<Integer> ids) {
        return powerDao.getByIDMultiple(ids);
    }
    public void insert(PowerDataHolder power) {
        MafiaRoomDatabase.databaseWriteExecutor.execute(() -> {
            powerDao.insert(power);
        });
    }


    public void update(PowerDataHolder power) {
        MafiaRoomDatabase.databaseWriteExecutor.execute(() -> {
            powerDao.update(power);
        });
    }
    public void delete(PowerDataHolder power) {
        MafiaRoomDatabase.databaseWriteExecutor.execute(() -> {
            powerDao.delete(power);
        });
    }

    public void insertMultiple(List<PowerDataHolder> powers) {
        MafiaRoomDatabase.databaseWriteExecutor.execute(() -> {
            powerDao.insertMultiple(powers);
        });
    }
}
