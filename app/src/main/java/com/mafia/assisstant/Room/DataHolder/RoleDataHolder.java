package com.mafia.assisstant.Room.DataHolder;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "tRole")
public class RoleDataHolder {

    @PrimaryKey(autoGenerate = true)
    int id;

    @ColumnInfo(name = "roleKindId")
    @NonNull
    int roleKindId;

    @ColumnInfo(name = "roleName")
    String roleName;

    @ColumnInfo(name = "draft")
    @NonNull
    boolean draft;

    @Ignore
    int priority;

    @Ignore
    boolean checked;

    public RoleDataHolder() {
    }

    public RoleDataHolder(int id, int roleKindId, String roleName, boolean draft) {
        this.id = id;
        this.roleKindId = roleKindId;
        this.roleName = roleName;
        this.draft = draft;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRoleKindId() {
        return roleKindId;
    }

    public void setRoleKindId(int roleKindId) {
        this.roleKindId = roleKindId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public boolean isDraft() {
        return draft;
    }

    public void setDraft(boolean draft) {
        this.draft = draft;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
