package com.smoothsys.qonsume_pos.FragmentsAndActivities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.smoothsys.qonsume_pos.Adapters.PaymentOptionsAdapter;
import com.smoothsys.qonsume_pos.Bambora.BamboraManager;
import com.smoothsys.qonsume_pos.Bambora.IBamboraTransaction;
import com.smoothsys.qonsume_pos.DataDump.PaymentOptions;
import com.smoothsys.qonsume_pos.DataDump.PaymentTypes;
import com.smoothsys.qonsume_pos.Factories.SubmitObjectFactory;
import com.smoothsys.qonsume_pos.Models.Credit;
import com.smoothsys.qonsume_pos.Models.OrderClasses.Order;
import com.smoothsys.qonsume_pos.Models.OrderClasses.OrderResponseObject;
import com.smoothsys.qonsume_pos.Models.PaymentOption;
import com.smoothsys.qonsume_pos.Models.SubmissionStatusObject;
import com.smoothsys.qonsume_pos.R;
import com.smoothsys.qonsume_pos.Retrofit.ISnabbOrderAPI;
import com.smoothsys.qonsume_pos.Retrofit.RetrofitManager;
import com.smoothsys.qonsume_pos.StateManagement.ScreenState;
import com.smoothsys.qonsume_pos.StateManagement.ScreenSwitcher;
import com.smoothsys.qonsume_pos.StateManagement.StateChanger;
import com.smoothsys.qonsume_pos.Utilities.Cache;
import com.smoothsys.qonsume_pos.Utilities.OrderDataDump;
import com.smoothsys.qonsume_pos.Utilities.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentOptionsFragment extends Fragment implements IBamboraTransaction {

    // == Data ==
    double amount = 0.0f;
    Boolean justScanned = false;
    PaymentOption selectedPayOption;
    private Order orderToBePayed;

    // == UI ==
    Button confirmBtn;
    ListView paymentOptionsLv;
    private ProgressDialog prgDialog;

    // == Misc. ==
    ScreenSwitcher screenSwitcher;
    private ISnabbOrderAPI ISnabbOrderAPI;

    // == Network Calls ==
    private static Call<ResponseBody> checkTransactionStatusCall;
    private Call<ResponseBody> payCall;
    private Call<List<Credit>> creditCheckCall;
    private Call<ResponseBody> decrementCall;
    private Call<ResponseBody> payOrderCall;
    private Call<SubmissionStatusObject> submitOrderCall;
    private Call<OrderResponseObject> getAllOrders;

    public PaymentOptionsFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        ISnabbOrderAPI = RetrofitManager.SetupInterfaceFromCredentials(prefs);
        screenSwitcher = new ScreenSwitcher(R.id.fragment_container, getActivity());

        initProgressDialog();

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(OrderDataDump.orderToPay != null) {
            orderToBePayed = OrderDataDump.orderToPay;
        }

        if(getArguments() != null) {
            amount = getArguments().getDouble("Amount");

        } else {
            // we don't have arg data
        }
        return inflater.inflate(R.layout.payment_options_layout, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        StateChanger.setState(ScreenState.onPaymentOptionsScreen);
        (getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ((MainActivity)getActivity()).setTitleBarText(getString(R.string.payment_options_title));

        setupPaymentOptionList();

        setupConfirmBtn();
    }

    // == Setup UI ==
    private void setupPaymentOptionList() {

        paymentOptionsLv = getView().findViewById(R.id.paymentOptionsLv);

        List<PaymentOption> paymentOptions = PaymentOptions.getPaymentOptions(getContext());

        if(paymentOptions.size() == 0) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(R.string.no_pay_options)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            screenSwitcher.goToCreateOrderFragment();
                        }
                    });
            Dialog dia = builder.create();
            dia.show();
        }

        PaymentOptionsAdapter adapter = new PaymentOptionsAdapter(getContext(),paymentOptions , getActivity());
        paymentOptionsLv.setAdapter(adapter);
        paymentOptionsLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for(int i = 0; i< parent.getChildCount(); i++) {
                    parent.getChildAt(i).setBackground(getResources().getDrawable(R.drawable.rounded_background));
                }
                view.setBackground(getResources().getDrawable(R.drawable.rounded_background_dark));
                selectedPayOption = (PaymentOption) view.getTag();
            }
        });
    }

    private void setupConfirmBtn() {

        confirmBtn = getView().findViewById(R.id.confirm_btn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePayment();
            }
        });
    }

    public void makePayment() {
        if(selectedPayOption == null) {
            Toast.makeText(getContext(), "No option selected.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(OrderDataDump.orderToPay == null){
            payFromCreateScreen(selectedPayOption.getType());
        } else {
            makePaymentForExistingOrder(selectedPayOption.getType(), 0);
        }
    }

    private void initProgressDialog() {
        try {
            prgDialog = new ProgressDialog(getContext());
            prgDialog.setMessage("Please wait...");
            prgDialog.setCancelable(false);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initProgressDialog.", e);
        }
    }

    // == Payment Stuff ==
    private void payFromCreateScreen(final String paymentType) {

        // == Check order type ==
        if(paymentType.equals(PaymentTypes.QPAY)) {
            pre_makeSnabbpayPayment(amount);
        } else if(paymentType.equals(PaymentTypes.PAY_ON_DELIVERY)) {
            submitWithNoPayment(SubmitObjectFactory.createOrderObject(getContext(), ((MainActivity)getActivity()).tableNumber, PaymentTypes.PAY_ON_DELIVERY));
        } else if(paymentType.equals(PaymentTypes.CREDIT_CARD)){
            pre_makeBamboraPayment();
        } else if(paymentType.equals(PaymentTypes.SWISH)){
            pre_makePaymentSwish();
        } else {
            Toast.makeText(getContext(), "No Payment Type Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void makePaymentForExistingOrder(final String paymentType, double manualAmount) {

        // == If order is null, we are making a demo payment ==
        if(orderToBePayed == null) {

            orderToBePayed = new Order();
            orderToBePayed.setTotalAmount(String.valueOf(manualAmount));
            orderToBePayed.setId("-1");
            orderToBePayed.setShopId(String.valueOf(Cache.mMerchant.getShopID()));
        }

        // == Check payment status ==
        checkTransactionStatusCall = ISnabbOrderAPI.paymentStatus(Integer.parseInt(orderToBePayed.getId()));
        checkTransactionStatusCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                String paymentDate = "";
                try {
                    paymentDate = response.body().string();

                    // == if no response - this means it's part of demo ==
                    if(paymentDate.equals("[]")) {
                        makeSnabbpayPayment();
                    } else {
                        paymentDate = paymentDate.split(":")[1];
                    }

                } catch (IOException e) {
                    
                    e.printStackTrace();
                }

                // == If not payed for before ==
                if(paymentDate.equals("null}]") || paymentDate.equals("[]")) {
                    payFromOrderScreen(paymentType);
                } else {
                    Toast.makeText(getContext(), "This order has already been payed.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Something went wrong - Please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void payFromOrderScreen(String paymentType) {

        // == Check order type ==
        if(paymentType.equals(PaymentTypes.QPAY)) {
            makeSnabbpayPayment();
        } else if(paymentType.equals(PaymentTypes.SWISH)) {
            makePaymentSwish();
        } else if(paymentType.equals(PaymentTypes.CREDIT_CARD)){
            makeBamboraPayment();
        } else if(paymentType.equals(PaymentTypes.PAY_ON_DELIVERY)){
            makeCashPayment();
        } else {
            Toast.makeText(getContext(), "No Payment Type Selected!", Toast.LENGTH_SHORT).show();
        }
    }

    // == Cash Payment ==
    private void makeCashPayment() {

        payOrderCall = ISnabbOrderAPI.payOrder(Integer.parseInt(OrderDataDump.orderToPay.getId()), Cache.mMerchant.getShopID(), PaymentTypes.PAY_ON_DELIVERY, Double.parseDouble(OrderDataDump.orderToPay.getTotalAmount()));
        payOrderCall.enqueue(new Callback<ResponseBody>() {
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

                Toast.makeText(getContext(), "Successful payment! - " + message, Toast.LENGTH_LONG).show();

                OrderDataDump.orderToPay = null;
                screenSwitcher.goToOrderTabsFragment();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Order was submitted, but payment status was not changed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // == Bambora Payment ==
    private void pre_makeBamboraPayment() {
        BamboraManager.selectCardAndPrePay(getContext(), this);
    }

    private void makeBamboraPayment() {
        BamboraManager.selectCardAndPay(getContext(), this);
    }

    @Override
    public void successfullBamboraPayment() {

        payCall = ISnabbOrderAPI.payOrder(Integer.parseInt(orderToBePayed.getId()), Integer.parseInt(orderToBePayed.getShopId()), PaymentTypes.CREDIT_CARD, Double.parseDouble(orderToBePayed.getTotalAmount()));
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

                Toast.makeText(getContext(), "Successful payment! - " + message, Toast.LENGTH_LONG).show();

                fragmentFinished();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Something went wrong with payment - " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fragmentFinished() {

        // == Special Case For demo ==
        if(orderToBePayed.getId().equals("-1")) {
            new ScreenSwitcher(R.id.fragment_container, getActivity()).goToManualPaymentFragment();
        } else {
            new ScreenSwitcher(R.id.fragment_container, getActivity()).goToOrderTabsFragment();
        }
    }

    @Override
    public void successfullBamboraPrePayment() {

        // Payment successful - now submit order
        JSONArray orderObject = SubmitObjectFactory.createOrderObject(getContext(), ((MainActivity)getActivity()).tableNumber, PaymentTypes.CREDIT_CARD);
        //submitOrderObjectAndSetPayStatus(orderObject, PaymentTypes.CREDIT_CARD);
        submitWithNoPayment(orderObject);
    }

    // == Swish Payment ==
    private void pre_makePaymentSwish() {

        JSONArray object = SubmitObjectFactory.createOrderObject(getContext(),((MainActivity)getActivity()).tableNumber, PaymentTypes.SWISH);
        //submitOrderObjectAndSetPayStatus(object, PaymentTypes.SWISH);
        submitWithNoPayment(object);
    }

    private void makePaymentSwish() {

        payOrderCall = ISnabbOrderAPI.payOrder(Integer.parseInt(OrderDataDump.orderToPay.getId()), Cache.mMerchant.getShopID(), PaymentTypes.SWISH, Double.parseDouble(OrderDataDump.orderToPay.getTotalAmount()));
        payOrderCall.enqueue(new Callback<ResponseBody>() {
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

                Toast.makeText(getContext(), "Successful payment! - " + message, Toast.LENGTH_LONG).show();

                OrderDataDump.orderToPay = null;
                screenSwitcher.goToOrderTabsFragment();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Order was submitted, but payment status was not changed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // == Snabbpay Methods ==
    private void pre_makeSnabbpayPayment(double amount) {
        justScanned = true;
        ((MainActivity)getActivity()).scanForPrePayment(amount);
    }

    private void makeSnabbpayPayment() {
        justScanned = true;
        ((MainActivity)getActivity()).scanForPayment(orderToBePayed);
    }

    //This method is executed after Snabbpay Scan
    public void checkAndReduceSnabbpayCredit(final double amountToBePayed, final String email_from_qr) {

        /*if(!email_from_qr.equals(orderToBePayed.getEmail())) {
            Toast.makeText(getContext(), "QR email does not match order email!", Toast.LENGTH_LONG).show();
            return;
        }*/

        creditCheckCall = ISnabbOrderAPI.getCustomerCredit(email_from_qr);
        creditCheckCall.enqueue(new Callback<List<Credit>>() {
            @Override
            public void onResponse(Call<List<Credit>> call, Response<List<Credit>> response) {
                if(!isResponseValid(response)) {
                    Toast.makeText(getContext(), "Something went wrong - Response is not valid!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Credit credit = response.body().get(0);
                if(credit.getAmount() >= amountToBePayed) {
                    // == Scan Code And Reduce Payment ==
                    double amountToBeReducedTo = credit.getAmount() - amountToBePayed;
                    reduce(email_from_qr, amountToBeReducedTo);

                } else {
                    Toast.makeText(getContext(), "Insufficient Credit!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Credit>> call, Throwable t) {
                Toast.makeText(getContext(), "Something went wrong when checking customer credit.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void reduce(final String custEmail, double amountToReduceTo) {

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

                String message = "";
                try {
                    JSONObject jObj = new JSONObject(resString);
                    jObj = jObj.getJSONObject("success");
                    message = jObj.getString("message");
                } catch (JSONException e) {
                    
                    e.printStackTrace();
                }

                if(message.equals("SnabbCredit reduced")) {

                    if(orderToBePayed != null) {
                        sendQPayPayment();
                    } else {

                        JSONArray orderObject = SubmitObjectFactory.createOrderObject(getContext(), ((MainActivity)getActivity()).tableNumber, PaymentTypes.QPAY);
                        //submitOrderObjectAndSetPayStatus(orderObject, PaymentTypes.QPAY);
                        submitWithNoPayment(orderObject);
                    }
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

    private void sendQPayPayment() {

        // == Make the payment ==
        payCall = ISnabbOrderAPI.payOrder(Integer.parseInt(orderToBePayed.getId()), Integer.parseInt(orderToBePayed.getShopId()), PaymentTypes.QPAY, Double.parseDouble(orderToBePayed.getTotalAmount()));
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

                Toast.makeText(getContext(), "Succesful payment! - " + message, Toast.LENGTH_LONG).show();

                // == Special Case For demo ==
                if(orderToBePayed.getId().equals("-1")) {
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

    // == Submission Methods ==
    private void submitOrderObjectAndSetPayStatus(JSONArray orderObject, final String paymentType) {

        submitOrderCall = ISnabbOrderAPI.submitOrder(orderObject);
        submitOrderCall.enqueue(new Callback<SubmissionStatusObject>() {
            @Override
            public void onResponse(Call<SubmissionStatusObject> call, Response<SubmissionStatusObject> response) {

                if(badResponse(response)) {
                    return;
                }

                Toast.makeText(getContext(), "Order successfully submitted to Server.", Toast.LENGTH_LONG).show();
                ((MainActivity)getActivity()).clearBasketData();

                // Here we should get the order from our order list
                getAllOrders = ISnabbOrderAPI.getOrders_old(Cache.mMerchant.getShopID());
                getAllOrders.enqueue(new Callback<OrderResponseObject>() {
                    @Override
                    public void onResponse(Call<OrderResponseObject> call, Response<OrderResponseObject> response) {

                        //find correct order
                        List<Order> allOrders = response.body().getData();
                        Order firstOrderForMerchant = null;
                        for(int i = 0; i < allOrders.size(); i++) {

                            Order cOrder = allOrders.get(i);

                            if(cOrder.getEmail().equals(Cache.mMerchant.getUserEmail())) {
                                firstOrderForMerchant = cOrder;
                                break;
                            }
                        }

                        if(firstOrderForMerchant == null) {
                            // TODO - what to do here? Make another call again?
                            String errorText = "Error - could not find the recently added order!";
                            Toast.makeText(getContext(), errorText, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Change order status of firstOrder...
                        payOrderCall = ISnabbOrderAPI.payOrder(Integer.parseInt(firstOrderForMerchant.getId()), Cache.mMerchant.getShopID(), paymentType, Double.parseDouble(firstOrderForMerchant.getTotalAmount()));
                        payOrderCall.enqueue(new Callback<ResponseBody>() {
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

                                Toast.makeText(getContext(), "Successful payment! - " + message, Toast.LENGTH_LONG).show();

                                OrderDataDump.orderToPay = null;
                                screenSwitcher.goToCreateOrderFragment();
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toast.makeText(getContext(), "Order was submitted, but payment status was not changed!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<OrderResponseObject> call, Throwable t) {
                        Toast.makeText(getContext(), "ERROR! Could not get your newly created order from server.", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<SubmissionStatusObject> call, Throwable t) {
                Toast.makeText(getContext(), "Something went wrong submitting the order to the server: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitWithNoPayment(JSONArray orderObject) {

        // Grab submit code from basket fragment!
        submitOrderCall = ISnabbOrderAPI.submitOrder(orderObject);
        submitOrderCall.enqueue(new Callback<SubmissionStatusObject>() {
            @Override
            public void onResponse(Call<SubmissionStatusObject> call, Response<SubmissionStatusObject> response) {
                if(badResponse(response)) {
                    return;
                }

                Toast.makeText(getContext(), "Order successfully submitted to Server.", Toast.LENGTH_LONG).show();
                ((MainActivity)getActivity()).clearBasketData();
                screenSwitcher.goToCreateOrderFragment();
            }

            @Override
            public void onFailure(Call<SubmissionStatusObject> call, Throwable t) { }
        });
    }

    // == Network Error Handling ==
    private boolean badResponse(Response<SubmissionStatusObject> response) {

        if(response.code() != 200) {
            Toast.makeText(getContext(), "Something went wrong submitting the order to the server: " + response.code(), Toast.LENGTH_LONG).show();
            return true;
        }

        if (!response.body().getStatus().equals("success")) {
            Toast.makeText(getContext(), "Something went wrong submitting the order to the server: " + response.body().getData(), Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
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