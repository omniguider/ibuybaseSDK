package com.omni.y5citysdk.view.point;

import android.content.Context;
import android.content.res.TypedArray;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.omni.y5citysdk.R;
import com.omni.y5citysdk.manager.UserInfoManager;
import com.omni.y5citysdk.module.OmniEvent;
import com.omni.y5citysdk.module.point.AreaData;
import com.omni.y5citysdk.module.point.PointData;
import com.omni.y5citysdk.module.point.PointInfo;
import com.omni.y5citysdk.module.point.TrafficInfo;
import com.omni.y5citysdk.network.NetworkManager;
import com.omni.y5citysdk.network.Y5CityAPI;
import com.omni.y5citysdk.service.OGService;
import com.omni.y5citysdk.tool.DialogTools;
import com.omni.y5citysdk.tool.Tools;
import com.omni.y5citysdk.tool.Y5CityText;
import com.omni.y5citysdk.view.OmniEditText;
import com.omni.y5citysdk.view.items.NestedListView;
import com.omni.y5citysdk.view.items.OmniClusterItem;
import com.omni.y5citysdk.view.navi.OmniClusterRender;
import com.omni.y5citysdk.view.theme.ReligionInfoFragment;
import com.omni.y5citysdk.view.theme.WebViewFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.omni.y5citysdk.tool.Y5CityText.KS_STATION_LAT;
import static com.omni.y5citysdk.tool.Y5CityText.KS_STATION_LNG;
import static com.omni.y5citysdk.tool.Y5CityText.LOG_TAG;

public class PointMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowCloseListener {

    public static final String TAG = "fragment_tag_point_map";
    private static final String ARG_KEY_TYPE = "arg_key_type";
    private static final String ARG_KEY_TITLE = "arg_key_title";
    private static final String ARG_KEY_LATITUDE = "arg_key_latitude";
    private static final String ARG_KEY_LONGITUDE = "arg_key_longitude";

    private View mView;
    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;
    private boolean mIsMapInited = false;
    private ClusterManager<OmniClusterItem> mClusterManager;
    private OmniClusterRender mOmniClusterRender;
    private List<OmniClusterItem> itemList;

    private Location mLastLocation;
    private PointInfo[] poiData;
    private TextView religion_tv, food_tv, view_tv, shopping_tv, hotel_tv, traffic_tv;
    private PointData mPointData;
    private int current_tab = 1;
    private FrameLayout traffic_fl;

    private RelativeLayout mPOIInfoLayout;
    private NetworkImageView mPOIInfoIconNIV;
    private TextView mPOIInfoTitleTV;
    private TextView mPOIInfoAddressTV;
    private TextView mPOIInfoFavTV;
    private ImageView mPOIInfoFavIV;
    private RelativeLayout mPOIInfoHeaderLayout;
    private FrameLayout mPOIInfoHeaderFrameLayout;
    private ImageView mPOIInfoHeaderArrowIV;
    private BottomSheetBehavior mBottomSheetBehavior;
    private NestedListView mPOIInfoListView;
    private ImageView mPOIInfoAddPointIV;
    private RecyclerView recyclerView;
    private EventBus mEventBus;
    private InfoListAdapter mInfoListAdapter;
    private String type = "main";
    private String title;
    private String lat = KS_STATION_LAT;
    private String lng = KS_STATION_LNG;
    private OmniClusterItem mOmniClusterItem;

    private String a_id = "";
    private String keyword = "";
    private Spinner spinnerArea;
    private OmniEditText searchOET;
    private Marker mUserMarker;
    private Circle mUserAccuracyCircle;
    private ImageView currentPositionIV;
    private FrameLayout spinner_fl;
    private String[] areaList;
    private String[] areaListAll;
    private AreaData[] areaDataRegion;
    private AreaData[] areaDataAll;
    private MySpinnerAdapter ageAdapter;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(OmniEvent event) {
        switch (event.getType()) {
            case OmniEvent.TYPE_FAVORITE_CHANGED:
                updatePointMapList();
                break;

            case OmniEvent.TYPE_USER_LOCATION:
                mLastLocation = (Location) event.getObj();
                showUserPosition();
                break;
        }
    }

    public static PointMapFragment newInstance() {
        PointMapFragment fragment = new PointMapFragment();

        return fragment;
    }

    public static PointMapFragment newInstance(String type) {
        PointMapFragment fragment = new PointMapFragment();

        Bundle arg = new Bundle();
        arg.putString(ARG_KEY_TYPE, type);
        fragment.setArguments(arg);

        return fragment;
    }

    public static PointMapFragment newInstance(String type, String title, float lat, float lng) {
        PointMapFragment fragment = new PointMapFragment();

        Bundle arg = new Bundle();
        arg.putString(ARG_KEY_TYPE, type);
        arg.putString(ARG_KEY_TITLE, title);
        arg.putFloat(ARG_KEY_LATITUDE, lat);
        arg.putFloat(ARG_KEY_LONGITUDE, lng);
        fragment.setArguments(arg);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLastLocation = OGService.mLastLocation;
        if (mEventBus == null) {
            mEventBus = EventBus.getDefault();
        }
        mEventBus.register(this);
        type = getArguments() != null ? getArguments().getString(ARG_KEY_TYPE) : "main";
        title = getArguments() != null ? getArguments().getString(ARG_KEY_TITLE) : getString(R.string.activity_main_btn_around);
        lat = getArguments() != null ? String.valueOf(getArguments().getFloat(ARG_KEY_LATITUDE)) : KS_STATION_LAT;
        lng = getArguments() != null ? String.valueOf(getArguments().getFloat(ARG_KEY_LONGITUDE)) : KS_STATION_LNG;

        if (lat.equals("0.0")) {
            lat = KS_STATION_LAT;
            lng = KS_STATION_LNG;
        }

//        lat = String.valueOf(mLastLocation.getLatitude());
//        lng = String.valueOf(mLastLocation.getLongitude());
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!mIsMapInited) {
            mIsMapInited = true;
            mMapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mEventBus != null) {
            mEventBus.unregister(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_point_map, container, false);
//        mView.setPadding(0, Tools.STATUS_BAR, 0, 0);

        initPOIInfoView();

        if (type.equals("guide")) {
            mView.findViewById(R.id.fragment_point_map_action_bar_back_fl).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.fragment_point_map_action_bar_back_fl).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
            ((TextView) mView.findViewById(R.id.fragment_point_map_action_bar_title)).setText(R.string.activity_main_btn_guide);
            mView.findViewById(R.id.fragment_point_map_tab_ll).setVisibility(View.GONE);
            mView.findViewById(R.id.fragment_point_map_tab_divider).setVisibility(View.GONE);
        } else if (type.equals("addPoint")) {
            ((LinearLayout) mView.findViewById(R.id.fragment_point_map_tab_ll)).setWeightSum((float) 5);
            mView.findViewById(R.id.fragment_point_map_action_bar_back_fl).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.fragment_point_map_action_bar_back_fl).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
            ((TextView) mView.findViewById(R.id.fragment_point_map_action_bar_title)).setText(R.string.fragment_custom_route_new);
            mPOIInfoAddPointIV.setVisibility(View.VISIBLE);
            mPOIInfoFavTV.setVisibility(View.GONE);
            mPOIInfoFavIV.setVisibility(View.GONE);
            mPOIInfoHeaderArrowIV.setVisibility(View.GONE);

            mView.findViewById(R.id.fragment_point_map_action_bar).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.fragment_point_map_tab_divider).setVisibility(View.VISIBLE);
        } else if (type.equals("nearby")) {
            ((LinearLayout) mView.findViewById(R.id.fragment_point_map_tab_ll)).setWeightSum((float) 5);
            mView.findViewById(R.id.fragment_point_map_action_bar_back_fl).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.fragment_point_map_action_bar_back_fl).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
            ((TextView) mView.findViewById(R.id.fragment_point_map_action_bar_title)).setText(title);
            mPOIInfoFavTV.setVisibility(View.GONE);
            mPOIInfoFavIV.setVisibility(View.GONE);
        } else {
            final TypedArray styledAttributes = getContext().getTheme().obtainStyledAttributes(
                    new int[]{android.R.attr.actionBarSize});
            int mActionBarSize = (int) styledAttributes.getDimension(0, 0);
            Tools.setMargins(mView, 0, 0, 0, mActionBarSize);
        }

        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_point_map_map);

        traffic_fl = mView.findViewById(R.id.fragment_point_map_traffic_fl);
        recyclerView = mView.findViewById(R.id.fragment_point_map_traffic_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        religion_tv = mView.findViewById(R.id.fragment_point_map_tab_religion);
        food_tv = mView.findViewById(R.id.fragment_point_map_tab_food);
        view_tv = mView.findViewById(R.id.fragment_point_map_tab_view);
        shopping_tv = mView.findViewById(R.id.fragment_point_map_tab_shopping);
        hotel_tv = mView.findViewById(R.id.fragment_point_map_tab_hotel);
        traffic_tv = mView.findViewById(R.id.fragment_point_map_tab_traffic);

        currentPositionIV = mView.findViewById(R.id.fragment_point_map_fab_current_position);
        spinner_fl = mView.findViewById(R.id.fragment_point_map_spinner_fl);

        switch (current_tab) {
            case 0:
                religion_tv.setBackgroundColor(getResources().getColor(R.color.sdkColorPrimary));
                religion_tv.setTextColor(getResources().getColor(android.R.color.white));
                break;
            case 1:
                food_tv.setBackgroundColor(getResources().getColor(R.color.sdkColorPrimary));
                food_tv.setTextColor(getResources().getColor(android.R.color.white));
                break;
            case 2:
                view_tv.setBackgroundColor(getResources().getColor(R.color.sdkColorPrimary));
                view_tv.setTextColor(getResources().getColor(android.R.color.white));
                break;
            case 3:
                shopping_tv.setBackgroundColor(getResources().getColor(R.color.sdkColorPrimary));
                shopping_tv.setTextColor(getResources().getColor(android.R.color.white));
                break;
            case 4:
                hotel_tv.setBackgroundColor(getResources().getColor(R.color.sdkColorPrimary));
                hotel_tv.setTextColor(getResources().getColor(android.R.color.white));
                break;
            case 5:
                traffic_tv.setBackgroundColor(getResources().getColor(R.color.sdkColorPrimary));
                traffic_tv.setTextColor(getResources().getColor(android.R.color.white));
                traffic_fl.setVisibility(View.VISIBLE);
                currentPositionIV.setVisibility(View.GONE);
                spinner_fl.setVisibility(View.GONE);
                break;
        }

        religion_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                current_tab = 0;
                setTabColorDefault();
                religion_tv.setBackgroundColor(getResources().getColor(R.color.sdkColorPrimary));
                religion_tv.setTextColor(getResources().getColor(android.R.color.white));
                if (mPointData != null) {
                    poiData = mPointData.getReligion();
                    addPOIMarkers(poiData, "religion");
                    mInfoListAdapter.updateAdapter(poiData);
                    mPOIInfoListView.setAdapter(new InfoListAdapter(getActivity(), poiData));
                    traffic_fl.setVisibility(View.GONE);
                    currentPositionIV.setVisibility(View.VISIBLE);
                    spinner_fl.setVisibility(View.VISIBLE);
                    updateAreaList(areaList, areaDataRegion);
                }
            }
        });

        food_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                current_tab = 1;
                setTabColorDefault();
                food_tv.setBackgroundColor(getResources().getColor(R.color.sdkColorPrimary));
                food_tv.setTextColor(getResources().getColor(android.R.color.white));
                if (mPointData != null) {
                    poiData = mPointData.getFood();
                    addPOIMarkers(poiData, "food");
                    mInfoListAdapter.updateAdapter(poiData);
                    mPOIInfoListView.setAdapter(new InfoListAdapter(getActivity(), poiData));
                    traffic_fl.setVisibility(View.GONE);
                    currentPositionIV.setVisibility(View.VISIBLE);
                    spinner_fl.setVisibility(View.VISIBLE);
                    updateAreaList(areaListAll, areaDataAll);
                }
            }
        });

        view_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                current_tab = 2;
                setTabColorDefault();
                view_tv.setBackgroundColor(getResources().getColor(R.color.sdkColorPrimary));
                view_tv.setTextColor(getResources().getColor(android.R.color.white));
                if (mPointData != null) {
                    poiData = mPointData.getView();
                    addPOIMarkers(poiData, "view");
                    mInfoListAdapter.updateAdapter(poiData);
                    mPOIInfoListView.setAdapter(new InfoListAdapter(getActivity(), poiData));
                    traffic_fl.setVisibility(View.GONE);
                    currentPositionIV.setVisibility(View.VISIBLE);
                    spinner_fl.setVisibility(View.VISIBLE);
                    updateAreaList(areaListAll, areaDataAll);
                }
            }
        });

        shopping_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                current_tab = 3;
                setTabColorDefault();
                shopping_tv.setBackgroundColor(getResources().getColor(R.color.sdkColorPrimary));
                shopping_tv.setTextColor(getResources().getColor(android.R.color.white));
                if (mPointData != null) {
                    poiData = mPointData.getShopping();
                    addPOIMarkers(poiData, "shopping");
                    mInfoListAdapter.updateAdapter(poiData);
                    mPOIInfoListView.setAdapter(new InfoListAdapter(getActivity(), poiData));
                    traffic_fl.setVisibility(View.GONE);
                    currentPositionIV.setVisibility(View.VISIBLE);
                    spinner_fl.setVisibility(View.VISIBLE);
                    updateAreaList(areaListAll, areaDataAll);
                }
            }
        });

        hotel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                current_tab = 4;
                setTabColorDefault();
                hotel_tv.setBackgroundColor(getResources().getColor(R.color.sdkColorPrimary));
                hotel_tv.setTextColor(getResources().getColor(android.R.color.white));
                if (mPointData != null) {
                    poiData = mPointData.getHotel();
                    addPOIMarkers(poiData, "hotel");
                    mInfoListAdapter.updateAdapter(poiData);
                    mPOIInfoListView.setAdapter(new InfoListAdapter(getActivity(), poiData));
                    traffic_fl.setVisibility(View.GONE);
                    currentPositionIV.setVisibility(View.VISIBLE);
                    spinner_fl.setVisibility(View.VISIBLE);
                    updateAreaList(areaListAll, areaDataAll);
                }
            }
        });

        traffic_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                current_tab = 5;
                setTabColorDefault();
                traffic_tv.setBackgroundColor(getResources().getColor(R.color.sdkColorPrimary));
                traffic_tv.setTextColor(getResources().getColor(android.R.color.white));
                if (mPointData != null) {
                    traffic_fl.setVisibility(View.VISIBLE);
                    mBottomSheetBehavior.setPeekHeight(0);
                    currentPositionIV.setVisibility(View.GONE);
                    spinner_fl.setVisibility(View.GONE);
                }
            }
        });


        spinnerArea = mView.findViewById(R.id.fragment_point_map_area_sp);
        Y5CityAPI.getInstance().getArea(getActivity(), "", new NetworkManager.NetworkManagerListener<AreaData[]>() {
            @Override
            public void onSucceed(final AreaData[] areaData) {
                areaDataAll = areaData;
                areaListAll = new String[areaData.length + 1];
                areaListAll[0] = getResources().getString(R.string.fragment_guide_spinner_tip_text);
                for (int i = 1; i < areaData.length + 1; i++) {
                    areaListAll[i] = areaData[i - 1].getTitle();
                }

                a_id = areaDataAll[1].getA_id();
                if (mLastLocation != null) {
                    Y5CityAPI.getInstance().getPoints(getActivity(), "all", a_id, keyword,
                            lat, lng, "300",
//                    String.valueOf(mLastLocation.getLatitude()), String.valueOf(mLastLocation.getLongitude()), "10",
                            UserInfoManager.Companion.getInstance().getUserLoginToken(getActivity()), new NetworkManager.NetworkManagerListener<PointData>() {
                                @Override
                                public void onSucceed(PointData pointData) {
                                    mPointData = pointData;
                                    switch (current_tab) {
                                        case 0:
                                            poiData = pointData.getReligion();
                                            addPOIMarkers(poiData, "religion");
                                            break;
                                        case 1:
                                            poiData = pointData.getFood();
                                            addPOIMarkers(poiData, "food");
                                            break;
                                        case 2:
                                            poiData = pointData.getView();
                                            addPOIMarkers(poiData, "view");
                                            break;
                                        case 3:
                                            poiData = pointData.getShopping();
                                            addPOIMarkers(poiData, "shopping");
                                            break;
                                        case 4:
                                            poiData = pointData.getHotel();
                                            addPOIMarkers(poiData, "hotel");
                                            break;
                                    }

                                    mInfoListAdapter = new InfoListAdapter(getActivity(), poiData);
                                    mPOIInfoListView.setAdapter(mInfoListAdapter);
                                    recyclerView.setAdapter(new TrafficAdapter(getActivity(), pointData.getTraffic()));
                                }

                                @Override
                                public void onFail(String errorMsg, boolean shouldRetry) {
                                }
                            });
                }
            }

            @Override
            public void onFail(String errorMsg, boolean shouldRetry) {

            }
        });

        Y5CityAPI.getInstance().getArea(getActivity(), "religion", new NetworkManager.NetworkManagerListener<AreaData[]>() {
            @Override
            public void onSucceed(final AreaData[] areaData) {
                areaDataRegion = areaData;
                areaList = new String[areaData.length + 1];
                areaList[0] = getResources().getString(R.string.fragment_guide_spinner_tip_text);
                for (int i = 1; i < areaData.length + 1; i++) {
                    areaList[i] = areaData[i - 1].getTitle();
                }
                updateAreaList(areaList, areaDataRegion);
            }

            @Override
            public void onFail(String errorMsg, boolean shouldRetry) {

            }
        });

        searchOET = mView.findViewById(R.id.fragment_point_map_search);
        searchOET.setOmniEditTextListener(new OmniEditText.OmniEditTextListener() {
            @Override
            public void onSoftKeyboardDismiss() {
                setFocusOnFragment();
            }

            @Override
            public void onTouch(MotionEvent event) {

            }
        });

        mView.findViewById(R.id.fragment_point_map_search_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyword = searchOET.getText().toString().trim();
                Tools.getInstance().hideKeyboard(getContext(), mView);
                updatePointMapList();
            }
        });

        currentPositionIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMap != null && mLastLocation != null) {
                    final LatLng current = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    addUserMarker(current, mLastLocation);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current, Y5CityText.MAP_ZOOM_LEVEL));
                        }
                    }, 1000);
                }
            }
        });

        return mView;
    }

    private void updateAreaList(String[] areaList, final AreaData[] mAreaData) {
        ageAdapter = new MySpinnerAdapter(getActivity(), R.layout.item_guide_area_list, areaList);
        spinnerArea.setAdapter(ageAdapter);
        spinnerArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == 0) {
                    a_id = "";
                } else {
                    a_id = mAreaData[position - 1].getA_id();
                }
                updatePointMapList();
                if (position != 0) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (poiData != null && poiData.length > 0) {
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(poiData[0].getlat(), poiData[0].getlng()), Y5CityText.MAP_ZOOM_LEVEL));
                            }
                        }
                    }, 1000);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerArea.setSelection(1);
    }

    private void setFocusOnFragment() {
        mView.setFocusableInTouchMode(true);
        mView.requestFocus();
    }

    private void initPOIInfoView() {

        mPOIInfoLayout = mView.findViewById(R.id.fragment_point_map_poi_info);

        mPOIInfoHeaderLayout = mPOIInfoLayout.findViewById(R.id.poi_info_view_header);
        mPOIInfoHeaderFrameLayout = mPOIInfoLayout.findViewById(R.id.item_poi_header_fl);
        mPOIInfoHeaderArrowIV = mPOIInfoLayout.findViewById(R.id.item_poi_header_iv_arrow);
        mPOIInfoIconNIV = mPOIInfoHeaderLayout.findViewById(R.id.item_poi_header_niv);
        mPOIInfoTitleTV = mPOIInfoLayout.findViewById(R.id.item_poi_header_title);
        mPOIInfoAddressTV = mPOIInfoLayout.findViewById(R.id.item_poi_header_address);
        mPOIInfoFavTV = mPOIInfoLayout.findViewById(R.id.item_poi_header_fav_count);
        mPOIInfoFavIV = mPOIInfoLayout.findViewById(R.id.item_poi_header_fav_iv);
        mPOIInfoListView = mPOIInfoLayout.findViewById(R.id.poi_info_view_lv);
        mPOIInfoAddPointIV = mPOIInfoLayout.findViewById(R.id.item_poi_header_add_point_iv);
        mPOIInfoAddPointIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new OmniEvent(OmniEvent.TYPE_ADD_POINT_MAP, mOmniClusterItem));
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        FrameLayout fl = (FrameLayout) mPOIInfoLayout.getParent();
        mBottomSheetBehavior = BottomSheetBehavior.from(fl);
        mBottomSheetBehavior.setHideable(false);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (type.equals("addPoint") && newState == BottomSheetBehavior.STATE_DRAGGING) {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    mPOIInfoLayout.requestLayout();
//                    mPOIInfoHeaderFrameLayout.setVisibility(View.GONE);
                    mPOIInfoHeaderArrowIV.setRotation(180);
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    mPOIInfoLayout.requestLayout();
//                    mPOIInfoHeaderFrameLayout.setVisibility(View.VISIBLE);
                    mPOIInfoHeaderArrowIV.setRotation(0);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
    }

    @Override
    public void onInfoWindowClose(Marker marker) {
        switch (current_tab) {
            case 0:
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.pin_religion_b));
                break;
            case 1:
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.pin_food_b));
                break;
            case 2:
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.pin_landscape_b));
                break;
            case 3:
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.pin_shopping_b));
                break;
            case 4:
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.pin_hotel_b));
                break;
            default:
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_ping_bg_b));
                break;
        }
        mBottomSheetBehavior.setPeekHeight(0);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e(LOG_TAG, "onMapReady");
        mMap = googleMap;
        mMap.setOnInfoWindowCloseListener(this);

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if (mClusterManager != null) {
                    mClusterManager.cluster();
                }
            }
        });

        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setCompassEnabled(true);

        setupClusterManager();
        if (mLastLocation != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)),
//                    new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()),
                    Y5CityText.MAP_ZOOM_LEVEL));
        }
    }

    private void addPOIMarkers(PointInfo[] pointInfo, String type) {

        DialogTools.getInstance().showProgress(getActivity());
        removePreviousMarkers();

        OmniClusterItem item;
        if (itemList == null) {
            itemList = new ArrayList<>();
        }

        for (PointInfo poi : pointInfo) {
            item = new OmniClusterItem(poi, type);
            itemList.add(item);
        }
        if (mClusterManager != null) {
            mClusterManager.addItems(itemList);
            mClusterManager.cluster();
        }
        DialogTools.getInstance().dismissProgress(getActivity());

        if (mMap != null && mLastLocation != null) {
            final LatLng current = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            addUserMarker(current, mLastLocation);
        }
    }

    private void removePreviousMarkers() {

        if (mClusterManager != null) {
            mClusterManager.clearItems();
            mClusterManager.cluster();
        }

        if (itemList != null) {
            itemList.clear();
        }

        if (mMap != null)
            mMap.clear();
    }

    private void setupClusterManager() {
        if (mClusterManager == null) {
            mClusterManager = new ClusterManager<>(getActivity(), mMap);
        }
        if (mOmniClusterRender == null) {
            mOmniClusterRender = new OmniClusterRender(getActivity(), mMap, mClusterManager);
        }
        mClusterManager.setRenderer(mOmniClusterRender);
        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<OmniClusterItem>() {
            @Override
            public boolean onClusterItemClick(OmniClusterItem omniClusterItem) {
                mOmniClusterItem = omniClusterItem;
                Marker marker = mOmniClusterRender.getMarker(omniClusterItem);
                if (marker.getTag() == null) {
                    marker.setTag(omniClusterItem.getPOIPoint());
                }
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_pin_pick_b));
                showPOIInfo(omniClusterItem.getTitle(), omniClusterItem.getAddress(), omniClusterItem.getIconUrl(),
                        omniClusterItem.getIs_fav(), omniClusterItem.getTotal_fav(), omniClusterItem.getPOIPoint().getP_id());
                return false;
            }
        });
        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<OmniClusterItem>() {
            @Override
            public boolean onClusterClick(Cluster<OmniClusterItem> cluster) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        cluster.getPosition(),
                        (float) Math.floor(mMap.getCameraPosition().zoom + 1)),
                        300,
                        null);
                return true;
            }
        });
        mMap.setOnMarkerClickListener(mClusterManager);
    }

    private void showPOIInfo(String title, String address, String imgUrl, boolean isFav, int totalFav, final int p_id) {
        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(address)) {

            int height = mPOIInfoHeaderLayout.getHeight() + 100;
            mBottomSheetBehavior.setPeekHeight(height);
            mPOIInfoHeaderLayout.requestLayout();
            mPOIInfoHeaderLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openFragmentPage(ReligionInfoFragment.newInstance(
                            String.valueOf(p_id), (current_tab == 0 ? "r" : "e")), ReligionInfoFragment.TAG);
                }
            });

            NetworkManager.getInstance().setNetworkImage(getContext(),
                    mPOIInfoIconNIV, imgUrl);

            mPOIInfoTitleTV.setText(title);
            mPOIInfoAddressTV.setText(address);
            mPOIInfoFavTV.setText(String.valueOf(totalFav));
            mPOIInfoFavIV.setImageResource(isFav ? R.mipmap.btn_favorite_r : R.mipmap.btn_favorite_g);
        }
    }

    private class ViewHolder {
        private TextView title;
        private TextView address;
        private NetworkImageView image;
        private ImageView fav_iv;
        private TextView fav_tv;
    }

    public class InfoListAdapter extends BaseAdapter {
        Context context;
        PointInfo[] pointInfo;
        LayoutInflater inflater;

        public InfoListAdapter(Context context, PointInfo[] pointInfo) {
            this.context = context;
            this.pointInfo = pointInfo;
        }

        public void updateAdapter(PointInfo[] pointInfo) {
            this.pointInfo = pointInfo;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return pointInfo.length;
        }

        @Override
        public Object getItem(int position) {
            return pointInfo[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.item_favorite_list, null);
                holder = new ViewHolder();
                holder.image = convertView.findViewById(R.id.item_favorite_list_iv);
                holder.image.setVisibility(View.GONE);
                holder.title = convertView.findViewById(R.id.item_favorite_list_title);
                holder.address = convertView.findViewById(R.id.item_favorite_list_address);
                convertView.findViewById(R.id.item_favorite_list_fav_ll).setVisibility(View.VISIBLE);
                holder.fav_iv = convertView.findViewById(R.id.item_favorite_list_fav_iv);
                holder.fav_tv = convertView.findViewById(R.id.item_favorite_list_fav_count);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

//            NetworkManager.getInstance().setNetworkImage(context, holder.image, pointInfo[position].getImage());
            holder.title.setText(pointInfo[position].getname());
            holder.address.setText(pointInfo[position].getaddress());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openFragmentPage(ReligionInfoFragment.newInstance(
                            String.valueOf(pointInfo[position].getP_id()), (current_tab == 0 ? "r" : "e")), ReligionInfoFragment.TAG);
                }
            });
            holder.fav_tv.setText(String.valueOf(pointInfo[position].getTotal_fav()));
            holder.fav_iv.setImageResource(pointInfo[position].getIs_fav() ? R.mipmap.btn_favorite_r : R.mipmap.btn_favorite_g);

            return convertView;
        }
    }

    class TrafficAdapter extends RecyclerView.Adapter<PointMapFragment.TrafficAdapter.mViewHolder> {
        private Context context;
        private TrafficInfo[] trafficInfos;

        public TrafficAdapter(Context context, TrafficInfo[] trafficInfos) {
            this.context = context;
            this.trafficInfos = trafficInfos;
        }

        public class mViewHolder extends RecyclerView.ViewHolder {
            public NetworkImageView Pic;
            public TextView title;

            public mViewHolder(View itemView) {
                super(itemView);

                title = itemView.findViewById(R.id.trip_theme_card_view_tv);
                Pic = itemView.findViewById(R.id.trip_theme_card_view_iv);
            }
        }

        @Override
        public PointMapFragment.TrafficAdapter.mViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.trip_theme_card_view, parent, false);
            return new PointMapFragment.TrafficAdapter.mViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(PointMapFragment.TrafficAdapter.mViewHolder holder, final int position) {
            final TrafficInfo trafficInfo = trafficInfos[position];

            if (trafficInfo.getImage() != null) {
                NetworkManager.getInstance().setNetworkImage(getContext(),
                        holder.Pic, trafficInfo.getImage());
            }
            holder.title.setText(trafficInfo.getTitle());

            holder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url;
                    if (mLastLocation != null && trafficInfo.getUrl().contains("religitrav.kcg.gov.tw")) {
                        url = trafficInfo.getUrl() +
                                "?lat=" + mLastLocation.getLatitude() +
                                "&lng=" + mLastLocation.getLongitude();
                    } else {
                        url = trafficInfo.getUrl();
                    }
                    openFragmentPage(WebViewFragment.newInstance(trafficInfo.getTitle(), url), WebViewFragment.TAG);
//                    Intent i = new Intent(Intent.ACTION_VIEW);
//                    i.setData(Uri.parse(url));
//                    startActivity(i);
                }
            });

            holder.Pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url;
                    if (mLastLocation != null && trafficInfo.getUrl().contains("religitrav.kcg.gov.tw")) {
                        url = trafficInfo.getUrl() +
                                "?lat=" + mLastLocation.getLatitude() +
                                "&lng=" + mLastLocation.getLongitude();
                    } else {
                        url = trafficInfo.getUrl();
                    }
                    openFragmentPage(WebViewFragment.newInstance(trafficInfo.getTitle(), url), WebViewFragment.TAG);
//                    Intent i = new Intent(Intent.ACTION_VIEW);
//                    i.setData(Uri.parse(url));
//                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return trafficInfos.length;
        }
    }

    private void addUserMarker(LatLng position, Location location) {
//        Log.e(LOG_TAG, "addUserMarker" + position.latitude + position.longitude);
        if (mMap == null) {
            return;
        }

        if (mUserMarker == null) {
            mUserMarker = mMap.addMarker(new MarkerOptions()
                    .flat(true)
                    .rotation(location.getBearing())
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.location))
                    .anchor(0.5f, 0.5f)
                    .position(position)
                    .zIndex(Y5CityText.MARKER_Z_INDEX));

            mUserAccuracyCircle = mMap.addCircle(new CircleOptions()
                    .center(position)
                    .radius(location.getAccuracy() / 2)
                    .strokeColor(ContextCompat.getColor(getActivity(), R.color.map_circle_stroke_color))
                    .fillColor(ContextCompat.getColor(getActivity(), R.color.map_circle_fill_color))
                    .strokeWidth(5)
                    .zIndex(Y5CityText.MARKER_Z_INDEX));
        } else {
            mUserMarker.remove();
            mUserMarker = mMap.addMarker(new MarkerOptions()
                    .flat(true)
                    .rotation(location.getBearing())
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.location))
                    .anchor(0.5f, 0.5f)
                    .position(position)
                    .zIndex(Y5CityText.MARKER_Z_INDEX));

            mUserAccuracyCircle.remove();
            mUserAccuracyCircle = mMap.addCircle(new CircleOptions()
                    .center(position)
                    .radius(location.getAccuracy() / 2)
                    .strokeColor(ContextCompat.getColor(getActivity(), R.color.map_circle_stroke_color))
                    .fillColor(ContextCompat.getColor(getActivity(), R.color.map_circle_fill_color))
                    .strokeWidth(5)
                    .zIndex(Y5CityText.MARKER_Z_INDEX));
//            mUserMarker.setPosition(position);
//            mUserMarker.setRotation(location.getBearing());
//
//            mUserAccuracyCircle.setCenter(position);
//            mUserAccuracyCircle.setRadius(location.getAccuracy() / 2);
        }
    }

    private void showUserPosition() {
        if (!mIsMapInited) {
            mIsMapInited = true;
            mMapFragment.getMapAsync(this);
        }
        LatLng current = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        addUserMarker(current, mLastLocation);
    }

    private void updatePointMapList() {
        if (mLastLocation != null) {
            Y5CityAPI.getInstance().getPoints(getActivity(), "all", a_id, keyword,
                    lat, lng, "300",
//                    String.valueOf(mLastLocation.getLatitude()), String.valueOf(mLastLocation.getLongitude()), "10",
                    UserInfoManager.Companion.getInstance().getUserLoginToken(getActivity()), new NetworkManager.NetworkManagerListener<PointData>() {
                        @Override
                        public void onSucceed(PointData pointData) {
                            keyword = "";
                            mPointData = pointData;
                            switch (current_tab) {
                                case 0:
                                    poiData = pointData.getReligion();
                                    addPOIMarkers(poiData, "religion");
                                    break;
                                case 1:
                                    poiData = pointData.getFood();
                                    addPOIMarkers(poiData, "food");
                                    break;
                                case 2:
                                    poiData = pointData.getView();
                                    addPOIMarkers(poiData, "view");
                                    break;
                                case 3:
                                    poiData = pointData.getShopping();
                                    addPOIMarkers(poiData, "shopping");
                                    break;
                                case 4:
                                    poiData = pointData.getHotel();
                                    addPOIMarkers(poiData, "hotel");
                                    break;
                            }
                            if (mInfoListAdapter != null) {
                                mInfoListAdapter.updateAdapter(poiData);
                                mPOIInfoListView.setAdapter(mInfoListAdapter);
                            }

                            if (mMap != null && mLastLocation != null) {
                                final LatLng current = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                                addUserMarker(current, mLastLocation);
                            }
                        }

                        @Override
                        public void onFail(String errorMsg, boolean shouldRetry) {
                        }
                    });
        }
    }

    private static class MySpinnerAdapter extends ArrayAdapter<String> {

        private MySpinnerAdapter(Context context, int resource, String[] items) {
            super(context, resource, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getDropDownView(position, convertView, parent);
            return view;
        }
    }

    private void setTabColorDefault() {
        religion_tv.setBackgroundColor(getResources().getColor(R.color.gray_f3));
        religion_tv.setTextColor(getResources().getColor(R.color.gray_a7));
        food_tv.setBackgroundColor(getResources().getColor(R.color.gray_f3));
        food_tv.setTextColor(getResources().getColor(R.color.gray_a7));
        view_tv.setBackgroundColor(getResources().getColor(R.color.gray_f3));
        view_tv.setTextColor(getResources().getColor(R.color.gray_a7));
        shopping_tv.setBackgroundColor(getResources().getColor(R.color.gray_f3));
        shopping_tv.setTextColor(getResources().getColor(R.color.gray_a7));
        hotel_tv.setBackgroundColor(getResources().getColor(R.color.gray_f3));
        hotel_tv.setTextColor(getResources().getColor(R.color.gray_a7));
        traffic_tv.setBackgroundColor(getResources().getColor(R.color.gray_f3));
        traffic_tv.setTextColor(getResources().getColor(R.color.gray_a7));
    }

    private void openFragmentPage(Fragment fragment, String tag) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_main_fl, fragment, tag)
                .addToBackStack(null)
                .commit();
    }
}
