package com.smoothsys.qonsume_pos.FragmentsAndActivities;


import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.smoothsys.qonsume_pos.DataDump.PaymentTypes;
import com.smoothsys.qonsume_pos.Models.Credit;
import com.smoothsys.qonsume_pos.Models.OrderClasses.Order;
import com.smoothsys.qonsume_pos.R;
import com.smoothsys.qonsume_pos.Retrofit.ISnabbOrderAPI;
import com.smoothsys.qonsume_pos.Retrofit.RetrofitManager;
import com.smoothsys.qonsume_pos.StateManagement.ScreenSwitcher;
import com.smoothsys.qonsume_pos.Utilities.Cache;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class ManualPaymentFragment extends Fragment {
    View view;
    String inputString;
    private ISnabbOrderAPI ISnabbOrderAPI;
    Call<List<Credit>> creditCheckCall;
    Call<ResponseBody> decrementCall;
    Call<ResponseBody> payCall;
    Order gOrderToBePayed;

    public ManualPaymentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_transaction, container, false);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        ISnabbOrderAPI = RetrofitManager.SetupInterfaceFromCredentials(prefs);
        setupScanButton();
        return view;
    }

    private void setupScanButton() {

        final EditText amountInput = view.findViewById(R.id.amountField);
        //Setting up button events
        Button scanBtn = view.findViewById(R.id.scan_Btn);
        scanBtn.setOnClickListener(v -> {

            if(amountInput.getText().toString().equals("")) {
                Toast.makeText(getContext(), "Please enter a valid amount.", Toast.LENGTH_SHORT).show();
                return;
            }

            inputString = amountInput.getText().toString();
            double input = Double.parseDouble(inputString);

            makeSnabbPayPayment(input);
            amountInput.setText("");
        });
    }

    private void makeSnabbPayPayment(double manualAmount) {

        Order orderToBePayed = new Order();
        orderToBePayed.setTotalAmount(String.valueOf(manualAmount));
        orderToBePayed.setId("-1");
        orderToBePayed.setShopId(String.valueOf(Cache.mMerchant.getShopID()));

        ((MainActivity)getActivity()).scanForPayment(orderToBePayed);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void onResume() {
        ((MainActivity)getActivity()).setTitleBarText(getString(R.string.trans_name));
        super.onResume();
    }

    public void runThisAfterScan(final Order orderToBePayed, final String custEmail) {
        gOrderToBePayed = orderToBePayed;
        creditCheckCall = ISnabbOrderAPI.getCustomerCredit(custEmail);
        creditCheckCall.enqueue(new Callback<List<Credit>>() {
            @Override
            public void onResponse(Call<List<Credit>> call, Response<List<Credit>> response) {
                if(!isResponseValid(response)) {
                    Toast.makeText(getContext(), "Something went wrong - Response is not valid!", Toast.LENGTH_LONG).show();
                    return;
                }

                Credit credit = response.body().get(0);
                if(credit.getAmount() >= Float.parseFloat(orderToBePayed.getTotalAmount())) {
                    // == Scan Code And Reduce Payment ==
                    float amountToBeReducedTo = credit.getAmount() - Float.parseFloat(orderToBePayed.getTotalAmount());
                    reduceSnabbpayBalance(custEmail, amountToBeReducedTo);

                } else {
                    Toast.makeText(getContext(), "Insufficient Credit!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Credit>> call, Throwable t) {
                Toast.makeText(getContext(), "Something went wrong when checking customer credit.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void reduceSnabbpayBalance(String custEmail, float amountToReduceTo) {

        decrementCall = ISnabbOrderAPI.decrementCredits(custEmail, amountToReduceTo );
        decrementCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String resString ="";
                try {
                    resString = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(resString.equals("SnabbCredit reduced")) {
                    sendSnabbpayPayment();

                } else {
                    Toast.makeText(getContext(), "Insufficient Credit!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Could not connect to service!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendSnabbpayPayment() {

        // == Make the payment ==
        payCall = ISnabbOrderAPI.payOrder(Integer.parseInt(gOrderToBePayed.getId()), Integer.parseInt(gOrderToBePayed.getShopId()), PaymentTypes.QPAY, Double.parseDouble(gOrderToBePayed.getTotalAmount()));
        payCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                String resString;
                String message = "";
                try {
                    resString = response.body().string();
                    JSONObject jObj = new JSONObject(resString);
                    message = jObj.getString("success");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Toast.makeText(getContext(), "Successful payment!", Toast.LENGTH_LONG).show();

                // == Special Case For demo ==
                if(gOrderToBePayed.getId().equals("-1")) {
                    new ScreenSwitcher(R.id.fragment_container, getActivity()).goToManualPaymentFragment();
                } else {
                    new ScreenSwitcher(R.id.fragment_container, getActivity()).goToOrderTabsFragment();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Something went wrong with payment - " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isResponseValid(Response<List<Credit>> response) {

        //If no transactions in list
        if(response == null) {
            Toast.makeText(getContext(), "No transactions found", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(response.body().size() < 1) {
            Toast.makeText(getContext(), "No credit found!", Toast.LENGTH_SHORT).show();
            return false;
        }

        Credit credit = response.body().get(0);
        if(credit == null) {
            return false;
        }
        return true;
    }
}
