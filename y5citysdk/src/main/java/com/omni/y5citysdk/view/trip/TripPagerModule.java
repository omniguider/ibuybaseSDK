package com.omni.y5citysdk.view.trip;


import com.omni.y5citysdk.R;

public enum TripPagerModule {

    THEME(R.string.info_pager_title_theme),
    CUSTOM(R.string.info_pager_title_custom),
    MAP(R.string.info_pager_title_around);

    private int mTitleResId;

    TripPagerModule(int titleResId) {
        mTitleResId = titleResId;
    }

    public int getTitleResId() {
        return mTitleResId;
    }
}
