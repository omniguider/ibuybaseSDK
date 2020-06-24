package com.omni.y5citysdk.view.theme;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.omni.y5citysdk.R;
import com.omni.y5citysdk.Y5CitySDKActivity;
import com.omni.y5citysdk.module.trip.ThemeFeedback;
import com.omni.y5citysdk.network.NetworkManager;
import com.omni.y5citysdk.network.Y5CityAPI;
import com.omni.y5citysdk.tool.DialogTools;

import static com.omni.y5citysdk.tool.Y5CityText.LOG_TAG;

public class ThemeMainFragment extends Fragment {

    public static final String TAG = "fragment_tag_theme_main";

    private RecyclerView recyclerView;
    private ThemeAdapter themeAdapter;
    private boolean isCreate = false;
    private ThemeFeedback[] mThemeFeedback;
    private String tripId_o;
    private String tripId_t;
    private String start_time_o;
    private String start_time_t;

    private Handler mTimeHandler;
    private final Runnable mTimeRunner = new Runnable() {
        @Override
        public void run() {
            DialogTools.getInstance().dismissProgress(getActivity());
        }
    };

    public static ThemeMainFragment newInstance() {
        return new ThemeMainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mTimeHandler == null) {
            mTimeHandler = new Handler();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_theme_main, container, false);
        recyclerView = mView.findViewById(R.id.fragment_theme_main_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        Y5CityAPI.getInstance().getOfficialTrip(getActivity(), Y5CitySDKActivity.locale, new NetworkManager.NetworkManagerListener<ThemeFeedback[]>() {
            @Override
            public void onSucceed(ThemeFeedback[] themeFeedbacks) {
                Log.e(LOG_TAG, "themeFeedbacks" + themeFeedbacks[0].getTc_title());
                mThemeFeedback = themeFeedbacks;
                themeAdapter = new ThemeAdapter(getActivity(), themeFeedbacks);
                recyclerView.setAdapter(themeAdapter);
                themeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(String errorMsg, boolean shouldRetry) {
            }
        });

        return mView;
    }

    class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.mViewHolder> {
        private Context context;
        private ThemeFeedback[] theme;

        public ThemeAdapter(Context context, ThemeFeedback[] theme) {
            this.context = context;
            this.theme = theme;
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
        public ThemeAdapter.mViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.trip_theme_card_view, parent, false);
            if (!isCreate) {
                DialogTools.getInstance().showProgress(getActivity());
                mTimeHandler.postDelayed(mTimeRunner, 1000);
                isCreate = true;
            }
            return new mViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ThemeAdapter.mViewHolder holder, final int position) {
            final ThemeFeedback themeData = theme[position];

            if (themeData.getTc_img_mobile() != null) {
//                new DownloadImageTask(holder.Pic).execute(themeData.getTc_img_mobile());
                NetworkManager.getInstance().setNetworkImage(getContext(),
                        holder.Pic, themeData.getTc_img_mobile());
            }
            holder.title.setText(themeData.getTc_title());

            holder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    final View item = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_traffic, null);
//                    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
//                            .setTitle(R.string.fragment_theme_dialog_traffic_title)
//                            .setView(item)
//                            .setNegativeButton(R.string.dialog_button_cancel_text, new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    dialogInterface.dismiss();
//                                }
//                            }).show();
//
//
//                    item.findViewById(R.id.dialog_traffic_car_ll).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            alertDialog.dismiss();
//                            if (mThemeFeedback[position].getTrips().length > 1) {
//                                tripId_o = mThemeFeedback[position].getTrips()[0].getT_id();
//                                tripId_t = mThemeFeedback[position].getTrips()[1].getT_id();
//                                start_time_o = mThemeFeedback[position].getTrips()[0].getT_start_time();
//                                start_time_t = mThemeFeedback[position].getTrips()[1].getT_start_time();
//                            } else {
//                                tripId_o = mThemeFeedback[position].getTrips()[0].getT_id();
//                                tripId_t = tripId_o;
//                                start_time_o = mThemeFeedback[position].getTrips()[0].getT_start_time();
//                                start_time_t = start_time_o;
//                            }
//                            openFragmentPage(ThemeRouteFragment.newInstance(
//                                    tripId_o,
//                                    tripId_t,
//                                    mThemeFeedback[position].getTc_title(),
//                                    mThemeFeedback[position].getTc_img_mobile(),
//                                    start_time_o,
//                                    start_time_t),
//                                    ThemeRouteFragment.TAG);
//                        }
//                    });
//
//                    item.findViewById(R.id.dialog_traffic_mrt_ll).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            alertDialog.dismiss();
//                            if (mThemeFeedback[position].getTrips().length > 1) {
//                                tripId_o = mThemeFeedback[position].getTrips()[2].getT_id();
//                                tripId_t = mThemeFeedback[position].getTrips()[3].getT_id();
//                                start_time_o = mThemeFeedback[position].getTrips()[2].getT_start_time();
//                                start_time_t = mThemeFeedback[position].getTrips()[3].getT_start_time();
//                            } else {
//                                tripId_o = mThemeFeedback[position].getTrips()[2].getT_id();
//                                tripId_t = tripId_o;
//                                start_time_o = mThemeFeedback[position].getTrips()[2].getT_start_time();
//                                start_time_t = start_time_o;
//                            }
//                            openFragmentPage(ThemeRouteFragment.newInstance(
//                                    tripId_o,
//                                    tripId_t,
//                                    mThemeFeedback[position].getTc_title(),
//                                    mThemeFeedback[position].getTc_img_mobile(),
//                                    start_time_o,
//                                    start_time_t),
//                                    ThemeRouteFragment.TAG);
//                        }
//                    });

                    if (mThemeFeedback[position].getTrips().length > 1) {
                        tripId_o = mThemeFeedback[position].getTrips()[0].getT_id();
                        tripId_t = mThemeFeedback[position].getTrips()[1].getT_id();
                        start_time_o = mThemeFeedback[position].getTrips()[0].getT_start_time();
                        start_time_t = mThemeFeedback[position].getTrips()[1].getT_start_time();
                    } else {
                        tripId_o = mThemeFeedback[position].getTrips()[0].getT_id();
                        tripId_t = tripId_o;
                        start_time_o = mThemeFeedback[position].getTrips()[0].getT_start_time();
                        start_time_t = start_time_o;
                    }
                    openFragmentPage(ThemeRouteFragment.newInstance(
                            tripId_o,
                            tripId_t,
                            mThemeFeedback[position].getTc_title(),
                            mThemeFeedback[position].getTc_img_mobile(),
                            start_time_o,
                            start_time_t),
                            ThemeRouteFragment.TAG);
                }
            });

            holder.Pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    final View item = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_traffic, null);
//                    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
//                            .setTitle(R.string.fragment_theme_dialog_traffic_title)
//                            .setView(item)
//                            .setNegativeButton(R.string.dialog_button_cancel_text, new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    dialogInterface.dismiss();
//                                }
//                            }).show();
//
//
//                    item.findViewById(R.id.dialog_traffic_car_ll).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            alertDialog.dismiss();
//                            if (mThemeFeedback[position].getTrips().length > 1) {
//                                tripId_o = mThemeFeedback[position].getTrips()[0].getT_id();
//                                tripId_t = mThemeFeedback[position].getTrips()[1].getT_id();
//                                start_time_o = mThemeFeedback[position].getTrips()[0].getT_start_time();
//                                start_time_t = mThemeFeedback[position].getTrips()[1].getT_start_time();
//                            } else {
//                                tripId_o = mThemeFeedback[position].getTrips()[0].getT_id();
//                                tripId_t = tripId_o;
//                                start_time_o = mThemeFeedback[position].getTrips()[0].getT_start_time();
//                                start_time_t = start_time_o;
//                            }
//                            openFragmentPage(ThemeRouteFragment.newInstance(
//                                    tripId_o,
//                                    tripId_t,
//                                    mThemeFeedback[position].getTc_title(),
//                                    mThemeFeedback[position].getTc_img_mobile(),
//                                    start_time_o,
//                                    start_time_t),
//                                    ThemeRouteFragment.TAG);
//                        }
//                    });
//
//                    item.findViewById(R.id.dialog_traffic_mrt_ll).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            alertDialog.dismiss();
//                            if (mThemeFeedback[position].getTrips().length > 1) {
//                                tripId_o = mThemeFeedback[position].getTrips()[2].getT_id();
//                                tripId_t = mThemeFeedback[position].getTrips()[3].getT_id();
//                                start_time_o = mThemeFeedback[position].getTrips()[2].getT_start_time();
//                                start_time_t = mThemeFeedback[position].getTrips()[3].getT_start_time();
//                            } else {
//                                tripId_o = mThemeFeedback[position].getTrips()[2].getT_id();
//                                tripId_t = tripId_o;
//                                start_time_o = mThemeFeedback[position].getTrips()[2].getT_start_time();
//                                start_time_t = start_time_o;
//                            }
//                            openFragmentPage(ThemeRouteFragment.newInstance(
//                                    tripId_o,
//                                    tripId_t,
//                                    mThemeFeedback[position].getTc_title(),
//                                    mThemeFeedback[position].getTc_img_mobile(),
//                                    start_time_o,
//                                    start_time_t),
//                                    ThemeRouteFragment.TAG);
//                        }
//                    });

                    if (mThemeFeedback[position].getTrips().length > 1) {
                        tripId_o = mThemeFeedback[position].getTrips()[0].getT_id();
                        tripId_t = mThemeFeedback[position].getTrips()[1].getT_id();
                        start_time_o = mThemeFeedback[position].getTrips()[0].getT_start_time();
                        start_time_t = mThemeFeedback[position].getTrips()[1].getT_start_time();
                    } else {
                        tripId_o = mThemeFeedback[position].getTrips()[0].getT_id();
                        tripId_t = tripId_o;
                        start_time_o = mThemeFeedback[position].getTrips()[0].getT_start_time();
                        start_time_t = start_time_o;
                    }
                    openFragmentPage(ThemeRouteFragment.newInstance(
                            tripId_o,
                            tripId_t,
                            mThemeFeedback[position].getTc_title(),
                            mThemeFeedback[position].getTc_img_mobile(),
                            start_time_o,
                            start_time_t),
                            ThemeRouteFragment.TAG);
                }
            });
        }

        @Override
        public int getItemCount() {
            return theme.length;
        }
    }

    private void openFragmentPage(Fragment fragment, String tag) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_main_fl, fragment, tag)
                .addToBackStack(null)
                .commit();
    }
}
