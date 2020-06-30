package com.mafia.assisstant.Room.Repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.mafia.assisstant.Room.Dao.ActionDao;
import com.mafia.assisstant.Room.DataHolder.ActionDataHolder;
import com.mafia.assisstant.Room.MafiaRoomDatabase;

import java.util.List;

public class ActionRepository {
    private ActionDao actionDao;

    public ActionRepository(Application application) {
        MafiaRoomDatabase db = MafiaRoomDatabase.getDatabase(application);
        actionDao = db.actionDao();
    }

    public LiveData<List<ActionDataHolder>> getByAbilityId(int abilityId) {
        return actionDao.getByAbilityId(abilityId);
    }

    public LiveData<List<ActionDataHolder>> getAll() {
        return actionDao.getAll();
    }

    public void delete(ActionDataHolder action) {
        MafiaRoomDatabase.databaseWriteExecutor.execute(() -> actionDao.delete(action));
    }

    public void insert(ActionDataHolder action) {
        MafiaRoomDatabase.databaseWriteExecutor.execute(() -> actionDao.insert(action));
    }

    public LiveData<ActionDataHolder> getById(int actionId) {
       return actionDao.getById(actionId);
    }

    public void update(ActionDataHolder action) {
        MafiaRoomDatabase.databaseWriteExecutor.execute(() -> actionDao.update(action));
    }

    public LiveData<List<ActionDataHolder>> getByConditionId(int id) {
        return actionDao.getByConditionId(id);
    }

    public void deleteMultiple(List<ActionDataHolder> actions) {
        MafiaRoomDatabase.databaseWriteExecutor.execute(() -> actionDao.deleteMultiple(actions));
    }
}
