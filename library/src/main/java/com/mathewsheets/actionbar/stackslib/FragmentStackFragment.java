package com.mathewsheets.actionbar.stackslib;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class FragmentStackFragment extends Fragment {

    private FragmentManager.OnBackStackChangedListener backStackChangedListener;

    private static FragmentStackFragment newInstance() {

        FragmentStackFragment fragmentStack = new FragmentStackFragment();

        return fragmentStack;
    }

    private boolean enableDebugLogging;
    public boolean isEnableDebugLogging() {
        return enableDebugLogging;
    }
    public void setEnableDebugLogging(boolean enableDebugLogging) {
        this.enableDebugLogging = enableDebugLogging;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);


        backStackChangedListener = new FragmentManager.OnBackStackChangedListener() {

            @Override
            public void onBackStackChanged() {


            }
        };

        if (savedInstanceState == null) {


        }
    }

    private void addFragment(String title, Fragment fragment, int fragmentRes) {

        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction tx = fragmentManager.beginTransaction();
        tx.replace(fragmentRes, fragment, title);
        tx.setBreadCrumbShortTitle(title);
        tx.addToBackStack(title);
        tx.commit();
    }

    private void popBackStack() {

        getChildFragmentManager().popBackStack();
    }

    private int getBackStackEntryCount() {

        return getChildFragmentManager().getBackStackEntryCount();
    }

    @Override
    public void onResume() {

        super.onResume();

        getChildFragmentManager().addOnBackStackChangedListener(backStackChangedListener);
    }

    @Override
    public void onPause() {

        getChildFragmentManager().removeOnBackStackChangedListener(backStackChangedListener);

        super.onPause();
    }

}
