package com.mafia.assisstant.Room.Repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.mafia.assisstant.Room.Dao.ConditionDao;
import com.mafia.assisstant.Room.DataHolder.ConditionDataholder;
import com.mafia.assisstant.Room.MafiaRoomDatabase;

import java.util.List;

public class ConditionRepository {
    private ConditionDao conditionDao;

    public ConditionRepository(Application application) {
        MafiaRoomDatabase db = MafiaRoomDatabase.getDatabase(application);
        this.conditionDao = db.conditionDao();
    }


    public LiveData<List<ConditionDataholder>> getAll() {
        return conditionDao.getAll();
    }

    public LiveData<List<ConditionDataholder>> getByAbilityId(int id) {
        return conditionDao.getByAbilityId(id);
    }

    public void insert(ConditionDataholder condition) {
        MafiaRoomDatabase.databaseWriteExecutor.execute(() -> conditionDao.insert(condition));
    }

    public void update(ConditionDataholder condition) {
        MafiaRoomDatabase.databaseWriteExecutor.execute(() -> conditionDao.update(condition));
    }

    public void deleteMultiple(List<ConditionDataholder> empty_conditions) {
        MafiaRoomDatabase.databaseWriteExecutor.execute(() -> conditionDao.deleteMultiple(empty_conditions));
    }

    public void deleteAll() {
        MafiaRoomDatabase.databaseWriteExecutor.execute(() -> conditionDao.deleteAll());
    }

    public void delete(ConditionDataholder condition) {
        MafiaRoomDatabase.databaseWriteExecutor.execute(() -> conditionDao.delete(condition));
    }

    public void upadteMultiple(List<ConditionDataholder> conditions) {
        MafiaRoomDatabase.databaseWriteExecutor.execute(() -> conditionDao.updateMultiple(conditions));
    }
}
