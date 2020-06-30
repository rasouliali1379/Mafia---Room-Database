package com.mafia.assisstant.ViewModels;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mafia.assisstant.AddAbilityActivity;
import com.mafia.assisstant.R;
import com.mafia.assisstant.Room.DataHolder.ActionDataHolder;
import com.mafia.assisstant.Room.DataHolder.PowerDataHolder;

import java.util.List;

public class AddAbilityViewModel extends AndroidViewModel {
    private MutableLiveData<PowerDataHolder> power;
    private MutableLiveData<PowerDataHolder> powerAgainst;
    private MutableLiveData<String> actionSlector;
    Context context;

    public AddAbilityViewModel(Application application) {
        super(application);
        context = application.getApplicationContext();
    }

    public MutableLiveData<PowerDataHolder> getSelectedPower(){
        if (power == null){
            power = new MutableLiveData<>();
            setSelectedPower(new PowerDataHolder(-1 ,context.getResources().getString(R.string.choose_power)));
        }
        return power;
    }

    public void setSelectedPower(PowerDataHolder power){
        this.power.setValue(power);
    }


    public MutableLiveData<PowerDataHolder> getSelectedPowerAgainst() {
        if (powerAgainst == null){
            powerAgainst = new MutableLiveData<>();
            setSelectedPowerAgainst(new PowerDataHolder(-1 ,context.getResources().getString(R.string.choose_power)));
        }
        return powerAgainst;
    }

    public void setSelectedPowerAgainst(PowerDataHolder power) {
        this.powerAgainst.setValue(power);
    }

    public MutableLiveData<String> getActionSlector() {
        if (actionSlector == null){
            actionSlector = new MutableLiveData<>();
            setActionSlector(context.getResources().getString(R.string.power_action_define_tv));
        }
        return actionSlector;
    }

    public void setActionSlector(String actionSlector) {
        this.actionSlector.setValue(actionSlector);
    }
}
