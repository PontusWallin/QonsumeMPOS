package com.smoothsys.qonsume_pos.StateManagement;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.smoothsys.qonsume_pos.DataDump.PaymentTypes;
import com.smoothsys.qonsume_pos.Factories.SubmitObjectFactory;
import com.smoothsys.qonsume_pos.FragmentsAndActivities.BasketFragment;
import com.smoothsys.qonsume_pos.FragmentsAndActivities.CardinfoFragment;
import com.smoothsys.qonsume_pos.FragmentsAndActivities.CardinfoFragment_Expanded;
import com.smoothsys.qonsume_pos.FragmentsAndActivities.CreateOrderFragment;
import com.smoothsys.qonsume_pos.FragmentsAndActivities.HomeFragment;
import com.smoothsys.qonsume_pos.FragmentsAndActivities.LoginFragment;
import com.smoothsys.qonsume_pos.FragmentsAndActivities.MainActivity;
import com.smoothsys.qonsume_pos.FragmentsAndActivities.ManualPaymentFragment;
import com.smoothsys.qonsume_pos.FragmentsAndActivities.OrderTabsFragment;
import com.smoothsys.qonsume_pos.FragmentsAndActivities.PaymentOptionsFragment;
import com.smoothsys.qonsume_pos.FragmentsAndActivities.ProfileFragment;
import com.smoothsys.qonsume_pos.FragmentsAndActivities.ReportsFragment;
import com.smoothsys.qonsume_pos.FragmentsAndActivities.SettingsFragment;
import com.smoothsys.qonsume_pos.FragmentsAndActivities.TransactionHistoryFragment;
import com.smoothsys.qonsume_pos.Models.OrderClasses.Order;
import com.smoothsys.qonsume_pos.Models.SubmissionStatusObject;
import com.smoothsys.qonsume_pos.R;
import com.smoothsys.qonsume_pos.Retrofit.ISnabbOrderAPI;
import com.smoothsys.qonsume_pos.Retrofit.RetrofitManager;
import com.smoothsys.qonsume_pos.Utilities.Cache;
import com.smoothsys.qonsume_pos.Utilities.Config;
import com.smoothsys.qonsume_pos.Utilities.OrderDataDump;

import org.json.JSONArray;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScreenSwitcher{

    private FragmentActivity parentActivity;
    private FragmentManager fragmentManager;
    private int containerId;
    
    public ScreenSwitcher(int containerId, FragmentActivity parentActivity) {
        fragmentManager = parentActivity.getSupportFragmentManager();
        this.containerId = containerId;
        this.parentActivity = parentActivity;
    }

    public void goToHomeFragment() {

        HomeFragment fragment = new HomeFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerId, fragment);
        fragmentTransaction.commitAllowingStateLoss();
        hideKeyboard();
    }

    public void goToProfilePayment() {

        ProfileFragment fragment = new ProfileFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerId, fragment);
        fragmentTransaction.commitAllowingStateLoss();
        hideKeyboard();
    }

    public void goToOrderTabsFragment() {

        if(Cache.mMerchant == null) {
            goToLogInFragment();
            return;
        }

        OrderTabsFragment fragment = new OrderTabsFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(containerId, fragment, "OrderTabsFragment");
        fragmentTransaction.addToBackStack("OrderTabsFragment");
        fragmentTransaction.commit();

        parentActivity.invalidateOptionsMenu();
        parentActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        hideKeyboard();
    }

    public void goToHistoryFragment() {

        TransactionHistoryFragment fragment = new TransactionHistoryFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(containerId, fragment, "HistoryScreenFragment");
        fragmentTransaction.addToBackStack("HistoryScreenFragment");
        fragmentTransaction.commit();

        fragmentManager.executePendingTransactions();

        parentActivity.invalidateOptionsMenu();
        parentActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        hideKeyboard();
    }

    public void goToCreateOrderFragment() {

        final CreateOrderFragment fragment = new CreateOrderFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putBoolean("clear_basket", true);
        fragment.setArguments(bundle);

        fragmentTransaction.replace(containerId, fragment, "CreateOrderFragment");
        fragmentTransaction.commit();
        fragmentTransaction.addToBackStack("CreateOrderFragment");
        parentActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        hideKeyboard();
    }

    public void goToPayOptionsFragment(int table, double price, Context context) {

        if(Config.DEBUG_DISABLE_PAY_OPTIONS) {
            submitOrderDirectly(table, context);
            return;
        }

        OrderDataDump.orderToPay = null;
        OrderDataDump.independentAmount = price;
        PaymentOptionsFragment fragment = new PaymentOptionsFragment();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(containerId, fragment, "PaymentOptionsFragment");
        fragmentTransaction.addToBackStack("PaymentOptionsFragment");
        fragmentTransaction.commit();

        fragmentManager.executePendingTransactions();

        parentActivity.invalidateOptionsMenu();
        parentActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        hideKeyboard();
    }

    public void goToPayOptionsFragment(Order order, int table, Context context) {

        if(Config.DEBUG_DISABLE_PAY_OPTIONS) {
            submitOrderDirectly(table, context);
            return;
        }

        OrderDataDump.orderToPay = order;
        OrderDataDump.independentAmount = -1;
        PaymentOptionsFragment fragment = new PaymentOptionsFragment();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(containerId, fragment, "PaymentOptionsFragment");
        fragmentTransaction.addToBackStack("PaymentOptionsFragment");
        fragmentTransaction.commit();

        fragmentManager.executePendingTransactions();

        parentActivity.invalidateOptionsMenu();
        parentActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        hideKeyboard();
    }

    private ISnabbOrderAPI ISnabbOrderAPI;

    private void submitOrderDirectly(int table, Context context) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        ISnabbOrderAPI = RetrofitManager.SetupInterfaceFromCredentials(prefs);

        JSONArray object = SubmitObjectFactory.createOrderObject(context, table, PaymentTypes.NULL_PAYMENT);
        submitWithNoPayment(object, context);
    }

    private Call<SubmissionStatusObject> submitOrderCall;
    private void submitWithNoPayment(JSONArray orderObject, final Context context) {

        // Grab submit code from basket fragment!
        submitOrderCall = ISnabbOrderAPI.submitOrder(orderObject);
        submitOrderCall.enqueue(new Callback<SubmissionStatusObject>() {
            @Override
            public void onResponse(Call<SubmissionStatusObject> call, Response<SubmissionStatusObject> response) {
                if(badResponse(response, context)) {
                    return;
                }

                Toast.makeText(context, "Order successfully submitted to Server.", Toast.LENGTH_LONG).show();
                goToCreateOrderFragment();
            }

            @Override
            public void onFailure(Call<SubmissionStatusObject> call, Throwable t) {
                Toast.makeText(context, "connection failure to server!", Toast.LENGTH_LONG).show();
            }
        });
    }

    // == Network Error Handling ==
    private boolean badResponse(Response<SubmissionStatusObject> response, final Context context) {

        if(response.code() != 200) {
            Toast.makeText(context, "Something went wrong submitting the order to the server: " + response.code(), Toast.LENGTH_LONG).show();
            return true;
        }

        if (!response.body().getStatus().equals("success")) {
            Toast.makeText(context, "Something went wrong submitting the order to the server: " + response.body().getData(), Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    public void goToCardInfoFragment() {

        CardinfoFragment fragment = new CardinfoFragment();
        android.support.v4.app.FragmentTransaction cardInfoFragmentTransaction =
                fragmentManager.beginTransaction();

        cardInfoFragmentTransaction.replace(containerId, fragment);
        cardInfoFragmentTransaction.commit();
        cardInfoFragmentTransaction.addToBackStack("cardInfoFragment");

        parentActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        hideKeyboard();
    }

    public void goToDetailedCardInfoFragment(){

        CardinfoFragment_Expanded fragment = new CardinfoFragment_Expanded();
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();

        fragmentTransaction.replace(containerId, fragment);
        fragmentTransaction.commit();
        fragmentTransaction.addToBackStack("detailedCardFragment");

        ((MainActivity)parentActivity).hasJustScanned = false;
        parentActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        hideKeyboard();
    }

    public void goToSettingsFragment() {

        SettingsFragment fragment = new SettingsFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();

        fragmentTransaction.replace(containerId, fragment);
        fragmentTransaction.commit();
        fragmentTransaction.addToBackStack("SettingsFragment");
        parentActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        hideKeyboard();
    }

    public void goToLogInFragment() {

        LoginFragment fragment = new LoginFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();

        fragmentTransaction.replace(containerId, fragment);
        fragmentTransaction.commit();
        parentActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        hideKeyboard();
    }

    public void goToManualPaymentFragment() {
        ManualPaymentFragment fragment = new ManualPaymentFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();

        fragmentTransaction.replace(containerId, fragment, "ManualPaymentFragment");
        fragmentTransaction.addToBackStack("ManualPaymentFragment");
        fragmentTransaction.commit();

        parentActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        StateChanger.setState(ScreenState.onManualPaymentScreen);
        hideKeyboard();
    }

    public void goToBasketFragment() {
        BasketFragment fragment = new BasketFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container, fragment, "BasketFragment");
        fragmentTransaction.commit();
        fragmentTransaction.addToBackStack("BasketFragment");
        hideKeyboard();
    }

    public void goToReportsFragment() {
        ReportsFragment fragment = new ReportsFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container, fragment, "ReportsFragment");
        fragmentTransaction.commit();
        fragmentTransaction.addToBackStack("ReportsFragment");
        hideKeyboard();
    }

    private void hideKeyboard() {

        View view = parentActivity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)parentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}