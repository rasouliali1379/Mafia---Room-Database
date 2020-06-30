package com.mafia.assisstant.Room.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.mafia.assisstant.Room.DataHolder.AbilityDataHolder;
import com.mafia.assisstant.Room.DataHolder.RoleDataHolder;

import java.util.List;

@Dao
public interface AbilityDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(AbilityDataHolder abilities);

    @Query("DELETE FROM tAbility")
    void deleteAll();

    @Query("DELETE FROM tAbility WHERE draft = 1")
    void deleteDrafts();

    @Query("SELECT * from tAbility")
    LiveData<List<AbilityDataHolder>> getAll();

    @Query("SELECT * from tAbility WHERE PowerId=:id")
    LiveData<List<AbilityDataHolder>> getByPowerId(int id);

    @Query("SELECT * from tAbility WHERE RoleId=:id")
    LiveData<List<AbilityDataHolder>> getByRoleId(int id);

    @Query("SELECT * from tAbility WHERE id=:id")
    LiveData<AbilityDataHolder> getById(int id);

    @Delete
    void delete(AbilityDataHolder ability);

    @Delete
    void deleteMultiple(List<AbilityDataHolder> ability);

    @Update
    void update(AbilityDataHolder ability);

    @Update
    void updateMultiple(List<AbilityDataHolder> abilities);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertMultiple(List<AbilityDataHolder> abilities);
}