package com.mafia.assisstant.Room.DataHolder;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "tPlayer")
public class PlayerDataHolder {

    @PrimaryKey(autoGenerate = true)
    int playerId;

    @ColumnInfo(name = "RoleId")
    int roleId;

    @ColumnInfo(name = "PlayerName")
    @NonNull
    String playerName;

    @ColumnInfo(name = "priority")
    @NonNull
    int priority;

    @Ignore
    boolean active;

    @Ignore
    boolean seen;

    @Ignore
    boolean dead;

    @Ignore
    boolean checked;

    public PlayerDataHolder() {
    }

    public PlayerDataHolder(@NonNull String playerName) {
        this.playerName = playerName;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
