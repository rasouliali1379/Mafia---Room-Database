package com.mafia.assisstant.Room.DataHolder;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tAction")
public class ActionDataHolder {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    int id;

    @ColumnInfo(name = "AbilityId")
    @NonNull
    int abilityId;

    @ColumnInfo(name = "ActionTypeId")
    @NonNull
    int actionTypeId;

    @ColumnInfo(name = "ToRoleID")
    @NonNull
    int toRoleId;

    @ColumnInfo(name = "FromPowerId")
    @NonNull
    int abilityFromId;

    @ColumnInfo(name = "ToPowerId")
    @NonNull
    int abilityToId;

    @ColumnInfo(name = "AbilityReward")
    @NonNull
    int abilityReward;

    @ColumnInfo(name = "ToRoleTypeId")
    @NonNull
    int toRoleTypeId;

    @ColumnInfo(name = "ConditionId")
    int conditionId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAbilityId() {
        return abilityId;
    }

    public void setAbilityId(int abilityId) {
        this.abilityId = abilityId;
    }

    public int getActionTypeId() {
        return actionTypeId;
    }

    public void setActionTypeId(int actionTypeId) {
        this.actionTypeId = actionTypeId;
    }

    public int getToRoleId() {
        return toRoleId;
    }

    public void setToRoleId(int toRoleId) {
        this.toRoleId = toRoleId;
    }

    public int getAbilityFromId() {
        return abilityFromId;
    }

    public void setAbilityFromId(int abilityFromId) {
        this.abilityFromId = abilityFromId;
    }

    public int getAbilityToId() {
        return abilityToId;
    }

    public void setAbilityToId(int abilityToId) {
        this.abilityToId = abilityToId;
    }

    public int getAbilityReward() {
        return abilityReward;
    }

    public void setAbilityReward(int abilityReward) {
        this.abilityReward = abilityReward;
    }

    public int getConditionId() {
        return conditionId;
    }

    public void setConditionId(int conditionId) {
        this.conditionId = conditionId;
    }

    public int getToRoleTypeId() {
        return toRoleTypeId;
    }

    public void setToRoleTypeId(int toRoleTypeId) {
        this.toRoleTypeId = toRoleTypeId;
    }
}
