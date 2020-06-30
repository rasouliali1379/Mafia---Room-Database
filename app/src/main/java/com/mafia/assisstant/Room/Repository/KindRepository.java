package com.mafia.assisstant.Room.Repository;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.mafia.assisstant.Room.Dao.KindDao;
import com.mafia.assisstant.Room.DataHolder.KindDataHolder;
import com.mafia.assisstant.Room.MafiaRoomDatabase;

import java.util.List;

public class KindRepository {
    private KindDao kindDao;
    public KindRepository(Application application) {
        MafiaRoomDatabase db = MafiaRoomDatabase.getDatabase(application);
        kindDao = db.kindDao();
    }

    public LiveData<List<KindDataHolder>> getAll() {
        return kindDao.getAll();
    }



    public void insert(KindDataHolder kind) {
        MafiaRoomDatabase.databaseWriteExecutor.execute(() -> {
            kindDao.insert(kind);
        });
    }

    public void insertMultiple(List<KindDataHolder> kinds) {
        MafiaRoomDatabase.databaseWriteExecutor.execute(() -> {
            kindDao.insertMultiple(kinds);
        });
    }

    public void deleteAll() {
        MafiaRoomDatabase.databaseWriteExecutor.execute(() -> {
            kindDao.deleteAll();
        });
    }
}