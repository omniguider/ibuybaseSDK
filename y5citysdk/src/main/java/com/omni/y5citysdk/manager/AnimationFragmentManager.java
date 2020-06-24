package com.omni.y5citysdk.manager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.omni.y5citysdk.R;

public class AnimationFragmentManager {

    private static AnimationFragmentManager sAnimFragmentManager;

    public static AnimationFragmentManager getInstance() {
        if (sAnimFragmentManager == null) {
            sAnimFragmentManager = new AnimationFragmentManager();
        }
        return sAnimFragmentManager;
    }

    private AnimationFragmentManager() {

    }

    private FragmentTransaction beginTransaction(FragmentActivity fragmentActivity) {
        FragmentTransaction transaction = fragmentActivity.getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

        return transaction;
    }

    public void addFragmentPage(FragmentActivity fragmentActivity,
                                int layoutResId,
                                Fragment fragment,
                                String tag) {
        beginTransaction(fragmentActivity).add(layoutResId, fragment, tag)
                .addToBackStack(null)
                .commit();
    }

    public void replaceFragmentPage(FragmentActivity fragmentActivity,
                                    int layoutResId,
                                    Fragment fragment,
                                    String tag) {
        beginTransaction(fragmentActivity).replace(layoutResId, fragment, tag)
                .addToBackStack(null)
                .commit();
    }
}
