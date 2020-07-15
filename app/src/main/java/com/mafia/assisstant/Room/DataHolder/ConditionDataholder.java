package com.mafia.assisstant.Room.DataHolder;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "tCondition")
public class ConditionDataholder {

    @PrimaryKey(autoGenerate = true)
    int id;

    @ColumnInfo(name = "AbilityId")
    int abilityId;

    @ColumnInfo(name = "IncludeRoles")
    String includeRoles;

    @ColumnInfo(name = "KindCondition")
    boolean kind;

    @ColumnInfo(name = "Priority")
    int priority;

    @ColumnInfo(name = "Command")
    boolean command;

    @Ignore
    boolean selected;



    public ConditionDataholder() {
    }

    public  ConditionDataholder(int abilityId, boolean kind, int priority, boolean command) {
        this.abilityId = abilityId;
        this.kind = kind;
        this.priority = priority;
        this.command = command;
    }

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

    public String getIncludeRoles() {
        return includeRoles;
    }

    public void setIncludeRoles(String includeRoles) {
        this.includeRoles = includeRoles;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isKind() {
        return kind;
    }

    public void setKind(boolean kind) {
        this.kind = kind;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isCommand() {
        return command;
    }

    public void setCommand(boolean command) {
        this.command = command;
    }
}
