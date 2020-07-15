package com.mafia.assisstant.Room.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mafia.assisstant.Room.DataHolder.ConditionDataholder;
import com.mafia.assisstant.Room.Repository.ConditionRepository;

import java.util.List;

public class ConditionViewModel extends AndroidViewModel {

    ConditionRepository mRepository;
    public ConditionViewModel(@NonNull Application application) {
        super(application);
        mRepository = new ConditionRepository(application);
    }

    public LiveData<List<ConditionDataholder>> getAll() {
        return mRepository.getAll();
    }

    public LiveData<List<ConditionDataholder>> getByAbilityId(int id) {
        return mRepository.getByAbilityId(id);
    }

    public LiveData<List<ConditionDataholder>> getByAbilityIdAndCmd(int id, boolean cmd) {
        return mRepository.getByAbilityIdAndCmd(id, cmd);
    }

    public void insert(ConditionDataholder condition) {
        mRepository.insert(condition);
    }

    public void update(ConditionDataholder condition) {
        mRepository.update(condition);
    }

    public void deleteMultiple(List<ConditionDataholder> empty_conditions) {
        mRepository.deleteMultiple(empty_conditions);
    }

    public void deleteAll() {
        mRepository.deleteAll();
    }

    public void delete(ConditionDataholder condition) {
        mRepository.delete(condition);
    }

    public void updateMultiple(List<ConditionDataholder> conditions) {
        mRepository.upadteMultiple(conditions);
    }
}
