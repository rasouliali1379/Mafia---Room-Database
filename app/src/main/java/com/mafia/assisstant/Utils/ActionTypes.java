package com.mafia.assisstant.Utils;

import android.content.Context;

import com.mafia.assisstant.DataHolders.ActionTypesDataHolder;
import com.mafia.assisstant.R;
import com.mafia.assisstant.Room.DataHolder.ActionDataHolder;

import java.util.ArrayList;
import java.util.List;

public class ActionTypes {
    private List<ActionTypesDataHolder> actionTypes;
    Context context;

    public ActionTypes(Context context) {
        this.context = context;
    }

    public List<ActionTypesDataHolder> getActionTypes() {
        String[] types = context.getResources().getStringArray(R.array.action_types);
        actionTypes = new ArrayList<>();
        actionTypes.add(new ActionTypesDataHolder(1, types[0]));

        return actionTypes;
    }
}
