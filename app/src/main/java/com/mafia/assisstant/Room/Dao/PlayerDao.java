package com.mafia.assisstant.Room.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.mafia.assisstant.Room.DataHolder.PlayerDataHolder;

import java.util.List;

@Dao
public interface PlayerDao {

    // allowing the insert of the same word multiple times by passing a
    // conflict resolution strategy
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(PlayerDataHolder players);

    @Insert
    void insertMultiple(List<PlayerDataHolder> players);

    @Query("DELETE FROM tPlayer")
    void deleteAll();

    @Query("SELECT * from tPlayer")
    LiveData<List<PlayerDataHolder>> getAll();

    @Delete
    void delete(PlayerDataHolder player);

    @Update
    void update(PlayerDataHolder player);

    @Update
    void updateMultiple(List<PlayerDataHolder> players);



}