package com.mafia.assisstant.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mafia.assisstant.R;

import butterknife.ButterKnife;

public class ActionChangeRoleFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_action_change_role, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}