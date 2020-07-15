package com.mafia.assisstant.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class ActionActivityViewModel extends AndroidViewModel {

    private MutableLiveData<Integer> selectedCondition;
    public ActionActivityViewModel(@NonNull Application application) {
        super(application);
        selectedCondition = new MutableLiveData<>();
    }

    public MutableLiveData<Integer> getSelectedCondition() {
        return selectedCondition;
    }

    public void setSelectedCondition(int selectedCondition) {
        this.selectedCondition.setValue(selectedCondition);
    }
}
