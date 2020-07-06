package com.omni.y5citysdk.view.favorite;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.omni.y5citysdk.R;
import com.omni.y5citysdk.manager.AnimationFragmentManager;
import com.omni.y5citysdk.manager.UserInfoManager;
import com.omni.y5citysdk.module.OmniEvent;
import com.omni.y5citysdk.module.favorite.FavoriteInfo;
import com.omni.y5citysdk.module.favorite.FavoriteResponse;
import com.omni.y5citysdk.network.NetworkManager;
import com.omni.y5citysdk.network.Y5CityAPI;
import com.omni.y5citysdk.view.theme.ReligionInfoFragment;

import org.greenrobot.eventbus.EventBus;

import static com.omni.y5citysdk.tool.Y5CityText.LOG_TAG;

public class FavoriteListFragment extends Fragment {

    private View mView;
    private static final String ARG_KEY_FAVORITE_INFO = "arg_key_favorite_info";
    private FavoriteInfo[] mFavoriteInfo;
    public static boolean editMode = false;
    private TextView delete_tv;
    private boolean[] check;
    private int checkCount = 0;

    public static FavoriteListFragment newInstance(FavoriteInfo[] favoriteInfo) {
        FavoriteListFragment fragment = new FavoriteListFragment();

        Bundle arg = new Bundle();
        arg.putSerializable(ARG_KEY_FAVORITE_INFO, favoriteInfo);
        fragment.setArguments(arg);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFavoriteInfo = (FavoriteInfo[]) getArguments().getSerializable(ARG_KEY_FAVORITE_INFO);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_favorite_list, container, false);
            ListView favorite_list_lv = mView.findViewById(R.id.fragment_favorite_list_lv);
            favorite_list_lv.setAdapter(new ListAdapter(getActivity(), mFavoriteInfo));

            delete_tv = mView.findViewById(R.id.fragment_favorite_list_delete);
            delete_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int i = 0; i < check.length; i++) {
                        if (check[i]) {
                            Y5CityAPI.getInstance().setFavorite(getActivity(), String.valueOf(mFavoriteInfo[i].getP_id()),
                                    UserInfoManager.Companion.getInstance().getUserLoginTokenLoginHint(getActivity()),
                                    new NetworkManager.NetworkManagerListener<FavoriteResponse>() {
                                        @Override
                                        public void onSucceed(FavoriteResponse object) {
                                            EventBus.getDefault().post(new OmniEvent(OmniEvent.TYPE_FAVORITE_CHANGED, ""));
                                        }

                                        @Override
                                        public void onFail(String errorMsg, boolean shouldRetry) {

                                        }
                                    }
                            );
                        }
                    }
                }
            });

        }

        return mView;
    }

    private class ViewHolder {
        private TextView title;
        private TextView address;
        private NetworkImageView image;
        private ImageView check_iv;
        private ImageView addPoint_iv;
    }

    public class ListAdapter extends BaseAdapter {
        Context context;
        FavoriteInfo[] favoriteInfo;
        LayoutInflater inflater;

        public ListAdapter(Context context, FavoriteInfo[] favoriteInfo) {
            this.context = context;
            this.favoriteInfo = favoriteInfo;
            check = new boolean[favoriteInfo.length];
        }

        @Override
        public int getCount() {
            return favoriteInfo.length;
        }

        @Override
        public Object getItem(int position) {
            return favoriteInfo[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_favorite_list, null);
                holder = new ViewHolder();
                holder.image = convertView.findViewById(R.id.item_favorite_list_iv);
                holder.title = convertView.findViewById(R.id.item_favorite_list_title);
                holder.address = convertView.findViewById(R.id.item_favorite_list_address);
                holder.check_iv = convertView.findViewById(R.id.item_favorite_list_check_iv);
                holder.addPoint_iv = convertView.findViewById(R.id.item_favorite_list_add_point_iv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (FavoriteFragment.mode.equals("addPoint")) {
                holder.addPoint_iv.setVisibility(View.VISIBLE);
                holder.addPoint_iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EventBus.getDefault().post(new OmniEvent(OmniEvent.TYPE_ADD_POINT_FAVORITE, favoriteInfo[position]));
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                });
            }

            NetworkManager.getInstance().setNetworkImage(context, holder.image, favoriteInfo[position].getImage());
            holder.title.setText(favoriteInfo[position].getname());
            holder.address.setText(favoriteInfo[position].getaddress());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openFragmentPage(ReligionInfoFragment.newInstance(
                            String.valueOf(favoriteInfo[position].getP_id()), favoriteInfo[position].getType()), ReligionInfoFragment.TAG);
                }
            });
            holder.check_iv.setVisibility(editMode ? View.VISIBLE : View.GONE);
            holder.check_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e(LOG_TAG, "position" + position);
                    if (!check[position]) {
                        check[position] = true;
                        checkCount++;
                        delete_tv.setVisibility(View.VISIBLE);
                    } else {
                        check[position] = false;
                        checkCount--;
                        if (checkCount == 0)
                            delete_tv.setVisibility(View.GONE);
                    }
                    holder.check_iv.setImageResource(check[position] ? R.mipmap.btn_select : R.mipmap.btn_unselect);
                }
            });
            holder.check_iv.setImageResource(check[position]?R.mipmap.btn_select:R.mipmap.btn_unselect);

            return convertView;
        }
    }

    private void openFragmentPage(Fragment fragment, String tag) {
        AnimationFragmentManager.getInstance().addFragmentPage(getActivity(),
                R.id.activity_main_fl, fragment, tag);
    }

}
