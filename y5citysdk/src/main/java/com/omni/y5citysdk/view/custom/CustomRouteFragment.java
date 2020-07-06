package com.omni.y5citysdk.view.custom;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.widget.NestedScrollView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.toolbox.NetworkImageView;
import com.jmedeisis.draglinearlayout.DragLinearLayout;
import com.omni.y5citysdk.R;
import com.omni.y5citysdk.manager.UserInfoManager;
import com.omni.y5citysdk.module.OmniEvent;
import com.omni.y5citysdk.module.favorite.FavoriteInfo;
import com.omni.y5citysdk.module.trip.TripInfoData;
import com.omni.y5citysdk.module.trip.TripInfoFeedback;
import com.omni.y5citysdk.module.trip.UserTripCopyData;
import com.omni.y5citysdk.module.trip.UserTripData;
import com.omni.y5citysdk.module.trip.UserTripInfoFeedback;
import com.omni.y5citysdk.module.trip.UserTripPoint;
import com.omni.y5citysdk.module.trip.UserTripPointOnce;
import com.omni.y5citysdk.module.trip.UserTripUpdateData;
import com.omni.y5citysdk.network.NetworkManager;
import com.omni.y5citysdk.network.Y5CityAPI;
import com.omni.y5citysdk.tool.SetTimePickerDialog;
import com.omni.y5citysdk.view.favorite.FavoriteFragment;
import com.omni.y5citysdk.view.items.OmniClusterItem;
import com.omni.y5citysdk.view.navi.InfoMapFragment;
import com.omni.y5citysdk.view.point.PointMapFragment;
import com.omni.y5citysdk.view.theme.ReligionInfoFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.omni.y5citysdk.tool.Y5CityText.LOG_TAG;

public class CustomRouteFragment extends Fragment {

    public static final String TAG = "fragment_tag_theme_route";
    private static final String ARG_KEY_TITLE = "arg_key_title";
    private static final String ARG_KEY_DATE = "arg_key_date";
    private static final String ARG_KEY_TIME = "arg_key_time";
    private static final String ARG_KEY_TID = "arg_key_tid";
    private static final String ARG_KEY_TYPE = "arg_key_type";
    private static final String ARG_KEY_USER_TRIP_INFO_DATA = "arg_key_user_trip_info_data";
    private static final String ARG_KEY_BANNER = "arg_key_banner";
    int RC_OPEN_GALLERY = 9002;

    private View mView;
    private String title;
    private String type;
    private String date;
    private String time;
    private String banner;
    private TextView startTime_tv;
    private TextView startTimeAP_tv;
    private String am_pm = "AM";
    private int hour_of_12_hour_format = 9;
    private int hour_of_24_hour_format = 9;
    private int minute_tp = 0;
    private String am_pm_d = "AM";
    private int hour_d = 9;
    private int minute_d = 0;
    private int st_hour = 0;
    private TripInfoData[] tripInfoData;
    private UserTripCopyData[] userTripCopyData;
    private TripInfoFeedback[] tripInfoFeedback;
    private ImageView bannerEdit;
    private ImageView titleEdit;
    private ImageView dateEdit;
    private TextView day_one_btn;
    private TextView day_two_btn;
    private TextView day_three_btn;
    private ImageView addDay;
    private ImageView deleteOne;
    private ImageView deleteTwo;
    private ImageView deleteThree;
    private FrameLayout edit_fl;
    private TextView copy_tv;
    private FrameLayout complete_fl;
    private LinearLayout addPoint_ll;
    private LinearLayout addCover_ll;
    private ImageView addPoint_bg;
    private TextView deletePoint_tv;
    private boolean editMode = false;
    private String title_edit;
    private String date_edit;
    private LinearLayout startTime_ll;
    private NestedScrollView contentNSV;
    private DragLinearLayout contentLL;
    private List<UserTripPoint> mUserTripPointList;
    private List<UserTripPointOnce> mUserTripPointOnceList;
    private int tripDay = 0;
    private int tripTotalDay = 1;
    private EventBus mEventBus;
    private boolean[] isSelect;
    private ViewGroup container;
    private String t_id;
    private NetworkImageView banner_IV;
    private int traffic_pos = 0;
    private String[] start_time_array = new String[3];
    private String str;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(OmniEvent event) {
        switch (event.getType()) {
            case OmniEvent.TYPE_ADD_POINT_FAVORITE:
                addPoint_bg.setVisibility(View.GONE);
                addPoint_ll.setVisibility(View.GONE);

                FavoriteInfo favoriteInfo = (FavoriteInfo) event.getObj();
                TripInfoData addTripInfoData = new TripInfoData();
                addTripInfoData.setT_id(t_id);
                addTripInfoData.setP_id(String.valueOf(favoriteInfo.getP_id()));
                addTripInfoData.setTr_title(favoriteInfo.getname());
                addTripInfoData.setV_name(favoriteInfo.getname());
                addTripInfoData.setA_name(favoriteInfo.getArea());
                addTripInfoData.setV_image(favoriteInfo.getImage());
                addTripInfoData.setV_lat(favoriteInfo.getlat());
                addTripInfoData.setV_lng(favoriteInfo.getlng());
                addTripInfoData.setTr_stay("30");
                if (tripInfoFeedback.length == 0) {
                    List<TripInfoFeedback> addTripDaysList = new ArrayList<>();
                    addTripDaysList.add(new TripInfoFeedback());
                    tripInfoFeedback = addTripDaysList.toArray((new TripInfoFeedback[addTripDaysList.size()]));
                }
                addTripInfoData.setOrder(tripInfoFeedback[tripDay].getTrip_info().length);
                if (tripInfoFeedback[tripDay].getTrip_info()[0].getT_id() == null) {
                    addTripInfoData.setOrder(0);
                    tripInfoFeedback[tripDay].getTrip_info()[0] = addTripInfoData;
                } else {
                    TripInfoData[] addTripInfoList = new TripInfoData[tripInfoFeedback[tripDay].getTrip_info().length + 1];
                    for (int i = 0; i < tripInfoFeedback[tripDay].getTrip_info().length; i++) {
                        tripInfoFeedback[tripDay].getTrip_info()[i].setOrder(i);
                        addTripInfoList[i] = tripInfoFeedback[tripDay].getTrip_info()[i];
                    }
                    addTripInfoList[tripInfoFeedback[tripDay].getTrip_info().length] = addTripInfoData;
                    tripInfoFeedback[tripDay].setTrip_info(addTripInfoList);
                }
                updateEditPointListView();
                break;

            case OmniEvent.TYPE_ADD_POINT_MAP:
                addPoint_bg.setVisibility(View.GONE);
                addPoint_ll.setVisibility(View.GONE);

                OmniClusterItem omniClusterItem = (OmniClusterItem) event.getObj();
                TripInfoData addTripInfoData_map = new TripInfoData();
                addTripInfoData_map.setT_id(t_id);
                addTripInfoData_map.setP_id(String.valueOf(omniClusterItem.getPOIPoint().getP_id()));
                addTripInfoData_map.setTr_title(omniClusterItem.getTitle());
                addTripInfoData_map.setV_name(omniClusterItem.getTitle());
                addTripInfoData_map.setA_name(omniClusterItem.getArea());
                addTripInfoData_map.setV_image(omniClusterItem.getIconUrl());
                addTripInfoData_map.setV_lat(String.valueOf(omniClusterItem.getPosition().latitude));
                addTripInfoData_map.setV_lng(String.valueOf(omniClusterItem.getPosition().longitude));
                addTripInfoData_map.setTr_stay("30");
                if (tripInfoFeedback.length == 0) {
                    List<TripInfoFeedback> addTripDaysList = new ArrayList<>();
                    addTripDaysList.add(new TripInfoFeedback());
                    tripInfoFeedback = addTripDaysList.toArray((new TripInfoFeedback[addTripDaysList.size()]));
                }
                addTripInfoData_map.setOrder(tripInfoFeedback[tripDay].getTrip_info().length);
                if (tripInfoFeedback[tripDay].getTrip_info()[0].getT_id() == null) {
                    addTripInfoData_map.setOrder(0);
                    tripInfoFeedback[tripDay].getTrip_info()[0] = addTripInfoData_map;
                } else {
                    TripInfoData[] addTripInfoList = new TripInfoData[tripInfoFeedback[tripDay].getTrip_info().length + 1];
                    for (int i = 0; i < tripInfoFeedback[tripDay].getTrip_info().length; i++) {
                        tripInfoFeedback[tripDay].getTrip_info()[i].setOrder(i);
                        addTripInfoList[i] = tripInfoFeedback[tripDay].getTrip_info()[i];
                    }
                    addTripInfoList[tripInfoFeedback[tripDay].getTrip_info().length] = addTripInfoData_map;
                    tripInfoFeedback[tripDay].setTrip_info(addTripInfoList);
                }
                updateEditPointListView();
                break;

            case OmniEvent.TYPE_ADD_COVER_DEFAULT:
                addPoint_bg.setVisibility(View.GONE);
                addCover_ll.setVisibility(View.GONE);
                int c_id = (int) event.getObj();
                Log.e(LOG_TAG, "c_id" + c_id);
                Y5CityAPI.getInstance().userTripCover(getActivity(), UserInfoManager.Companion.getInstance().getUserLoginToken(getActivity()),
                        t_id, "", String.valueOf(c_id), new NetworkManager.NetworkManagerListener<UserTripData[]>() {
                            @Override
                            public void onSucceed(UserTripData[] userTripData) {
                                for (UserTripData data : userTripData) {
                                    if (data.getT_id().equals(t_id)) {
                                        NetworkManager.getInstance().setNetworkImage(getContext(),
                                                banner_IV, data.getT_image());
                                        EventBus.getDefault().post(new OmniEvent(OmniEvent.TYPE_USER_TRIP_UPDATE, ""));
                                    }
                                }
                            }

                            @Override
                            public void onFail(String errorMsg, boolean shouldRetry) {

                            }
                        });
                break;

            case OmniEvent.TYPE_TRAFFIC_TYPE_TIME_UPDATE:
                String content = event.getContent();
                String[] traffic = content.split(",");
                tripInfoFeedback[tripDay].getTrip_info()[traffic_pos].setTr_traffic(traffic[0]);
                tripInfoFeedback[tripDay].getTrip_info()[traffic_pos].setTr_traffic_time(traffic[1]);
                Y5CityAPI.getInstance().userTripPointOnce(getActivity(), UserInfoManager.Companion.getInstance().getUserLoginToken(getActivity()),
                        t_id, getTripPonitOnceJson(), new NetworkManager.NetworkManagerListener<UserTripInfoFeedback>() {
                            @Override
                            public void onSucceed(UserTripInfoFeedback feedback) {
                                updatePointListView();
                            }

                            @Override
                            public void onFail(String errorMsg, boolean shouldRetry) {

                            }
                        });
                break;
        }
    }

    public static CustomRouteFragment newInstance(String title, String date, String banner, String time,
                                                  String tId, String type, TripInfoFeedback[] tripInfoFeedback) {
        CustomRouteFragment fragment = new CustomRouteFragment();

        Bundle arg = new Bundle();
        arg.putString(ARG_KEY_TITLE, title);
        arg.putString(ARG_KEY_DATE, date);
        arg.putString(ARG_KEY_BANNER, banner);
        arg.putString(ARG_KEY_TIME, time);
        arg.putString(ARG_KEY_TID, tId);
        arg.putString(ARG_KEY_TYPE, type);
        arg.putSerializable(ARG_KEY_USER_TRIP_INFO_DATA, tripInfoFeedback);
        fragment.setArguments(arg);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mEventBus == null) {
            mEventBus = EventBus.getDefault();
        }
        mEventBus.register(this);

        title = getArguments().getString(ARG_KEY_TITLE);
        date = getArguments().getString(ARG_KEY_DATE);
        banner = getArguments().getString(ARG_KEY_BANNER);
        type = getArguments().getString(ARG_KEY_TYPE);

        title_edit = title;
        date_edit = date;

        if (getArguments().containsKey(ARG_KEY_USER_TRIP_INFO_DATA)) {
            tripInfoFeedback = (TripInfoFeedback[]) getArguments().getSerializable(ARG_KEY_USER_TRIP_INFO_DATA);
            time = getArguments().getString(ARG_KEY_TIME);
            tripTotalDay = tripInfoFeedback.length;
            if (tripTotalDay == 0)
                tripTotalDay = 1;
            t_id = getArguments().getString(ARG_KEY_TID);
        }

        for (int i = 0; i < tripInfoFeedback.length; i++) {
            start_time_array[i] = tripInfoFeedback[i].getStart_time();
            Log.e(LOG_TAG, "tripInfoFeedback[i].getStart_time()" + tripInfoFeedback[i].getStart_time());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mEventBus != null) {
            mEventBus.unregister(this);
        }
    }

    private void updateEditPointListView() {
        isSelect = new boolean[tripInfoFeedback[tripDay].getTrip_info().length];
        Log.e(LOG_TAG, "isSelect" + isSelect.length);
        contentLL.removeAllViews();
        for (int i = 0; i < tripInfoFeedback[tripDay].getTrip_info().length; i++) {
            View itemRouteView = LayoutInflater.from(getContext()).inflate(R.layout.item_theme_route_edit, container, false);

            final ImageView select_IV = itemRouteView.findViewById(R.id.item_theme_route_edit_select);
            final int finalI1 = i;
            select_IV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e(LOG_TAG, "currentSelect" + tripInfoFeedback[tripDay].getTrip_info()[finalI1].getOrder());
                    isSelect[tripInfoFeedback[tripDay].getTrip_info()[finalI1].getOrder()] =
                            !isSelect[tripInfoFeedback[tripDay].getTrip_info()[finalI1].getOrder()];
                    select_IV.setImageResource(isSelect[tripInfoFeedback[tripDay].getTrip_info()[finalI1].getOrder()]
                            ? R.mipmap.btn_select : R.mipmap.btn_unselect);
                    for (boolean b : isSelect) {
                        if (b) {
                            deletePoint_tv.setVisibility(View.VISIBLE);
                            break;
                        }
                        deletePoint_tv.setVisibility(View.GONE);
                    }
                }
            });
            NetworkImageView route_IV = itemRouteView.findViewById(R.id.item_theme_route_edit_route_iv);
            if (tripInfoFeedback[tripDay].getTrip_info()[i].getT_id() != null) {
                if (tripInfoFeedback[tripDay].getTrip_info()[i].getV_image().length() != 0) {
                    NetworkManager.getInstance().setNetworkImage(getContext(),
                            route_IV, tripInfoFeedback[tripDay].getTrip_info()[i].getV_image());
                }

                ((TextView) itemRouteView.findViewById(R.id.item_theme_route_edit_route_title)).setText(tripInfoFeedback[tripDay].getTrip_info()[i].getV_name());
                ((TextView) itemRouteView.findViewById(R.id.item_theme_route_edit_route_address)).setText(tripInfoFeedback[tripDay].getTrip_info()[i].getA_name());
                final int finalI = i;
                itemRouteView.findViewById(R.id.item_theme_route_edit_route_ll).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openFragmentPage(ReligionInfoFragment.newInstance(tripInfoFeedback[tripDay].getTrip_info()[finalI].getP_id(),
                                tripInfoFeedback[tripDay].getTrip_info()[finalI].getP_type()), ReligionInfoFragment.TAG);
                    }
                });
                itemRouteView.findViewById(R.id.item_theme_route_edit_route_ll).setClickable(false);
                contentLL.addView(itemRouteView);
            }
        }
        for (int i = 0; i < contentLL.getChildCount(); i++) {
            View child = contentLL.getChildAt(i);
            contentLL.setViewDraggable(child, child);
        }
        final TripInfoData[] tempTripInfoData = new TripInfoData[1];
        contentLL.setOnViewSwapListener(new DragLinearLayout.OnViewSwapListener() {
            @Override
            public void onSwap(View firstView, int firstPosition,
                               View secondView, int secondPosition) {
                tempTripInfoData[0] = tripInfoFeedback[tripDay].getTrip_info()[firstPosition];
                tripInfoFeedback[tripDay].getTrip_info()[firstPosition] = tripInfoFeedback[tripDay].getTrip_info()[secondPosition];
                tripInfoFeedback[tripDay].getTrip_info()[secondPosition] = tempTripInfoData[0];
            }
        });

        View itemRouteAddView = LayoutInflater.from(getContext()).inflate(R.layout.item_theme_route_add, container, false);
        itemRouteAddView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPoint_bg.setVisibility(View.VISIBLE);
                addPoint_ll.setVisibility(View.VISIBLE);
            }
        });
        contentLL.addView(itemRouteAddView);
    }

    private void updatePointListView() {
        deletePoint_tv.setVisibility(View.GONE);
        contentLL.removeAllViews();

        if (tripInfoFeedback.length != 0) {
            for (int day = 0; day < tripTotalDay; day++) {
                for (int i = 0; i < tripInfoFeedback[day].getTrip_info().length; i++) {
                    tripInfoFeedback[day].getTrip_info()[i].setOrder(i);
                }
            }

            String[] time_split = time.split(":");
            String AMPM = "AM";
            int hour_of_12_hour_format, minute_tp, st_hour;
            if (Integer.parseInt(time_split[0]) > 11) {
                AMPM = "PM";
            }

            if (Integer.parseInt(time_split[0]) > 12) {
                hour_of_12_hour_format = Integer.parseInt(time_split[0]) - 12;
            } else {
                hour_of_12_hour_format = Integer.parseInt(time_split[0]);
            }
            minute_tp = Integer.parseInt(time_split[1]);

            for (int i = 0; i < tripInfoFeedback[tripDay].getTrip_info().length; i++) {
                final View itemRouteView;
                if (type.equals("master")) {
                    itemRouteView = LayoutInflater.from(getContext()).inflate(R.layout.item_theme_route, container, false);
                } else {
                    itemRouteView = LayoutInflater.from(getContext()).inflate(R.layout.item_theme_route_custom, container, false);
                }

                ((TextView) itemRouteView.findViewById(R.id.item_theme_route_num)).setText(String.valueOf(i + 1));

                NetworkImageView route_IV = itemRouteView.findViewById(R.id.item_theme_route_route_iv);
                if (tripInfoFeedback[tripDay].getTrip_info()[i].getV_image().length() != 0) {
                    NetworkManager.getInstance().setNetworkImage(getContext(),
                            route_IV, tripInfoFeedback[tripDay].getTrip_info()[i].getV_image());
                }

                ((TextView) itemRouteView.findViewById(R.id.item_theme_route_route_title)).setText(tripInfoFeedback[tripDay].getTrip_info()[i].getV_name());
                ((TextView) itemRouteView.findViewById(R.id.item_theme_route_route_address)).setText(tripInfoFeedback[tripDay].getTrip_info()[i].getA_name());
                if (Integer.parseInt(tripInfoFeedback[tripDay].getTrip_info()[i].getTr_stay()) < 60) {
                    ((TextView) itemRouteView.findViewById(R.id.item_theme_route_route_time)).setText(tripInfoFeedback[tripDay].getTrip_info()[i].getTr_stay() + getString(R.string.fragment_info_map_minute));
                } else {
                    ((TextView) itemRouteView.findViewById(R.id.item_theme_route_route_time)).setText(
                            String.valueOf(Integer.parseInt(tripInfoFeedback[tripDay].getTrip_info()[i].getTr_stay()) / 60) + getString(R.string.fragment_info_map_hour) +
                                    String.valueOf(Integer.parseInt(tripInfoFeedback[tripDay].getTrip_info()[i].getTr_stay()) % 60) + getString(R.string.fragment_info_map_minute));
                }
                if (!type.equals("master")) {
                    final int finalI1 = i;
                    itemRouteView.findViewById(R.id.item_theme_route_route_time).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SetTimePickerDialog setTimePickerDialog = new SetTimePickerDialog(getActivity());
                            setTimePickerDialog.setOnDateTimeSetListener(new SetTimePickerDialog.OnDateTimeSetListener() {
                                @Override
                                public void OnDateTimeSet(AlertDialog dialog, int hour, int minute) {
                                    tripInfoFeedback[tripDay].getTrip_info()[finalI1].setTr_stay(String.valueOf(hour * 60 + minute));
                                    Y5CityAPI.getInstance().userTripPointOnce(getActivity(), UserInfoManager.Companion.getInstance().getUserLoginToken(getActivity()),
                                            t_id, getTripPonitOnceJson(), new NetworkManager.NetworkManagerListener<UserTripInfoFeedback>() {
                                                @Override
                                                public void onSucceed(UserTripInfoFeedback feedback) {
                                                    updatePointListView();
                                                }

                                                @Override
                                                public void onFail(String errorMsg, boolean shouldRetry) {

                                                }
                                            });
                                }
                            });
                            setTimePickerDialog.show();
                        }
                    });
                }

                ((TextView) itemRouteView.findViewById(R.id.item_theme_route_st)).setText(
                        hour_of_12_hour_format + ":" + String.format("%02d", minute_tp) + " " + AMPM);

                minute_tp = minute_tp + Integer.parseInt(tripInfoFeedback[tripDay].getTrip_info()[i].getTr_stay());
                st_hour = hour_of_12_hour_format;
                hour_of_12_hour_format = hour_of_12_hour_format + (minute_tp / 60);
                if (hour_of_12_hour_format > 11 && st_hour != hour_of_12_hour_format && st_hour != 12) {
                    switch (AMPM) {
                        case "AM":
                            AMPM = "PM";
                            break;
                        case "PM":
                            AMPM = "AM";
                            break;
                    }
                }
                if (hour_of_12_hour_format > 12) {
                    hour_of_12_hour_format = hour_of_12_hour_format - 12;
                }
                minute_tp = minute_tp % 60;

                ((TextView) itemRouteView.findViewById(R.id.item_theme_route_et)).setText(
                        hour_of_12_hour_format + ":" + String.format("%02d", minute_tp) + " " + AMPM);

                final int finalI = i;
                itemRouteView.findViewById(R.id.item_theme_route_route_ll).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openFragmentPage(ReligionInfoFragment.newInstance(tripInfoFeedback[tripDay].getTrip_info()[finalI].getP_id(),
                                tripInfoFeedback[tripDay].getTrip_info()[finalI].getP_type()), ReligionInfoFragment.TAG);
                    }
                });
                contentLL.addView(itemRouteView);

                if (i != tripInfoFeedback[tripDay].getTrip_info().length - 1) {
                    View itemTrafficView;
                    if (type.equals("master")) {
                        itemTrafficView = LayoutInflater.from(getContext()).inflate(R.layout.item_theme_traffic, container, false);
                    } else {
                        itemTrafficView = LayoutInflater.from(getContext()).inflate(R.layout.item_theme_traffic_custom, container, false);
                        final int finalI2 = i;
                        itemTrafficView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                traffic_pos = finalI2;
                                TripInfoData[] trafficPoints = new TripInfoData[2];
                                trafficPoints[0] = tripInfoFeedback[tripDay].getTrip_info()[finalI2];
                                trafficPoints[1] = tripInfoFeedback[tripDay].getTrip_info()[finalI2 + 1];
                                openFragmentPage(InfoMapFragment.newInstance(getString(R.string.fragment_info_map_title),
                                        trafficPoints, "traffic"), InfoMapFragment.TAG);
                            }
                        });
                        switch (tripInfoFeedback[tripDay].getTrip_info()[finalI2].getTr_traffic()) {
                            case "C":
                                ((ImageView) itemTrafficView.findViewById(R.id.item_theme_traffic_icon)).setImageResource(R.mipmap.icon_car_b);
                                break;
                            case "P":
                                ((ImageView) itemTrafficView.findViewById(R.id.item_theme_traffic_icon)).setImageResource(R.mipmap.icon_mrt_b);
                                break;
                            case "F":
                                ((ImageView) itemTrafficView.findViewById(R.id.item_theme_traffic_icon)).setImageResource(R.mipmap.icon_ship_b);
                                break;
                            case "W":
                                ((ImageView) itemTrafficView.findViewById(R.id.item_theme_traffic_icon)).setImageResource(R.mipmap.icon_walk_b);
                                break;
                        }
                    }
                    if (Integer.parseInt(tripInfoFeedback[tripDay].getTrip_info()[i].getTr_traffic_time()) < 60) {
                        ((TextView) itemTrafficView.findViewById(R.id.item_theme_traffic_time)).setText(tripInfoFeedback[tripDay].getTrip_info()[i].getTr_traffic_time() + getString(R.string.fragment_info_map_minute));
                    } else {
                        ((TextView) itemTrafficView.findViewById(R.id.item_theme_traffic_time)).setText(
                                String.valueOf(Integer.parseInt(tripInfoFeedback[tripDay].getTrip_info()[i].getTr_traffic_time()) / 60) + getString(R.string.fragment_info_map_hour) +
                                        String.valueOf(Integer.parseInt(tripInfoFeedback[tripDay].getTrip_info()[i].getTr_traffic_time()) % 60) + getString(R.string.fragment_info_map_minute));
                    }
                    contentLL.addView(itemTrafficView);

                    minute_tp = minute_tp + Integer.parseInt(tripInfoFeedback[tripDay].getTrip_info()[i].getTr_traffic_time());
                    st_hour = hour_of_12_hour_format;
                    hour_of_12_hour_format = hour_of_12_hour_format + (minute_tp / 60);
                    if (hour_of_12_hour_format > 11 && st_hour != hour_of_12_hour_format && st_hour != 12) {
                        switch (AMPM) {
                            case "AM":
                                AMPM = "PM";
                                break;
                            case "PM":
                                AMPM = "AM";
                                break;
                        }
                    }
                    if (hour_of_12_hour_format > 12) {
                        hour_of_12_hour_format = hour_of_12_hour_format - 12;
                    }
                    minute_tp = minute_tp % 60;
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == RC_OPEN_GALLERY && resultCode == RESULT_OK) {
            if (resultData != null) {
                Uri imageUri = resultData.getData();
                Log.e(LOG_TAG, "imageUri" + imageUri);
                String path = "";
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getActivity().getContentResolver().query(imageUri, filePathColumn, null, null, null);
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                    path = cursor.getString(columnIndex);
                }
                cursor.close();
                Log.e(LOG_TAG, "path" + path);
                Y5CityAPI.getInstance().userTripCover(getActivity(), UserInfoManager.Companion.getInstance().getUserLoginTokenLoginHint(getActivity()),
                        t_id, path, "", new NetworkManager.NetworkManagerListener<UserTripData[]>() {
                            @Override
                            public void onSucceed(UserTripData[] userTripData) {
                                for (UserTripData data : userTripData) {
                                    if (data.getT_id().equals(t_id)) {
                                        NetworkManager.getInstance().setNetworkImage(getContext(),
                                                banner_IV, data.getT_image());
                                        EventBus.getDefault().post(new OmniEvent(OmniEvent.TYPE_USER_TRIP_UPDATE, ""));
                                    }
                                }
                            }

                            @Override
                            public void onFail(String errorMsg, boolean shouldRetry) {

                            }
                        });
                addPoint_bg.setVisibility(View.GONE);
                addCover_ll.setVisibility(View.GONE);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (mView == null) {
            this.container = container;
            mView = inflater.inflate(R.layout.fragment_custom_route, container, false);
//            mView.setPadding(0, Tools.STATUS_BAR, 0, 0);

            mView.findViewById(R.id.fragment_custom_route_fl_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    EventBus.getDefault().post(new OmniEvent(OmniEvent.TYPE_CLICK_HOME, ""));
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });

            ((TextView) mView.findViewById(R.id.fragment_custom_route_tv_action_bar_title)).setText(title);

            banner_IV = mView.findViewById(R.id.fragment_custom_route_banner);
            Log.e(LOG_TAG, "banner" + banner);
            if (banner.length() != 0) {
                NetworkManager.getInstance().setNetworkImage(getContext(),
                        banner_IV, banner);
            }

            addCover_ll = mView.findViewById(R.id.fragment_custom_route_add_cover_ll);
            addCover_ll.findViewById(R.id.fragment_custom_route_default).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openFragmentPage(CoverFragment.newInstance(), CoverFragment.TAG);
                }
            });
            addCover_ll.findViewById(R.id.fragment_custom_route_gallery).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, RC_OPEN_GALLERY);
                }
            });
            addCover_ll.findViewById(R.id.fragment_custom_route_add_cover_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addPoint_bg.setVisibility(View.GONE);
                    addCover_ll.setVisibility(View.GONE);
                }
            });

            bannerEdit = mView.findViewById(R.id.fragment_custom_route_banner_edit_iv);
            bannerEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addPoint_bg.setVisibility(View.VISIBLE);
                    addCover_ll.setVisibility(View.VISIBLE);
                }
            });
            titleEdit = mView.findViewById(R.id.fragment_custom_route_title_edit_iv);
            dateEdit = mView.findViewById(R.id.fragment_custom_route_date_edit_iv);
            edit_fl = mView.findViewById(R.id.fragment_custom_route_fl_edit);
            copy_tv = mView.findViewById(R.id.fragment_custom_route_copy_tv);
            complete_fl = mView.findViewById(R.id.fragment_custom_route_fl_complete);
            addPoint_bg = mView.findViewById(R.id.fragment_custom_route_add_point_bg);
            deletePoint_tv = mView.findViewById(R.id.fragment_custom_route_delete);
            deletePoint_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    List<TripInfoData> deleteTripInfoList = new ArrayList<>();
                    for (int i = 0; i < tripInfoFeedback[tripDay].getTrip_info().length; i++) {
                        if (!isSelect[i]) {
                            deleteTripInfoList.add(tripInfoFeedback[tripDay].getTrip_info()[i]);
                        }
                    }
                    TripInfoData[] deleteTripInfoArray = deleteTripInfoList.toArray((new TripInfoData[deleteTripInfoList.size()]));
                    for (int i = 0; i < deleteTripInfoArray.length; i++) {
                        deleteTripInfoArray[i].setOrder(i);
                    }
                    tripInfoFeedback[tripDay].setTrip_info(deleteTripInfoArray);
                    updateEditPointListView();
                    deletePoint_tv.setVisibility(View.GONE);
                }
            });
            addPoint_ll = mView.findViewById(R.id.fragment_custom_route_add_point_ll);
            addPoint_ll.findViewById(R.id.fragment_custom_route_new).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openFragmentPage(PointMapFragment.newInstance("addPoint"), PointMapFragment.TAG);
                }
            });
            addPoint_ll.findViewById(R.id.fragment_custom_route_from_favorite).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openFragmentPage(FavoriteFragment.newInstance("addPoint"), FavoriteFragment.TAG);
                }
            });
            addPoint_ll.findViewById(R.id.fragment_custom_route_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addPoint_bg.setVisibility(View.GONE);
                    addPoint_ll.setVisibility(View.GONE);
                }
            });

            if (type.equals("master")) {
                edit_fl.setVisibility(View.GONE);
                copy_tv.setVisibility(View.VISIBLE);
            } else {
                edit_fl.setVisibility(View.VISIBLE);
                copy_tv.setVisibility(View.GONE);
            }

            copy_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openFragmentPage(CustomSetupFragment.newInstance(t_id, banner), CustomSetupFragment.TAG);
                }
            });

            edit_fl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if (type.equals("master")) {
//                        openFragmentPage(CustomSetupFragment.newInstance(t_id, banner), CustomSetupFragment.TAG);
//                    } else {
                    editMode = !editMode;
                    edit_fl.setVisibility(View.GONE);
                    complete_fl.setVisibility(View.VISIBLE);
                    bannerEdit.setVisibility(View.VISIBLE);
                    titleEdit.setVisibility(View.VISIBLE);
                    dateEdit.setVisibility(View.VISIBLE);
                    addDay.setVisibility(tripTotalDay < 3 ? View.VISIBLE : View.GONE);
                    deleteOne.setVisibility(View.VISIBLE);
                    deleteTwo.setVisibility(tripTotalDay > 1 ? View.VISIBLE : View.GONE);
                    deleteThree.setVisibility(tripTotalDay > 2 ? View.VISIBLE : View.GONE);
                    startTime_ll.setEnabled(false);
                    startTime_tv.setTextColor(getResources().getColor(R.color.gray_6a));
                    startTimeAP_tv.setTextColor(getResources().getColor(R.color.gray_6a));
                    contentNSV.setScrollbarFadingEnabled(false);
                    contentLL.removeAllViews();

                    if (getArguments().containsKey(ARG_KEY_USER_TRIP_INFO_DATA) && tripInfoFeedback.length > 0) {
                        isSelect = new boolean[tripInfoFeedback[tripDay].getTrip_info().length];
                        for (int i = 0; i < tripInfoFeedback[tripDay].getTrip_info().length; i++) {
                            View itemRouteView = LayoutInflater.from(getContext()).inflate(R.layout.item_theme_route_edit, container, false);

                            final ImageView select_IV = itemRouteView.findViewById(R.id.item_theme_route_edit_select);
                            final int finalI1 = i;
                            select_IV.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.e(LOG_TAG, "currentSelect" + tripInfoFeedback[tripDay].getTrip_info()[finalI1].getOrder());
                                    isSelect[tripInfoFeedback[tripDay].getTrip_info()[finalI1].getOrder()] =
                                            !isSelect[tripInfoFeedback[tripDay].getTrip_info()[finalI1].getOrder()];
                                    select_IV.setImageResource(isSelect[tripInfoFeedback[tripDay].getTrip_info()[finalI1].getOrder()]
                                            ? R.mipmap.btn_select : R.mipmap.btn_unselect);
                                    for (boolean b : isSelect) {
                                        if (b) {
                                            deletePoint_tv.setVisibility(View.VISIBLE);
                                            break;
                                        }
                                        deletePoint_tv.setVisibility(View.GONE);
                                    }
                                }
                            });
                            NetworkImageView route_IV = itemRouteView.findViewById(R.id.item_theme_route_edit_route_iv);
                            if (tripInfoFeedback[tripDay].getTrip_info()[i].getV_image() != null &&
                                    tripInfoFeedback[tripDay].getTrip_info()[i].getV_image().length() != 0) {
                                NetworkManager.getInstance().setNetworkImage(getContext(),
                                        route_IV, tripInfoFeedback[tripDay].getTrip_info()[i].getV_image());
                            }

                            ((TextView) itemRouteView.findViewById(R.id.item_theme_route_edit_route_title)).setText(tripInfoFeedback[tripDay].getTrip_info()[i].getV_name());
                            ((TextView) itemRouteView.findViewById(R.id.item_theme_route_edit_route_address)).setText(tripInfoFeedback[tripDay].getTrip_info()[i].getA_name());

                            final int finalI = i;
                            itemRouteView.findViewById(R.id.item_theme_route_edit_route_ll).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    openFragmentPage(ReligionInfoFragment.newInstance(tripInfoFeedback[tripDay].getTrip_info()[finalI].getP_id(),
                                            tripInfoFeedback[tripDay].getTrip_info()[finalI].getP_type()), ReligionInfoFragment.TAG);
                                }
                            });
                            itemRouteView.findViewById(R.id.item_theme_route_edit_route_ll).setClickable(false);
                            contentLL.addView(itemRouteView);
                        }
                        for (int i = 0; i < contentLL.getChildCount(); i++) {
                            View child = contentLL.getChildAt(i);
                            contentLL.setViewDraggable(child, child);
                        }
                        final TripInfoData[] tempTripInfoData = new TripInfoData[1];
                        contentLL.setOnViewSwapListener(new DragLinearLayout.OnViewSwapListener() {
                            @Override
                            public void onSwap(View firstView, int firstPosition,
                                               View secondView, int secondPosition) {
                                tempTripInfoData[0] = tripInfoFeedback[tripDay].getTrip_info()[firstPosition];
                                tripInfoFeedback[tripDay].getTrip_info()[firstPosition] = tripInfoFeedback[tripDay].getTrip_info()[secondPosition];
                                tripInfoFeedback[tripDay].getTrip_info()[secondPosition] = tempTripInfoData[0];
                            }
                        });
                    }
                    View itemRouteAddView = LayoutInflater.from(getContext()).inflate(R.layout.item_theme_route_add, container, false);
                    itemRouteAddView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addPoint_bg.setVisibility(View.VISIBLE);
                            addPoint_ll.setVisibility(View.VISIBLE);
                        }
                    });
                    contentLL.addView(itemRouteAddView);
                }
//                }
            });

            complete_fl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editMode = !editMode;
                    edit_fl.setVisibility(View.VISIBLE);
                    complete_fl.setVisibility(View.GONE);
                    bannerEdit.setVisibility(View.GONE);
                    titleEdit.setVisibility(View.GONE);
                    dateEdit.setVisibility(View.GONE);
                    addDay.setVisibility(View.GONE);
                    deleteOne.setVisibility(View.GONE);
                    deleteTwo.setVisibility(View.GONE);
                    deleteThree.setVisibility(View.GONE);
                    startTime_ll.setEnabled(true);
                    startTime_tv.setTextColor(getResources().getColor(R.color.time_blue));
                    startTimeAP_tv.setTextColor(getResources().getColor(R.color.time_blue));
                    contentNSV.setScrollbarFadingEnabled(true);

                    StringBuilder builder = new StringBuilder();
                    String prefix = "";
                    for (String s : start_time_array) {
                        builder.append(prefix);
                        prefix = ",";
                        builder.append("\"" + s + "\"");
                    }
                    str = builder.toString();

                    Y5CityAPI.getInstance().userTripUpdate(getActivity(), UserInfoManager.Companion.getInstance().getUserLoginToken(getActivity()),
                            t_id, title_edit, date_edit, "[" + str + "]", new NetworkManager.NetworkManagerListener<UserTripUpdateData>() {
                                @Override
                                public void onSucceed(UserTripUpdateData userTripUpdateData) {
                                    EventBus.getDefault().post(new OmniEvent(OmniEvent.TYPE_USER_TRIP_UPDATE, ""));
                                }

                                @Override
                                public void onFail(String errorMsg, boolean shouldRetry) {
                                }
                            });


                    Y5CityAPI.getInstance().userTripPoint(getActivity(), UserInfoManager.Companion.getInstance().getUserLoginToken(getActivity()),
                            t_id, getTripPointJson(), new NetworkManager.NetworkManagerListener<UserTripInfoFeedback>() {
                                @Override
                                public void onSucceed(UserTripInfoFeedback userTripInfoFeedback) {
                                    Log.e(LOG_TAG, "tripInfoFeedback" + tripInfoFeedback.length);
                                    tripInfoFeedback = userTripInfoFeedback.getTrip_days();
                                    switch (userTripInfoFeedback.getTrip_days().length) {
                                        case 0:
                                        case 1:
                                            day_two_btn.setVisibility(View.GONE);
                                            day_three_btn.setVisibility(View.GONE);
                                            break;
                                        case 2:
                                            day_three_btn.setVisibility(View.GONE);
                                            break;
                                    }
                                    if (tripTotalDay > userTripInfoFeedback.getTrip_days().length) {
                                        tripDay = 0;
                                    }
                                    tripTotalDay = userTripInfoFeedback.getTrip_days().length;
                                    if (tripTotalDay == 0)
                                        tripTotalDay = 1;
                                    setBtnColor();
                                    updatePointListView();
                                }

                                @Override
                                public void onFail(String errorMsg, boolean shouldRetry) {
                                    Log.e(LOG_TAG, "tripInfoFeedback errorMsg" + errorMsg);
                                    contentLL.removeAllViews();
                                }
                            });
                }
            });

            titleEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final View item = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_edittext, null);
                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.fragment_custom_setup_name_hint)
                            .setView(item)
                            .setPositiveButton(R.string.dialog_button_confirm_text, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    EditText editText = item.findViewById(R.id.edit_text);
                                    title_edit = editText.getText().toString();
                                    if (!TextUtils.isEmpty(title)) {
                                        ((TextView) mView.findViewById(R.id.fragment_custom_route_title)).setText(title_edit);
                                        ((TextView) mView.findViewById(R.id.fragment_custom_route_tv_action_bar_title)).setText(title_edit);
                                    }
                                }
                            })
                            .setNegativeButton(R.string.dialog_button_cancel_text, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                }
            });

            dateEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar m_Calendar = Calendar.getInstance();
                    int year = m_Calendar.get(Calendar.YEAR);
                    int month = m_Calendar.get(Calendar.MONTH);
                    int day = m_Calendar.get(Calendar.DAY_OF_MONTH);
                    new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            String month_s = String.valueOf(month + 1);
                            String day_s = String.valueOf(day);
                            if (month + 1 < 10) {
                                month_s = "0" + (month + 1);
                            }
                            if (day < 10) {
                                day_s = "0" + day;
                            }
                            date_edit = year + "-" + month_s + "-" + day_s;
                            ((TextView) mView.findViewById(R.id.fragment_custom_route_date)).setText(date_edit);
                        }
                    }, year, month, day).show();
                }
            });

            ((TextView) mView.findViewById(R.id.fragment_custom_route_title)).setText(title);

            startTime_tv = mView.findViewById(R.id.fragment_custom_route_start_time);
            startTimeAP_tv = mView.findViewById(R.id.fragment_custom_route_start_time_ap);

            if (getArguments().containsKey(ARG_KEY_USER_TRIP_INFO_DATA)) {
                String[] time_split;
                if (tripInfoFeedback.length > 0 && tripInfoFeedback[0].getStart_time() != null) {
                    time_split = tripInfoFeedback[0].getStart_time().split(":");
                } else {
                    time_split = "09:00".split(":");
                }
                if (Integer.parseInt(time_split[0]) > 11) {
                    am_pm = "PM";
                }

                if (Integer.parseInt(time_split[0]) > 12) {
                    hour_of_12_hour_format = Integer.parseInt(time_split[0]) - 12;
                } else {
                    hour_of_12_hour_format = Integer.parseInt(time_split[0]);
                }
                minute_tp = Integer.parseInt(time_split[1]);
                startTime_tv.setText(hour_of_12_hour_format + ":" + String.format("%02d", minute_tp));
                startTimeAP_tv.setText(am_pm);
            }

            startTime_ll = mView.findViewById(R.id.fragment_custom_route_start_time_ll);
            startTime_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Calendar c = Calendar.getInstance();
                    final int hour = c.get(Calendar.HOUR);
                    int minute = c.get(Calendar.MINUTE);
                    new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                            if (getArguments().containsKey(ARG_KEY_USER_TRIP_INFO_DATA)) {
                                start_time_array[tripDay] = hourOfDay + ":" + String.format("%02d", minute);
                                StringBuilder builder = new StringBuilder();
                                String prefix = "";
                                for (String s : start_time_array) {
                                    builder.append(prefix);
                                    prefix = ",";
                                    builder.append("\"" + s + "\"");
                                }
                                str = builder.toString();
                                Log.e(LOG_TAG, "start_time_array.toString()" + str);
                                Y5CityAPI.getInstance().userTripUpdate(getActivity(), UserInfoManager.Companion.getInstance().getUserLoginToken(getActivity()),
                                        t_id, title_edit, date_edit, "[" + str + "]", new NetworkManager.NetworkManagerListener<UserTripUpdateData>() {
                                            @Override
                                            public void onSucceed(UserTripUpdateData userTripUpdateData) {
                                            }

                                            @Override
                                            public void onFail(String errorMsg, boolean shouldRetry) {
                                                Log.e(LOG_TAG, "start_time_array errorMsg" + errorMsg);
                                            }
                                        });
                            }

                            hour_of_24_hour_format = hourOfDay;

                            if (hourOfDay > 11) {
                                am_pm = "PM";
                            } else {
                                am_pm = "AM";
                            }

                            if (hourOfDay > 12) {
                                hour_of_12_hour_format = hourOfDay - 12;
                            } else {
                                hour_of_12_hour_format = hourOfDay;
                            }

                            minute_tp = minute;

                            startTime_tv.setText(hour_of_12_hour_format + ":" + String.format("%02d", minute_tp));
                            startTimeAP_tv.setText(am_pm);

                            am_pm_d = am_pm;
                            hour_d = hour_of_12_hour_format;
                            minute_d = minute_tp;

                            Log.e(LOG_TAG, hour_of_12_hour_format + ":" + String.format("%02d", minute_tp) + am_pm);

                            String start_time = String.format("%02d", hour_of_24_hour_format) + ":" + String.format("%02d", minute_tp);
                            time = start_time;

                            Y5CityAPI.getInstance().getUserTripInfo(getActivity(), t_id, new NetworkManager.NetworkManagerListener<UserTripInfoFeedback>() {
                                @Override
                                public void onSucceed(UserTripInfoFeedback userTripInfoFeedback) {
                                    if (userTripInfoFeedback.getTrip_days().length > 0) {
                                        final TripInfoData[] tripInfoData = userTripInfoFeedback.getTrip_days()[tripDay].getTrip_info();
                                        LinearLayout contentLL = mView.findViewById(R.id.fragment_custom_route_ll_contents);
                                        contentLL.removeAllViews();
                                        for (int i = 0; i < tripInfoData.length; i++) {
                                            final View itemRouteView;
                                            if (type.equals("master")) {
                                                itemRouteView = LayoutInflater.from(getContext()).inflate(R.layout.item_theme_route, container, false);
                                            } else {
                                                itemRouteView = LayoutInflater.from(getContext()).inflate(R.layout.item_theme_route_custom, container, false);
                                            }

                                            ((TextView) itemRouteView.findViewById(R.id.item_theme_route_num)).setText(String.valueOf(i + 1));

                                            NetworkImageView route_IV = itemRouteView.findViewById(R.id.item_theme_route_route_iv);
                                            if (tripInfoData[i].getV_image().length() != 0) {
                                                NetworkManager.getInstance().setNetworkImage(getContext(),
                                                        route_IV, tripInfoData[i].getV_image());
                                            }

                                            ((TextView) itemRouteView.findViewById(R.id.item_theme_route_route_title)).setText(tripInfoData[i].getV_name());
                                            ((TextView) itemRouteView.findViewById(R.id.item_theme_route_route_address)).setText(tripInfoData[i].getA_name());
                                            if (Integer.parseInt(tripInfoData[i].getTr_stay()) < 60) {
                                                ((TextView) itemRouteView.findViewById(R.id.item_theme_route_route_time)).setText(tripInfoData[i].getTr_stay() + getString(R.string.fragment_info_map_minute));
                                            } else {
                                                ((TextView) itemRouteView.findViewById(R.id.item_theme_route_route_time)).setText(
                                                        String.valueOf(Integer.parseInt(tripInfoData[i].getTr_stay()) / 60) + getString(R.string.fragment_info_map_hour) +
                                                                String.valueOf(Integer.parseInt(tripInfoData[i].getTr_stay()) % 60) + getString(R.string.fragment_info_map_minute));
                                            }
                                            if (!type.equals("master")) {
                                                final int finalI1 = i;
                                                itemRouteView.findViewById(R.id.item_theme_route_route_time).setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        SetTimePickerDialog setTimePickerDialog = new SetTimePickerDialog(getActivity());
                                                        setTimePickerDialog.setOnDateTimeSetListener(new SetTimePickerDialog.OnDateTimeSetListener() {
                                                            @Override
                                                            public void OnDateTimeSet(AlertDialog dialog, int hour, int minute) {
                                                                tripInfoFeedback[tripDay].getTrip_info()[finalI1].setTr_stay(String.valueOf(hour * 60 + minute));
                                                                Y5CityAPI.getInstance().userTripPointOnce(getActivity(), UserInfoManager.Companion.getInstance().getUserLoginToken(getActivity()),
                                                                        t_id, getTripPonitOnceJson(), new NetworkManager.NetworkManagerListener<UserTripInfoFeedback>() {
                                                                            @Override
                                                                            public void onSucceed(UserTripInfoFeedback feedback) {
                                                                                updatePointListView();
                                                                            }

                                                                            @Override
                                                                            public void onFail(String errorMsg, boolean shouldRetry) {

                                                                            }
                                                                        });
                                                            }
                                                        });
                                                        setTimePickerDialog.show();
                                                    }
                                                });
                                            }

                                            ((TextView) itemRouteView.findViewById(R.id.item_theme_route_st)).setText(
                                                    hour_of_12_hour_format + ":" + String.format("%02d", minute_tp) + " " + am_pm);

                                            minute_tp = minute_tp + Integer.parseInt(tripInfoData[i].getTr_stay());
                                            st_hour = hour_of_12_hour_format;
                                            hour_of_12_hour_format = hour_of_12_hour_format + (minute_tp / 60);
                                            if (hour_of_12_hour_format > 11 && st_hour != hour_of_12_hour_format && st_hour != 12) {
                                                switch (am_pm) {
                                                    case "AM":
                                                        am_pm = "PM";
                                                        break;
                                                    case "PM":
                                                        am_pm = "AM";
                                                        break;
                                                }
                                            }
                                            if (hour_of_12_hour_format > 12) {
                                                hour_of_12_hour_format = hour_of_12_hour_format - 12;
                                            }
                                            minute_tp = minute_tp % 60;

                                            ((TextView) itemRouteView.findViewById(R.id.item_theme_route_et)).setText(
                                                    hour_of_12_hour_format + ":" + String.format("%02d", minute_tp) + " " + am_pm);

                                            final int finalI = i;
                                            itemRouteView.findViewById(R.id.item_theme_route_route_ll).setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    openFragmentPage(ReligionInfoFragment.newInstance(tripInfoData[finalI].getP_id(),
                                                            tripInfoData[finalI].getP_type()), ReligionInfoFragment.TAG);
                                                }
                                            });

                                            contentLL.addView(itemRouteView);

                                            if (i != tripInfoData.length - 1) {
                                                View itemTrafficView;
                                                if (type.equals("master")) {
                                                    itemTrafficView = LayoutInflater.from(getContext()).inflate(R.layout.item_theme_traffic, container, false);
                                                } else {
                                                    itemTrafficView = LayoutInflater.from(getContext()).inflate(R.layout.item_theme_traffic_custom, container, false);
                                                    final int finalI2 = i;
                                                    itemTrafficView.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            traffic_pos = finalI2;
                                                            TripInfoData[] trafficPoints = new TripInfoData[2];
                                                            trafficPoints[0] = tripInfoFeedback[tripDay].getTrip_info()[finalI2];
                                                            trafficPoints[1] = tripInfoFeedback[tripDay].getTrip_info()[finalI2 + 1];
                                                            openFragmentPage(InfoMapFragment.newInstance(getString(R.string.fragment_info_map_title),
                                                                    trafficPoints, "traffic"), InfoMapFragment.TAG);
                                                        }
                                                    });
                                                    switch (tripInfoFeedback[tripDay].getTrip_info()[finalI2].getTr_traffic()) {
                                                        case "C":
                                                            ((ImageView) itemTrafficView.findViewById(R.id.item_theme_traffic_icon)).setImageResource(R.mipmap.icon_car_b);
                                                            break;
                                                        case "P":
                                                            ((ImageView) itemTrafficView.findViewById(R.id.item_theme_traffic_icon)).setImageResource(R.mipmap.icon_mrt_b);
                                                            break;
                                                        case "F":
                                                            ((ImageView) itemTrafficView.findViewById(R.id.item_theme_traffic_icon)).setImageResource(R.mipmap.icon_ship_b);
                                                            break;
                                                        case "W":
                                                            ((ImageView) itemTrafficView.findViewById(R.id.item_theme_traffic_icon)).setImageResource(R.mipmap.icon_walk_b);
                                                            break;
                                                    }
                                                }
                                                if (Integer.parseInt(tripInfoData[i].getTr_traffic_time()) < 60) {
                                                    ((TextView) itemTrafficView.findViewById(R.id.item_theme_traffic_time)).setText(tripInfoData[i].getTr_traffic_time() + getString(R.string.fragment_info_map_minute));
                                                } else {
                                                    ((TextView) itemTrafficView.findViewById(R.id.item_theme_traffic_time)).setText(
                                                            String.valueOf(Integer.parseInt(tripInfoData[i].getTr_traffic_time()) / 60) + getString(R.string.fragment_info_map_hour) +
                                                                    String.valueOf(Integer.parseInt(tripInfoData[i].getTr_traffic_time()) % 60) + getString(R.string.fragment_info_map_minute));
                                                }
                                                contentLL.addView(itemTrafficView);

                                                minute_tp = minute_tp + Integer.parseInt(tripInfoData[i].getTr_traffic_time());
                                                st_hour = hour_of_12_hour_format;
                                                hour_of_12_hour_format = hour_of_12_hour_format + (minute_tp / 60);
                                                if (hour_of_12_hour_format > 11 && st_hour != hour_of_12_hour_format && st_hour != 12) {
                                                    switch (am_pm) {
                                                        case "AM":
                                                            am_pm = "PM";
                                                            break;
                                                        case "PM":
                                                            am_pm = "AM";
                                                            break;
                                                    }
                                                }
                                                if (hour_of_12_hour_format > 12) {
                                                    hour_of_12_hour_format = hour_of_12_hour_format - 12;
                                                }
                                                minute_tp = minute_tp % 60;
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onFail(String errorMsg, boolean shouldRetry) {
                                }
                            });
                        }
                    }, hour, minute, false).show();
                }
            });

            ((TextView) mView.findViewById(R.id.fragment_custom_route_date)).setText(date);

            contentNSV = mView.findViewById(R.id.fragment_custom_route_nsv);
            if (type.equals("master")) {
                contentNSV.setPadding(0, 0, 0, 150);
            }

            contentLL = mView.findViewById(R.id.fragment_custom_route_ll_contents);
            if (getArguments().containsKey(ARG_KEY_USER_TRIP_INFO_DATA) && tripInfoFeedback.length > 0 &&
                    tripInfoFeedback[0].getTrip_info()[0].getP_id() != null) {
                for (int day = 0; day < tripTotalDay; day++) {
                    for (int i = 0; i < tripInfoFeedback[day].getTrip_info().length; i++) {
                        tripInfoFeedback[day].getTrip_info()[i].setOrder(i);
                    }
                }

                for (int i = 0; i < tripInfoFeedback[tripDay].getTrip_info().length; i++) {
                    final View itemRouteView;
                    if (type.equals("master")) {
                        itemRouteView = LayoutInflater.from(getContext()).inflate(R.layout.item_theme_route, container, false);
                    } else {
                        itemRouteView = LayoutInflater.from(getContext()).inflate(R.layout.item_theme_route_custom, container, false);
                    }

                    ((TextView) itemRouteView.findViewById(R.id.item_theme_route_num)).setText(String.valueOf(i + 1));

                    NetworkImageView route_IV = itemRouteView.findViewById(R.id.item_theme_route_route_iv);
                    if (tripInfoFeedback[tripDay].getTrip_info()[i].getV_image().length() != 0) {
//                    new DownloadImageTask(route_IV).execute(userTripCopyData[i].getV_image());
                        NetworkManager.getInstance().setNetworkImage(getContext(),
                                route_IV, tripInfoFeedback[tripDay].getTrip_info()[i].getV_image());
                    }

                    ((TextView) itemRouteView.findViewById(R.id.item_theme_route_route_title)).setText(tripInfoFeedback[tripDay].getTrip_info()[i].getV_name());
                    ((TextView) itemRouteView.findViewById(R.id.item_theme_route_route_address)).setText(tripInfoFeedback[tripDay].getTrip_info()[i].getA_name());
                    if (Integer.parseInt(tripInfoFeedback[tripDay].getTrip_info()[i].getTr_stay()) < 60) {
                        ((TextView) itemRouteView.findViewById(R.id.item_theme_route_route_time)).setText(tripInfoFeedback[tripDay].getTrip_info()[i].getTr_stay() + getString(R.string.fragment_info_map_minute));
                    } else {
                        ((TextView) itemRouteView.findViewById(R.id.item_theme_route_route_time)).setText(
                                String.valueOf(Integer.parseInt(tripInfoFeedback[tripDay].getTrip_info()[i].getTr_stay()) / 60) + getString(R.string.fragment_info_map_hour) +
                                        String.valueOf(Integer.parseInt(tripInfoFeedback[tripDay].getTrip_info()[i].getTr_stay()) % 60) + getString(R.string.fragment_info_map_minute));
                    }
                    if (!type.equals("master")) {
                        final int finalI1 = i;
                        itemRouteView.findViewById(R.id.item_theme_route_route_time).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SetTimePickerDialog setTimePickerDialog = new SetTimePickerDialog(getActivity());
                                setTimePickerDialog.setOnDateTimeSetListener(new SetTimePickerDialog.OnDateTimeSetListener() {
                                    @Override
                                    public void OnDateTimeSet(AlertDialog dialog, int hour, int minute) {
                                        tripInfoFeedback[tripDay].getTrip_info()[finalI1].setTr_stay(String.valueOf(hour * 60 + minute));
                                        Y5CityAPI.getInstance().userTripPointOnce(getActivity(), UserInfoManager.Companion.getInstance().getUserLoginToken(getActivity()),
                                                t_id, getTripPonitOnceJson(), new NetworkManager.NetworkManagerListener<UserTripInfoFeedback>() {
                                                    @Override
                                                    public void onSucceed(UserTripInfoFeedback feedback) {
                                                        updatePointListView();
                                                    }

                                                    @Override
                                                    public void onFail(String errorMsg, boolean shouldRetry) {

                                                    }
                                                });
                                    }
                                });
                                setTimePickerDialog.show();
                            }
                        });
                    }

                    ((TextView) itemRouteView.findViewById(R.id.item_theme_route_st)).setText(
                            hour_of_12_hour_format + ":" + String.format("%02d", minute_tp) + " " + am_pm);

                    minute_tp = minute_tp + Integer.parseInt(tripInfoFeedback[tripDay].getTrip_info()[i].getTr_stay());
                    st_hour = hour_of_12_hour_format;
                    hour_of_12_hour_format = hour_of_12_hour_format + (minute_tp / 60);
                    if (hour_of_12_hour_format > 11 && st_hour != hour_of_12_hour_format && st_hour != 12) {
                        switch (am_pm) {
                            case "AM":
                                am_pm = "PM";
                                break;
                            case "PM":
                                am_pm = "AM";
                                break;
                        }
                    }
                    if (hour_of_12_hour_format > 12) {
                        hour_of_12_hour_format = hour_of_12_hour_format - 12;
                    }
                    minute_tp = minute_tp % 60;

                    ((TextView) itemRouteView.findViewById(R.id.item_theme_route_et)).setText(
                            hour_of_12_hour_format + ":" + String.format("%02d", minute_tp) + " " + am_pm);

                    final int finalI = i;
                    itemRouteView.findViewById(R.id.item_theme_route_route_ll).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openFragmentPage(ReligionInfoFragment.newInstance(tripInfoFeedback[tripDay].getTrip_info()[finalI].getP_id(),
                                    tripInfoFeedback[tripDay].getTrip_info()[finalI].getP_type()), ReligionInfoFragment.TAG);
                        }
                    });
                    contentLL.addView(itemRouteView);

                    if (i != tripInfoFeedback[tripDay].getTrip_info().length - 1) {
                        View itemTrafficView;
                        if (type.equals("master")) {
                            itemTrafficView = LayoutInflater.from(getContext()).inflate(R.layout.item_theme_traffic, container, false);
                        } else {
                            itemTrafficView = LayoutInflater.from(getContext()).inflate(R.layout.item_theme_traffic_custom, container, false);
                            final int finalI2 = i;
                            itemTrafficView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    traffic_pos = finalI2;
                                    TripInfoData[] trafficPoints = new TripInfoData[2];
                                    trafficPoints[0] = tripInfoFeedback[tripDay].getTrip_info()[finalI2];
                                    trafficPoints[1] = tripInfoFeedback[tripDay].getTrip_info()[finalI2 + 1];
                                    openFragmentPage(InfoMapFragment.newInstance(getString(R.string.fragment_info_map_title),
                                            trafficPoints, "traffic"), InfoMapFragment.TAG);
                                }
                            });
                            switch (tripInfoFeedback[tripDay].getTrip_info()[finalI2].getTr_traffic()) {
                                case "C":
                                    ((ImageView) itemTrafficView.findViewById(R.id.item_theme_traffic_icon)).setImageResource(R.mipmap.icon_car_b);
                                    break;
                                case "P":
                                    ((ImageView) itemTrafficView.findViewById(R.id.item_theme_traffic_icon)).setImageResource(R.mipmap.icon_mrt_b);
                                    break;
                                case "F":
                                    ((ImageView) itemTrafficView.findViewById(R.id.item_theme_traffic_icon)).setImageResource(R.mipmap.icon_ship_b);
                                    break;
                                case "W":
                                    ((ImageView) itemTrafficView.findViewById(R.id.item_theme_traffic_icon)).setImageResource(R.mipmap.icon_walk_b);
                                    break;
                            }
                        }
                        if (Integer.parseInt(tripInfoFeedback[tripDay].getTrip_info()[i].getTr_traffic_time()) < 60) {
                            ((TextView) itemTrafficView.findViewById(R.id.item_theme_traffic_time)).setText(tripInfoFeedback[tripDay].getTrip_info()[i].getTr_traffic_time() + getString(R.string.fragment_info_map_minute));
                        } else {
                            ((TextView) itemTrafficView.findViewById(R.id.item_theme_traffic_time)).setText(
                                    String.valueOf(Integer.parseInt(tripInfoFeedback[tripDay].getTrip_info()[i].getTr_traffic_time()) / 60) + getString(R.string.fragment_info_map_hour) +
                                            String.valueOf(Integer.parseInt(tripInfoFeedback[tripDay].getTrip_info()[i].getTr_traffic_time()) % 60) + getString(R.string.fragment_info_map_minute));
                        }
                        contentLL.addView(itemTrafficView);

                        minute_tp = minute_tp + Integer.parseInt(tripInfoFeedback[tripDay].getTrip_info()[i].getTr_traffic_time());
                        st_hour = hour_of_12_hour_format;
                        hour_of_12_hour_format = hour_of_12_hour_format + (minute_tp / 60);
                        if (hour_of_12_hour_format > 11 && st_hour != hour_of_12_hour_format && st_hour != 12) {
                            switch (am_pm) {
                                case "AM":
                                    am_pm = "PM";
                                    break;
                                case "PM":
                                    am_pm = "AM";
                                    break;
                            }
                        }
                        if (hour_of_12_hour_format > 12) {
                            hour_of_12_hour_format = hour_of_12_hour_format - 12;
                        }
                        minute_tp = minute_tp % 60;
                    }
                }
            }

            addDay = mView.findViewById(R.id.fragment_custom_route_add_day);
            day_one_btn = mView.findViewById(R.id.fragment_custom_route_one);
            day_two_btn = mView.findViewById(R.id.fragment_custom_route_two);
            day_two_btn.setVisibility(tripTotalDay > 1 ? View.VISIBLE : View.GONE);
            day_three_btn = mView.findViewById(R.id.fragment_custom_route_three);
            day_three_btn.setVisibility(tripTotalDay > 2 ? View.VISIBLE : View.GONE);
            deleteOne = mView.findViewById(R.id.fragment_custom_route_one_delete);
            deleteOne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tripTotalDay > 1) {
                        if (tripDay > 0)
                            tripDay--;
                        List<TripInfoFeedback> deleteTripDaysList = new ArrayList<>();
                        switch (tripTotalDay) {
                            case 2:
                                deleteTripDaysList.add(tripInfoFeedback[1]);
                                tripInfoFeedback = deleteTripDaysList.toArray((new TripInfoFeedback[deleteTripDaysList.size()]));
                                day_two_btn.setVisibility(View.GONE);
                                deleteTwo.setVisibility(View.GONE);
                                setBtnColor();
                                break;
                            case 3:
                                deleteTripDaysList.add(tripInfoFeedback[1]);
                                deleteTripDaysList.add(tripInfoFeedback[2]);
                                tripInfoFeedback = deleteTripDaysList.toArray((new TripInfoFeedback[deleteTripDaysList.size()]));
                                day_three_btn.setVisibility(View.GONE);
                                deleteThree.setVisibility(View.GONE);
                                setBtnColor();
                                break;
                        }
                        tripTotalDay--;
                        updateEditPointListView();
                    }
                }
            });
            deleteTwo = mView.findViewById(R.id.fragment_custom_route_two_delete);
            deleteTwo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tripDay > 0)
                        tripDay--;
                    List<TripInfoFeedback> deleteTripDaysList = new ArrayList<>();
                    switch (tripTotalDay) {
                        case 2:
                            deleteTripDaysList.add(tripInfoFeedback[0]);
                            tripInfoFeedback = deleteTripDaysList.toArray((new TripInfoFeedback[deleteTripDaysList.size()]));
                            day_two_btn.setVisibility(View.GONE);
                            deleteTwo.setVisibility(View.GONE);
                            setBtnColor();
                            break;
                        case 3:
                            deleteTripDaysList.add(tripInfoFeedback[0]);
                            deleteTripDaysList.add(tripInfoFeedback[2]);
                            tripInfoFeedback = deleteTripDaysList.toArray((new TripInfoFeedback[deleteTripDaysList.size()]));
                            day_three_btn.setVisibility(View.GONE);
                            deleteThree.setVisibility(View.GONE);
                            addDay.setVisibility(View.VISIBLE);
                            setBtnColor();
                            break;
                    }
                    tripTotalDay--;
                    updateEditPointListView();
                }
            });
            deleteThree = mView.findViewById(R.id.fragment_custom_route_three_delete);
            deleteThree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tripDay > 0)
                        tripDay--;
                    List<TripInfoFeedback> deleteTripDaysList = new ArrayList<>();
                    deleteTripDaysList.add(tripInfoFeedback[0]);
                    deleteTripDaysList.add(tripInfoFeedback[1]);
                    tripInfoFeedback = deleteTripDaysList.toArray((new TripInfoFeedback[deleteTripDaysList.size()]));
                    day_three_btn.setVisibility(View.GONE);
                    deleteThree.setVisibility(View.GONE);
                    addDay.setVisibility(View.VISIBLE);
                    setBtnColor();
                    tripTotalDay--;
                    updateEditPointListView();
                }
            });

            addDay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tripInfoFeedback.length != 0) {
                        switch (tripTotalDay) {
                            case 1:
                                tripDay = 1;
                                tripTotalDay++;
                                day_two_btn.setVisibility(View.VISIBLE);
                                deleteTwo.setVisibility(View.VISIBLE);
                                setBtnColor();
                                start_time_array[1] = "09:00";
                                break;
                            case 2:
                                tripDay = 2;
                                tripTotalDay++;
                                day_three_btn.setVisibility(View.VISIBLE);
                                deleteThree.setVisibility(View.VISIBLE);
                                addDay.setVisibility(View.GONE);
                                setBtnColor();
                                start_time_array[2] = "09:00";
                                break;
                        }
                        List<TripInfoFeedback> addTripDaysList = new ArrayList<>(Arrays.asList(tripInfoFeedback));
                        addTripDaysList.add(new TripInfoFeedback());
                        tripInfoFeedback = addTripDaysList.toArray((new TripInfoFeedback[addTripDaysList.size()]));
                        updateEditPointListView();
                    }
                }
            });

            day_one_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (start_time_array[0] == null)
                        return;

                    tripDay = 0;
                    setBtnColor();

                    //set default value
                    hour_of_12_hour_format = hour_d;
                    minute_tp = minute_d;
                    am_pm = am_pm_d;

                    if (getArguments().containsKey(ARG_KEY_USER_TRIP_INFO_DATA)) {
                        String[] time_split = start_time_array[0].split(":");
                        if (Integer.parseInt(time_split[0]) > 11) {
                            am_pm = "PM";
                        }

                        if (Integer.parseInt(time_split[0]) > 12) {
                            hour_of_12_hour_format = Integer.parseInt(time_split[0]) - 12;
                        } else {
                            hour_of_12_hour_format = Integer.parseInt(time_split[0]);
                        }
                        minute_tp = Integer.parseInt(time_split[1]);
                        startTime_tv.setText(hour_of_12_hour_format + ":" + String.format("%02d", minute_tp));
                        startTimeAP_tv.setText(am_pm);

                        time = time_split[0] + ":" + time_split[1];
                    }

                    if (editMode)
                        updateEditPointListView();
                    else
                        updatePointListView();
                }
            });

            day_two_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tripDay = 1;
                    setBtnColor();

                    hour_of_12_hour_format = hour_d;
                    minute_tp = minute_d;
                    am_pm = am_pm_d;

                    if (getArguments().containsKey(ARG_KEY_USER_TRIP_INFO_DATA)) {
                        String[] time_split = start_time_array[1].split(":");
                        if (Integer.parseInt(time_split[0]) > 11) {
                            am_pm = "PM";
                        }

                        if (Integer.parseInt(time_split[0]) > 12) {
                            hour_of_12_hour_format = Integer.parseInt(time_split[0]) - 12;
                        } else {
                            hour_of_12_hour_format = Integer.parseInt(time_split[0]);
                        }
                        minute_tp = Integer.parseInt(time_split[1]);
                        startTime_tv.setText(hour_of_12_hour_format + ":" + String.format("%02d", minute_tp));
                        startTimeAP_tv.setText(am_pm);

                        time = time_split[0] + ":" + time_split[1];
                    }

                    if (editMode)
                        updateEditPointListView();
                    else
                        updatePointListView();
                }
            });

            day_three_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tripDay = 2;
                    setBtnColor();

                    hour_of_12_hour_format = hour_d;
                    minute_tp = minute_d;
                    am_pm = am_pm_d;

                    if (getArguments().containsKey(ARG_KEY_USER_TRIP_INFO_DATA)) {
                        String[] time_split = start_time_array[2].split(":");
                        if (Integer.parseInt(time_split[0]) > 11) {
                            am_pm = "PM";
                        }

                        if (Integer.parseInt(time_split[0]) > 12) {
                            hour_of_12_hour_format = Integer.parseInt(time_split[0]) - 12;
                        } else {
                            hour_of_12_hour_format = Integer.parseInt(time_split[0]);
                        }
                        minute_tp = Integer.parseInt(time_split[1]);
                        startTime_tv.setText(hour_of_12_hour_format + ":" + String.format("%02d", minute_tp));
                        startTimeAP_tv.setText(am_pm);

                        time = time_split[0] + ":" + time_split[1];
                    }

                    if (editMode)
                        updateEditPointListView();
                    else
                        updatePointListView();
                }
            });

            mView.findViewById(R.id.fragment_custom_route_fl_map).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int i = 0; i < tripInfoFeedback[tripDay].getTrip_info().length; i++) {
                        tripInfoFeedback[tripDay].getTrip_info()[i].setTr_order(String.valueOf(i));
                    }
                    if (tripInfoFeedback[tripDay].getTrip_info().length > 0) {
                        openFragmentPage(InfoMapFragment.newInstance(title, tripInfoFeedback[tripDay].getTrip_info(), "map"), InfoMapFragment.TAG);
                    }
                }
            });
        }

        return mView;
    }

    private String getTripPointJson() {
        String jsonStr = "";
        StringBuilder jsonTripDays = new StringBuilder();
        for (int i = 0; i < tripTotalDay; i++) {
            if (mUserTripPointList != null) {
                mUserTripPointList.clear();
            }
            if (tripInfoFeedback.length > 0) {
                for (TripInfoData tripInfoData : tripInfoFeedback[i].getTrip_info()) {
                    if (tripInfoData.getT_id() != null) {
                        UserTripPoint userTripPoint = new UserTripPoint.Builder()
                                .setP_id(tripInfoData.getP_id())
                                .setTr_title(tripInfoData.getTr_title())
                                .setTr_stay(Integer.parseInt(tripInfoData.getTr_stay()))
                                .build();
                        if (mUserTripPointList == null) {
                            mUserTripPointList = new ArrayList<>();
                        }
                        mUserTripPointList.add(userTripPoint);
                    }
                }
            }
            jsonStr = NetworkManager.getInstance().getGson().toJson(mUserTripPointList);
            Log.e(LOG_TAG, "jsonStr" + jsonStr);
            if (!jsonStr.equals("null") && !jsonStr.equals("[]")) {
                if (i != tripTotalDay && i != 0 && jsonTripDays.toString().length() != 0) {
                    jsonTripDays.append(",");
                }
                jsonTripDays.append(jsonStr);
            }
        }
        if (jsonTripDays.toString().length() == 0) {
            jsonTripDays.append("[]");
        }

        return jsonTripDays.toString();
    }

    private String getTripPonitOnceJson() {
        String jsonStr = "";
        StringBuilder jsonTripDays = new StringBuilder();
        for (int i = 0; i < tripTotalDay; i++) {
            if (mUserTripPointOnceList != null) {
                mUserTripPointOnceList.clear();
            }
            if (tripInfoFeedback.length > 0) {
                for (TripInfoData tripInfoData : tripInfoFeedback[i].getTrip_info()) {
                    if (tripInfoData.getT_id() != null) {
                        UserTripPointOnce userTripPointOnce = new UserTripPointOnce.Builder()
                                .setP_id(tripInfoData.getP_id())
                                .setTr_title(tripInfoData.getTr_title())
                                .setTr_stay(Integer.parseInt(tripInfoData.getTr_stay()))
                                .setTraffic_tool(tripInfoData.getTr_traffic())
                                .setTraffic_time(Integer.parseInt(tripInfoData.getTr_traffic_time()))
                                .build();
                        if (mUserTripPointOnceList == null) {
                            mUserTripPointOnceList = new ArrayList<>();
                        }
                        mUserTripPointOnceList.add(userTripPointOnce);
                    }
                }
            }
            jsonStr = NetworkManager.getInstance().getGson().toJson(mUserTripPointOnceList);
            Log.e(LOG_TAG, "jsonStr" + jsonStr);
            if (!jsonStr.equals("null") && !jsonStr.equals("[]")) {
                if (i != tripTotalDay && i != 0 && jsonTripDays.toString().length() != 0) {
                    jsonTripDays.append(",");
                }
                jsonTripDays.append(jsonStr);
            }
        }
        if (jsonTripDays.toString().length() == 0) {
            jsonTripDays.append("[]");
        }

        return jsonTripDays.toString();
    }

    private void setBtnColor() {
        day_one_btn.setTextColor(tripDay == 0 ? getResources().getColor(android.R.color.white) : (getResources().getColor(R.color.sdkColorPrimary)));
        day_one_btn.setBackgroundResource(tripDay == 0 ? R.drawable.solid_round_rectangle_gradient_purple : R.drawable.solid_round_rectangle_gradient_purple_stroke);
        day_two_btn.setTextColor(tripDay == 1 ? getResources().getColor(android.R.color.white) : (getResources().getColor(R.color.sdkColorPrimary)));
        day_two_btn.setBackgroundResource(tripDay == 1 ? R.drawable.solid_round_rectangle_gradient_purple : R.drawable.solid_round_rectangle_gradient_purple_stroke);
        day_three_btn.setTextColor(tripDay == 2 ? getResources().getColor(android.R.color.white) : (getResources().getColor(R.color.sdkColorPrimary)));
        day_three_btn.setBackgroundResource(tripDay == 2 ? R.drawable.solid_round_rectangle_gradient_purple : R.drawable.solid_round_rectangle_gradient_purple_stroke);
    }

    private void openFragmentPage(Fragment fragment, String tag) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_main_fl, fragment, tag)
                .addToBackStack(null)
                .commit();
    }
}
