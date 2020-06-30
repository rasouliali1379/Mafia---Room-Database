package com.mafia.assisstant.Room.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mafia.assisstant.Room.DataHolder.AbilityDataHolder;
import com.mafia.assisstant.Room.Repository.AbilityRepository;

import java.util.List;

public class AbilityViewModel extends AndroidViewModel {

    private AbilityRepository mRepository;

    public AbilityViewModel (Application application) {
        super(application);
        mRepository = new AbilityRepository(application);
    }

    public LiveData<List<AbilityDataHolder>> getAll() { return mRepository.getAll(); }

    public LiveData<List<AbilityDataHolder>> getByRoleId(int id) { return mRepository.getByRoleId(id); }

    public void insert(AbilityDataHolder abilityDataHolder) { mRepository.insert(abilityDataHolder); }

    public void update(AbilityDataHolder abilityDataHolder) { mRepository.update(abilityDataHolder); }

    public void updateMultiple(List<AbilityDataHolder> abilities) { mRepository.updateMultiple(abilities); }

    public LiveData<AbilityDataHolder> getById(int id) {
        return mRepository.getById(id);
    }

    public LiveData<List<AbilityDataHolder>> getByPowerId(int id) {
        return mRepository.getByPowerId(id);
    }

    public void deleteMultiple(List<AbilityDataHolder> abilities){
        mRepository.deleteMultiple(abilities);
    }

    public void deleteDrafts(){
        mRepository.deleteDrafts();
    }

    public void insertMultiple(List<AbilityDataHolder> abilities) {
        mRepository.insertMultiple(abilities);
    }
}