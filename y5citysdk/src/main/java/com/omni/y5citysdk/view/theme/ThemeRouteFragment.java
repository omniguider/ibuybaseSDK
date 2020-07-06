package com.omni.y5citysdk.view.theme;

import android.app.TimePickerDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.toolbox.NetworkImageView;
import com.omni.y5citysdk.R;
import com.omni.y5citysdk.module.trip.TripInfoData;
import com.omni.y5citysdk.module.trip.TripInfoFeedback;
import com.omni.y5citysdk.network.NetworkManager;
import com.omni.y5citysdk.network.Y5CityAPI;
import com.omni.y5citysdk.view.custom.CustomSetupFragment;
import com.omni.y5citysdk.view.navi.InfoMapFragment;

import java.util.Calendar;

import static com.omni.y5citysdk.tool.Y5CityText.LOG_TAG;


public class ThemeRouteFragment extends Fragment {

    public static final String TAG = "fragment_tag_theme_route";
    private static final String ARG_KEY_TRIP_ID_ONE = "arg_key_trip_id_one";
    private static final String ARG_KEY_TRIP_ID_TWO = "arg_key_trip_id_two";
    private static final String ARG_KEY_TITLE = "arg_key_title";
    private static final String ARG_KEY_BANNER = "arg_key_banner";
    private static final String ARG_KEY_START_TIME_ONE = "arg_key_start_time_one";
    private static final String ARG_KEY_START_TIME_TWO = "arg_key_start_time_two";

    private View mView;
    private String current_tripId;
    private String tripId_o;
    private String tripId_t;
    private String title;
    private String banner;
    private String startTime_o, startTime_t;
    private TextView startTime_tv;
    private TextView startTimeAP_tv;
    private String am_pm = "AM";
    private int hour_of_12_hour_format = 9;
    private int minute_tp = 0;
    private String am_pm_d = "AM";
    private int hour_d = 9;
    private int minute_d = 0;
    private int st_hour = 0;
    private TripInfoData[] tripInfoData;

    public static ThemeRouteFragment newInstance(String tripId_o, String tripId_t, String title, String banner, String st_one, String st_two) {
        ThemeRouteFragment fragment = new ThemeRouteFragment();

        Bundle arg = new Bundle();
        arg.putString(ARG_KEY_TRIP_ID_ONE, tripId_o);
        arg.putString(ARG_KEY_TRIP_ID_TWO, tripId_t);
        arg.putString(ARG_KEY_TITLE, title);
        arg.putString(ARG_KEY_BANNER, banner);
        arg.putString(ARG_KEY_START_TIME_ONE, st_one);
        arg.putString(ARG_KEY_START_TIME_TWO, st_two);
        fragment.setArguments(arg);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tripId_o = getArguments().getString(ARG_KEY_TRIP_ID_ONE);
        tripId_t = getArguments().getString(ARG_KEY_TRIP_ID_TWO);
        title = getArguments().getString(ARG_KEY_TITLE);
        banner = getArguments().getString(ARG_KEY_BANNER);
        startTime_o = getArguments().getString(ARG_KEY_START_TIME_ONE);
        startTime_t = getArguments().getString(ARG_KEY_START_TIME_TWO);

        current_tripId = tripId_o;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_theme_route, container, false);
//            mView.setPadding(0, Tools.STATUS_BAR, 0, 0);

            mView.findViewById(R.id.fragment_theme_route_fl_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });

            mView.findViewById(R.id.fragment_theme_route_copy_tv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (!UserInfoManager.Companion.getInstance().isLoggedIn(getActivity())) {
//                        DialogTools.getInstance().showErrorMessage(getActivity(),
//                                R.string.dialog_title_text_note,
//                                R.string.dialog_message_no_login,
//                                new DialogInterface.OnDismissListener() {
//                                    @Override
//                                    public void onDismiss(DialogInterface dialog) {
//                                        openFragmentPage(LoginFragment.newInstance(), LoginFragment.TAG);
//                                    }
//                                });
//                    } else {
                    openFragmentPage(CustomSetupFragment.newInstance(current_tripId, banner), CustomSetupFragment.TAG);
//                    }
                }
            });

            NetworkImageView banner_IV = mView.findViewById(R.id.fragment_theme_route_banner);
            if (banner.length() != 0) {
//                new DownloadImageTask(banner_IV).execute(banner);
                NetworkManager.getInstance().setNetworkImage(getContext(),
                        banner_IV, banner);
            }

            ((TextView) mView.findViewById(R.id.fragment_theme_route_tv_action_bar_title)).setText(title);
            ((TextView) mView.findViewById(R.id.fragment_theme_route_title)).setText(title);

            startTime_tv = mView.findViewById(R.id.fragment_theme_route_start_time);
            startTimeAP_tv = mView.findViewById(R.id.fragment_theme_route_start_time_ap);

            String startTimeSplitOne[] = startTime_o.split(":");
            if (Integer.parseInt(startTimeSplitOne[0]) > 11) {
                startTimeAP_tv.setText("PM");
                am_pm = "PM";
            } else {
                startTimeAP_tv.setText("AM");
                am_pm = "AM";
            }
            int startHourOne;
            if (Integer.parseInt(startTimeSplitOne[0]) > 12) {
                startHourOne = Integer.parseInt(startTimeSplitOne[0]) - 12;
            } else {
                startHourOne = Integer.parseInt(startTimeSplitOne[0]);
            }
            startTime_tv.setText(startHourOne + ":" + startTimeSplitOne[1]);
            hour_of_12_hour_format = startHourOne;
            minute_tp = Integer.parseInt(startTimeSplitOne[1]);

            mView.findViewById(R.id.fragment_theme_route_start_time_ll).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Calendar c = Calendar.getInstance();
                    final int hour = c.get(Calendar.HOUR);
                    int minute = c.get(Calendar.MINUTE);
                    new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
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

                            Y5CityAPI.getInstance().getTripInfo(getActivity(), tripId_o, new NetworkManager.NetworkManagerListener<TripInfoFeedback>() {
                                @Override
                                public void onSucceed(TripInfoFeedback tripInfoFeedbacks) {

                                    tripInfoData = tripInfoFeedbacks.getTrip_info();
                                    for (int i = 0; i < tripInfoData.length; i++) {
                                        tripInfoData[i].setOrder(i);
                                    }

                                    ((TextView) mView.findViewById(R.id.fragment_theme_route_num)).setText(String.valueOf(tripInfoFeedbacks.getTotal_num()));
                                    ((TextView) mView.findViewById(R.id.fragment_theme_route_hour)).setText(String.valueOf(tripInfoFeedbacks.getTotal_min() / 60));
                                    ((TextView) mView.findViewById(R.id.fragment_theme_route_min)).setText(String.valueOf(tripInfoFeedbacks.getTotal_min() % 60));

                                    final TripInfoData[] mTripInfoData = tripInfoFeedbacks.getTrip_info();
                                    LinearLayout contentLL = mView.findViewById(R.id.fragment_theme_route_ll_contents);
                                    contentLL.removeAllViews();
                                    for (int i = 0; i < mTripInfoData.length; i++) {
                                        View itemRouteView = LayoutInflater.from(getContext()).inflate(R.layout.item_theme_route, container, false);

                                        ((TextView) itemRouteView.findViewById(R.id.item_theme_route_num)).setText(String.valueOf(i + 1));

                                        NetworkImageView route_IV = itemRouteView.findViewById(R.id.item_theme_route_route_iv);
                                        if (mTripInfoData[i].getV_image().length() != 0) {
//                                            new DownloadImageTask(route_IV).execute(mTripInfoData[i].getV_image());
                                            NetworkManager.getInstance().setNetworkImage(getContext(),
                                                    route_IV, mTripInfoData[i].getV_image());
                                        }

                                        ((TextView) itemRouteView.findViewById(R.id.item_theme_route_route_title)).setText(mTripInfoData[i].getV_name());
                                        ((TextView) itemRouteView.findViewById(R.id.item_theme_route_route_address)).setText(mTripInfoData[i].getA_name());
                                        if (Integer.parseInt(mTripInfoData[i].getTr_stay()) < 60) {
                                            ((TextView) itemRouteView.findViewById(R.id.item_theme_route_route_time)).setText(mTripInfoData[i].getTr_stay() + getString(R.string.fragment_info_map_minute));
                                        } else {
                                            ((TextView) itemRouteView.findViewById(R.id.item_theme_route_route_time)).setText(
                                                    String.valueOf(Integer.parseInt(mTripInfoData[i].getTr_stay()) / 60) + getString(R.string.fragment_info_map_hour) +
                                                            String.valueOf(Integer.parseInt(mTripInfoData[i].getTr_stay()) % 60) + getString(R.string.fragment_info_map_minute));
                                        }

                                        ((TextView) itemRouteView.findViewById(R.id.item_theme_route_st)).setText(
                                                hour_of_12_hour_format + ":" + String.format("%02d", minute_tp) + " " + am_pm);

                                        minute_tp = minute_tp + Integer.parseInt(mTripInfoData[i].getTr_stay());
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
                                                openFragmentPage(ReligionInfoFragment.newInstance(mTripInfoData[finalI].getP_id(),
                                                        mTripInfoData[finalI].getP_type()), ReligionInfoFragment.TAG);
                                            }
                                        });

                                        contentLL.addView(itemRouteView);

                                        if (i != tripInfoFeedbacks.getTrip_info().length - 1) {
                                            View itemTrafficView = LayoutInflater.from(getContext()).inflate(R.layout.item_theme_traffic, container, false);
                                            switch (mTripInfoData[i].getTr_traffic()) {
                                                case "C":
                                                    ((ImageView) itemTrafficView.findViewById(R.id.item_theme_traffic_iv)).setImageResource(R.mipmap.icon_car_g);
                                                    break;
                                                case "P":
                                                    ((ImageView) itemTrafficView.findViewById(R.id.item_theme_traffic_iv)).setImageResource(R.mipmap.icon_mrt_g);
                                                    break;
                                                case "F":
                                                    ((ImageView) itemTrafficView.findViewById(R.id.item_theme_traffic_iv)).setImageResource(R.mipmap.icon_ship_g);
                                                    break;
                                                case "W":
                                                    ((ImageView) itemTrafficView.findViewById(R.id.item_theme_traffic_iv)).setImageResource(R.mipmap.btn_walk_g);
                                                    break;
                                            }
                                            if (Integer.parseInt(mTripInfoData[i].getTr_traffic_time()) < 60) {
                                                ((TextView) itemTrafficView.findViewById(R.id.item_theme_traffic_time)).setText(mTripInfoData[i].getTr_traffic_time() + getString(R.string.fragment_info_map_minute));
                                            } else {
                                                ((TextView) itemTrafficView.findViewById(R.id.item_theme_traffic_time)).setText(
                                                        String.valueOf(Integer.parseInt(mTripInfoData[i].getTr_traffic_time()) / 60) + getString(R.string.fragment_info_map_hour) +
                                                                String.valueOf(Integer.parseInt(mTripInfoData[i].getTr_traffic_time()) % 60) + getString(R.string.fragment_info_map_minute));
                                            }
                                            contentLL.addView(itemTrafficView);

                                            minute_tp = minute_tp + Integer.parseInt(mTripInfoData[i].getTr_traffic_time());
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

                                @Override
                                public void onFail(String errorMsg, boolean shouldRetry) {
                                }
                            });
                        }
                    }, hour, minute, false).show();
                }
            });

            Calendar today = Calendar.getInstance();
            int year = today.get(Calendar.YEAR);
            int month = today.get(Calendar.MONTH);
            int day = today.get(Calendar.DAY_OF_MONTH);
            String month_s = String.valueOf(month + 1);
            String day_s = String.valueOf(day);
            if (month + 1 < 10) {
                month_s = "0" + (month + 1);
            }
            if (day < 10) {
                day_s = "0" + day;
            }
//            ((TextView) mView.findViewById(R.id.fragment_theme_route_date)).setText(
//                    year + "-" + month_s + "-" + day_s);

            Y5CityAPI.getInstance().getTripInfo(getActivity(), tripId_o, new NetworkManager.NetworkManagerListener<TripInfoFeedback>() {
                @Override
                public void onSucceed(TripInfoFeedback tripInfoFeedbacks) {

                    tripInfoData = tripInfoFeedbacks.getTrip_info();
                    for (int i = 0; i < tripInfoData.length; i++) {
                        tripInfoData[i].setOrder(i);
                    }

                    ((TextView) mView.findViewById(R.id.fragment_theme_route_num)).setText(String.valueOf(tripInfoFeedbacks.getTotal_num()));
                    ((TextView) mView.findViewById(R.id.fragment_theme_route_hour)).setText(String.valueOf(tripInfoFeedbacks.getTotal_min() / 60));
                    ((TextView) mView.findViewById(R.id.fragment_theme_route_min)).setText(String.valueOf(tripInfoFeedbacks.getTotal_min() % 60));

                    final TripInfoData[] mTripInfoData = tripInfoFeedbacks.getTrip_info();
                    LinearLayout contentLL = mView.findViewById(R.id.fragment_theme_route_ll_contents);
                    for (int i = 0; i < mTripInfoData.length; i++) {
                        View itemRouteView = LayoutInflater.from(getContext()).inflate(R.layout.item_theme_route, container, false);

                        ((TextView) itemRouteView.findViewById(R.id.item_theme_route_num)).setText(String.valueOf(i + 1));

                        NetworkImageView route_IV = itemRouteView.findViewById(R.id.item_theme_route_route_iv);
                        if (mTripInfoData[i].getV_image().length() != 0) {
//                            new DownloadImageTask(route_IV).execute(mTripInfoData[i].getV_image());
                            NetworkManager.getInstance().setNetworkImage(getContext(),
                                    route_IV, mTripInfoData[i].getV_image());
                        }

                        ((TextView) itemRouteView.findViewById(R.id.item_theme_route_route_title)).setText(mTripInfoData[i].getV_name());
                        ((TextView) itemRouteView.findViewById(R.id.item_theme_route_route_address)).setText(mTripInfoData[i].getA_name());
                        if (Integer.parseInt(mTripInfoData[i].getTr_stay()) < 60) {
                            ((TextView) itemRouteView.findViewById(R.id.item_theme_route_route_time)).setText(mTripInfoData[i].getTr_stay() + getString(R.string.fragment_info_map_minute));
                        } else {
                            ((TextView) itemRouteView.findViewById(R.id.item_theme_route_route_time)).setText(
                                    String.valueOf(Integer.parseInt(mTripInfoData[i].getTr_stay()) / 60) + getString(R.string.fragment_info_map_hour) +
                                            String.valueOf(Integer.parseInt(mTripInfoData[i].getTr_stay()) % 60) + getString(R.string.fragment_info_map_minute));
                        }

                        ((TextView) itemRouteView.findViewById(R.id.item_theme_route_st)).setText(
                                hour_of_12_hour_format + ":" + String.format("%02d", minute_tp) + " " + am_pm);

                        minute_tp = minute_tp + Integer.parseInt(mTripInfoData[i].getTr_stay());
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
                                openFragmentPage(ReligionInfoFragment.newInstance(mTripInfoData[finalI].getP_id(),
                                        mTripInfoData[finalI].getP_type()), ReligionInfoFragment.TAG);
                            }
                        });

                        contentLL.addView(itemRouteView);

                        if (i != tripInfoFeedbacks.getTrip_info().length - 1) {
                            View itemTrafficView = LayoutInflater.from(getContext()).inflate(R.layout.item_theme_traffic, container, false);
                            switch (mTripInfoData[i].getTr_traffic()) {
                                case "C":
                                    ((ImageView) itemTrafficView.findViewById(R.id.item_theme_traffic_iv)).setImageResource(R.mipmap.icon_car_g);
                                    break;
                                case "P":
                                    ((ImageView) itemTrafficView.findViewById(R.id.item_theme_traffic_iv)).setImageResource(R.mipmap.icon_mrt_g);
                                    break;
                                case "F":
                                    ((ImageView) itemTrafficView.findViewById(R.id.item_theme_traffic_iv)).setImageResource(R.mipmap.icon_ship_g);
                                    break;
                                case "W":
                                    ((ImageView) itemTrafficView.findViewById(R.id.item_theme_traffic_iv)).setImageResource(R.mipmap.btn_walk_g);
                                    break;
                            }
                            if (Integer.parseInt(mTripInfoData[i].getTr_traffic_time()) < 60) {
                                ((TextView) itemTrafficView.findViewById(R.id.item_theme_traffic_time)).setText(mTripInfoData[i].getTr_traffic_time() + getString(R.string.fragment_info_map_minute));
                            } else {
                                ((TextView) itemTrafficView.findViewById(R.id.item_theme_traffic_time)).setText(
                                        String.valueOf(Integer.parseInt(mTripInfoData[i].getTr_traffic_time()) / 60) + getString(R.string.fragment_info_map_hour) +
                                                String.valueOf(Integer.parseInt(mTripInfoData[i].getTr_traffic_time()) % 60) + getString(R.string.fragment_info_map_minute));
                            }
                            contentLL.addView(itemTrafficView);

                            minute_tp = minute_tp + Integer.parseInt(mTripInfoData[i].getTr_traffic_time());
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

                @Override
                public void onFail(String errorMsg, boolean shouldRetry) {
                }
            });

            final TextView route_one_btn = mView.findViewById(R.id.fragment_theme_route_one);
            final TextView route_two_btn = mView.findViewById(R.id.fragment_theme_route_two);

            route_one_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    current_tripId = tripId_o;

                    route_one_btn.setTextColor(getResources().getColor(android.R.color.white));
                    route_one_btn.setBackgroundResource(R.drawable.solid_round_rectangle_gradient_purple);
                    route_two_btn.setTextColor(getResources().getColor(R.color.sdkColorPrimary));
                    route_two_btn.setBackgroundResource(R.drawable.solid_round_rectangle_gradient_purple_stroke);

                    //set default value
//                    hour_of_12_hour_format = hour_d;
//                    minute_tp = minute_d;
//                    am_pm = am_pm_d;

                    String startTimeSplitOne[] = startTime_o.split(":");
                    if (Integer.parseInt(startTimeSplitOne[0]) > 11) {
                        startTimeAP_tv.setText("PM");
                        am_pm = "PM";
                    } else {
                        startTimeAP_tv.setText("AM");
                        am_pm = "AM";
                    }
                    int startHourOne;
                    if (Integer.parseInt(startTimeSplitOne[0]) > 12) {
                        startHourOne = Integer.parseInt(startTimeSplitOne[0]) - 12;
                    } else {
                        startHourOne = Integer.parseInt(startTimeSplitOne[0]);
                    }
                    startTime_tv.setText(startHourOne + ":" + startTimeSplitOne[1]);
                    hour_of_12_hour_format = startHourOne;
                    minute_tp = Integer.parseInt(startTimeSplitOne[1]);

                    Y5CityAPI.getInstance().getTripInfo(getActivity(), tripId_o, new NetworkManager.NetworkManagerListener<TripInfoFeedback>() {
                        @Override
                        public void onSucceed(TripInfoFeedback tripInfoFeedbacks) {

                            tripInfoData = tripInfoFeedbacks.getTrip_info();
                            for (int i = 0; i < tripInfoData.length; i++) {
                                tripInfoData[i].setOrder(i);
                            }

                            ((TextView) mView.findViewById(R.id.fragment_theme_route_num)).setText(String.valueOf(tripInfoFeedbacks.getTotal_num()));
                            ((TextView) mView.findViewById(R.id.fragment_theme_route_hour)).setText(String.valueOf(tripInfoFeedbacks.getTotal_min() / 60));
                            ((TextView) mView.findViewById(R.id.fragment_theme_route_min)).setText(String.valueOf(tripInfoFeedbacks.getTotal_min() % 60));

                            final TripInfoData[] mTripInfoData = tripInfoFeedbacks.getTrip_info();
                            LinearLayout contentLL = mView.findViewById(R.id.fragment_theme_route_ll_contents);
                            contentLL.removeAllViews();
                            for (int i = 0; i < mTripInfoData.length; i++) {
                                View itemRouteView = LayoutInflater.from(getContext()).inflate(R.layout.item_theme_route, container, false);

                                ((TextView) itemRouteView.findViewById(R.id.item_theme_route_num)).setText(String.valueOf(i + 1));

                                NetworkImageView route_IV = itemRouteView.findViewById(R.id.item_theme_route_route_iv);
                                if (mTripInfoData[i].getV_image().length() != 0) {
//                                    new DownloadImageTask(route_IV).execute(mTripInfoData[i].getV_image());
                                    NetworkManager.getInstance().setNetworkImage(getContext(),
                                            route_IV, mTripInfoData[i].getV_image());
                                }

                                ((TextView) itemRouteView.findViewById(R.id.item_theme_route_route_title)).setText(mTripInfoData[i].getV_name());
                                ((TextView) itemRouteView.findViewById(R.id.item_theme_route_route_address)).setText(mTripInfoData[i].getA_name());
                                if (Integer.parseInt(mTripInfoData[i].getTr_stay()) < 60) {
                                    ((TextView) itemRouteView.findViewById(R.id.item_theme_route_route_time)).setText(mTripInfoData[i].getTr_stay() + getString(R.string.fragment_info_map_minute));
                                } else {
                                    ((TextView) itemRouteView.findViewById(R.id.item_theme_route_route_time)).setText(
                                            String.valueOf(Integer.parseInt(mTripInfoData[i].getTr_stay()) / 60) + getString(R.string.fragment_info_map_hour) +
                                                    String.valueOf(Integer.parseInt(mTripInfoData[i].getTr_stay()) % 60) + getString(R.string.fragment_info_map_minute));
                                }

                                ((TextView) itemRouteView.findViewById(R.id.item_theme_route_st)).setText(
                                        hour_of_12_hour_format + ":" + String.format("%02d", minute_tp) + " " + am_pm);

                                minute_tp = minute_tp + Integer.parseInt(mTripInfoData[i].getTr_stay());
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
                                        openFragmentPage(ReligionInfoFragment.newInstance(mTripInfoData[finalI].getP_id(),
                                                mTripInfoData[finalI].getP_type()), ReligionInfoFragment.TAG);
                                    }
                                });

                                contentLL.addView(itemRouteView);

                                if (i != tripInfoFeedbacks.getTrip_info().length - 1) {
                                    View itemTrafficView = LayoutInflater.from(getContext()).inflate(R.layout.item_theme_traffic, container, false);
                                    switch (mTripInfoData[i].getTr_traffic()) {
                                        case "C":
                                            ((ImageView) itemTrafficView.findViewById(R.id.item_theme_traffic_iv)).setImageResource(R.mipmap.icon_car_g);
                                            break;
                                        case "P":
                                            ((ImageView) itemTrafficView.findViewById(R.id.item_theme_traffic_iv)).setImageResource(R.mipmap.icon_mrt_g);
                                            break;
                                        case "F":
                                            ((ImageView) itemTrafficView.findViewById(R.id.item_theme_traffic_iv)).setImageResource(R.mipmap.icon_ship_g);
                                            break;
                                        case "W":
                                            ((ImageView) itemTrafficView.findViewById(R.id.item_theme_traffic_iv)).setImageResource(R.mipmap.btn_walk_g);
                                            break;
                                    }
                                    if (Integer.parseInt(mTripInfoData[i].getTr_traffic_time()) < 60) {
                                        ((TextView) itemTrafficView.findViewById(R.id.item_theme_traffic_time)).setText(mTripInfoData[i].getTr_traffic_time() + getString(R.string.fragment_info_map_minute));
                                    } else {
                                        ((TextView) itemTrafficView.findViewById(R.id.item_theme_traffic_time)).setText(
                                                String.valueOf(Integer.parseInt(mTripInfoData[i].getTr_traffic_time()) / 60) + getString(R.string.fragment_info_map_hour) +
                                                        String.valueOf(Integer.parseInt(mTripInfoData[i].getTr_traffic_time()) % 60) + getString(R.string.fragment_info_map_minute));
                                    }
                                    contentLL.addView(itemTrafficView);

                                    minute_tp = minute_tp + Integer.parseInt(mTripInfoData[i].getTr_traffic_time());
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

                        @Override
                        public void onFail(String errorMsg, boolean shouldRetry) {
                        }
                    });
                }
            });

            route_two_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    current_tripId = tripId_t;

                    route_two_btn.setTextColor(getResources().getColor(android.R.color.white));
                    route_two_btn.setBackgroundResource(R.drawable.solid_round_rectangle_gradient_purple);
                    route_one_btn.setTextColor(getResources().getColor(R.color.sdkColorPrimary));
                    route_one_btn.setBackgroundResource(R.drawable.solid_round_rectangle_gradient_purple_stroke);

                    //set default value
//                    hour_of_12_hour_format = hour_d;
//                    minute_tp = minute_d;
//                    am_pm = am_pm_d;

                    String startTimeSplitTwo[] = startTime_t.split(":");
                    if (Integer.parseInt(startTimeSplitTwo[0]) > 11) {
                        startTimeAP_tv.setText("PM");
                        am_pm = "PM";
                    } else {
                        startTimeAP_tv.setText("AM");
                        am_pm = "AM";
                    }
                    int startHourTwo;
                    if (Integer.parseInt(startTimeSplitTwo[0]) > 12) {
                        startHourTwo = Integer.parseInt(startTimeSplitTwo[0]) - 12;
                    } else {
                        startHourTwo = Integer.parseInt(startTimeSplitTwo[0]);
                    }
                    startTime_tv.setText(startHourTwo + ":" + startTimeSplitTwo[1]);
                    hour_of_12_hour_format = startHourTwo;
                    minute_tp = Integer.parseInt(startTimeSplitTwo[1]);

                    Y5CityAPI.getInstance().getTripInfo(getActivity(), tripId_t, new NetworkManager.NetworkManagerListener<TripInfoFeedback>() {
                        @Override
                        public void onSucceed(TripInfoFeedback tripInfoFeedbacks) {

                            tripInfoData = tripInfoFeedbacks.getTrip_info();
                            for (int i = 0; i < tripInfoData.length; i++) {
                                tripInfoData[i].setOrder(i);
                            }

                            ((TextView) mView.findViewById(R.id.fragment_theme_route_num)).setText(String.valueOf(tripInfoFeedbacks.getTotal_num()));
                            ((TextView) mView.findViewById(R.id.fragment_theme_route_hour)).setText(String.valueOf(tripInfoFeedbacks.getTotal_min() / 60));
                            ((TextView) mView.findViewById(R.id.fragment_theme_route_min)).setText(String.valueOf(tripInfoFeedbacks.getTotal_min() % 60));

                            final TripInfoData[] mTripInfoData = tripInfoFeedbacks.getTrip_info();
                            LinearLayout contentLL = mView.findViewById(R.id.fragment_theme_route_ll_contents);
                            contentLL.removeAllViews();
                            for (int i = 0; i < mTripInfoData.length; i++) {
                                View itemRouteView = LayoutInflater.from(getContext()).inflate(R.layout.item_theme_route, container, false);

                                ((TextView) itemRouteView.findViewById(R.id.item_theme_route_num)).setText(String.valueOf(i + 1));

                                NetworkImageView route_IV = itemRouteView.findViewById(R.id.item_theme_route_route_iv);
                                if (mTripInfoData[i].getV_image().length() != 0) {
//                                    new DownloadImageTask(route_IV).execute(mTripInfoData[i].getV_image());
                                    NetworkManager.getInstance().setNetworkImage(getContext(),
                                            route_IV, mTripInfoData[i].getV_image());
                                }

                                ((TextView) itemRouteView.findViewById(R.id.item_theme_route_route_title)).setText(mTripInfoData[i].getV_name());
                                ((TextView) itemRouteView.findViewById(R.id.item_theme_route_route_address)).setText(mTripInfoData[i].getA_name());
                                if (Integer.parseInt(mTripInfoData[i].getTr_stay()) < 60) {
                                    ((TextView) itemRouteView.findViewById(R.id.item_theme_route_route_time)).setText(mTripInfoData[i].getTr_stay() + getString(R.string.fragment_info_map_minute));
                                } else {
                                    ((TextView) itemRouteView.findViewById(R.id.item_theme_route_route_time)).setText(
                                            String.valueOf(Integer.parseInt(mTripInfoData[i].getTr_stay()) / 60) + getString(R.string.fragment_info_map_hour) +
                                                    String.valueOf(Integer.parseInt(mTripInfoData[i].getTr_stay()) % 60) + getString(R.string.fragment_info_map_minute));
                                }

                                ((TextView) itemRouteView.findViewById(R.id.item_theme_route_st)).setText(
                                        hour_of_12_hour_format + ":" + String.format("%02d", minute_tp) + " " + am_pm);

                                minute_tp = minute_tp + Integer.parseInt(mTripInfoData[i].getTr_stay());
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
                                        openFragmentPage(ReligionInfoFragment.newInstance(mTripInfoData[finalI].getP_id(),
                                                mTripInfoData[finalI].getP_type()), ReligionInfoFragment.TAG);
                                    }
                                });

                                contentLL.addView(itemRouteView);

                                if (i != tripInfoFeedbacks.getTrip_info().length - 1) {
                                    View itemTrafficView = LayoutInflater.from(getContext()).inflate(R.layout.item_theme_traffic, container, false);
                                    switch (mTripInfoData[i].getTr_traffic()) {
                                        case "C":
                                            ((ImageView) itemTrafficView.findViewById(R.id.item_theme_traffic_iv)).setImageResource(R.mipmap.icon_car_g);
                                            break;
                                        case "P":
                                            ((ImageView) itemTrafficView.findViewById(R.id.item_theme_traffic_iv)).setImageResource(R.mipmap.icon_mrt_g);
                                            break;
                                        case "F":
                                            ((ImageView) itemTrafficView.findViewById(R.id.item_theme_traffic_iv)).setImageResource(R.mipmap.icon_ship_g);
                                            break;
                                        case "W":
                                            ((ImageView) itemTrafficView.findViewById(R.id.item_theme_traffic_iv)).setImageResource(R.mipmap.btn_walk_g);
                                            break;
                                    }
                                    if (Integer.parseInt(mTripInfoData[i].getTr_traffic_time()) < 60) {
                                        ((TextView) itemTrafficView.findViewById(R.id.item_theme_traffic_time)).setText(mTripInfoData[i].getTr_traffic_time() + getString(R.string.fragment_info_map_minute));
                                    } else {
                                        ((TextView) itemTrafficView.findViewById(R.id.item_theme_traffic_time)).setText(
                                                String.valueOf(Integer.parseInt(mTripInfoData[i].getTr_traffic_time()) / 60) + getString(R.string.fragment_info_map_hour) +
                                                        String.valueOf(Integer.parseInt(mTripInfoData[i].getTr_traffic_time()) % 60) + getString(R.string.fragment_info_map_minute));
                                    }
                                    contentLL.addView(itemTrafficView);

                                    minute_tp = minute_tp + Integer.parseInt(mTripInfoData[i].getTr_traffic_time());
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

                        @Override
                        public void onFail(String errorMsg, boolean shouldRetry) {
                        }
                    });
                }
            });

            mView.findViewById(R.id.fragment_theme_route_fl_map).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openFragmentPage(InfoMapFragment.newInstance(title, tripInfoData, "map"), InfoMapFragment.TAG);
                }
            });
        }

        return mView;
    }

    private void openFragmentPage(Fragment fragment, String tag) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_main_fl, fragment, tag)
                .addToBackStack(null)
                .commit();
    }
}
