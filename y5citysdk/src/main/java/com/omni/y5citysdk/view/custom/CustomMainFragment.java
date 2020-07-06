package com.omni.y5citysdk.view.custom;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.omni.y5citysdk.R;
import com.omni.y5citysdk.manager.UserInfoManager;
import com.omni.y5citysdk.module.OmniEvent;
import com.omni.y5citysdk.module.trip.UserTripData;
import com.omni.y5citysdk.module.trip.UserTripInfoFeedback;
import com.omni.y5citysdk.module.trip.UserTripUpdateData;
import com.omni.y5citysdk.network.NetworkManager;
import com.omni.y5citysdk.network.Y5CityAPI;
import com.omni.y5citysdk.tool.Y5CityText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static android.content.Context.CLIPBOARD_SERVICE;
import static com.omni.y5citysdk.Y5CitySDKActivity.loginToken;
import static com.omni.y5citysdk.tool.Y5CityText.LOG_TAG;

public class CustomMainFragment extends Fragment {

    public static final String TAG = "fragment_tag_custom_main";

    private Context mContext;
    private View mView;
    private EventBus mEventBus;
    private String[] title;
    private String[] imageUrl;
    private String[] t_id;
    private CustomGridAdapter adapter;
    private GridView gridView;
    private LinearLayout item_menu_ll;
    private ImageView menu_bg;
    private int currentPos = 0;
    private ClipboardManager myClipboard;
    private ClipData myClip;
    private UserTripData[] mUserTripData;


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(OmniEvent event) {
        switch (event.getType()) {
            case OmniEvent.TYPE_USER_TRIP_UPDATE:
                Y5CityAPI.getInstance().getUserTrip(getActivity(), UserInfoManager.Companion.getInstance().getUserLoginToken(getActivity()),
                        new NetworkManager.NetworkManagerListener<UserTripData[]>() {
                            @Override
                            public void onSucceed(final UserTripData[] userTripData) {
                                title = new String[userTripData.length + 1];
                                imageUrl = new String[userTripData.length + 1];
                                t_id = new String[userTripData.length];
                                for (int i = 0; i < userTripData.length; i++) {
                                    title[i] = userTripData[i].getT_title();
                                    imageUrl[i] = userTripData[i].getT_image();
                                    t_id[i] = userTripData[i].getT_id();
                                }
                                title[userTripData.length] = getResources().getString(R.string.fragment_custom_add);
                                imageUrl[userTripData.length] = "new";
                                adapter = new CustomGridAdapter(getActivity(), title, imageUrl);
                                gridView.setAdapter(adapter);
                                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                        if (position == userTripData.length) {
                                            openFragmentPage(CustomSetupFragment.newInstance(), CustomSetupFragment.TAG);
                                        } else {
                                            Y5CityAPI.getInstance().getUserTripInfo(getActivity(), userTripData[position].getT_id(),
                                                    new NetworkManager.NetworkManagerListener<UserTripInfoFeedback>() {
                                                        @Override
                                                        public void onSucceed(UserTripInfoFeedback userTripInfoFeedback) {
                                                            openFragmentPage(CustomRouteFragment.newInstance(
                                                                    userTripData[position].getT_title(),
                                                                    userTripData[position].getT_start_date(),
                                                                    userTripData[position].getT_image(),
                                                                    userTripData[position].getT_start_time()[0],
                                                                    userTripData[position].getT_id(),
                                                                    "custom",
                                                                    userTripInfoFeedback.getTrip_days()), CustomRouteFragment.TAG);
                                                        }

                                                        @Override
                                                        public void onFail(String errorMsg, boolean shouldRetry) {

                                                        }
                                                    });
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onFail(String errorMsg, boolean shouldRetry) {

                            }
                        });
                break;
        }
    }

    public static CustomMainFragment newInstance() {
        return new CustomMainFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
Log.e(LOG_TAG,"onCreateView");
        mView = inflater.inflate(R.layout.fragment_theme_custom, container, false);

        menu_bg = mView.findViewById(R.id.fragment_custom_main_menu_bg);
        item_menu_ll = mView.findViewById(R.id.fragment_custom_main_menu_ll);
        item_menu_ll.findViewById(R.id.fragment_custom_main_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu_bg.setVisibility(View.GONE);
                item_menu_ll.setVisibility(View.GONE);
            }
        });
        item_menu_ll.findViewById(R.id.fragment_custom_main_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Y5CityAPI.getInstance().userTripDelete(getActivity(), UserInfoManager.Companion.getInstance().getUserLoginToken(getActivity()),
                        t_id[currentPos], new NetworkManager.NetworkManagerListener<UserTripUpdateData[]>() {
                            @Override
                            public void onSucceed(UserTripUpdateData[] userTripUpdateData) {
                                EventBus.getDefault().post(new OmniEvent(OmniEvent.TYPE_USER_TRIP_UPDATE, ""));
                            }

                            @Override
                            public void onFail(String errorMsg, boolean shouldRetry) {
                            }
                        });
                menu_bg.setVisibility(View.GONE);
                item_menu_ll.setVisibility(View.GONE);
            }
        });
        item_menu_ll.findViewById(R.id.fragment_custom_main_master_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUserTripData[currentPos].getT_share().equals("N")) {
                    Y5CityAPI.getInstance().userTripShare(getActivity(), UserInfoManager.Companion.getInstance().getUserLoginToken(getActivity()),
                            t_id[currentPos], "Y", new NetworkManager.NetworkManagerListener<UserTripUpdateData[]>() {
                                @Override
                                public void onSucceed(UserTripUpdateData[] userTripUpdateData) {
                                    mUserTripData[currentPos].setT_share("Y");
                                    Toast.makeText(getActivity(), R.string.fragment_custom_main_master_share_done, Toast.LENGTH_SHORT).show();
                                    EventBus.getDefault().post(new OmniEvent(OmniEvent.TYPE_MASTER_SHARE_UPDATE, ""));
                                }

                                @Override
                                public void onFail(String errorMsg, boolean shouldRetry) {
                                }
                            });
                } else {
                    Y5CityAPI.getInstance().userTripShare(getActivity(), UserInfoManager.Companion.getInstance().getUserLoginToken(getActivity()),
                            t_id[currentPos], "N", new NetworkManager.NetworkManagerListener<UserTripUpdateData[]>() {
                                @Override
                                public void onSucceed(UserTripUpdateData[] userTripUpdateData) {
                                    mUserTripData[currentPos].setT_share("N");
                                    Toast.makeText(getActivity(), R.string.fragment_custom_main_master_share_cancel_done, Toast.LENGTH_SHORT).show();
                                    EventBus.getDefault().post(new OmniEvent(OmniEvent.TYPE_MASTER_SHARE_UPDATE, ""));
                                }

                                @Override
                                public void onFail(String errorMsg, boolean shouldRetry) {
                                }
                            });
                }
                menu_bg.setVisibility(View.GONE);
                item_menu_ll.setVisibility(View.GONE);
            }
        });
        item_menu_ll.findViewById(R.id.fragment_custom_main_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Y5CityAPI.getInstance().userTripShare(getActivity(), UserInfoManager.Companion.getInstance().getUserLoginToken(getActivity()),
                        t_id[currentPos], "Y", new NetworkManager.NetworkManagerListener<UserTripUpdateData[]>() {
                            @Override
                            public void onSucceed(UserTripUpdateData[] userTripUpdateData) {
                            }

                            @Override
                            public void onFail(String errorMsg, boolean shouldRetry) {
                            }
                        });

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, Y5CityText.MASTER_SHARE_URL + t_id[currentPos]);

                String title = getResources().getString(R.string.fragment_custom_main_share_to);
                Intent chooser = Intent.createChooser(sharingIntent, title);
                if (sharingIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(chooser);
                }

                menu_bg.setVisibility(View.GONE);
                item_menu_ll.setVisibility(View.GONE);

//                startActivity(Intent.createChooser(sharingIntent, chooserTitle));
//                View item = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_share, null);
//                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                builder.setView(item);
//                final AlertDialog alertDialog = builder.create();
//                alertDialog.show();
//                DisplayMetrics displaymetrics = new DisplayMetrics();
//                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//                int screenWidth = displaymetrics.widthPixels;
//                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//                lp.copyFrom(alertDialog.getWindow().getAttributes());
//                lp.width = (int) (screenWidth * 0.7);
//                alertDialog.getWindow().setAttributes(lp);
//                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//                item.findViewById(R.id.dialog_share_copy_link_ll).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        copyToClipBoard();
//                        alertDialog.dismiss();
//                    }
//                });
            }
        });
        Y5CityAPI.getInstance().getUserTrip(getActivity(), loginToken,
                new NetworkManager.NetworkManagerListener<UserTripData[]>() {
                    @Override
                    public void onSucceed(final UserTripData[] userTripData) {
                        mUserTripData = userTripData;
                        title = new String[userTripData.length + 1];
                        imageUrl = new String[userTripData.length + 1];
                        t_id = new String[userTripData.length];
                        for (int i = 0; i < userTripData.length; i++) {
                            title[i] = userTripData[i].getT_title();
                            imageUrl[i] = userTripData[i].getT_image();
                            t_id[i] = userTripData[i].getT_id();
                        }
                        title[userTripData.length] = getResources().getString(R.string.fragment_custom_add);
                        imageUrl[userTripData.length] = "new";

                        adapter = new CustomGridAdapter(getActivity(), title, imageUrl);
                        gridView = mView.findViewById(R.id.fragment_custom_main_grid_view);
                        gridView.setAdapter(adapter);
                        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                if (position == userTripData.length) {
                                    openFragmentPage(CustomSetupFragment.newInstance(), CustomSetupFragment.TAG);
                                } else {
                                    Y5CityAPI.getInstance().getUserTripInfo(getActivity(), userTripData[position].getT_id(),
                                            new NetworkManager.NetworkManagerListener<UserTripInfoFeedback>() {
                                                @Override
                                                public void onSucceed(UserTripInfoFeedback userTripInfoFeedback) {
                                                    openFragmentPage(CustomRouteFragment.newInstance(
                                                            userTripData[position].getT_title(),
                                                            userTripData[position].getT_start_date(),
                                                            userTripData[position].getT_image(),
                                                            userTripData[position].getT_start_time()[0],
                                                            userTripData[position].getT_id(),
                                                            "custom",
                                                            userTripInfoFeedback.getTrip_days()), CustomRouteFragment.TAG);
                                                }

                                                @Override
                                                public void onFail(String errorMsg, boolean shouldRetry) {

                                                }
                                            });
                                }
                            }
                        });

                    }

                    @Override
                    public void onFail(String errorMsg, boolean shouldRetry) {
                        Log.e(LOG_TAG,"errorMsg"+errorMsg);
                    }
                });

//        if (!UserInfoManager.Companion.getInstance().isLoggedIn(getActivity())) {
//            title = new String[1];
//            imageUrl = new String[1];
//            title[0] = getResources().getString(R.string.fragment_custom_add);
//            imageUrl[0] = "new";
//
//            adapter = new CustomGridAdapter(getActivity(), title, imageUrl);
//            gridView = mView.findViewById(R.id.fragment_custom_main_grid_view);
//            gridView.setAdapter(adapter);
//            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
//                    DialogTools.getInstance().showErrorMessage(getActivity(),
//                            R.string.dialog_title_text_note,
//                            R.string.dialog_message_no_login,
//                            new DialogInterface.OnDismissListener() {
//                                @Override
//                                public void onDismiss(DialogInterface dialog) {
//                                    openFragmentPage(LoginFragment.newInstance(), LoginFragment.TAG);
//                                }
//                            });
//                }
//            });
//        }

        return mView;
    }

    private class ViewHolder {
        private TextView title;
        private GridViewItem image;
        private ImageView menu;
    }

    public class CustomGridAdapter extends BaseAdapter {
        private Context context;
        private final String[] title;
        private final String[] imageUrl;

        public CustomGridAdapter(Context context, String[] title, String[] imageUrl) {
            this.context = context;
            this.title = title;
            this.imageUrl = imageUrl;
        }

        @Override
        public int getCount() {
            return title.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.custom_grid_item, null);
                holder = new ViewHolder();
                holder.title = convertView.findViewById(R.id.grid_text);
                holder.image = convertView.findViewById(R.id.grid_image);
                holder.menu = convertView.findViewById(R.id.grid_item_menu);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.title.setText(title[position]);
            if (imageUrl[position].equals("new")) {
                NetworkManager.getInstance().setNetworkImage(getContext(),
                        holder.image, "https://religitrav.kcg.gov.tw/images/btn_add_trip.png");
                holder.menu.setVisibility(View.GONE);
                convertView.findViewById(R.id.grid_item_menu_black).setVisibility(View.GONE);
//                convertView.findViewById(R.id.grid_item_menu).setVisibility(View.GONE);
//                holder.image.setDefaultImageResId(R.mipmap.btn_add_travel_g);
//                holder.image.setPadding(120, 120, 120, 120);
            } else {
                NetworkManager.getInstance().setNetworkImage(getContext(),
                        holder.image, imageUrl[position]);
                holder.menu.setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.grid_item_menu_black).setVisibility(View.VISIBLE);
            }

            convertView.findViewById(R.id.grid_item_menu).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentPos = position;
                    if (!imageUrl[position].equals("new")) {
//                        if (mUserTripData[position].getT_share().equals("Y")) {
//                            ((TextView) item_menu_ll.findViewById(R.id.fragment_custom_main_master_share)).setText(R.string.fragment_custom_main_master_share_cancel);
//                        } else {
//                            ((TextView) item_menu_ll.findViewById(R.id.fragment_custom_main_master_share)).setText(R.string.fragment_custom_main_master_share);
//                        }
                        menu_bg.setVisibility(View.VISIBLE);
                        item_menu_ll.setVisibility(View.VISIBLE);
                    }
                }
            });

            return convertView;
        }
    }

    private void copyToClipBoard() {
        myClipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
        myClip = ClipData.newPlainText("text", "123");
        myClipboard.setPrimaryClip(myClip);
        Toast.makeText(getActivity(), R.string.dialog_share_copy_done, Toast.LENGTH_SHORT).show();
    }

    private void openFragmentPage(Fragment fragment, String tag) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_main_fl, fragment, tag)
                .addToBackStack(null)
                .commit();
    }

}
