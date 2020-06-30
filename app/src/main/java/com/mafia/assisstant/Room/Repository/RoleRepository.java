package com.mafia.assisstant.Room.Repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.mafia.assisstant.Room.Dao.RoleDao;
import com.mafia.assisstant.Room.DataHolder.RoleDataHolder;
import com.mafia.assisstant.Room.MafiaRoomDatabase;

import java.util.List;

public class RoleRepository {
    private RoleDao roleDao;
    public RoleRepository(Application application) {
        MafiaRoomDatabase db = MafiaRoomDatabase.getDatabase(application);
        roleDao = db.roleDao();
    }

    public LiveData<List<RoleDataHolder>> getAll() {
        return roleDao.getAll();
    }

    public LiveData<RoleDataHolder> getById(int id){
        return roleDao.getById(id);
    }

    public void insert(RoleDataHolder roleDataHolder) {
        MafiaRoomDatabase.databaseWriteExecutor.execute(() -> {
            roleDao.insert(roleDataHolder);
        });
    }

    public void delete(RoleDataHolder roleDataHolder) {
        MafiaRoomDatabase.databaseWriteExecutor.execute(() -> {
            roleDao.delete(roleDataHolder);
        });
    }

    public void deleteDrafts() {
        MafiaRoomDatabase.databaseWriteExecutor.execute(() -> {
            roleDao.deleteDrafts();
        });
    }

    public void deleteMultiple(List<RoleDataHolder> roles){
        MafiaRoomDatabase.databaseWriteExecutor.execute(() -> roleDao.deleteMultiple(roles));
    }

    public void update(RoleDataHolder roleDataHolder) {
        MafiaRoomDatabase.databaseWriteExecutor.execute(() -> {
            roleDao.update(roleDataHolder);
        });
    }

    public void insertMultiple(List<RoleDataHolder> roles) {
        MafiaRoomDatabase.databaseWriteExecutor.execute(() -> {
            roleDao.insertMultiple(roles);
        });
    }
}
