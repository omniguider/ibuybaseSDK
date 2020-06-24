package com.omni.y5citysdk.tool.viewpager_card;

import android.content.Context;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.omni.y5citysdk.R;
import com.omni.y5citysdk.manager.AnimationFragmentManager;

import java.util.List;

public class CardPagerAdapter extends PagerAdapter implements CardAdapter {

    private Context mContext;
    private List<CardItem> mCardList;
    private View[] mViews;

    private float mBaseElevation;

    public CardPagerAdapter(Context context, List<CardItem> mCardList) {
        this.mContext = context;
        this.mCardList = mCardList;
        initView();
    }

    private void initView() {
        mViews = new View[mCardList.size()];
        LayoutInflater inflater = LayoutInflater.from(mContext);
        for (int index = 0, len = mCardList.size(); index < len; index++) {
            mViews[index] = inflater.inflate(R.layout.item_viewpager, null, false);
        }
    }

    @Override
    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return (CardView) mViews[position];
    }

    @Override
    public int getCount() {
        return mCardList.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public void destroyItem(ViewGroup arg0, int arg1, Object arg2) {
        int position = arg1 % mCardList.size();
        arg0.removeView(mViews[position]);
    }

    @Override
    public Object instantiateItem(ViewGroup arg0, int arg1) {
        int position = arg1 % mCardList.size();

        ImageView view = mViews[position].findViewById(R.id.item_viewpager_iv);
        view.setImageResource(mCardList.get(position).getImage_id());

        if (mCardList.size() == 1) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }

        try {
            if (mViews[position].getParent() == null) {
                arg0.addView(mViews[position], 0);
            } else {
                ((ViewGroup) mViews[position].getParent()).removeAllViews();
                arg0.addView(mViews[position], 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        CardView cardView = mViews[position].findViewById(R.id.item_viewpager_cardview);
        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }
        cardView.setCardBackgroundColor(Color.TRANSPARENT);
        cardView.setCardElevation(0);
        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        setOnClickListener(mViews[position].findViewById(R.id.item_viewpager_cardview), position);

        return mViews[position];
    }

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    private void setOnClickListener(View view, final int index) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        };
        view.setOnClickListener(listener);
    }

    private void openFragmentPage(Fragment fragment, String tag) {
        AnimationFragmentManager.getInstance().addFragmentPage((FragmentActivity) mContext,
                R.id.activity_main_fl, fragment, tag);
    }
}
