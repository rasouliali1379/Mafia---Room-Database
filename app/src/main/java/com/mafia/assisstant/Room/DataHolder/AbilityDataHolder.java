package com.mafia.assisstant.Room.DataHolder;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tAbility")
public class AbilityDataHolder {

    @PrimaryKey(autoGenerate = true)
    int id;

    @ColumnInfo(name = "PowerId")
    int powerId;

    @ColumnInfo(name = "RoleId")
    int roleId;

    @ColumnInfo(name = "PowerAgainst")
    int powerAgainst;

    @ColumnInfo(name = "Day")
    boolean day;

    @ColumnInfo(name = "ByDefault")
    boolean byDefault;

    @ColumnInfo(name = "PowerCount")
    int powerCount;

    @ColumnInfo(name = "Ratio")
    boolean ratio;

    @ColumnInfo(name = "AbilityAgainstId")
    int abilityAgainstId;

    @ColumnInfo(name = "AbilityDesc")
    String abilityDesc;

    @ColumnInfo(name = "Health")
    int health;

    @ColumnInfo(name = "Times")
    int times;

    @ColumnInfo(name = "ExecutionDelay")
    int executionDelay;

    @ColumnInfo(name = "Protection")
    int protection;

    @ColumnInfo(name = "Draft")
    boolean draft;

    public AbilityDataHolder() {
    }

    public AbilityDataHolder(int id, int powerId, int roleId, int powerCount, boolean draft) {
        this.id = id;
        this.powerId = powerId;
        this.roleId = roleId;
        this.draft = draft;
        this.powerCount = powerCount;
    }

    public AbilityDataHolder(int roleId, boolean draft) {
        this.roleId = roleId;
        this.draft = draft;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPowerId() {
        return powerId;
    }

    public void setPowerId(int powerId) {
        this.powerId = powerId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public boolean isDraft() {
        return draft;
    }

    public void setDraft(boolean draft) {
        this.draft = draft;
    }

    public boolean isDay() {
        return day;
    }

    public void setDay(boolean day) {
        this.day = day;
    }

    public int getPowerCount() {
        return powerCount;
    }

    public void setPowerCount(int powerCount) {
        this.powerCount = powerCount;
    }

    public boolean isRatio() {
        return ratio;
    }

    public void setRatio(boolean ratio) {
        this.ratio = ratio;
    }

    public int getAbilityAgainstId() {
        return abilityAgainstId;
    }

    public void setAbilityAgainstId(int abilityAgainstId) {
        this.abilityAgainstId = abilityAgainstId;
    }

    public String getAbilityDesc() {
        return abilityDesc;
    }

    public void setAbilityDesc(String abilityDesc) {
        this.abilityDesc = abilityDesc;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public int getExecutionDelay() {
        return executionDelay;
    }

    public void setExecutionDelay(int executionDelay) {
        this.executionDelay = executionDelay;
    }

    public int getProtection() {
        return protection;
    }

    public void setProtection(int protection) {
        this.protection = protection;
    }

    public boolean isByDefault() {
        return byDefault;
    }

    public void setByDefault(boolean byDefault) {
        this.byDefault = byDefault;
    }

    public int getPowerAgainst() {
        return powerAgainst;
    }

    public void setPowerAgainst(int powerAgainst) {
        this.powerAgainst = powerAgainst;
    }
}