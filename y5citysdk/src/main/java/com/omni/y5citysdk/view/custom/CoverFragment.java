package com.omni.y5citysdk.view.custom;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.toolbox.NetworkImageView;
import com.omni.y5citysdk.R;
import com.omni.y5citysdk.module.OmniEvent;
import com.omni.y5citysdk.module.info.CoverData;
import com.omni.y5citysdk.network.NetworkManager;
import com.omni.y5citysdk.network.Y5CityAPI;

import org.greenrobot.eventbus.EventBus;

public class CoverFragment extends Fragment {

    public static final String TAG = "fragment_tag_cover";

    private View mView;

    public static CoverFragment newInstance() {
        return new CoverFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_cover, null, false);
//            mView.setPadding(0, Tools.STATUS_BAR, 0, 0);

            mView.findViewById(R.id.fragment_cover_fl_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });

            Y5CityAPI.getInstance().getCover(getActivity(), new NetworkManager.NetworkManagerListener<CoverData[]>() {
                @Override
                public void onSucceed(final CoverData[] coverData) {
                    LinearLayout contentLL = mView.findViewById(R.id.fragment_cover_ll_contents);
                    for (int i = 0; i < coverData.length; i++) {
                        View itemGuideView = LayoutInflater.from(getContext()).inflate(R.layout.item_cover, container, false);

                        NetworkManager.getInstance().setNetworkImage(getContext(),
                                (NetworkImageView) itemGuideView.findViewById(R.id.item_cover_iv),
                                coverData[i].getImage());

                        final int finalI = i;
                        itemGuideView.findViewById(R.id.item_cover_iv).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                EventBus.getDefault().post(new OmniEvent(OmniEvent.TYPE_ADD_COVER_DEFAULT, coverData[finalI].getId()));
                                getActivity().getSupportFragmentManager().popBackStack();
                            }
                        });

                        contentLL.addView(itemGuideView);
                    }
                }

                @Override
                public void onFail(String errorMsg, boolean shouldRetry) {

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
