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
        actionTypes.add(new ActionTypesDataHolder(2, types[1]));
        actionTypes.add(new ActionTypesDataHolder(3, types[2]));
        actionTypes.add(new ActionTypesDataHolder(4, types[3]));
        actionTypes.add(new ActionTypesDataHolder(5, types[4]));
        actionTypes.add(new ActionTypesDataHolder(6, types[5]));
        actionTypes.add(new ActionTypesDataHolder(7, types[6]));
        actionTypes.add(new ActionTypesDataHolder(8, types[7]));
        actionTypes.add(new ActionTypesDataHolder(9, types[8]));

        return actionTypes;
    }
}
