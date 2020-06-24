package com.omni.y5citysdk.view.theme;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.maps.model.LatLng;
import com.omni.y5citysdk.R;
import com.omni.y5citysdk.manager.DataCacheManager;
import com.omni.y5citysdk.manager.LocaleManager;
import com.omni.y5citysdk.manager.UserInfoManager;
import com.omni.y5citysdk.module.OmniEvent;
import com.omni.y5citysdk.module.favorite.FavoriteResponse;
import com.omni.y5citysdk.module.trip.PointInfoFeedback;
import com.omni.y5citysdk.module.trip.ReligionInfoFeedback;
import com.omni.y5citysdk.network.NetworkManager;
import com.omni.y5citysdk.network.Y5CityAPI;
import com.omni.y5citysdk.tool.Tools;
import com.omni.y5citysdk.tool.Y5CityText;
import com.omni.y5citysdk.view.custom.CustomSetupFragment;
import com.omni.y5citysdk.view.point.PointMapFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import static com.omni.y5citysdk.tool.Y5CityText.LOG_TAG;

public class ReligionInfoFragment extends Fragment {

    public static final String TAG = "fragment_tag_religion_info";
    private static final String ARG_KEY_RELIGION_ID = "arg_key_religion_id";
    private static final String ARG_KEY_TYPE = "arg_key_type";
    private static final String ARG_KEY_MODE = "arg_key_mode";

    private EventBus mEventBus;
    private View mView;
    private String religion_id;
    private String type;
    private int mode;
    private String title;
    private LatLng latLng;
    private Location mLastLocation;

    private LinearLayout feature_ll;
    private LinearLayout flow_ll;
    private LinearLayout info_ll;
    private LinearLayout intro_ll;
    private LinearLayout allusion_ll;
    private FrameLayout banner_fl;
    private ScrollView mScrollView;
    private ImageView favorite_iv;
    private int current_fav;
    private int update_fav;

    private MediaPlayer mp_god = new MediaPlayer();
    private MediaPlayer mp_flow = new MediaPlayer();
    private MediaPlayer mp_intro = new MediaPlayer();
    private MediaPlayer mp_fest = new MediaPlayer();
    private MediaPlayer mp_allusion = new MediaPlayer();
    private boolean isPlaying = false;

    private int height = 0;
    private ReligionInfoFeedback mReligionInfoFeedback;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(OmniEvent event) {
        switch (event.getType()) {
            case OmniEvent.TYPE_USER_LOCATION:
                mLastLocation = (Location) event.getObj();
                break;
        }
    }

    public static ReligionInfoFragment newInstance(String religion_id, String type) {
        ReligionInfoFragment fragment = new ReligionInfoFragment();

        Bundle arg = new Bundle();
        arg.putString(ARG_KEY_RELIGION_ID, religion_id);
        arg.putString(ARG_KEY_TYPE, type);
        arg.putInt(ARG_KEY_MODE, Y5CityText.RELIGION_INFO_NORMAL);
        fragment.setArguments(arg);

        return fragment;
    }

    public static ReligionInfoFragment newInstance(String religion_id, int mode) {
        ReligionInfoFragment fragment = new ReligionInfoFragment();

        Bundle arg = new Bundle();
        arg.putString(ARG_KEY_RELIGION_ID, religion_id);
        arg.putString(ARG_KEY_TYPE, "r");
        arg.putInt(ARG_KEY_MODE, mode);
        fragment.setArguments(arg);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        religion_id = getArguments().getString(ARG_KEY_RELIGION_ID);
        type = getArguments().getString(ARG_KEY_TYPE);
        mode = getArguments().getInt(ARG_KEY_MODE);
        if (mEventBus == null) {
            mEventBus = EventBus.getDefault();
        }
        mEventBus.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mEventBus != null) {
            mEventBus.unregister(this);
        }

        if (mp_god != null) {
            mp_god.stop();
            mp_god.release();
        }

        if (mp_flow != null) {
            mp_flow.stop();
            mp_flow.release();
        }

        if (mp_intro != null) {
            mp_intro.stop();
            mp_intro.release();
        }

        if (mp_fest != null) {
            mp_fest.stop();
            mp_fest.release();
        }

        if (mp_allusion != null) {
            mp_allusion.stop();
            mp_allusion.release();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_religion_info, container, false);
//        mView.setPadding(0, Tools.STATUS_BAR, 0, 0);

        mView.findViewById(R.id.fragment_religion_info_fl_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        if (mode == Y5CityText.RELIGION_INFO_ADD_POINT) {
            ((LinearLayout) mView.findViewById(R.id.fragment_religion_info_btn_ll)).setWeightSum(6);
            ((LinearLayout) mView.findViewById(R.id.fragment_religion_info_btn_ll_copy)).setWeightSum(6);
        } else {
            ((LinearLayout) mView.findViewById(R.id.fragment_religion_info_btn_ll)).setWeightSum(5);
            ((LinearLayout) mView.findViewById(R.id.fragment_religion_info_btn_ll_copy)).setWeightSum(5);
        }

        favorite_iv = mView.findViewById(R.id.fragment_religion_info_favorite_iv);
        mView.findViewById(R.id.fragment_religion_info_favorite_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Y5CityAPI.getInstance().setFavorite(getActivity(), religion_id,
                        UserInfoManager.Companion.getInstance().getUserLoginTokenLoginHint(getActivity()),
                        new NetworkManager.NetworkManagerListener<FavoriteResponse>() {
                            @Override
                            public void onSucceed(FavoriteResponse favoriteResponse) {
                                if (favoriteResponse.getInsert_success() == 1) {
                                    favorite_iv.setImageResource(R.mipmap.btn_favorite_r);
                                    update_fav = update_fav + 1;
                                    ((TextView) mView.findViewById(R.id.fragment_religion_info_favorite_tv))
                                            .setText(String.valueOf(update_fav));
                                }
                                if (favoriteResponse.getDelete_success() == 1) {
                                    favorite_iv.setImageResource(R.mipmap.btn_favorite_w);
                                    update_fav = update_fav - 1;
                                    ((TextView) mView.findViewById(R.id.fragment_religion_info_favorite_tv))
                                            .setText(String.valueOf(update_fav));
                                }
                            }

                            @Override
                            public void onFail(String errorMsg, boolean shouldRetry) {

                            }
                        });
            }
        });

        if (type.equals("r")) {
            Y5CityAPI.getInstance().getReligionInfo(getActivity(), religion_id, new NetworkManager.NetworkManagerListener<ReligionInfoFeedback>() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public void onSucceed(final ReligionInfoFeedback religionInfoFeedback) {
                    mReligionInfoFeedback = religionInfoFeedback;
                    if (mode == Y5CityText.RELIGION_INFO_ADD_POINT) {
                        if (religionInfoFeedback.getMatterport().length() != 0) {
                            ((LinearLayout) mView.findViewById(R.id.fragment_religion_info_btn_ll)).setWeightSum(6);
                            ((LinearLayout) mView.findViewById(R.id.fragment_religion_info_btn_ll_copy)).setWeightSum(6);
                        } else {
                            ((LinearLayout) mView.findViewById(R.id.fragment_religion_info_btn_ll)).setWeightSum(5);
                            ((LinearLayout) mView.findViewById(R.id.fragment_religion_info_btn_ll_copy)).setWeightSum(5);
                            mView.findViewById(R.id.fragment_religion_info_btn_matterport).setVisibility(View.GONE);
                            mView.findViewById(R.id.fragment_religion_info_btn_matterport_copy).setVisibility(View.GONE);
                        }
                    } else {
                        if (religionInfoFeedback.getMatterport().length() != 0) {
                            ((LinearLayout) mView.findViewById(R.id.fragment_religion_info_btn_ll)).setWeightSum(5);
                            ((LinearLayout) mView.findViewById(R.id.fragment_religion_info_btn_ll_copy)).setWeightSum(5);
                        } else {
                            ((LinearLayout) mView.findViewById(R.id.fragment_religion_info_btn_ll)).setWeightSum(4);
                            ((LinearLayout) mView.findViewById(R.id.fragment_religion_info_btn_ll_copy)).setWeightSum(4);
                            mView.findViewById(R.id.fragment_religion_info_btn_matterport).setVisibility(View.GONE);
                            mView.findViewById(R.id.fragment_religion_info_btn_matterport_copy).setVisibility(View.GONE);
                        }
                    }

                    if (LocaleManager.getLocale(getResources()).toString().equals("en")) {
                        title = religionInfoFeedback.getName_en();
                    } else {
                        title = religionInfoFeedback.getName();
                    }
                    if (UserInfoManager.Companion.getInstance().isLoggedIn(getActivity())) {
                        favorite_iv.setImageResource(DataCacheManager.getInstance().isFavorite(getActivity(), title) ? R.mipmap.btn_favorite_r : R.mipmap.btn_favorite_w);
                    }
                    latLng = new LatLng(religionInfoFeedback.getLat(), religionInfoFeedback.getLng());
                    if (LocaleManager.getLocale(getResources()).toString().equals("en")) {
                        ((TextView) mView.findViewById(R.id.fragment_religion_info_action_bar_title)).setText(religionInfoFeedback.getName_en());
                        ((TextView) mView.findViewById(R.id.fragment_religion_info_area_tv))
                                .setText(religionInfoFeedback.getArea_en());
                    } else {
                        ((TextView) mView.findViewById(R.id.fragment_religion_info_action_bar_title)).setText(religionInfoFeedback.getName());
                        ((TextView) mView.findViewById(R.id.fragment_religion_info_area_tv))
                                .setText(religionInfoFeedback.getArea());
                    }
                    current_fav = religionInfoFeedback.getTotal_fav();
                    update_fav = current_fav;
                    ((TextView) mView.findViewById(R.id.fragment_religion_info_favorite_tv)).setText(String.valueOf(religionInfoFeedback.getTotal_fav()));
                    final NetworkImageView banner_iv = mView.findViewById(R.id.fragment_religion_info_banner);
                    if (religionInfoFeedback.getImage().length() != 0) {
//                        new DownloadImageTask(banner_iv).execute(religionInfoFeedback.getImage());
                        NetworkManager.getInstance().setNetworkImage(getContext(),
                                banner_iv, religionInfoFeedback.getImage());
                        banner_iv.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                height = banner_iv.getHeight();
                                Log.e(LOG_TAG, "height" + height);
                            }
                        }, 3000);
                    }

                    //feature
                    final NetworkImageView feature_iv = mView.findViewById(R.id.fragment_religion_info_feature_iv);
                    if (religionInfoFeedback.getImage_map().length() != 0) {
                        NetworkManager.getInstance().setNetworkImage(getContext(),
                                feature_iv, religionInfoFeedback.getImage_map());
                        feature_iv.setVisibility(View.VISIBLE);
                    }
                    feature_ll = mView.findViewById(R.id.fragment_religion_info_feature_ll);
                    feature_iv.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                                float touchX, touchY, realX, realY;
                                if (religionInfoFeedback.getImage_map_w() != 0 &&
                                        religionInfoFeedback.getImage_map_h() != 0) {
                                    for (int i = 0; i < religionInfoFeedback.getExhibit().length; i++) {
                                        touchX = motionEvent.getX() / feature_iv.getWidth();
                                        realX = religionInfoFeedback.getExhibit()[i].getImage_map_x() / religionInfoFeedback.getImage_map_w();
                                        touchY = motionEvent.getY() / feature_iv.getHeight();
                                        realY = religionInfoFeedback.getExhibit()[i].getImage_map_y() / religionInfoFeedback.getImage_map_h();
                                        if (touchX < realX + 0.05 && touchX > realX - 0.05
                                                && touchY < realY + 0.05 && touchY > realY - 0.05) {
                                            View feature_info_view = getLayoutInflater().inflate(R.layout.item_religion_feature_dialog, null);
                                            if (LocaleManager.getLocale(getResources()).toString().equals("en")) {
                                                ((TextView) feature_info_view.findViewById(R.id.item_religion_feature_dialog_title))
                                                        .setText(religionInfoFeedback.getExhibit()[i].getName_en());
                                                ((TextView) feature_info_view.findViewById(R.id.item_religion_feature_dialog_desc))
                                                        .setText(religionInfoFeedback.getExhibit()[i].getIntro_en());
                                            } else {
                                                ((TextView) feature_info_view.findViewById(R.id.item_religion_feature_dialog_title))
                                                        .setText(religionInfoFeedback.getExhibit()[i].getName());
                                                ((TextView) feature_info_view.findViewById(R.id.item_religion_feature_dialog_desc))
                                                        .setText(religionInfoFeedback.getExhibit()[i].getIntro());
                                            }
                                            NetworkImageView feature_info_niv = feature_info_view.findViewById(R.id.item_religion_feature_dialog_niv);
                                            if (religionInfoFeedback.getExhibit()[i].getImage().length() != 0) {
                                                NetworkManager.getInstance().setNetworkImage(getContext(),
                                                        feature_info_niv, religionInfoFeedback.getExhibit()[i].getImage());
                                            }
                                        }
                                    }
                                }
                            }
                            return true;
                        }
                    });


                    //info
                    TextView address_tv = mView.findViewById(R.id.fragment_religion_info_address);
                    if (LocaleManager.getLocale(getResources()).toString().equals("en")) {
                        address_tv.setText(religionInfoFeedback.getAddress_en());
                    } else {
                        address_tv.setText(religionInfoFeedback.getAddress());
                    }
                    SpannableString address_content = new SpannableString(address_tv.getText());
                    address_content.setSpan(new UnderlineSpan(), 0, address_content.length(), 0);
                    address_tv.setText(address_content);
                    address_tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            gotoGoogleMap();
                        }
                    });

                    TextView web_tv = mView.findViewById(R.id.fragment_religion_info_web);
                    web_tv.setText(religionInfoFeedback.getWeb());
                    SpannableString content = new SpannableString(web_tv.getText());
                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                    web_tv.setText(content);
                    web_tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openFragmentPage(WebViewFragment.newInstance(title, religionInfoFeedback.getWeb()), WebViewFragment.TAG);
                        }
                    });

                    TextView call_tv = mView.findViewById(R.id.fragment_religion_info_call);
                    call_tv.setText(religionInfoFeedback.getTel());
                    SpannableString call_content = new SpannableString(call_tv.getText());
                    call_content.setSpan(new UnderlineSpan(), 0, call_content.length(), 0);
                    call_tv.setText(call_content);
                    call_tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + religionInfoFeedback.getTel()));
                            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(callIntent);
                        }
                    });

                    ((TextView) mView.findViewById(R.id.fragment_religion_info_opentime))
                            .setText(getResources().getString(R.string.fragment_religion_info_opne_time) + " " + religionInfoFeedback.getOpentime());
                    info_ll = mView.findViewById(R.id.fragment_religion_info_info_ll);

                    //flow
                    NetworkImageView flow_iv = mView.findViewById(R.id.fragment_religion_info_flow_iv);
                    if (religionInfoFeedback.getFlow().getImage().length() != 0) {
                        NetworkManager.getInstance().setNetworkImage(getContext(),
                                flow_iv, religionInfoFeedback.getFlow().getImage());
                        flow_iv.setVisibility(View.VISIBLE);
                    }
                    NetworkImageView flow_god_iv = mView.findViewById(R.id.fragment_religion_info_flow_god_iv);
                    if (religionInfoFeedback.getFlow().getGod_image().length() != 0) {
                        NetworkManager.getInstance().setNetworkImage(getContext(),
                                flow_god_iv, religionInfoFeedback.getFlow().getGod_image());
                        flow_god_iv.setVisibility(View.VISIBLE);
                    }
                    if (LocaleManager.getLocale(getResources()).toString().equals("en")) {
                        ((TextView) mView.findViewById(R.id.fragment_religion_info_flow_tv)).setText(religionInfoFeedback.getFlow().getDesc_en());
                        ((TextView) mView.findViewById(R.id.fragment_religion_info_god_tv)).setText(religionInfoFeedback.getFlow().getGod_en());
                    } else {
                        String desc = religionInfoFeedback.getFlow().getDesc().replace("&lt;", "<");
                        ((TextView) mView.findViewById(R.id.fragment_religion_info_flow_tv)).setText(desc);
                        ((TextView) mView.findViewById(R.id.fragment_religion_info_god_tv)).setText(religionInfoFeedback.getFlow().getGod());
                    }
                    flow_ll = mView.findViewById(R.id.fragment_religion_info_flow_ll);

                    try {
                        if (LocaleManager.getLocale(getResources()).toString().equals("en")) {
                            mp_god.setDataSource(getActivity(), Uri.parse(religionInfoFeedback.getFlow().getGod_audio_en()));
                        } else {
                            mp_god.setDataSource(getActivity(), Uri.parse(religionInfoFeedback.getFlow().getGod_audio()));
                        }
                        mp_god.prepareAsync();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mView.findViewById(R.id.fragment_religion_info_god_audio_iv).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!isPlaying) {
                                ((ImageView) mView.findViewById(R.id.fragment_religion_info_god_audio_iv)).setImageResource(R.mipmap.btn_paus_w);
                                isPlaying = true;
                                mp_god.start();
                            } else {
                                ((ImageView) mView.findViewById(R.id.fragment_religion_info_god_audio_iv)).setImageResource(R.mipmap.btn_play_w);
                                isPlaying = false;
                                mp_god.pause();
                            }
                        }
                    });
                    try {
                        if (LocaleManager.getLocale(getResources()).toString().equals("en")) {
                            mp_flow.setDataSource(getActivity(), Uri.parse(religionInfoFeedback.getFlow().getAudio_en()));
                        } else {
                            mp_flow.setDataSource(getActivity(), Uri.parse(religionInfoFeedback.getFlow().getAudio()));
                        }
                        mp_flow.prepareAsync();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mView.findViewById(R.id.fragment_religion_info_flow_audio_iv).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!isPlaying) {
                                ((ImageView) mView.findViewById(R.id.fragment_religion_info_flow_audio_iv)).setImageResource(R.mipmap.btn_paus_w);
                                isPlaying = true;
                                mp_flow.start();
                            } else {
                                ((ImageView) mView.findViewById(R.id.fragment_religion_info_flow_audio_iv)).setImageResource(R.mipmap.btn_play_w);
                                isPlaying = false;
                                mp_flow.pause();
                            }
                        }
                    });

                    //intro
                    NetworkImageView intro_iv = mView.findViewById(R.id.fragment_religion_info_intro_iv);
                    if (religionInfoFeedback.getIntro().getImage().length() != 0) {
                        NetworkManager.getInstance().setNetworkImage(getContext(),
                                intro_iv, religionInfoFeedback.getIntro().getImage());
                        intro_iv.setVisibility(View.VISIBLE);
                    }
                    if (LocaleManager.getLocale(getResources()).toString().equals("en")) {
                        ((TextView) mView.findViewById(R.id.fragment_religion_info_intro_desc_tv)).setText(religionInfoFeedback.getIntro().getDesc_en());
                        ((TextView) mView.findViewById(R.id.fragment_religion_info_intro_fest_tv)).setText(religionInfoFeedback.getIntro().getFestival_en());
                    } else {
                        ((TextView) mView.findViewById(R.id.fragment_religion_info_intro_desc_tv)).setText(religionInfoFeedback.getIntro().getDesc());
                        ((TextView) mView.findViewById(R.id.fragment_religion_info_intro_fest_tv)).setText(religionInfoFeedback.getIntro().getFestival());
                    }
                    intro_ll = mView.findViewById(R.id.fragment_religion_info_intro_ll);
                    try {
                        if (LocaleManager.getLocale(getResources()).toString().equals("en")) {
                            mp_intro.setDataSource(getActivity(), Uri.parse(religionInfoFeedback.getIntro().getAudio_en()));
                        } else {
                            mp_intro.setDataSource(getActivity(), Uri.parse(religionInfoFeedback.getIntro().getAudio()));
                        }
                        mp_intro.prepareAsync();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mView.findViewById(R.id.fragment_religion_info_intro_audio_iv).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!isPlaying) {
                                ((ImageView) mView.findViewById(R.id.fragment_religion_info_intro_audio_iv)).setImageResource(R.mipmap.btn_paus_w);
                                isPlaying = true;
                                mp_intro.start();
                            } else {
                                ((ImageView) mView.findViewById(R.id.fragment_religion_info_intro_audio_iv)).setImageResource(R.mipmap.btn_play_w);
                                isPlaying = false;
                                mp_intro.pause();
                            }
                        }
                    });
                    try {
                        if (LocaleManager.getLocale(getResources()).toString().equals("en")) {
                            mp_fest.setDataSource(getActivity(), Uri.parse(religionInfoFeedback.getIntro().getFestival_audio_en()));
                        } else {
                            mp_fest.setDataSource(getActivity(), Uri.parse(religionInfoFeedback.getIntro().getFestival_audio()));
                        }
                        mp_fest.prepareAsync();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mView.findViewById(R.id.fragment_religion_info_fest_audio_iv).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!isPlaying) {
                                ((ImageView) mView.findViewById(R.id.fragment_religion_info_fest_audio_iv)).setImageResource(R.mipmap.btn_paus_w);
                                isPlaying = true;
                                mp_fest.start();
                            } else {
                                ((ImageView) mView.findViewById(R.id.fragment_religion_info_fest_audio_iv)).setImageResource(R.mipmap.btn_play_w);
                                isPlaying = false;
                                mp_fest.pause();
                            }
                        }
                    });

                    //allusion
                    NetworkImageView allusion_iv = mView.findViewById(R.id.fragment_religion_info_allusion_iv);
                    if (religionInfoFeedback.getMiracle().getImage().length() != 0) {
                        NetworkManager.getInstance().setNetworkImage(getContext(),
                                allusion_iv, religionInfoFeedback.getMiracle().getImage());
                        allusion_iv.setVisibility(View.VISIBLE);
                    }
                    if (LocaleManager.getLocale(getResources()).toString().equals("en")) {
                        ((TextView) mView.findViewById(R.id.fragment_religion_info_allusion_tv)).setText(religionInfoFeedback.getMiracle().getDesc_en());
                    } else {
                        ((TextView) mView.findViewById(R.id.fragment_religion_info_allusion_tv)).setText(religionInfoFeedback.getMiracle().getDesc());
                    }
                    allusion_ll = mView.findViewById(R.id.fragment_religion_info_allusion_ll);
                    try {
                        mp_allusion.setDataSource(getActivity(), Uri.parse(religionInfoFeedback.getMiracle().getAudio()));
                        mp_allusion.prepareAsync();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mView.findViewById(R.id.fragment_religion_info_allusion_audio_iv).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!isPlaying) {
                                ((ImageView) mView.findViewById(R.id.fragment_religion_info_allusion_audio_iv)).setImageResource(R.mipmap.btn_paus_w);
                                isPlaying = true;
                                mp_allusion.start();
                            } else {
                                ((ImageView) mView.findViewById(R.id.fragment_religion_info_allusion_audio_iv)).setImageResource(R.mipmap.btn_play_w);
                                isPlaying = false;
                                mp_allusion.pause();
                            }
                        }
                    });


                    mView.findViewById(R.id.fragment_religion_info_feature).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            feature_ll.setVisibility(View.VISIBLE);
                            flow_ll.setVisibility(View.GONE);
                            intro_ll.setVisibility(View.GONE);
                            allusion_ll.setVisibility(View.GONE);
                            info_ll.setVisibility(View.GONE);
                            if (type.equals("r") && mode == Y5CityText.RELIGION_INFO_ADD_POINT) {
                                ((TextView) mView.findViewById(R.id.fragment_religion_info_goto)).setText(R.string.fragment_religion_info_add_point);
                                mView.findViewById(R.id.fragment_religion_info_goto).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        openFragmentPage(CustomSetupFragment.newInstance(religionInfoFeedback), CustomSetupFragment.TAG);
                                    }
                                });
                            }
                        }
                    });

                    mView.findViewById(R.id.fragment_religion_info_feature_copy).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            feature_ll.setVisibility(View.VISIBLE);
                            flow_ll.setVisibility(View.GONE);
                            intro_ll.setVisibility(View.GONE);
                            allusion_ll.setVisibility(View.GONE);
                            info_ll.setVisibility(View.GONE);
                            if (type.equals("r") && mode == Y5CityText.RELIGION_INFO_ADD_POINT) {
                                ((TextView) mView.findViewById(R.id.fragment_religion_info_goto)).setText(R.string.fragment_religion_info_add_point);
                                mView.findViewById(R.id.fragment_religion_info_goto).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        openFragmentPage(CustomSetupFragment.newInstance(religionInfoFeedback), CustomSetupFragment.TAG);
                                    }
                                });
                            }
                        }
                    });

                    mView.findViewById(R.id.fragment_religion_info_visiting).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            feature_ll.setVisibility(View.GONE);
                            flow_ll.setVisibility(View.VISIBLE);
                            intro_ll.setVisibility(View.GONE);
                            allusion_ll.setVisibility(View.GONE);
                            info_ll.setVisibility(View.GONE);
                            if (type.equals("r") && mode == Y5CityText.RELIGION_INFO_ADD_POINT) {
                                ((TextView) mView.findViewById(R.id.fragment_religion_info_goto)).setText(R.string.fragment_religion_info_add_point);
                                mView.findViewById(R.id.fragment_religion_info_goto).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        openFragmentPage(CustomSetupFragment.newInstance(religionInfoFeedback), CustomSetupFragment.TAG);
                                    }
                                });
                            }
//                            openFragmentPage(FlowFragment.newInstance(religionInfoFeedback.getName(),
//                                    religionInfoFeedback.getFlow().getDesc(),
//                                    religionInfoFeedback.getFlow().getImage(),
//                                    religionInfoFeedback.getFlow().getGod()), AllusionFragment.TAG);
                        }
                    });

                    mView.findViewById(R.id.fragment_religion_info_visiting_copy).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            feature_ll.setVisibility(View.GONE);
                            flow_ll.setVisibility(View.VISIBLE);
                            intro_ll.setVisibility(View.GONE);
                            allusion_ll.setVisibility(View.GONE);
                            info_ll.setVisibility(View.GONE);
                            if (type.equals("r") && mode == Y5CityText.RELIGION_INFO_ADD_POINT) {
                                ((TextView) mView.findViewById(R.id.fragment_religion_info_goto)).setText(R.string.fragment_religion_info_add_point);
                                mView.findViewById(R.id.fragment_religion_info_goto).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        openFragmentPage(CustomSetupFragment.newInstance(religionInfoFeedback), CustomSetupFragment.TAG);
                                    }
                                });
                            }
                        }
                    });

                    mView.findViewById(R.id.fragment_religion_info_intro).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            feature_ll.setVisibility(View.GONE);
                            flow_ll.setVisibility(View.GONE);
                            intro_ll.setVisibility(View.VISIBLE);
                            allusion_ll.setVisibility(View.GONE);
                            info_ll.setVisibility(View.GONE);
                            if (type.equals("r") && mode == Y5CityText.RELIGION_INFO_ADD_POINT) {
                                ((TextView) mView.findViewById(R.id.fragment_religion_info_goto)).setText(R.string.fragment_religion_info_add_point);
                                mView.findViewById(R.id.fragment_religion_info_goto).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        openFragmentPage(CustomSetupFragment.newInstance(religionInfoFeedback), CustomSetupFragment.TAG);
                                    }
                                });
                            }
//                            openFragmentPage(IntroFragment.newInstance(religionInfoFeedback.getName(),
//                                    religionInfoFeedback.getIntro().getDesc(),
//                                    religionInfoFeedback.getIntro().getImage(),
//                                    religionInfoFeedback.getIntro().getFestival()), AllusionFragment.TAG);
                        }
                    });

                    mView.findViewById(R.id.fragment_religion_info_intro_copy).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            feature_ll.setVisibility(View.GONE);
                            flow_ll.setVisibility(View.GONE);
                            intro_ll.setVisibility(View.VISIBLE);
                            allusion_ll.setVisibility(View.GONE);
                            info_ll.setVisibility(View.GONE);
                            if (type.equals("r") && mode == Y5CityText.RELIGION_INFO_ADD_POINT) {
                                ((TextView) mView.findViewById(R.id.fragment_religion_info_goto)).setText(R.string.fragment_religion_info_add_point);
                                mView.findViewById(R.id.fragment_religion_info_goto).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        openFragmentPage(CustomSetupFragment.newInstance(religionInfoFeedback), CustomSetupFragment.TAG);
                                    }
                                });
                            }
                        }
                    });

                    if (religionInfoFeedback.getMiracle().getDesc().length() == 0) {
                        mView.findViewById(R.id.fragment_religion_info_allusion).setVisibility(View.GONE);
                        mView.findViewById(R.id.fragment_religion_info_allusion_copy).setVisibility(View.GONE);
                        ((LinearLayout) mView.findViewById(R.id.fragment_religion_info_func_ll)).setWeightSum(4);
                        ((LinearLayout) mView.findViewById(R.id.fragment_religion_info_func_ll_copy)).setWeightSum(4);
                    }
                    mView.findViewById(R.id.fragment_religion_info_allusion).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            openFragmentPage(AllusionFragment.newInstance(religionInfoFeedback.getName(),
//                                    religionInfoFeedback.getMiracle().getDesc(),
//                                    religionInfoFeedback.getMiracle().getImage()), AllusionFragment.TAG);
                            feature_ll.setVisibility(View.GONE);
                            flow_ll.setVisibility(View.GONE);
                            intro_ll.setVisibility(View.GONE);
                            allusion_ll.setVisibility(View.VISIBLE);
                            info_ll.setVisibility(View.GONE);
                            if (type.equals("r") && mode == Y5CityText.RELIGION_INFO_ADD_POINT) {
                                ((TextView) mView.findViewById(R.id.fragment_religion_info_goto)).setText(R.string.fragment_religion_info_add_point);
                                mView.findViewById(R.id.fragment_religion_info_goto).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        openFragmentPage(CustomSetupFragment.newInstance(religionInfoFeedback), CustomSetupFragment.TAG);
                                    }
                                });
                            }
                        }
                    });

                    mView.findViewById(R.id.fragment_religion_info_allusion_copy).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            feature_ll.setVisibility(View.GONE);
                            flow_ll.setVisibility(View.GONE);
                            intro_ll.setVisibility(View.GONE);
                            allusion_ll.setVisibility(View.VISIBLE);
                            info_ll.setVisibility(View.GONE);
                            if (type.equals("r") && mode == Y5CityText.RELIGION_INFO_ADD_POINT) {
                                ((TextView) mView.findViewById(R.id.fragment_religion_info_goto)).setText(R.string.fragment_religion_info_add_point);
                                mView.findViewById(R.id.fragment_religion_info_goto).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        openFragmentPage(CustomSetupFragment.newInstance(religionInfoFeedback), CustomSetupFragment.TAG);
                                    }
                                });
                            }
                        }
                    });

                    mView.findViewById(R.id.fragment_religion_info_info).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            feature_ll.setVisibility(View.GONE);
                            flow_ll.setVisibility(View.GONE);
                            intro_ll.setVisibility(View.GONE);
                            allusion_ll.setVisibility(View.GONE);
                            info_ll.setVisibility(View.VISIBLE);
                            if (type.equals("r") && mode == Y5CityText.RELIGION_INFO_ADD_POINT) {
                                ((TextView) mView.findViewById(R.id.fragment_religion_info_goto)).setText(R.string.fragment_religion_info_goto);
                                mView.findViewById(R.id.fragment_religion_info_goto).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        gotoGoogleMap();
                                    }
                                });
                            }
                        }
                    });

                    mView.findViewById(R.id.fragment_religion_info_info_copy).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            feature_ll.setVisibility(View.GONE);
                            flow_ll.setVisibility(View.GONE);
                            intro_ll.setVisibility(View.GONE);
                            allusion_ll.setVisibility(View.GONE);
                            info_ll.setVisibility(View.VISIBLE);
                            if (type.equals("r") && mode == Y5CityText.RELIGION_INFO_ADD_POINT) {
                                ((TextView) mView.findViewById(R.id.fragment_religion_info_goto)).setText(R.string.fragment_religion_info_goto);
                                mView.findViewById(R.id.fragment_religion_info_goto).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        gotoGoogleMap();
                                    }
                                });
                            }
                        }
                    });

//                    mView.findViewById(R.id.fragment_religion_info_btn_matterport).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if (religionInfoFeedback.getPano().length() != 0) {
//                                Intent intent = new Intent(getActivity(), WebViewActivity.class);
//                                intent.putExtra(Y5CityText.KS_PANO_TITLE, title);
//                                intent.putExtra(Y5CityText.KS_PANO_URL, religionInfoFeedback.getMatterport());
//                                startActivity(intent);
//                            }
//                        }
//                    });
//
//                    mView.findViewById(R.id.fragment_religion_info_btn_matterport_copy).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if (religionInfoFeedback.getPano().length() != 0) {
//                                Intent intent = new Intent(getActivity(), WebViewActivity.class);
//                                intent.putExtra(Y5CityText.KS_PANO_TITLE, title);
//                                intent.putExtra(Y5CityText.KS_PANO_URL, religionInfoFeedback.getMatterport());
//                                startActivity(intent);
//                            }
//                        }
//                    });

//                    mView.findViewById(R.id.fragment_religion_info_btn_pano).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if (religionInfoFeedback.getPano().length() != 0) {
//                                Intent intent = new Intent(getActivity(), WebViewActivity.class);
//                                intent.putExtra(Y5CityText.KS_PANO_TITLE, title);
//                                intent.putExtra(Y5CityText.KS_PANO_URL, religionInfoFeedback.getPano());
//                                startActivity(intent);
////                                openFragmentPage(WebViewFragment.newInstance(title, religionInfoFeedback.getPano()), WebViewFragment.TAG);
////                                Uri uri = Uri.parse(religionInfoFeedback.getPano());
////                                Intent myIntent = new Intent(Intent.ACTION_VIEW, uri);
////                                startActivity(myIntent);
//                            }
//                        }
//                    });
//
//                    mView.findViewById(R.id.fragment_religion_info_btn_pano_copy).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if (religionInfoFeedback.getPano().length() != 0) {
//                                Intent intent = new Intent(getActivity(), WebViewActivity.class);
//                                intent.putExtra(Y5CityText.KS_PANO_TITLE, title);
//                                intent.putExtra(Y5CityText.KS_PANO_URL, religionInfoFeedback.getPano());
//                                startActivity(intent);
////                                Uri uri = Uri.parse(religionInfoFeedback.getPano());
////                                Intent myIntent = new Intent(Intent.ACTION_VIEW, uri);
////                                startActivity(myIntent);
//                            }
//                        }
//                    });

//                    mView.findViewById(R.id.fragment_religion_info_btn_comment).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if (religionInfoFeedback.getPano().length() != 0) {
//                                if (MainActivity.locale.equals("en")) {
//                                    openFragmentPage(WebViewFragment.newInstance(title, religionInfoFeedback.getGoogle_comment() + "&hl=en"), WebViewFragment.TAG);
//                                } else {
//                                    openFragmentPage(WebViewFragment.newInstance(title, religionInfoFeedback.getGoogle_comment()), WebViewFragment.TAG);
//                                }
//                            }
//                        }
//                    });

                    mView.findViewById(R.id.fragment_religion_info_btn_comment_copy).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (religionInfoFeedback.getPano().length() != 0) {
                                openFragmentPage(WebViewFragment.newInstance(title, religionInfoFeedback.getGoogle_comment()), WebViewFragment.TAG);
                            }
                        }
                    });

                    mView.findViewById(R.id.fragment_religion_info_btn_traffic).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (religionInfoFeedback.getPano().length() != 0) {
                                openFragmentPage(WebViewFragment.newInstance(title,
                                        Y5CityText.RELIGION_INFO_TRAFFIC_URL + religionInfoFeedback.getRl_id()), WebViewFragment.TAG);
                            }
                        }
                    });

                    mView.findViewById(R.id.fragment_religion_info_btn_traffic_copy).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (religionInfoFeedback.getPano().length() != 0) {
                                openFragmentPage(WebViewFragment.newInstance(title,
                                        Y5CityText.RELIGION_INFO_TRAFFIC_URL + religionInfoFeedback.getRl_id()), WebViewFragment.TAG);
                            }
                        }
                    });

                    mView.findViewById(R.id.fragment_religion_info_btn_nearby).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (religionInfoFeedback.getPano().length() != 0) {
                                openFragmentPage(PointMapFragment.newInstance("nearby", title,
                                        religionInfoFeedback.getLat(), religionInfoFeedback.getLng()), PointMapFragment.TAG);
//                                openFragmentPage(WebViewFragment.newInstance(title,
//                                        Y5CityText.RELIGION_INFO_NEARBY_URL + religionInfoFeedback.getRl_id()), WebViewFragment.TAG);
                            }
                        }
                    });

                    mView.findViewById(R.id.fragment_religion_info_btn_nearby_copy).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (religionInfoFeedback.getPano().length() != 0) {
                                openFragmentPage(PointMapFragment.newInstance("nearby", title,
                                        religionInfoFeedback.getLat(), religionInfoFeedback.getLng()), PointMapFragment.TAG);
//                                openFragmentPage(WebViewFragment.newInstance(title,
//                                        Y5CityText.RELIGION_INFO_NEARBY_URL + religionInfoFeedback.getRl_id()), WebViewFragment.TAG);
                            }
                        }
                    });

                    mView.findViewById(R.id.fragment_religion_info_btn_add_point).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openFragmentPage(CustomSetupFragment.newInstance(religionInfoFeedback), CustomSetupFragment.TAG);
                        }
                    });

                    mView.findViewById(R.id.fragment_religion_info_btn_add_point_copy).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openFragmentPage(CustomSetupFragment.newInstance(religionInfoFeedback), CustomSetupFragment.TAG);
                        }
                    });

                    banner_fl = mView.findViewById(R.id.fragment_religion_info_banner_fl);
                    mScrollView = mView.findViewById(R.id.fragment_religion_info_sv);
                    mScrollView.setEnabled(false);
                    mScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                        @Override
                        public void onScrollChanged() {
                            int scrollY = mScrollView.getScrollY();
                            if (height != 0 && scrollY > height) {
                                mView.findViewById(R.id.fragment_religion_info_btn_ll_copy).setVisibility(View.VISIBLE);
                                mView.findViewById(R.id.fragment_religion_info_func_ll_copy).setVisibility(View.VISIBLE);
                            } else {
                                mView.findViewById(R.id.fragment_religion_info_btn_ll_copy).setVisibility(View.GONE);
                                mView.findViewById(R.id.fragment_religion_info_func_ll_copy).setVisibility(View.GONE);
                            }
                        }
                    });
                }

                @Override
                public void onFail(String errorMsg, boolean shouldRetry) {

                }
            });
        } else {
            Y5CityAPI.getInstance().getPointInfo(getActivity(), religion_id, new NetworkManager.NetworkManagerListener<PointInfoFeedback>() {
                @Override
                public void onSucceed(final PointInfoFeedback pointInfoFeedback) {
                    title = pointInfoFeedback.getName();
                    if (UserInfoManager.Companion.getInstance().isLoggedIn(getActivity())) {
                        favorite_iv.setImageResource(DataCacheManager.getInstance().isFavorite(getActivity(), title) ? R.mipmap.btn_favorite_r : R.mipmap.btn_favorite_w);
                    }
                    latLng = new LatLng(pointInfoFeedback.getLat(), pointInfoFeedback.getLng());
                    if (LocaleManager.getLocale(getResources()).toString().equals("en")) {
                        ((TextView) mView.findViewById(R.id.fragment_religion_info_action_bar_title)).setText(pointInfoFeedback.getName_en());
                        ((TextView) mView.findViewById(R.id.fragment_religion_info_area_tv))
                                .setText(pointInfoFeedback.getArea_en());
                    } else {
                        ((TextView) mView.findViewById(R.id.fragment_religion_info_action_bar_title)).setText(pointInfoFeedback.getName());
                        ((TextView) mView.findViewById(R.id.fragment_religion_info_area_tv))
                                .setText(pointInfoFeedback.getArea());
                    }
                    current_fav = pointInfoFeedback.getTotal_fav();
                    update_fav = current_fav;
                    ((TextView) mView.findViewById(R.id.fragment_religion_info_favorite_tv)).setText(String.valueOf(pointInfoFeedback.getTotal_fav()));
                    TextView address_tv = mView.findViewById(R.id.fragment_religion_info_address);
                    if (LocaleManager.getLocale(getResources()).toString().equals("en")) {
                        address_tv.setText(pointInfoFeedback.getAddress_en());
                    } else {
                        address_tv.setText(pointInfoFeedback.getAddress());
                    }
                    SpannableString address_content = new SpannableString(address_tv.getText());
                    address_content.setSpan(new UnderlineSpan(), 0, address_content.length(), 0);
                    address_tv.setText(address_content);
                    address_tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            gotoGoogleMap();
                        }
                    });
                    TextView web_tv = mView.findViewById(R.id.fragment_religion_info_web);
                    web_tv.setText(pointInfoFeedback.getWeb());
                    SpannableString content = new SpannableString(web_tv.getText());
                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                    web_tv.setText(content);
                    web_tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openFragmentPage(WebViewFragment.newInstance(title, pointInfoFeedback.getWeb()), WebViewFragment.TAG);
                        }
                    });

                    TextView call_tv = mView.findViewById(R.id.fragment_religion_info_call);
                    call_tv.setText(pointInfoFeedback.getTel());
                    SpannableString call_content = new SpannableString(call_tv.getText());
                    call_content.setSpan(new UnderlineSpan(), 0, call_content.length(), 0);
                    call_tv.setText(call_content);
                    call_tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + pointInfoFeedback.getTel()));
                            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(callIntent);
                        }
                    });

                    ((TextView) mView.findViewById(R.id.fragment_religion_info_opentime))
                            .setText(getResources().getString(R.string.fragment_religion_info_opne_time) + " " + pointInfoFeedback.getOpentime());
                    NetworkImageView banner_iv = mView.findViewById(R.id.fragment_religion_info_banner);
                    if (pointInfoFeedback.getImage().length() != 0) {
//                        new DownloadImageTask(banner_iv).execute(pointInfoFeedback.getImage());
                        NetworkManager.getInstance().setNetworkImage(getContext(),
                                banner_iv, pointInfoFeedback.getImage());
                    }
                    mView.findViewById(R.id.fragment_religion_info_btn_ll).setVisibility(View.GONE);
                    mView.findViewById(R.id.fragment_religion_info_func_ll).setVisibility(View.GONE);
                    info_ll = mView.findViewById(R.id.fragment_religion_info_info_ll);
                    info_ll.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFail(String errorMsg, boolean shouldRetry) {

                }
            });
        }

        if (type.equals("r") && mode == Y5CityText.RELIGION_INFO_ADD_POINT) {
            ((TextView) mView.findViewById(R.id.fragment_religion_info_goto)).setText(R.string.fragment_religion_info_add_point);
            mView.findViewById(R.id.fragment_religion_info_goto).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFragmentPage(CustomSetupFragment.newInstance(mReligionInfoFeedback), CustomSetupFragment.TAG);
                }
            });
        } else {
            mView.findViewById(R.id.fragment_religion_info_goto).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoGoogleMap();
                }
            });
        }

        return mView;
    }

    private void checkLocationService() {
        LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            ensurePermissions();
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
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
                }
            });
            dialog.show();
        }
    }

    private void ensurePermissions() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    99);

        }
    }

    private void gotoGoogleMap() {
        checkLocationService();
        if (mLastLocation != null) {
            double startLatitude = mLastLocation.getLatitude();
            double startLongitude = mLastLocation.getLongitude();
            double endLatitude = latLng.latitude;
            double endLongitude = latLng.longitude;

            String saddr = "saddr=" + startLatitude + "," + startLongitude;
            String daddr = "daddr=" + endLatitude + "," + endLongitude;
            String uriString = "http://maps.google.com/maps?" + saddr + "&" + daddr;

            Uri uri = Uri.parse(uriString);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            startActivity(intent);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (current_fav != update_fav) {
            EventBus.getDefault().post(new OmniEvent(OmniEvent.TYPE_FAVORITE_CHANGED, ""));
        }
    }

    private void openFragmentPage(Fragment fragment, String tag) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_main_fl, fragment, tag)
                .addToBackStack(null)
                .commit();
    }

}
