package com.mafia.assisstant.Room.ViewModel;


import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mafia.assisstant.Room.DataHolder.KindDataHolder;
import com.mafia.assisstant.Room.Repository.KindRepository;

import java.util.List;

public class KindViewModel extends AndroidViewModel {

    private KindRepository mRepository;
    private LiveData<List<KindDataHolder>> kinds;
    public KindViewModel (Application application) {
        super(application);
        mRepository = new KindRepository(application);
        kinds = mRepository.getAll();
    }

    public LiveData<List<KindDataHolder>> getAll() {
        return kinds;
    }

    public void insert(KindDataHolder kindDataHolder) {
        mRepository.insert(kindDataHolder);
    }

    public void insertMultiple(List<KindDataHolder> kinds) {
        mRepository.insertMultiple(kinds);
    }

    public void deleteAll() {
        mRepository.deleteAll();
    }
}