package com.mafia.assisstant.Room.DataHolder;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tKind")
public class KindDataHolder {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "kindName")
    @NonNull
    private String kindName;

    @ColumnInfo(name = "KindType")
    @NonNull
    private String kindType;

    public KindDataHolder() {
    }

    public KindDataHolder(@NonNull String kindName, @NonNull String kindType) {
        this.kindName = kindName;
        this.kindType = kindType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKindName() {
        return kindName;
    }

    public void setKindName(String kindName) {
        this.kindName = kindName;
    }

    public String getKindType() {
        return kindType;
    }

    public void setKindType(String kindType) {
        this.kindType = kindType;
    }
}
