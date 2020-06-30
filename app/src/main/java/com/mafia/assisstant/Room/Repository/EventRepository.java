package com.mafia.assisstant.Room.Repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.mafia.assisstant.Room.Dao.EventDao;
import com.mafia.assisstant.Room.DataHolder.EventDataHolder;
import com.mafia.assisstant.Room.MafiaRoomDatabase;

import java.util.List;

public class EventRepository {
    private EventDao eventDao;

    public EventRepository(Application application) {
        MafiaRoomDatabase db = MafiaRoomDatabase.getDatabase(application);
        eventDao = db.eventDao();
    }

    public void insert(List<EventDataHolder> events) {
        MafiaRoomDatabase.databaseWriteExecutor.execute(() -> eventDao.insert(events));
    }

    public LiveData<List<EventDataHolder>> getAll() {
        return eventDao.getAll();
    }

    public void deleteAll() {
        MafiaRoomDatabase.databaseWriteExecutor.execute(() -> {
            eventDao.deleteAll();
        });
    }

    public void updateMultiple(List<EventDataHolder> events_temp) {
        MafiaRoomDatabase.databaseWriteExecutor.execute(() -> {
            eventDao.updateMultiple(events_temp);
        });
    }
}
