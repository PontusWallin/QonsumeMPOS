package com.smoothsys.qonsume_pos.DataDump;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.smoothsys.qonsume_pos.Models.OrderClasses.Order;
import com.smoothsys.qonsume_pos.Models.PaymentOption;
import com.smoothsys.qonsume_pos.Models.RestaurantClasses.Restaurant;
import com.smoothsys.qonsume_pos.R;
import com.smoothsys.qonsume_pos.Utilities.Cache;
import com.smoothsys.qonsume_pos.Utilities.Config;
import com.smoothsys.qonsume_pos.Utilities.OrderDataDump;
import com.smoothsys.qonsume_pos.Utilities.Utils;

import java.util.ArrayList;

public class PaymentOptions {

    public static ArrayList<PaymentOption> paymentOptions;

    private static boolean pod_enabled;
    private static boolean credit_card_enabled;
    private static boolean paypal_enabled;
    private static boolean swish_enabled;

    public static ArrayList<PaymentOption> getPaymentOptions(Context context) {

        setPaymentAvailableBooleans();

        paymentOptions = new ArrayList<>();

        // == If new Order ==
        if(OrderDataDump.orderToPay == null) {

            // Show pay on delivery
            showFullOrdersPOD(context);

        } else {

            // == If previously existing order ==

            // 1. Previous order - set to Pay On Delivery
            if(OrderDataDump.orderToPay.getPaymentMethod().equals(PaymentTypes.PAY_ON_DELIVERY) || OrderDataDump.orderToPay.getPaymentMethod().equals(PaymentTypes.PAY_ON_DELIVERY_LONG)){

                showFullOrdersCash(context);

            // 2. Previous order - not set to Pay On Delivery
            } else {

                showFullOrdersPOD(context);
            }
        }

        return paymentOptions;
    }

    private static void setPaymentAvailableBooleans() {

        Restaurant thisRestaurant = Cache.mRestaurant;

        pod_enabled = Utils.convertToBoolean(thisRestaurant.getCodEnabled());
        credit_card_enabled = Utils.convertToBoolean(thisRestaurant.getBanktransferEnabled());
        paypal_enabled = Utils.convertToBoolean(thisRestaurant.getPaypalEnabled());
        swish_enabled = Utils.convertToBoolean(thisRestaurant.getStripeEnabled());
    }

    private static void showFullOrdersMinusPOD(Context context) {

        returnQPay(context);
        returnSwish(context);
        returnCreditCard(context);
    }

    private static void showFullOrdersPOD(Context context) {

        returnQPay(context);
        returnSwish(context);
        returnPOD(context);
    }

    private static void showFullOrdersCash(Context context) {

        returnQPay(context);
        returnSwish(context);
        returnPOD(context);
    }

    private static void returnSwish(Context context) {

        if(swish_enabled) {
            String optionNameSwish = context.getResources().getString(R.string.payOption_swish);
            String optionDescSwish = context.getResources().getString(R.string.payDesc_swish);
            paymentOptions.add(new PaymentOption(optionNameSwish, "swish_icon", optionDescSwish, PaymentTypes.SWISH));
        }
    }

    private static void returnCreditCard(Context context) {

        if(credit_card_enabled) {
            String optionNameCreditCard = context.getResources().getString(R.string.payOption_credit);
            String optionDescCreditCard = context.getResources().getString(R.string.payDesc_credit);
            paymentOptions.add(new PaymentOption(optionNameCreditCard, "credit_card_icon", optionDescCreditCard, PaymentTypes.CREDIT_CARD));
        }
    }

    private static void returnQPay(Context context) {

        if(Config.DEBUG_ENABLE_QPAY) {
            String optionNameSnabbpay = context.getResources().getString(R.string.payOption_Qpay);
            String optionDescSnabbPay = context.getResources().getString(R.string.payDesc_Qpay);
            paymentOptions.add(new PaymentOption(optionNameSnabbpay, "snabbpay_icon", optionDescSnabbPay, PaymentTypes.QPAY));
        }
    }

    private static void returnPOD(Context context) {

        if(pod_enabled) {
            String optionNameCash = context.getResources().getString(R.string.payOption_cash);
            String optionDescCash = context.getResources().getString(R.string.payDesc_cash);
            paymentOptions.add(new PaymentOption(optionNameCash, "pod_icon", optionDescCash, PaymentTypes.PAY_ON_DELIVERY));
        }
    }

    public static Drawable getPaymentIcon(String iconName, Activity activity) {
        if(iconName.equals("snabbpay_icon")) {
            return ContextCompat.getDrawable(activity, R.drawable.qpay_icon);
        } else if (iconName.equals("swish_icon")){
            return ContextCompat.getDrawable(activity, R.drawable.swish_icon);
        } else if(iconName.equals("credit_card_icon")) {
            return ContextCompat.getDrawable(activity, R.drawable.credit_card_icon);
        } else if(iconName.equals("pod_icon")) {
            return ContextCompat.getDrawable(activity, R.drawable.pod_icon);
        }
        else {
            return null;
        }
    }

    private static void showPreviouslySelectedPaymentType(Context context) {
        Order orderToPay = OrderDataDump.orderToPay;
        String paymentMethod = orderToPay.getPaymentMethod();

        if(paymentMethod.equals(PaymentTypes.SWISH_CAPITAL_S) || paymentMethod.equals(PaymentTypes.SWISH)){
            returnSwish(context);
        } else if(paymentMethod.equals(PaymentTypes.CREDIT_CARD)) {
            returnCreditCard(context);
        } else if(paymentMethod.equals(PaymentTypes.QPAY)) {
            returnQPay(context);
        }
    }
}
