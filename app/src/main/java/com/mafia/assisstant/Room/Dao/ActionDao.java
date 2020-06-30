package com.mafia.assisstant.Room.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.mafia.assisstant.Room.DataHolder.ActionDataHolder;

import java.util.List;

@Dao
public interface ActionDao {

    @Insert
    void insert(ActionDataHolder action);

    @Query("SELECT * FROM tAction WHERE AbilityId=:abilityId")
    LiveData<List<ActionDataHolder>> getByAbilityId(int abilityId);

    @Query("SELECT * FROM tAction")
    LiveData<List<ActionDataHolder>> getAll();

    @Delete
    void delete(ActionDataHolder action);

    @Query("SELECT * FROM tAction WHERE id=:actionId")
    LiveData<ActionDataHolder> getById(int actionId);

    @Update
    void update(ActionDataHolder action);

    @Query("SELECT * FROM tAction WHERE conditionId=:id")
    LiveData<List<ActionDataHolder>> getByConditionId(int id);

    @Delete
    void deleteMultiple(List<ActionDataHolder> actions);
}