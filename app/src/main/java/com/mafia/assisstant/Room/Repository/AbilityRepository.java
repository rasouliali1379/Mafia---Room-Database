package com.mafia.assisstant.Room.Repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.mafia.assisstant.Room.Dao.AbilityDao;
import com.mafia.assisstant.Room.DataHolder.AbilityDataHolder;
import com.mafia.assisstant.Room.MafiaRoomDatabase;

import java.util.List;

public class AbilityRepository {
    private AbilityDao abilityDao;

    public AbilityRepository(Application application) {
        MafiaRoomDatabase db = MafiaRoomDatabase.getDatabase(application);
        abilityDao = db.abilityDao();
    }

    public LiveData<List<AbilityDataHolder>> getAll() {
        return abilityDao.getAll();
    }

    public LiveData<List<AbilityDataHolder>> getByRoleId(int id) {
        return abilityDao.getByRoleId(id);
    }

    public LiveData<List<AbilityDataHolder>> getByPowerId(int id) {
        return abilityDao.getByPowerId(id);
    }

    public LiveData<AbilityDataHolder> getById(int id) {
        return abilityDao.getById(id);
    }

    public void insert(AbilityDataHolder abilityDataHolder) {
        MafiaRoomDatabase.databaseWriteExecutor.execute(() -> {
            abilityDao.insert(abilityDataHolder);
        });
    }

    public void update(AbilityDataHolder abilityDataHolder) {
        MafiaRoomDatabase.databaseWriteExecutor.execute(() -> {
            abilityDao.update(abilityDataHolder);
        });
    }

    public void delete(AbilityDataHolder abilityDataHolder) {
        MafiaRoomDatabase.databaseWriteExecutor.execute(() -> {
            abilityDao.delete(abilityDataHolder);
        });
    }

    public void deleteMultiple(List<AbilityDataHolder> abilityDataHolder) {
        MafiaRoomDatabase.databaseWriteExecutor.execute(() -> {
            abilityDao.deleteMultiple(abilityDataHolder);
        });
    }

    public void deleteDrafts() {
        MafiaRoomDatabase.databaseWriteExecutor.execute(() -> {
            abilityDao.deleteDrafts();
        });
    }

    public void updateMultiple(List<AbilityDataHolder> abilities) {
        MafiaRoomDatabase.databaseWriteExecutor.execute(() -> {
            abilityDao.updateMultiple(abilities);
        });
    }

    public void insertMultiple(List<AbilityDataHolder> abilities) {
        MafiaRoomDatabase.databaseWriteExecutor.execute(() -> {
            abilityDao.insertMultiple(abilities);
        });
    }
}
