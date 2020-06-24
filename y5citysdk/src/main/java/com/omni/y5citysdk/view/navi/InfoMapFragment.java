package com.omni.y5citysdk.view.navi;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.omni.y5citysdk.R;
import com.omni.y5citysdk.module.OmniEvent;
import com.omni.y5citysdk.module.trip.TripInfoData;
import com.omni.y5citysdk.network.NetworkManager;
import com.omni.y5citysdk.tool.SetTimePickerDialog;
import com.omni.y5citysdk.tool.Tools;
import com.omni.y5citysdk.tool.Y5CityText;
import com.omni.y5citysdk.view.items.OmniClusterItem;
import com.omni.y5citysdk.view.theme.ReligionInfoFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class InfoMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowCloseListener {

    public static final String TAG = "fragment_tag_info_map";
    private static final String ARG_KEY_TITLE = "arg_key_title";
    private static final String ARG_KEY_POI_DATA = "arg_key_poi_data";
    private static final String ARG_KEY_TYPE = "arg_key_type";

    private String title;
    private String type;
    private Bitmap resultImage;
    private View mView;
    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;
    private boolean mIsMapInited = false;
    private ClusterManager<OmniClusterItem> mClusterManager;
    private OmniClusterRender mOmniClusterRender;
    private TripInfoData[] poiData;
    private List<OmniClusterItem> itemList;
    private List<LatLng> pointList = new ArrayList<>();
    private int selectOrder = 0;
    private LinearLayout info_ll;
    private LinearLayout custom_time_ll;
    private TextView edit_tv;
    private boolean isCustomMode = false;
    private TextView time_tv;
    private String traffic_tool;
    private String traffic_time;
    private String custom_tool;
    private String custom_time;
    private ImageView car_iv;
    private ImageView mrt_iv;
    private ImageView ship_iv;
    private ImageView walk_iv;

    public static InfoMapFragment newInstance(String title, TripInfoData[] tripInfoData, String type) {
        InfoMapFragment fragment = new InfoMapFragment();

        Bundle arg = new Bundle();
        arg.putString(ARG_KEY_TITLE, title);
        arg.putSerializable(ARG_KEY_POI_DATA, tripInfoData);
        arg.putString(ARG_KEY_TYPE, type);
        fragment.setArguments(arg);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getArguments().getString(ARG_KEY_TITLE);
        poiData = (TripInfoData[]) getArguments().getSerializable(ARG_KEY_POI_DATA);
        type = getArguments().getString(ARG_KEY_TYPE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mIsMapInited) {
            mIsMapInited = true;
            mMapFragment.getMapAsync(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_info_map, container, false);
//        mView.setPadding(0, Tools.STATUS_BAR, 0, 0);

        ((TextView) mView.findViewById(R.id.fragment_info_map_action_bar_title)).setText(title);

        mView.findViewById(R.id.fragment_info_map_fl_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
//                if (type.equals("traffic")) {
//                    EventBus.getDefault().post(new OmniEvent(OmniEvent.TYPE_TRAFFIC_TYPE_TIME_UPDATE, traffic_tool + "," + traffic_time));
//                }
            }
        });

        if (type.equals("traffic")) {
            mView.findViewById(R.id.fragment_info_map_fl_google).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.fragment_info_map_fl_google).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoGoogleMap(poiData[0].getV_lat(), poiData[0].getV_lng(),
                            poiData[1].getV_lat(), poiData[1].getV_lng());
                }
            });
            mView.findViewById(R.id.fragment_info_map_fl_complete).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.fragment_info_map_fl_complete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new OmniEvent(OmniEvent.TYPE_TRAFFIC_TYPE_TIME_UPDATE, traffic_tool + "," + traffic_time));
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
        }

        info_ll = mView.findViewById(R.id.fragment_info_map_info_ll);
        info_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFragmentPage(ReligionInfoFragment.newInstance(poiData[selectOrder - 1].getP_id(),
                        poiData[selectOrder - 1].getP_type()), ReligionInfoFragment.TAG);
            }
        });

        if (type.equals("traffic")) {
            traffic_tool = poiData[0].getTr_traffic();
            traffic_time = poiData[0].getTr_traffic_time();
            custom_tool = traffic_tool;
            custom_time = traffic_time;

            custom_time_ll = mView.findViewById(R.id.fragment_info_map_custom_time_ll);
            custom_time_ll.setVisibility(View.VISIBLE);
            car_iv = mView.findViewById(R.id.fragment_info_map_car_iv);
            car_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isCustomMode) {
                        car_iv.setImageResource(R.mipmap.btn_car_p);
                        mrt_iv.setImageResource(R.mipmap.btn_mrt_g);
                        ship_iv.setImageResource(R.mipmap.btn_ship_g);
                        walk_iv.setImageResource(R.mipmap.btn_walk_g);
                        custom_tool = "C";
                        traffic_tool = custom_tool;
                    }
                }
            });
            mrt_iv = mView.findViewById(R.id.fragment_info_map_mrt_iv);
            mrt_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isCustomMode) {
                        car_iv.setImageResource(R.mipmap.btn_car_g);
                        mrt_iv.setImageResource(R.mipmap.btn_mrt_p);
                        ship_iv.setImageResource(R.mipmap.btn_ship_g);
                        walk_iv.setImageResource(R.mipmap.btn_walk_g);
                        custom_tool = "P";
                        traffic_tool = custom_tool;
                    }
                }
            });
            ship_iv = mView.findViewById(R.id.fragment_info_map_ship_iv);
            ship_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isCustomMode) {
                        car_iv.setImageResource(R.mipmap.btn_car_g);
                        mrt_iv.setImageResource(R.mipmap.btn_mrt_g);
                        ship_iv.setImageResource(R.mipmap.btn_ship_p);
                        walk_iv.setImageResource(R.mipmap.btn_walk_g);
                        custom_tool = "F";
                        traffic_tool = custom_tool;
                    }
                }
            });
            walk_iv = mView.findViewById(R.id.fragment_info_map_walk_iv);
            walk_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isCustomMode) {
                        car_iv.setImageResource(R.mipmap.btn_car_g);
                        mrt_iv.setImageResource(R.mipmap.btn_mrt_g);
                        ship_iv.setImageResource(R.mipmap.btn_ship_g);
                        walk_iv.setImageResource(R.mipmap.btn_walk_p);
                        custom_tool = "W";
                        traffic_tool = custom_tool;
                    }
                }
            });
            time_tv = mView.findViewById(R.id.fragment_info_map_estimate_tv);
            if (Integer.parseInt(poiData[0].getTr_traffic_time()) < 60) {
                time_tv.setText(getString(R.string.fragment_info_map_estimated_time)
                        + poiData[0].getTr_traffic_time() + getString(R.string.fragment_info_map_minute));
            } else {
                time_tv.setText(getString(R.string.fragment_info_map_estimated_time)
                        + Integer.parseInt(poiData[0].getTr_traffic_time()) / 60 + getString(R.string.fragment_info_map_hour) +
                        Integer.parseInt(poiData[0].getTr_traffic_time()) % 60 + getString(R.string.fragment_info_map_minute));
            }
            edit_tv = mView.findViewById(R.id.fragment_info_map_estimate_edit_tv);
            edit_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SetTimePickerDialog setTimePickerDialog = new SetTimePickerDialog(getActivity());
                    setTimePickerDialog.setOnDateTimeSetListener(new SetTimePickerDialog.OnDateTimeSetListener() {
                        @Override
                        public void OnDateTimeSet(AlertDialog dialog, int hour, int minute) {
                            if (hour > 0) {
                                time_tv.setText(getString(R.string.fragment_info_map_custom_time) + hour + getString(R.string.fragment_info_map_hour) +
                                        minute + getString(R.string.fragment_info_map_minute));
                            } else {
                                time_tv.setText(getString(R.string.fragment_info_map_custom_time) + minute + getString(R.string.fragment_info_map_minute));
                            }
                            custom_time = String.valueOf(hour * 60 + minute);
                            traffic_time = custom_time;
                        }
                    });
                    setTimePickerDialog.show();
                }
            });
            Switch customSwitch = mView.findViewById(R.id.fragment_info_map_switch);
            customSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                    if (isCheck) {
                        edit_tv.setVisibility(View.VISIBLE);
                        isCustomMode = true;
                        if (Integer.parseInt(custom_time) < 60) {
                            time_tv.setText(getString(R.string.fragment_info_map_custom_time) + custom_time + getString(R.string.fragment_info_map_minute));
                        } else {
                            time_tv.setText(getString(R.string.fragment_info_map_custom_time) + Integer.parseInt(custom_time) / 60 + getString(R.string.fragment_info_map_hour) +
                                    Integer.parseInt(custom_time) % 60 + getString(R.string.fragment_info_map_minute));
                        }
                        traffic_tool = custom_tool;
                        traffic_time = custom_time;
                        switch (custom_tool) {
                            case "C":
                                car_iv.setImageResource(R.mipmap.btn_car_p);
                                mrt_iv.setImageResource(R.mipmap.btn_mrt_g);
                                ship_iv.setImageResource(R.mipmap.btn_ship_g);
                                walk_iv.setImageResource(R.mipmap.btn_walk_g);
                                break;
                            case "P":
                                car_iv.setImageResource(R.mipmap.btn_car_g);
                                mrt_iv.setImageResource(R.mipmap.btn_mrt_p);
                                ship_iv.setImageResource(R.mipmap.btn_ship_g);
                                walk_iv.setImageResource(R.mipmap.btn_walk_g);
                                break;
                            case "F":
                                car_iv.setImageResource(R.mipmap.btn_car_g);
                                mrt_iv.setImageResource(R.mipmap.btn_mrt_g);
                                ship_iv.setImageResource(R.mipmap.btn_ship_p);
                                walk_iv.setImageResource(R.mipmap.btn_walk_g);
                                break;
                            case "W":
                                car_iv.setImageResource(R.mipmap.btn_car_g);
                                mrt_iv.setImageResource(R.mipmap.btn_mrt_g);
                                ship_iv.setImageResource(R.mipmap.btn_ship_g);
                                walk_iv.setImageResource(R.mipmap.btn_walk_p);
                                break;
                        }
                    } else {
                        edit_tv.setVisibility(View.GONE);
                        isCustomMode = false;
                        if (Integer.parseInt(poiData[0].getTr_traffic_gtime()) < 60) {
                            time_tv.setText(getString(R.string.fragment_info_map_estimated_time)
                                    + poiData[0].getTr_traffic_gtime() + getString(R.string.fragment_info_map_minute));
                        } else {
                            time_tv.setText(getString(R.string.fragment_info_map_estimated_time)
                                    + Integer.parseInt(poiData[0].getTr_traffic_gtime()) / 60 + getString(R.string.fragment_info_map_hour) +
                                    Integer.parseInt(poiData[0].getTr_traffic_gtime()) % 60 + getString(R.string.fragment_info_map_minute));
                        }
                        car_iv.setImageResource(R.mipmap.btn_car_p);
                        mrt_iv.setImageResource(R.mipmap.btn_mrt_g);
                        ship_iv.setImageResource(R.mipmap.btn_ship_g);
                        walk_iv.setImageResource(R.mipmap.btn_walk_g);
                        traffic_tool = "C";
                        traffic_time = poiData[0].getTr_traffic_gtime();
                    }
                }
            });

            switch (traffic_tool) {
                case "C":
                    car_iv.setImageResource(R.mipmap.btn_car_p);
                    mrt_iv.setImageResource(R.mipmap.btn_mrt_g);
                    ship_iv.setImageResource(R.mipmap.btn_ship_g);
                    walk_iv.setImageResource(R.mipmap.btn_walk_g);
                    if (!traffic_time.equals(poiData[0].getTr_traffic_gtime())) {
                        customSwitch.setChecked(true);
                    }
                    break;
                case "P":
                    car_iv.setImageResource(R.mipmap.btn_car_g);
                    mrt_iv.setImageResource(R.mipmap.btn_mrt_p);
                    ship_iv.setImageResource(R.mipmap.btn_ship_g);
                    walk_iv.setImageResource(R.mipmap.btn_walk_g);
                    customSwitch.setChecked(true);
                    break;
                case "F":
                    car_iv.setImageResource(R.mipmap.btn_car_g);
                    mrt_iv.setImageResource(R.mipmap.btn_mrt_g);
                    ship_iv.setImageResource(R.mipmap.btn_ship_p);
                    walk_iv.setImageResource(R.mipmap.btn_walk_g);
                    customSwitch.setChecked(true);
                    break;
                case "W":
                    car_iv.setImageResource(R.mipmap.btn_car_g);
                    mrt_iv.setImageResource(R.mipmap.btn_mrt_g);
                    ship_iv.setImageResource(R.mipmap.btn_ship_g);
                    walk_iv.setImageResource(R.mipmap.btn_walk_p);
                    customSwitch.setChecked(true);
                    break;
            }
        }

        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_info_map_map);

        return mView;
    }

    @Override
    public void onInfoWindowClose(Marker marker) {
        Bitmap icon = BitmapFactory.decodeResource(getActivity().getResources(), R.mipmap.icon_ping_bg_b).copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(icon);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getActivity().getResources().getColor(R.color.sdkColorPrimary)); // Text Color
        paint.setTextSize(getActivity().getResources().getDimension(R.dimen.text_size_normal)); //Text Size
        int x = (int) (canvas.getWidth() / 2 - paint.measureText(String.valueOf(selectOrder)) / 2);
        int y = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2));
        canvas.drawText(String.valueOf(selectOrder), x, y - 6, paint);

        marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));

        info_ll.setVisibility(View.GONE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
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
        addPOIMarkers(poiData);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(Double.parseDouble(poiData[0].getV_lat()),
                        Double.parseDouble(poiData[0].getV_lng())), Y5CityText.MAP_ZOOM_LEVEL));

    }

    private void addPOIMarkers(TripInfoData[] poiData) {
        removePreviousMarkers();

        OmniClusterItem item;
        if (itemList == null) {
            itemList = new ArrayList<>();
        }

        for (TripInfoData poi : poiData) {
            item = new OmniClusterItem(poi);
            itemList.add(item);

            LatLng point = new LatLng(Double.valueOf(poi.getV_lat()), Double.valueOf(poi.getV_lng()));
            pointList.add(point);
        }

        drawPolyline(pointList);

        mClusterManager.addItems(itemList);
        mClusterManager.cluster();
    }

    private void removePreviousMarkers() {

        if (mClusterManager != null) {
            mClusterManager.clearItems();
            mClusterManager.cluster();
        }
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
                Marker marker = mOmniClusterRender.getMarker(omniClusterItem);
                if (marker.getTag() == null) {
                    marker.setTag(omniClusterItem.getPOITripInfo());
                }
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_pin_pick_b));
                selectOrder = Integer.parseInt(omniClusterItem.getOrder());
                NetworkImageView iconIV = info_ll.findViewById(R.id.fragment_info_map_iv);
                if (omniClusterItem.getIconUrl().length() != 0) {
//                    new DownloadImageTask(iconIV).execute(omniClusterItem.getIconUrl());
                    NetworkManager.getInstance().setNetworkImage(getContext(),
                            iconIV, omniClusterItem.getIconUrl());
                }
                ((TextView) info_ll.findViewById(R.id.fragment_info_map_title)).setText(omniClusterItem.getTitle());
                ((TextView) info_ll.findViewById(R.id.fragment_info_map_address)).setText(omniClusterItem.getAddress());
                if (type.equals("map")) {
                    info_ll.setVisibility(View.VISIBLE);
                }
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

    private void drawPolyline(List<LatLng> pointList) {
        PolylineOptions lineOptions = null;

        if (!pointList.isEmpty()) {
            lineOptions = new PolylineOptions()
                    .addAll(pointList)
                    .width(Y5CityText.POLYLINE_WIDTH)
                    .color(getResources().getColor(R.color.sdkColorPrimary));
        }
        if (lineOptions != null) {
            Polyline polyline = mMap.addPolyline(lineOptions);
            polyline.setZIndex(Y5CityText.POLYLINE_Z_INDEX);
            polyline.setVisible(true);
        }
    }

    private void gotoGoogleMap(String lat_s, String lng_s, String lat_e, String lng_e) {
        double startLatitude = Double.valueOf(lat_s);
        double startLongitude = Double.valueOf(lng_s);
        double endLatitude = Double.valueOf(lat_e);
        double endLongitude = Double.valueOf(lng_e);

        String saddr = "saddr=" + startLatitude + "," + startLongitude;
        String daddr = "daddr=" + endLatitude + "," + endLongitude;
        String uriString = "http://maps.google.com/maps?" + saddr + "&" + daddr;

        Uri uri = Uri.parse(uriString);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }

    private void openFragmentPage(Fragment fragment, String tag) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_main_fl, fragment, tag)
                .addToBackStack(null)
                .commit();
    }
}
