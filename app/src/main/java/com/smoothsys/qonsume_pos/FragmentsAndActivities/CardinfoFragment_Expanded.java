package com.smoothsys.qonsume_pos.FragmentsAndActivities;


import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.smoothsys.qonsume_pos.Factories.CardInfoListFactory;
import com.smoothsys.qonsume_pos.Factories.CardInfoTransactionListFactory;
import com.smoothsys.qonsume_pos.R;

public class CardinfoFragment_Expanded extends Fragment {

    public CardinfoFragment_Expanded() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cardinfo2, container, false);

        createCardInfoList(view);
        createTransactionList(view);
        return view;
    }

    private void createCardInfoList(View view) {
        CardInfoListFactory factory = new CardInfoListFactory();
        factory.createCardInfoList((MainActivity)getActivity(), view);
    }

    private void createTransactionList(View view) {
        final ListView transactionList = view.findViewById(R.id.transactionList);

        CardInfoTransactionListFactory factory = new CardInfoTransactionListFactory();
        factory.createCardInfoTransactionList((MainActivity)getActivity(), view);

        ((MainActivity)getActivity()).APIGetTransactionList();
        transactionList.setAdapter(((MainActivity)getActivity()).cardTransactionListAdapter);
    }

    @Override
    public void onStart() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onStart();
    }

    public void onResume() {
        ((MainActivity)getActivity()).setTitleBarText(getString(R.string.card_name));
        super.onResume();
    }
}
