package com.mafia.assisstant.Room.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.mafia.assisstant.Room.DataHolder.AbilityDataHolder;
import com.mafia.assisstant.Room.DataHolder.KindDataHolder;
import com.mafia.assisstant.Room.DataHolder.PowerDataHolder;
import com.mafia.assisstant.Room.DataHolder.RoleDataHolder;

import java.util.List;

@Dao
public interface PowerDao {

    // allowing the insert of the same word multiple times by passing a
    // conflict resolution strategy
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(PowerDataHolder powers);

    @Query("DELETE FROM tPower")
    void deleteAll();

    @Query("SELECT * from tPower")
    LiveData<List<PowerDataHolder>> getAll();



    @Query("SELECT * from tPower WHERE id=:id")
    LiveData<PowerDataHolder> getByID(int id);

    @Query("SELECT * from tPower WHERE id IN (:ids)")
    LiveData<List<PowerDataHolder>> getByIDMultiple(List<Integer> ids);

    @Delete
    void delete(PowerDataHolder power);

    @Update
    void update(PowerDataHolder power);

    @Insert
    void insertMultiple(List<PowerDataHolder> powers);
}