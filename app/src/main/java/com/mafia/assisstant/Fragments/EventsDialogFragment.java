package com.mafia.assisstant.Fragments;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import com.mafia.assisstant.Helpers.TabAdapter;
import com.mafia.assisstant.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class EventsDialogFragment extends DialogFragment {

    Context context;
    boolean isDay, darkTheme;

    public EventsDialogFragment(Context context, boolean isDay, boolean darkTheme) {
        this.context = context;
        this.isDay = isDay;
        this.darkTheme = darkTheme;
    }

    @BindView(R.id.events_layout_tablayout) TabLayout tabLayout;
    @BindView(R.id.events_layout_viewpager) ViewPager viewPager;
    @BindView(R.id.events_dialog_root_layout)LinearLayout rootLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        if (darkTheme){
            view = inflater.inflate(R.layout.fragment_events_dialog_dark_theme, container, true);
        } else {
            view = inflater.inflate(R.layout.fragment_events_dialog, container, true);
        }
        ButterKnife.bind(this, view);
        defineTheme();
        tabLayoutSetup();
        return view;
    }

    private void defineTheme() {
        if (darkTheme){
            rootLayout.setBackgroundColor(context.getResources().getColor(R.color.darkThemeColorSecondary));
            tabLayout.setSelectedTabIndicatorColor(context.getResources().getColor(R.color.colorPrimary));
            tabLayout.setTabTextColors(context.getResources().getColor(R.color.colorPrimaryDark),
                    context.getResources().getColor(R.color.colorPrimary));
        } else {
            rootLayout.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            tabLayout.setSelectedTabIndicatorColor(context.getResources().getColor(R.color.darkThemeColorPrimary));

            ViewGroup tabStrip = (ViewGroup) tabLayout.getChildAt(0);
            for (int i = 0; i < tabStrip.getChildCount(); i++) {
                View tabView = tabStrip.getChildAt(i);
                if (tabView != null) {
                    ViewCompat.setBackground(tabView, AppCompatResources.getDrawable(tabView.getContext(), R.drawable.tab_layout_white_bg));
                }
            }

            tabLayout.setTabTextColors(context.getResources().getColor(R.color.colorPrimaryDark),
                    context.getResources().getColor(R.color.darkThemeColorPrimary));
        }
    }

    private void tabLayoutSetup() {

        Fragment fragment1 = new EventsFragment(context, getDialog(), isDay, darkTheme);
        Fragment fragment2 = new EventsFragment(context, getDialog(), isDay, darkTheme);
        TabAdapter adapter;
        adapter = new TabAdapter(getChildFragmentManager());

        adapter.addFragment(fragment1, getResources().getString(R.string.recent_events));
        adapter.addFragment(fragment2, getResources().getString(R.string.all_events));

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

    }
}
