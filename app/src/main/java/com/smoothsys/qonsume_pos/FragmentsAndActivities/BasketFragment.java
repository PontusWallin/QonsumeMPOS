package com.smoothsys.qonsume_pos.FragmentsAndActivities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.smoothsys.qonsume_pos.Adapters.BasketAdapter;
import com.smoothsys.qonsume_pos.Adapters.BasketFragmentInterface;
import com.smoothsys.qonsume_pos.Models.BasketData;
import com.smoothsys.qonsume_pos.R;
import com.smoothsys.qonsume_pos.StateManagement.ScreenState;
import com.smoothsys.qonsume_pos.StateManagement.StateChanger;
import com.smoothsys.qonsume_pos.Utilities.BundleKeys;
import com.smoothsys.qonsume_pos.Utilities.Cache;
import com.smoothsys.qonsume_pos.Utilities.DBHandler;
import com.smoothsys.qonsume_pos.Utilities.TimeUtils;
import com.smoothsys.qonsume_pos.Utilities.Utils;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BasketFragment extends Fragment implements BasketFragmentInterface {

    private List<BasketData> basketDataSet;
    private BasketAdapter adapter;

    private ListView basketListView;
    TextView totalPriceTv;
    TextView totalVAT_Tv;
    DBHandler dbHandler;
    private Picasso picasso;

    public static String currencySymbol;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        dbHandler = new DBHandler(getContext());
        ((MainActivity)getActivity()).setBasketInterface(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_basket, container, false);
        return view;
    }

    @Override
    public void onStart() {

        super.onStart();
        StateChanger.setState(ScreenState.onBasketScreen);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initData();
        bindData();
        setupUI();
    }

    private void initData() {

        picasso = new Picasso.Builder(getContext()).build();
        basketListView = getView().findViewById(R.id.basketList);
    }

    private void bindData() {

        basketDataSet = new ArrayList<>();
        adapter = new BasketAdapter(getActivity(), this, basketDataSet, Cache.mUserId, dbHandler, picasso, getContext());
        loadBasketData();
        basketListView.setAdapter(adapter);
    }

    private void setupUI() {

        ConstraintLayout submitBtn = getView().findViewById(R.id.btn_layout);
        submitBtn.setOnClickListener(v -> {

            Boolean isOpen = TimeUtils.shopIsOpen(Cache.mRestaurant);
            if(!isOpen) {
                Toast.makeText(getContext(), "Shop is closed!", Toast.LENGTH_LONG).show();
                return;
            }

            if(basketDataSet.size() < 1) {
                Toast.makeText(getContext(), "Basket is empty!", Toast.LENGTH_LONG).show();
                return;
            }

            AppCompatEditText tableNoInput = getView().findViewById(R.id.tableNoTv);
            int tableNo;
            if(tableNoInput.getText().toString().equals("")) {
                tableNo = 0;
            } else {
                tableNo = Integer.parseInt(tableNoInput.getText().toString());
            }

            ((MainActivity)getActivity()).tableNumber = tableNo;
            ((MainActivity)getActivity()).screenSwitcher.goToPayOptionsFragment(tableNo, g_totalAmount, getContext());
        });

        totalPriceTv = getView().findViewById(R.id.amountTv);
        totalVAT_Tv = getView().findViewById(R.id.vat_amount);
    }

    private void loadBasketData() {
        try {
            clearBasketDataSet();
            addDataToBasketDataSet();
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            
            e.printStackTrace();
            Toast.makeText(getContext(), "Something went wrong loading basket data from local memory.", Toast.LENGTH_LONG).show();
        }
    }

    private void clearBasketDataSet() {
        if (basketDataSet != null)
            basketDataSet.clear();
    }

    private void addDataToBasketDataSet() {
        // TODO merchant is restored here ..
        List<BasketData> basket = dbHandler.getAllBasketDataByShopId(Cache.mMerchant.getShopID());

        for (BasketData basketData : basket) {

            if(basketData.getQty() < 1) {
                continue;
            }
            basketDataSet.add(basketData);
            currencySymbol = basketData.getCurrencySymbol();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(Cache.mMerchant != null) {
            outState.putParcelable(BundleKeys.MERCHANT, Cache.mMerchant);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(Cache.mMerchant == null) {
            Cache.mMerchant = savedInstanceState.getParcelable(BundleKeys.MERCHANT);
        }
    }

    private double g_totalAmount = -1;
    public void updateTotal(double totalAmount, double totalVAT) {
        updateTotalText(totalAmount);
        updateVAT_Text(totalVAT);
        g_totalAmount = totalAmount;
    }

    public void updateTotalText(double totalAmount) {

        DecimalFormat df = Utils.getDecimalFormat();
        if (basketDataSet == null || basketDataSet.size() < 1) {

            totalPriceTv.setText(df.format(totalAmount));
        } else {
            totalPriceTv.setText(df.format(totalAmount) + basketDataSet.get(0).getCurrencyShortForm());
        }
    }

    public void updateVAT_Text(double totalVAT) {

        DecimalFormat df = Utils.getDecimalFormat();
        if (basketDataSet == null || basketDataSet.size() < 1) {

            totalVAT_Tv.setText(df.format(totalVAT));
        } else {
            totalVAT_Tv.setText(df.format(totalVAT) + basketDataSet.get(0).getCurrencyShortForm());
        }
    }

    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).setTitleBarText(getString(R.string.basket));
    }

    @Override
    public void clearDataAfterOrderSubmission() {
        loadBasketData();
        updateTotal(0.0, 0.0);
    }
}