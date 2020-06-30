package com.mafia.assisstant.Room.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.mafia.assisstant.Room.DataHolder.EventDataHolder;

import java.util.List;

@Dao
public interface EventDao {

    @Insert
    void insert(List<EventDataHolder> events);

    @Query("SELECT * FROM tEvent")
    LiveData<List<EventDataHolder>> getAll();

    @Query("DELETE FROM tEvent")
    void deleteAll();

    @Update
    void updateMultiple(List<EventDataHolder> events_temp);
}
