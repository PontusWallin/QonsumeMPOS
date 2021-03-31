package com.smoothsys.qonsume_pos.Adapters.OrderListScreen;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import com.smoothsys.qonsume_pos.DataDump.OrderStatus;
import com.smoothsys.qonsume_pos.Models.Message.MessageSentResponse;
import com.smoothsys.qonsume_pos.Models.OrderClasses.Detail;
import com.smoothsys.qonsume_pos.Models.OrderClasses.Order;
import com.smoothsys.qonsume_pos.Models.SimpleResponse;
import com.smoothsys.qonsume_pos.Models.SubmissionStatusObject;
import com.smoothsys.qonsume_pos.Retrofit.ISnabbOrderAPI;
import com.smoothsys.qonsume_pos.Retrofit.RetrofitManager;
import com.smoothsys.qonsume_pos.Utilities.Cache;
import com.smoothsys.qonsume_pos.Utilities.Config;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderStatusChanger {

    private static ISnabbOrderAPI ISnabbOrderAPI = null;

    private static void init(SharedPreferences prefs) {
        ISnabbOrderAPI = RetrofitManager.SetupInterfaceFromCredentials(prefs);
    }

    public static int convertOrderStatusStringToInt(String orderStatus) {

        int orderSt = 0;
        switch(orderStatus) {
            case OrderStatus.PENDING:
                orderSt = 1;
                break;
            case OrderStatus.COMPLETED:
                orderSt = 2;
                break;
            case OrderStatus.DELIVERED:
                orderSt = 3;
                break;
            case OrderStatus.CANCELED:
                orderSt = 4;
                break;
        }
        return orderSt;
    }

    private static int orderStatusInt;
    static IOrderChange orderStatusDelegate;
    public static void changeOrderStatus(final Order order, int orderStatus, SharedPreferences prefs, final Context ct, IOrderChange delegate) {
        orderStatusDelegate = delegate;

        // instantiate if not already so..
        if(ISnabbOrderAPI == null) {
            ISnabbOrderAPI = RetrofitManager.SetupInterfaceFromCredentials(prefs);
        }

        orderStatusInt = orderStatus;
        changeOrderStatusAndSetNotificationMessage(ct, order, orderStatus);
    }

    private static void changeOrderStatusAndSetNotificationMessage(final Context ct, final Order order, final int orderStatus) {
        Call<SubmissionStatusObject> call = ISnabbOrderAPI.changeOrderStatus(Integer.parseInt(order.getId()),orderStatus);
        call.enqueue(new Callback<SubmissionStatusObject>() {
            @Override
            public void onResponse(Call<SubmissionStatusObject> call, Response<SubmissionStatusObject> response) {

                if(Config.NOTIFICATION_MSG_TYPE == Config.MSG_PER_ORDER) {
                    if (orderStatus == 2) {
                        setNotificationMessage(ct, order);
                    }
                }

                String orderStatusIntToString = convertOrderStatusIntToString(orderStatusInt);
                orderStatusDelegate.updateOrderStatus(orderStatusIntToString);
            }

            @Override
            public void onFailure(Call<SubmissionStatusObject> call, Throwable t) {
                Toast.makeText(ct, "Something went wrong when changing order status!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void changeItemStatus(Detail detail, int itemId, int receiptNo, int itemState, int itemPosition, IOrderChange delegate, Context context, View v) {
        orderStatusDelegate = delegate;

        // instantiate if not already so..
        if(ISnabbOrderAPI == null) {
            ISnabbOrderAPI = RetrofitManager.SetupInterfaceFromCredentials(PreferenceManager.getDefaultSharedPreferences(context));
        }

        do_changeItemStatus(detail, itemId, receiptNo, itemState, itemPosition, context, v);
    }

    private static void do_changeItemStatus(final Detail detail, final int itemId, final int receiptNo, final int itemState, final int itemPosition, final Context context, final View v){
        Call<SimpleResponse> call = ISnabbOrderAPI.changeDetailStatus(itemId, itemState);
        call.enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {

                if(Config.NOTIFICATION_MSG_TYPE == Config.MSG_PER_ITEM) {
                    if (itemState == 2) {
                        setNotificationMessage(context, detail, receiptNo);
                    }
                }
                orderStatusDelegate.updateDetailStatus(itemId, itemState, itemPosition);
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) { }
        });
    }

    private static void setNotificationMessage(final Context ct, Detail detail, int receiptNo) {
        int detailID = detail.getId();
        String orderMsg = generateMsgString(detail, receiptNo);
        Call<MessageSentResponse> msgCall = ISnabbOrderAPI.sendOrderMessage(detailID, Cache.mMerchant.getUserID(), orderMsg);
        msgCall.enqueue(new Callback<MessageSentResponse>() {
            @Override
            public void onResponse(Call<MessageSentResponse> call, Response<MessageSentResponse> response) {}

            @Override
            public void onFailure(Call<MessageSentResponse> call, Throwable t) {
                Toast.makeText(ct, "Something went wrong when sending notification message to customer!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static void setNotificationMessage(final Context ct, Order order) {
        int orderID = Integer.parseInt(order.getId());
        String orderMsg = generateMsgString(order);
        Call<MessageSentResponse> msgCall = ISnabbOrderAPI.sendOrderMessage(orderID, Cache.mMerchant.getUserID(), orderMsg);
        msgCall.enqueue(new Callback<MessageSentResponse>() {
            @Override
            public void onResponse(Call<MessageSentResponse> call, Response<MessageSentResponse> response) {}

            @Override
            public void onFailure(Call<MessageSentResponse> call, Throwable t) {
                Toast.makeText(ct, "Something went wrong when sending notification message to customer!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static String generateMsgString(Detail detail, int receiptNo) {
        return Cache.mRestaurant.getName() + ": " + detail.getItem_name() + " from order: " + receiptNo + " is ready!";
    }

    private static String generateMsgString(Order order) {
        return "Order No " + order.getReceipt_number() + " is finished!";
    }

    private static String convertOrderStatusIntToString(int orderSt) {

        String returnString = "";
        switch(orderSt) {
            case 1:
                returnString = OrderStatus.PENDING;
                break;

            case 2:
                returnString = OrderStatus.COMPLETED;
                break;

            case 3:
                returnString = OrderStatus.DELIVERED;
                break;

            case 4:
                returnString = OrderStatus.CANCELED;
                break;
        }
        return returnString;
    }
}