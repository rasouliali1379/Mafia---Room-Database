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
        actionTypes.add(new ActionTypesDataHolder(1, types[0], context.getResources().getString(R.string.action_type_delete_desc)));
        actionTypes.add(new ActionTypesDataHolder(2, types[1], context.getResources().getString(R.string.action_type_delete_temp_desc)));
        actionTypes.add(new ActionTypesDataHolder(3, types[2], context.getResources().getString(R.string.action_type_suicide_desc)));
        actionTypes.add(new ActionTypesDataHolder(4, types[3], context.getResources().getString(R.string.action_type_change_role_desc)));
        actionTypes.add(new ActionTypesDataHolder(5, types[4], context.getResources().getString(R.string.action_type_change_power_desc)));
        actionTypes.add(new ActionTypesDataHolder(6, types[5], context.getResources().getString(R.string.action_type_increase_power_desc)));
        actionTypes.add(new ActionTypesDataHolder(7, types[6], context.getResources().getString(R.string.action_type_decrease_power_desc)));
        actionTypes.add(new ActionTypesDataHolder(8, types[7], context.getResources().getString(R.string.action_type_remove_desc)));
        actionTypes.add(new ActionTypesDataHolder(9, types[8], context.getResources().getString(R.string.action_type_always_safe_night_desc)));
        actionTypes.add(new ActionTypesDataHolder(10, types[9], context.getResources().getString(R.string.action_type_always_safe_day_desc)));
        actionTypes.add(new ActionTypesDataHolder(11, types[10], context.getResources().getString(R.string.action_type_regular_safe_desc)));
        actionTypes.add(new ActionTypesDataHolder(12, types[11], context.getResources().getString(R.string.action_type_self_safe_desc)));
        actionTypes.add(new ActionTypesDataHolder(13, types[12], context.getResources().getString(R.string.action_type_change_role_type_desc)));
        actionTypes.add(new ActionTypesDataHolder(14, types[13], context.getResources().getString(R.string.action_type_copy_ability_desc)));
        actionTypes.add(new ActionTypesDataHolder(15, types[14], context.getResources().getString(R.string.action_type_mirror_desc)));
        actionTypes.add(new ActionTypesDataHolder(16, types[15], context.getResources().getString(R.string.action_type_actNone_desc)));

        return actionTypes;
    }
}
