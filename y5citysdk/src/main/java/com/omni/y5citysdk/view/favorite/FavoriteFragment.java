package com.omni.y5citysdk.view.favorite;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.omni.y5citysdk.R;
import com.omni.y5citysdk.manager.AnimationFragmentManager;
import com.omni.y5citysdk.manager.UserInfoManager;
import com.omni.y5citysdk.module.OmniEvent;
import com.omni.y5citysdk.module.favorite.FavoriteData;
import com.omni.y5citysdk.network.NetworkManager;
import com.omni.y5citysdk.network.Y5CityAPI;
import com.omni.y5citysdk.tool.PreferencesTools;
import com.omni.y5citysdk.view.OmniViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.omni.y5citysdk.view.favorite.FavoriteListFragment.editMode;


public class FavoriteFragment extends Fragment {

    public static final String TAG = "fragment_favorite";
    private View mView;
    private OmniViewPager mViewPager;
    private TabLayout mTabLayout;
    private FavoritePagerAdapter favoritePagerAdapter;
    private EventBus mEventBus;
    private FrameLayout edit_fl;
    private FrameLayout complete_fl;
    private FrameLayout back_fl;
    private static final String ARG_KEY_MODE = "arg_key_mode";
    public static String mode = "normal";

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(OmniEvent event) {
        switch (event.getType()) {
            case OmniEvent.TYPE_FAVORITE_CHANGED:
                updateFavoriteList();
                break;
        }
    }

    public static FavoriteFragment newInstance(String mode) {
        FavoriteFragment fragment = new FavoriteFragment();

        Bundle arg = new Bundle();
        arg.putString(ARG_KEY_MODE, mode);
        fragment.setArguments(arg);

        return fragment;
    }

    private void updateFavoriteList() {
        Y5CityAPI.getInstance().getFavorite(getActivity(), UserInfoManager.Companion.getInstance().getUserLoginToken(getActivity()),
                new NetworkManager.NetworkManagerListener<FavoriteData>() {
                    @Override
                    public void onSucceed(FavoriteData favoriteData) {
                        PreferencesTools.getInstance().saveProperty(getActivity(), PreferencesTools.KEY_PREFERENCES_FAVORITE, favoriteData);
                        if (favoritePagerAdapter != null)
                            favoritePagerAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFail(String errorMsg, boolean shouldRetry) {
                    }
                });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mEventBus == null) {
            mEventBus = EventBus.getDefault();
        }
        mEventBus.register(this);

        if (getArguments() != null && getArguments().containsKey(ARG_KEY_MODE)) {
            mode = getArguments().getString(ARG_KEY_MODE);
        } else {
            mode = "normal";
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mEventBus != null) {
            mEventBus.unregister(this);
        }
        editMode = false;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
//        if (mView == null) {
        mView = inflater.inflate(R.layout.fragment_favorite, container, false);
//        mView.setPadding(0, Tools.STATUS_BAR, 0, 0);

        back_fl = mView.findViewById(R.id.fragment_favorite_fl_back);
        back_fl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        mViewPager = mView.findViewById(R.id.fragment_favorite_ovp);

        if (UserInfoManager.Companion.getInstance().isLoggedIn(getActivity())) {
            favoritePagerAdapter = new FavoritePagerAdapter(getActivity().getSupportFragmentManager(), getActivity());
            mViewPager.setAdapter(favoritePagerAdapter);
            mViewPager.setCurrentItem(0);

            mTabLayout = mView.findViewById(R.id.fragment_favorite_tl);
            mTabLayout.setupWithViewPager(mViewPager, true);

            edit_fl = mView.findViewById(R.id.fragment_favorite_fl_edit);
            edit_fl.setVisibility(editMode ? View.GONE : View.VISIBLE);
            if (mode.equals("addPoint")) {
                back_fl.setVisibility(View.VISIBLE);
                edit_fl.setVisibility(View.GONE);
                editMode = false;
                favoritePagerAdapter.notifyDataSetChanged();
            }
            complete_fl = mView.findViewById(R.id.fragment_favorite_fl_complete);
            complete_fl.setVisibility(editMode ? View.VISIBLE : View.GONE);
            edit_fl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editMode = !editMode;
                    edit_fl.setVisibility(View.GONE);
                    complete_fl.setVisibility(View.VISIBLE);
                    favoritePagerAdapter.notifyDataSetChanged();
                }
            });
            complete_fl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editMode = !editMode;
                    edit_fl.setVisibility(View.VISIBLE);
                    complete_fl.setVisibility(View.GONE);
                    favoritePagerAdapter.notifyDataSetChanged();
                }
            });
        }
//        }

        return mView;
    }

    private void openFragmentPage(Fragment fragment, String tag) {
        AnimationFragmentManager.getInstance().addFragmentPage(getActivity(),
                R.id.activity_main_fl, fragment, tag);
    }

}
