package com.mafia.assisstant.Room.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mafia.assisstant.Room.DataHolder.RoleDataHolder;
import com.mafia.assisstant.Room.Repository.RoleRepository;

import java.util.List;

public class RoleViewModel extends AndroidViewModel {

    private RoleRepository mRepository;

    private LiveData<List<RoleDataHolder>> roles;

    public RoleViewModel (Application application) {
        super(application);
        mRepository = new RoleRepository(application);
        roles = mRepository.getAll();
    }

    public LiveData<List<RoleDataHolder>> getAll() {
        return roles;
    }

    public LiveData<RoleDataHolder> getById(int id){
        return mRepository.getById(id);
    }

    public void insert(RoleDataHolder roleDataHolder) { mRepository.insert(roleDataHolder); }

    public void update(RoleDataHolder role) {
        mRepository.update(role);
    }

    public void deleteMultiple(List<RoleDataHolder> roles){
        mRepository.deleteMultiple(roles);
    }

    public void delete(RoleDataHolder role){
        mRepository.delete(role);
    }

    public void deleteDrafts(){
        mRepository.deleteDrafts();
    }

    public void insertMultiple(List<RoleDataHolder> roles) {
        mRepository.insertMultiple(roles);
    }
}