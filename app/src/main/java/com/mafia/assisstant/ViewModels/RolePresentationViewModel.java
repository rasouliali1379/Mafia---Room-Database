package com.mafia.assisstant.ViewModels;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.mafia.assisstant.Room.DataHolder.PlayerDataHolder;
import com.mafia.assisstant.Room.ViewModel.PlayerViewModel;

import java.util.List;

public class RolePresentationViewModel extends AndroidViewModel {

    private MutableLiveData<String> playerName;
    private MutableLiveData<String> roleName;
    private MutableLiveData<String> roleDesc;
    public RolePresentationViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<String> getPlayerName() {
        if (playerName == null){
            playerName = new MutableLiveData<>();
            setPlayerName(" -");
        }
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName.setValue(playerName);
    }

    public MutableLiveData<String> getRoleName() {
        if (roleName == null){
            roleName = new MutableLiveData<>();
            setRoleName(" -");
        }
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName.setValue(roleName);
    }

    public MutableLiveData<String> getRoleDesc() {
        if (roleDesc == null){
            roleDesc = new MutableLiveData<>();
            setRoleDesc(" -");
        }
        return roleDesc;
    }

    public void setRoleDesc(String roleDesc) {
        this.roleDesc.setValue(roleDesc);
    }
}
