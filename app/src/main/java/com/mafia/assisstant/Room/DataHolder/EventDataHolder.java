package com.mafia.assisstant.Room.DataHolder;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tEvent")
public class EventDataHolder {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    int id;

    @ColumnInfo(name = "PlayerId")
    int playerId;

    @ColumnInfo(name = "CausedPlayerId")
    int causedPlayerId;

    @ColumnInfo(name = "AbilityId")
    int abilityId;

    @ColumnInfo(name = "Day")
    boolean day;

    @ColumnInfo(name = "IsCurrent")
    boolean isCurrent;

    public EventDataHolder(int playerId, int causedPlayerId, int abilityId, boolean day, boolean isCurrent) {
        this.playerId = playerId;
        this.causedPlayerId = causedPlayerId;
        this.abilityId = abilityId;
        this.day = day;
        this.isCurrent = isCurrent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getCausedPlayerId() {
        return causedPlayerId;
    }

    public void setCausedPlayerId(int causedPlayerId) {
        this.causedPlayerId = causedPlayerId;
    }

    public int getAbilityId() {
        return abilityId;
    }

    public void setAbilityId(int abilityId) {
        this.abilityId = abilityId;
    }

    public boolean isDay() {
        return day;
    }

    public void setDay(boolean day) {
        this.day = day;
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public void setCurrent(boolean current) {
        isCurrent = current;
    }
}
