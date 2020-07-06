package com.omni.y5citysdk.view.trip;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.omni.y5citysdk.view.custom.CustomMainFragment;
import com.omni.y5citysdk.view.point.PointMapFragment;
import com.omni.y5citysdk.view.theme.ThemeMainFragment;


public class TripPagerAdapter extends FragmentStatePagerAdapter {

    private Context mContext;

    public TripPagerAdapter(FragmentManager manager, Context context) {
        super(manager);
        mContext = context;
    }

    @Override
    public int getCount() {
        return TripPagerModule.values().length;
    }

    @Override
    public Fragment getItem(int position) {

        if (position == TripPagerModule.THEME.ordinal()) {
            return ThemeMainFragment.newInstance();
        } else if (position == TripPagerModule.CUSTOM.ordinal()) {
            return CustomMainFragment.newInstance();
        } else if (position == TripPagerModule.MAP.ordinal()) {
            return PointMapFragment.newInstance();
        }

        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getString(TripPagerModule.values()[position].getTitleResId());
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
