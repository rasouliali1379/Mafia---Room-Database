package com.mafia.assisstant.Room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.mafia.assisstant.Room.Dao.AbilityDao;
import com.mafia.assisstant.Room.Dao.ActionDao;
import com.mafia.assisstant.Room.Dao.ConditionDao;
import com.mafia.assisstant.Room.Dao.EventDao;
import com.mafia.assisstant.Room.Dao.KindDao;
import com.mafia.assisstant.Room.Dao.PlayerDao;
import com.mafia.assisstant.Room.Dao.PowerDao;
import com.mafia.assisstant.Room.Dao.RoleDao;
import com.mafia.assisstant.DataHolders.AbilityCountDataHolder;
import com.mafia.assisstant.Room.DataHolder.AbilityDataHolder;
import com.mafia.assisstant.Room.DataHolder.ActionDataHolder;
import com.mafia.assisstant.Room.DataHolder.ConditionDataholder;
import com.mafia.assisstant.Room.DataHolder.EventDataHolder;
import com.mafia.assisstant.Room.DataHolder.KindDataHolder;
import com.mafia.assisstant.Room.DataHolder.PlayerDataHolder;
import com.mafia.assisstant.Room.DataHolder.PowerDataHolder;
import com.mafia.assisstant.Room.DataHolder.RoleDataHolder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {KindDataHolder.class , RoleDataHolder.class, AbilityDataHolder.class,
        PlayerDataHolder.class, PowerDataHolder.class, ActionDataHolder.class,
        EventDataHolder.class, ConditionDataholder.class}, version = 1, exportSchema = false)
public abstract class MafiaRoomDatabase extends RoomDatabase {

    public abstract KindDao kindDao();
    public abstract RoleDao roleDao();
    public abstract PlayerDao playerDao();
    public abstract AbilityDao abilityDao();
    public abstract PowerDao powerDao();
    public abstract ActionDao actionDao();
    public abstract EventDao eventDao();
    public abstract ConditionDao conditionDao();

    private static volatile MafiaRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 10;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static MafiaRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MafiaRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MafiaRoomDatabase.class, "MafiaDB")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}