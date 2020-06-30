package com.mafia.assisstant.DataHolders;

public class ChangeAbilityDataHolder {
    int playerId;
    int abilityToId;
    int abilityFromId;

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getAbilityToId() {
        return abilityToId;
    }

    public void setAbilityToId(int abilityToId) {
        this.abilityToId = abilityToId;
    }

    public int getAbilityFromId() {
        return abilityFromId;
    }

    public void setAbilityFromId(int abilityFromId) {
        this.abilityFromId = abilityFromId;
    }
}
