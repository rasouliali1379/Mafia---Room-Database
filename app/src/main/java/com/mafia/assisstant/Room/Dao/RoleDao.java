package com.mafia.assisstant.Room.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.mafia.assisstant.Room.DataHolder.KindDataHolder;
import com.mafia.assisstant.Room.DataHolder.RoleDataHolder;

import java.util.List;

@Dao
public interface RoleDao {
    // allowing the insert of the same word multiple times by passing a
    // conflict resolution strategy
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(RoleDataHolder role);

    @Delete
    void delete(RoleDataHolder role);

    @Query("DELETE FROM tRole")
    void deleteAll();

    @Query("DELETE FROM tRole WHERE draft = 1")
    void deleteDrafts();

    @Delete
    void deleteMultiple(List<RoleDataHolder> roles);

    @Query("SELECT * FROM tRole")
    LiveData<List<RoleDataHolder>> getAll();

    @Query("SELECT * FROM tRole WHERE id =:id")
    LiveData<RoleDataHolder> getById(int id);


    @Update
    void update(RoleDataHolder role);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertMultiple(List<RoleDataHolder> roles);
}
