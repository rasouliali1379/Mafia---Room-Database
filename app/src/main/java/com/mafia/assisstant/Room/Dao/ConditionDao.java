package com.mafia.assisstant.Room.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.mafia.assisstant.Room.DataHolder.ConditionDataholder;

import java.util.List;

@Dao
public interface ConditionDao {

    @Insert
    void insert(ConditionDataholder condition);

    @Query("SELECT * FROM tCondition")
    LiveData<List<ConditionDataholder>> getAll();

    @Query("SELECT * FROM tCondition WHERE AbilityId=:id")
    LiveData<List<ConditionDataholder>> getByAbilityId(int id);

    @Update
    void update(ConditionDataholder condition);

    @Delete
    void deleteMultiple(List<ConditionDataholder> empty_conditions);

    @Query("DELETE FROM tCondition")
    void deleteAll();

    @Delete
    void delete(ConditionDataholder condition);

    @Update
    void updateMultiple(List<ConditionDataholder> conditions);
}
