package com.mafia.assisstant.Room.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mafia.assisstant.Room.DataHolder.PowerDataHolder;
import com.mafia.assisstant.Room.Repository.PowerRepository;

import java.util.List;

public class PowerViewModel extends AndroidViewModel {

    private PowerRepository mRepository;

    private LiveData<List<PowerDataHolder>> powers;

    public PowerViewModel (Application application) {
        super(application);
        mRepository = new PowerRepository(application);
        powers = mRepository.getAll();
    }

    public LiveData<List<PowerDataHolder>> getAll() { return powers; }

    public LiveData<List<PowerDataHolder>> getByIDMultiple(List<Integer> ids) {
        return mRepository.getByIdMultiple(ids);
    }

    public LiveData<PowerDataHolder>getById(int id){
        return mRepository.getById(id);
    }

    public void insert(PowerDataHolder powerDataHolder) {
        mRepository.insert(powerDataHolder);
    }

    public void delete(PowerDataHolder power) {
        mRepository.delete(power);
    }

    public void update(PowerDataHolder power) {
        mRepository.update(power);
    }

    public void insertMultiple(List<PowerDataHolder> powers) {
            mRepository.insertMultiple(powers);
    }
}