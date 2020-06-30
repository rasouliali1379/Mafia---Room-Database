package com.mafia.assisstant.Room.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.mafia.assisstant.Room.DataHolder.KindDataHolder;

import java.util.List;

@Dao
public interface KindDao {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(KindDataHolder... kind);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertMultiple(List<KindDataHolder> kind);

    @Query("DELETE FROM tKind")
    void deleteAll();

    @Query("SELECT * from tKind")
    LiveData<List<KindDataHolder>> getAll();

}