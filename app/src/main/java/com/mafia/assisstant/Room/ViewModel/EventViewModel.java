package com.mafia.assisstant.Room.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mafia.assisstant.Room.DataHolder.EventDataHolder;
import com.mafia.assisstant.Room.Repository.EventRepository;

import java.util.List;

public class EventViewModel extends AndroidViewModel {

    private EventRepository mRepository;

    public EventViewModel(@NonNull Application application) {
        super(application);
        mRepository = new EventRepository(application);
    }

    public void insert(List<EventDataHolder> events) {
        mRepository.insert(events);
    }

    public LiveData<List<EventDataHolder>> getAll() {
        return mRepository.getAll();
    }

    public void deleteAll() {
        mRepository.deleteAll();
    }

    public void updateMultiple(List<EventDataHolder> events_temp) {
        mRepository.updateMultiple(events_temp);
    }
}
