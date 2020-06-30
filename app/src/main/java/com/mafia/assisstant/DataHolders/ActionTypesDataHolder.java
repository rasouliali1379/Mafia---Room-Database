package com.mafia.assisstant.DataHolders;

public class ActionTypesDataHolder {
    private int id;
    String name, desc;

    public ActionTypesDataHolder(int id, String name, String desc) {
        this.id = id;
        this.name = name;
        this.desc = desc;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }
}
