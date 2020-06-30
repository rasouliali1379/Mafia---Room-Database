package com.mafia.assisstant.Room.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mafia.assisstant.Room.DataHolder.ActionDataHolder;
import com.mafia.assisstant.Room.Repository.ActionRepository;

import java.util.List;

public class ActionViewModel extends AndroidViewModel {

    private LiveData<List<ActionDataHolder>> actions;
    ActionRepository mRepository;
    public ActionViewModel(@NonNull Application application) {
        super(application);
        mRepository = new ActionRepository(application);
    }

    public LiveData<List<ActionDataHolder>> getAll() {
        return mRepository.getAll();
    }

    public LiveData<List<ActionDataHolder>> getByAbilityId(int abilityId) {
        return mRepository.getByAbilityId(abilityId);
    }

    public void delete(ActionDataHolder action) {
        mRepository.delete(action);
    }

    public void insert(ActionDataHolder action) {
        mRepository.insert(action);
    }

    public LiveData<ActionDataHolder> getById(int actionId) {
        return mRepository.getById(actionId);
    }

    public void update(ActionDataHolder action) {
        mRepository.update(action);
    }

    public LiveData<List<ActionDataHolder>> getByConditionId(int id) {
        return mRepository.getByConditionId(id);
    }

    public void deleteMultiple(List<ActionDataHolder> actions) {
        mRepository.deleteMultiple(actions);
    }
}
