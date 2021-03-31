package com.smoothsys.qonsume_pos.FragmentsAndActivities;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.smoothsys.qonsume_pos.Adapters.TransactionListAdapter;
import com.smoothsys.qonsume_pos.Models.OrderClasses.Order;
import com.smoothsys.qonsume_pos.Models.OrderClasses.OrderResponseObject;
import com.smoothsys.qonsume_pos.R;
import com.smoothsys.qonsume_pos.Retrofit.ISnabbOrderAPI;
import com.smoothsys.qonsume_pos.Retrofit.RetrofitManager;
import com.smoothsys.qonsume_pos.StateManagement.ScreenState;
import com.smoothsys.qonsume_pos.StateManagement.StateChanger;
import com.smoothsys.qonsume_pos.Utilities.Cache;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Pontus on 2017-07-05.
 */

public class TransactionHistoryFragment extends Fragment {

    ListView historyList;
    ListAdapter listAdapter;
    RelativeLayout emailBtnIMG;
    SharedPreferences prefs;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupList();
    }

    private void setupEmailBtn(View view) {
        emailBtnIMG = view.findViewById(R.id.emailBtnLayout);
        emailBtnIMG.setOnClickListener(v -> createPopUpInput());
    }

    private void createPopUpInput() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Send Receipt To Email");

        final EditText input = new EditText(getContext());

        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> Toast.makeText(getContext(),input.getText().toString() , Toast.LENGTH_LONG).show());
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_transaction_history, container, false);
        historyList = view.findViewById(R.id.historyinfolist);

        setupEmailBtn(view);

        return view;
    }

    public void onResume() {

        ((MainActivity)getActivity()).setTitleBarText(getString(R.string.historyScreen_name));
        StateChanger.setState(ScreenState.onHistoryScreen);
        super.onResume();
    }

    @Override
    public void onStop() {
        // reset search view in app bar
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        StateChanger.setState(ScreenState.NULL_screenState);
        getActivity().invalidateOptionsMenu();
        super.onStop();
    }

    List<Order> allTransactions = new ArrayList<>();
    private void setupList() {

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        ISnabbOrderAPI ISnabbOrderAPI = RetrofitManager.SetupInterfaceFromCredentials(prefs);
        Call<OrderResponseObject> call = ISnabbOrderAPI.merchantStatement(Cache.mMerchant.getUserEmail());
        call.enqueue(new Callback<OrderResponseObject>() {
            @Override
            public void onResponse(Call<OrderResponseObject> call, Response<OrderResponseObject> response) {
                if(!isValidResponse(response)) {
                    return;
                }

                if(getContext() == null) {
                    return;
                }

                // setup list
                allTransactions = response.body().getData();
                listAdapter = new TransactionListAdapter(getContext(),R.layout.list_item_transaction, allTransactions);
                historyList.setAdapter(listAdapter);
            }

            @Override
            public void onFailure(Call<OrderResponseObject> call, Throwable t) {}
        });
    }

    List<Order> searchedTransactions = new ArrayList<>();
    public void searchForTransaction(String query) {

        searchedTransactions = new ArrayList<>();
        for(int i = 0; i<allTransactions.size(); i++) {
            // find based on search string
            Order o = allTransactions.get(i);
            String receiptNoAsString = String.valueOf(o.getReceipt_number());
            if(receiptNoAsString.contains(query)) {
                searchedTransactions.add(o);
            }
        }

        // repopulate list with these new transactions
        listAdapter = new TransactionListAdapter(getContext(),R.layout.list_item_transaction, searchedTransactions);
        historyList.setAdapter(listAdapter);

    }

    private Boolean isValidResponse(Response<OrderResponseObject> response) {

        if(response == null) {
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            return false;
        }

        if(response.code() == 401) {
            // return to login screen if this happens?
            Toast.makeText(getContext(), "Authorization error.", Toast.LENGTH_LONG).show();
            return false;
        }

        if(response.body() == null) {
            Toast.makeText(getContext(), "No transactions found for your shop", Toast.LENGTH_LONG).show();
            return false;
        }

        OrderResponseObject respObject = response.body();

        if(!respObject.getStatus().equals("success")) {
            Toast.makeText(getContext(), "No transactions found for your shop", Toast.LENGTH_LONG).show();
            return false;
        }

        List<Order> paidOrders = respObject.getData();

        if(paidOrders.size() < 1) {
            Toast.makeText(getContext(), "No transactions found for your shop", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
