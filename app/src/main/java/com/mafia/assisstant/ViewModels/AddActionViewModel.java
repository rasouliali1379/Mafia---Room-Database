package com.mafia.assisstant.ViewModels;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.mafia.assisstant.R;
import com.mafia.assisstant.Room.DataHolder.AbilityDataHolder;
import com.mafia.assisstant.Room.DataHolder.PowerDataHolder;
import com.mafia.assisstant.Room.DataHolder.RoleDataHolder;

public class AddActionViewModel extends AndroidViewModel {

    Context context;
    MutableLiveData<RoleDataHolder> changeRole;
    MutableLiveData<AbilityDataHolder> targetAbility;
    MutableLiveData<AbilityDataHolder> changeAbility;

    public AddActionViewModel(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
    }

    public MutableLiveData<AbilityDataHolder> getTargetAbility(){
        if (targetAbility == null){
            targetAbility = new MutableLiveData<>();
            setTargetAbility(new AbilityDataHolder(-1 , false));
        }
        return targetAbility;
    }

    public void setTargetAbility(AbilityDataHolder ability){
        this.targetAbility.setValue(ability);
    }

    public MutableLiveData<AbilityDataHolder> getChangeAbility(){
        if (changeAbility == null){
            changeAbility = new MutableLiveData<>();
            setChangeAbility(new AbilityDataHolder(-1, false));
        }
        return changeAbility;
    }

    public void setChangeAbility(AbilityDataHolder power){
        this.changeAbility.setValue(power);
    }

    public MutableLiveData<RoleDataHolder> getChangeRole(){
        if (changeRole == null){
            changeRole = new MutableLiveData<>();
            setChangeRole(new RoleDataHolder(-1, -1 ,context.getResources().getString(R.string.add), false));
        }
        return changeRole;
    }

    public void setChangeRole(RoleDataHolder role){
        this.changeRole.setValue(role);
    }
}
