package com.smoothsys.qonsume_pos.Factories;

import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.smoothsys.qonsume_pos.Adapters.CardInfoListAdapter;
import com.smoothsys.qonsume_pos.Utilities.Cache;
import com.smoothsys.qonsume_pos.FragmentsAndActivities.MainActivity;
import com.smoothsys.qonsume_pos.R;

/**
 * Created by Pontu on 2018-02-22.
 */

public class CardInfoListFactory {
    public CardInfoListFactory() {}

    private String[] cardInfoList;
    private MainActivity mainActivity;
    public void createCardInfoList(MainActivity mainActivity, View view) {
        this.mainActivity = mainActivity;
        this.cardInfoList = mainActivity.cardInfoList;
        initList(view);
    }

    private void initList(View view) {

        populateStrings();
        setHeader(view);
        initContent(view);
    }

    private void populateStrings() {
        mainActivity.populateCardInfoList();
    }

    private void setHeader(View view) {
        TextView cardInfoHeader = view.findViewById(R.id.cardInfoHeaderText);
        cardInfoHeader.setText(Cache.mMerchant.getUserEmail());
    }

    private void initContent(View view){

        ListView listView =(ListView) view.findViewById(R.id.cardinfolist);
        mainActivity.cardInfoListAdapter = new CardInfoListAdapter(mainActivity.getBaseContext(), cardInfoList);
        listView.setAdapter(mainActivity.cardInfoListAdapter);
    }
}
