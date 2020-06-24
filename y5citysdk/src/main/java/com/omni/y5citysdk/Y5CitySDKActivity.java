package com.omni.y5citysdk;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.omni.y5citysdk.manager.LocaleManager;
import com.omni.y5citysdk.manager.UserInfoManager;
import com.omni.y5citysdk.module.OmniEvent;
import com.omni.y5citysdk.module.favorite.FavoriteData;
import com.omni.y5citysdk.module.login.CheckUserLoginData;
import com.omni.y5citysdk.module.project.ProjectData;
import com.omni.y5citysdk.network.NetworkManager;
import com.omni.y5citysdk.network.Y5CityAPI;
import com.omni.y5citysdk.service.OGService;
import com.omni.y5citysdk.tool.DialogTools;
import com.omni.y5citysdk.tool.PreferencesTools;
import com.omni.y5citysdk.tool.Tools;
import com.omni.y5citysdk.view.OmniViewPager;
import com.omni.y5citysdk.view.trip.TripPagerAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.omni.y5citysdk.network.NetworkManager.DOMAIN_NAME;
import static com.omni.y5citysdk.tool.Y5CityText.LOG_TAG;

public class Y5CitySDKActivity extends BaseActivity {

    private static final String ARG_KEY_USER_ID = "arg_key_user_id";
    private static final String ARG_KEY_USER_NAME = "arg_key_user_name";
    private static final String ARG_KEY_PROJECT_ID = "arg_key_project_id";

    private String user_id;
    private String user_name;
    private String project_id;

    public static String locale;

    private OGService mOGService;
    private EventBus mEventBus;

    private TextView actionBarTitle;
    private OmniViewPager mViewPager;
    private TabLayout mTabLayout;

    public static String loginToken;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(OmniEvent event) {
        switch (event.getType()) {
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_trip);

        user_id = getIntent().getStringExtra(ARG_KEY_USER_ID);
        user_name = getIntent().getStringExtra(ARG_KEY_USER_NAME);
        project_id = getIntent().getStringExtra(ARG_KEY_PROJECT_ID);

        actionBarTitle = findViewById(R.id.fragment_trip_tv_action_bar_title);

        Y5CityAPI.getInstance().getProject(this, new NetworkManager.NetworkManagerListener<ProjectData[]>() {
            @Override
            public void onSucceed(ProjectData[] projectData) {
                for (ProjectData data : projectData) {
                    if (data.getId().equals(project_id)) {
                        DOMAIN_NAME = "https://www.omniguider.com/" + data.getCode() + "/";
                        Log.e(LOG_TAG, "DOMAIN_NAME" + DOMAIN_NAME);

                        String parameter = "[{\"id\":\"" + user_id + "\",\"name\":\"" + user_name + "\"}]";
                        Y5CityAPI.getInstance().sdkLogin(Y5CitySDKActivity.this, parameter, new NetworkManager.NetworkManagerListener<CheckUserLoginData>() {
                            @Override
                            public void onSucceed(CheckUserLoginData checkUserLoginData) {
                                UserInfoManager.Companion.getInstance().saveUserData(
                                        Y5CitySDKActivity.this, checkUserLoginData);
                                loginToken = checkUserLoginData.getLoginToken();

                                mViewPager = findViewById(R.id.fragment_trip_ovp);
                                mViewPager.setAdapter(new TripPagerAdapter(getSupportFragmentManager(), Y5CitySDKActivity.this));
                                mViewPager.setCurrentItem(0);
                                mViewPager.setOffscreenPageLimit(3);

                                mTabLayout = findViewById(R.id.fragment_trip_tl);
                                mTabLayout.setupWithViewPager(mViewPager, true);
                            }

                            @Override
                            public void onFail(String errorMsg, boolean shouldRetry) {

                            }
                        });

                        actionBarTitle.setText(data.getTitle());

                        Y5CityAPI.getInstance().getFavorite(Y5CitySDKActivity.this, UserInfoManager.Companion.getInstance().getUserLoginToken(Y5CitySDKActivity.this),
                                new NetworkManager.NetworkManagerListener<FavoriteData>() {
                                    @Override
                                    public void onSucceed(FavoriteData favoriteData) {
                                        PreferencesTools.getInstance().saveProperty(Y5CitySDKActivity.this, PreferencesTools.KEY_PREFERENCES_FAVORITE, favoriteData);
                                    }

                                    @Override
                                    public void onFail(String errorMsg, boolean shouldRetry) {
                                    }
                                });
                    }
                }
            }

            @Override
            public void onFail(String errorMsg, boolean shouldRetry) {
            }
        });

        Tools.STATUS_BAR = getStatusBarHeight();
        findViewById(R.id.activity_main_fl).setPadding(0, Tools.STATUS_BAR, 0, 0);

        if (mEventBus == null) {
            mEventBus = EventBus.getDefault();
        }
        mEventBus.register(this);

        checkLocationService();

        locale = LocaleManager.getLocale(getResources()).toString();

        findViewById(R.id.fragment_trip_fl_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void checkLocationService() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            ensurePermissions();
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("如要繼續，請開啟裝置定位功能(需使用Google定位服務)");
            dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }
            });
            dialog.setNegativeButton("不用了，謝謝", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    registerService();
                }
            });
            dialog.show();
        }
    }

    private void ensurePermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CHANGE_WIFI_STATE,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_ADMIN,
                            Manifest.permission.CAMERA,
                            Manifest.permission.CALL_PHONE},
                    99);

        } else {
            registerService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[2] == PackageManager.PERMISSION_GRANTED) {
            PreferencesTools.getInstance().saveProperty(this, PreferencesTools.KEY_PREFERENCES_LOCATION_PERMISSION, "true");
        }
        registerService();

        mViewPager.getAdapter().notifyDataSetChanged();
    }

    private void registerService() {
        if (mOGService == null) {
            mOGService = new OGService(this);
        }
        mOGService.startService(new OGService.GoogleApiClientConnectCallBack() {
            @Override
            public void onGoogleApiClientConnected() {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetworkManager.getInstance().destroyRetrofit();

        if (mOGService != null) {
            mOGService.destroy();
        }

        if (mEventBus != null) {
            mEventBus.unregister(this);
        }
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
