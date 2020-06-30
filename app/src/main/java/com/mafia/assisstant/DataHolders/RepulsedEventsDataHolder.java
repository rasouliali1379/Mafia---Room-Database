package com.mafia.assisstant.DataHolders;

public class RepulsedEventsDataHolder {

    private int attackedBy, repulsedBy;

    public RepulsedEventsDataHolder(int attackedBy, int repulsedBy) {
        this.attackedBy = attackedBy;
        this.repulsedBy = repulsedBy;
    }

    public int getAttackedBy() {
        return attackedBy;
    }

    public void setAttackedBy(int attackedBy) {
        this.attackedBy = attackedBy;
    }

    public int getRepulsedBy() {
        return repulsedBy;
    }

    public void setRepulsedBy(int repulsedBy) {
        this.repulsedBy = repulsedBy;
    }
}
