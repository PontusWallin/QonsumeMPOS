package com.smoothsys.qonsume_pos.FragmentsAndActivities;


import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.smoothsys.qonsume_pos.Factories.CardInfoListFactory;
import com.smoothsys.qonsume_pos.R;
import com.smoothsys.qonsume_pos.StateManagement.StateChanger;
import com.smoothsys.qonsume_pos.StateManagement.ScreenState;

public class CardinfoFragment extends Fragment {

    public CardinfoFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return setupView(inflater, container);
    }

    private void doCardInfoScan() {
        if(((MainActivity)getActivity()).hasJustScanned == false) {
            ((MainActivity)getActivity()).scanForCardInfo();
        }
    }

    private View setupView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_cardinfo, container, false);
        setupTransactionButton(view);
        setupCardInfoList(view);
        return view;
    }

    private void setupCardInfoList(View view) {

        CardInfoListFactory factory = new CardInfoListFactory();
        factory.createCardInfoList((MainActivity)getActivity(), view);
    }

    private void setupTransactionButton(View view) {
        Button btn = view.findViewById(R.id.showTransactionButton);
        btn.setOnClickListener(v -> doCardInfoScan());
    }

    @Override
    public void onStart() {
        super.onStart();
        StateChanger.setState(ScreenState.onCardInfoScreen);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onResume() {

        ((MainActivity)getActivity()).setTitleBarText(getString(R.string.card_name));

        if(((MainActivity)getActivity()).hasJustScanned == true) {
            ((MainActivity)getActivity()).screenSwitcher.goToDetailedCardInfoFragment();
        }

        super.onResume();
    }
}