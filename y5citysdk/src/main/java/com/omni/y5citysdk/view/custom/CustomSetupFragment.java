package com.omni.y5citysdk.view.custom;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.omni.y5citysdk.R;
import com.omni.y5citysdk.manager.UserInfoManager;
import com.omni.y5citysdk.module.OmniEvent;
import com.omni.y5citysdk.module.trip.ReligionInfoFeedback;
import com.omni.y5citysdk.module.trip.TripInfoData;
import com.omni.y5citysdk.module.trip.TripInfoFeedback;
import com.omni.y5citysdk.module.trip.UserTripInfoFeedback;
import com.omni.y5citysdk.module.trip.UserTripPoint;
import com.omni.y5citysdk.module.trip.UserTripUpdateData;
import com.omni.y5citysdk.network.NetworkManager;
import com.omni.y5citysdk.network.Y5CityAPI;
import com.omni.y5citysdk.tool.Tools;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.omni.y5citysdk.tool.Y5CityText.LOG_TAG;


public class CustomSetupFragment extends Fragment {

    public static final String TAG = "fragment_tag_custom_setup";
    private static final String ARG_KEY_RELIGION_INFO_FEEDBACK = "arg_key_religion_info_feedback";
    private static final String ARG_KEY_TRIP_ID = "arg_key_trip_id";
    private static final String ARG_KEY_BANNER = "arg_key_banner";

    private Context mContext;
    private View mView;
    private final Calendar myCalendar = Calendar.getInstance();
    private EditText date_et;
    private EditText title;
    private String tripId;
    private String banner;
    private ReligionInfoFeedback mReligionInfoFeedback;
    private String traffic_tool = "C";
    private ImageView carSelect;
    private ImageView mrtSelect;
    private ImageView carIcon;
    private ImageView mrtIcon;

    public static CustomSetupFragment newInstance() {
        CustomSetupFragment fragment = new CustomSetupFragment();
        return fragment;
    }

    public static CustomSetupFragment newInstance(String tripId, String banner) {
        CustomSetupFragment fragment = new CustomSetupFragment();

        Bundle arg = new Bundle();
        arg.putString(ARG_KEY_TRIP_ID, tripId);
        arg.putString(ARG_KEY_BANNER, banner);
        fragment.setArguments(arg);

        return fragment;
    }

    public static CustomSetupFragment newInstance(ReligionInfoFeedback religionInfoFeedback) {
        CustomSetupFragment fragment = new CustomSetupFragment();

        Bundle arg = new Bundle();
        arg.putSerializable(ARG_KEY_RELIGION_INFO_FEEDBACK, religionInfoFeedback);
        fragment.setArguments(arg);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ARG_KEY_TRIP_ID)) {
            tripId = getArguments().getString(ARG_KEY_TRIP_ID);
            banner = getArguments().getString(ARG_KEY_BANNER);
        }

        if (getArguments() != null && getArguments().containsKey(ARG_KEY_RELIGION_INFO_FEEDBACK)) {
            mReligionInfoFeedback = (ReligionInfoFeedback) getArguments().getSerializable(ARG_KEY_RELIGION_INFO_FEEDBACK);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_custom_setup, container, false);
//        mView.setPadding(0, Tools.STATUS_BAR, 0, 0);

        mView.findViewById(R.id.fragment_custom_setup_fl_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        title = mView.findViewById(R.id.fragment_custom_setup_title_et);

        carSelect = mView.findViewById(R.id.fragment_custom_setup_traffic_car_select_iv);
        mrtSelect = mView.findViewById(R.id.fragment_custom_setup_traffic_mrt_select_iv);
        carIcon = mView.findViewById(R.id.fragment_custom_setup_traffic_car_iv);
        mrtIcon = mView.findViewById(R.id.fragment_custom_setup_traffic_mrt_iv);

        carSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carSelect.setImageResource(R.mipmap.btn_select);
                mrtSelect.setImageResource(R.mipmap.btn_unselect);
                carIcon.setImageResource(R.mipmap.btn_car_p);
                mrtIcon.setImageResource(R.mipmap.btn_mrt_g);
                traffic_tool = "C";
            }
        });

        mrtSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carSelect.setImageResource(R.mipmap.btn_unselect);
                mrtSelect.setImageResource(R.mipmap.btn_select);
                carIcon.setImageResource(R.mipmap.btn_car_g);
                mrtIcon.setImageResource(R.mipmap.btn_mrt_p);
                traffic_tool = "P";
            }
        });


        mView.findViewById(R.id.fragment_custom_setup_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getArguments() != null && getArguments().containsKey(ARG_KEY_TRIP_ID)) {
                    Y5CityAPI.getInstance().userTripCopy(getActivity(), UserInfoManager.Companion.getInstance().getUserLoginToken(getActivity()),
                            tripId, title.getText().toString(), new NetworkManager.NetworkManagerListener<UserTripInfoFeedback>() {
                                @Override
                                public void onSucceed(UserTripInfoFeedback userTripInfoFeedback) {
                                    openFragmentPage(CustomRouteFragment.newInstance(title.getText().toString(),
                                            date_et.getText().toString(), banner, "09:00",
                                            userTripInfoFeedback.getTrip_days()[0].getTrip_info()[0].getT_id(),
                                            "custom",
                                            userTripInfoFeedback.getTrip_days()), CustomRouteFragment.TAG);
                                    EventBus.getDefault().post(new OmniEvent(OmniEvent.TYPE_USER_TRIP_UPDATE, ""));
                                }

                                @Override
                                public void onFail(String errorMsg, boolean shouldRetry) {
                                    Log.e(LOG_TAG, "errorMsg" + errorMsg);
                                }
                            });
                } else {
                    Y5CityAPI.getInstance().userTripCreate(getActivity(), UserInfoManager.Companion.getInstance().getUserLoginToken(getActivity()),
                            title.getText().toString(), traffic_tool, new NetworkManager.NetworkManagerListener<UserTripUpdateData>() {
                                @Override
                                public void onSucceed(UserTripUpdateData userTripUpdateData) {

                                    TripInfoFeedback[] tripInfoFeedback;
//                                    TripInfoData addTripInfoData = new TripInfoData();
//                                    addTripInfoData.setT_id(userTripUpdateData.getT_id());
//                                    addTripInfoData.setP_id(String.valueOf(mReligionInfoFeedback.getP_id()));
//                                    addTripInfoData.setTr_title(mReligionInfoFeedback.getName());
//                                    addTripInfoData.setV_name(mReligionInfoFeedback.getName());
//                                    addTripInfoData.setA_name(mReligionInfoFeedback.getArea());
//                                    addTripInfoData.setV_image(mReligionInfoFeedback.getImage());
//                                    addTripInfoData.setV_lat(String.valueOf(mReligionInfoFeedback.getLat()));
//                                    addTripInfoData.setV_lng(String.valueOf(mReligionInfoFeedback.getLng()));
//                                    addTripInfoData.setTr_stay("30");
//                                    addTripInfoData.setP_type("r");
                                    List<TripInfoFeedback> addTripDaysList = new ArrayList<>();
                                    addTripDaysList.add(new TripInfoFeedback());
                                    tripInfoFeedback = addTripDaysList.toArray((new TripInfoFeedback[addTripDaysList.size()]));
//                                    addTripInfoData.setOrder(0);
//                                    tripInfoFeedback[0].getTrip_info()[0] = addTripInfoData;

                                    String jsonStr = "";
                                    StringBuilder jsonTripDays = new StringBuilder();
                                    List<UserTripPoint> mUserTripPointList = new ArrayList<>();
//                                    UserTripPoint userTripPoint = new UserTripPoint.Builder()
//                                            .setP_id(addTripInfoData.getP_id())
//                                            .setTr_title(addTripInfoData.getTr_title())
//                                            .setTr_stay(Integer.parseInt(addTripInfoData.getTr_stay()))
//                                            .build();
//                                    mUserTripPointList.add(userTripPoint);
                                    jsonStr = NetworkManager.getInstance().getGson().toJson(mUserTripPointList);
                                    jsonTripDays.append(jsonStr);

                                    Y5CityAPI.getInstance().userTripPoint(getActivity(), UserInfoManager.Companion.getInstance().getUserLoginToken(getActivity()),
                                            userTripUpdateData.getT_id(), jsonTripDays.toString(), new NetworkManager.NetworkManagerListener<UserTripInfoFeedback>() {
                                                @Override
                                                public void onSucceed(UserTripInfoFeedback userTripInfoFeedback) {

                                                }

                                                @Override
                                                public void onFail(String errorMsg, boolean shouldRetry) {

                                                }
                                            });

//                                    openFragmentPage(CustomRouteFragment.newInstance(title.getText().toString(),
//                                            date_et.getText().toString(), "", "09:00",
//                                            userTripUpdateData.getT_id(),
//                                            "custom",
//                                            tripInfoFeedback), CustomRouteFragment.TAG);
                                    EventBus.getDefault().post(new OmniEvent(OmniEvent.TYPE_USER_TRIP_UPDATE, ""));

                                    getActivity().getSupportFragmentManager().popBackStack();
                                }

                                @Override
                                public void onFail(String errorMsg, boolean shouldRetry) {

                                }
                            });
                }
            }
        });

        date_et = mView.findViewById(R.id.fragment_custom_setup_date_et);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        date_et.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        return mView;
    }

    private void openFragmentPage(Fragment fragment, String tag) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_main_fl, fragment, tag)
                .addToBackStack(null)
                .commit();
    }

    private void updateLabel() {
        String myFormat = "yyyy/MM/dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.TAIWAN);
        date_et.setText(sdf.format(myCalendar.getTime()));
    }

}
