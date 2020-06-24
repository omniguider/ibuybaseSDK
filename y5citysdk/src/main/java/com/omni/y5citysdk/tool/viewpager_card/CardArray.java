package com.omni.y5citysdk.tool.viewpager_card;

import java.util.ArrayList;
import java.util.List;

public class CardArray {
    private List<CardItem> mCardItems;

    public List<CardItem> getmCardItems() {
        return mCardItems;
    }

    public void setmCardItems(List<CardItem> mCardItems) {
        this.mCardItems = mCardItems;
    }

    public void parseData(int[] data) {
        mCardItems = new ArrayList<>();

        for (int i = 0; i < data.length; i++) {
            CardItem cardItem = new CardItem();
            cardItem.setImage_id(data[i]);
            mCardItems.add(cardItem);
        }
    }
}