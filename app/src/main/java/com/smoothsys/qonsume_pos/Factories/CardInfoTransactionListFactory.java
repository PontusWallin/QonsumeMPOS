package com.smoothsys.qonsume_pos.Factories;

import android.view.View;
import android.widget.ListView;

import com.smoothsys.qonsume_pos.Adapters.CardInfoTransactionListAdapter;
import com.smoothsys.qonsume_pos.FragmentsAndActivities.MainActivity;
import com.smoothsys.qonsume_pos.R;

import java.util.ArrayList;

/**
 * Created by Pontu on 2018-02-22.
 */

public class CardInfoTransactionListFactory {
    public CardInfoTransactionListFactory() {}

    private ArrayList<String> transactionList;
    private MainActivity mainActivity;
    public void createCardInfoTransactionList(MainActivity mainActivity, View view) {
        this.mainActivity = mainActivity;
        transactionList = mainActivity.cardTransactionList;
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
        //TextView cardInfoHeader = (TextView) view.findViewById(R.id.cardInfoHeaderText);
        //cardInfoHeader.setText(Cache.mMerchant.getName());
    }

    private void initContent(View view){

        ListView listView =(ListView) view.findViewById(R.id.transactionList);
        mainActivity.cardTransactionListAdapter = new CardInfoTransactionListAdapter(mainActivity.getBaseContext(), transactionList);
        listView.setAdapter(mainActivity.cardInfoListAdapter);
    }
}
