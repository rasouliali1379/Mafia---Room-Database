package com.mafia.assisstant.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mafia.assisstant.R;

import butterknife.ButterKnife;

public class ActionNotifyFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_action_notify, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}