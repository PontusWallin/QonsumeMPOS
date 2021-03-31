package com.smoothsys.qonsume_pos.Factories;

import android.content.Context;
import android.widget.Toast;

import com.smoothsys.qonsume_pos.Models.BasketData;
import com.smoothsys.qonsume_pos.Utilities.Cache;
import com.smoothsys.qonsume_pos.Utilities.DBHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SubmitObjectFactory {

    public static JSONArray createOrderObject(Context context, int tableNumber, String paymentMethod) {
        JSONArray jsonArray = new JSONArray();
        DBHandler db = new DBHandler(context);

        final List<BasketData> basket = db.getAllBasketDataByShopId(Cache.mMerchant.getShopID());
        for (BasketData basketData : basket) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject = SubmitObjectFactory.createSingleItem(jsonObject, basketData, tableNumber, paymentMethod);
                jsonArray.put(jsonObject);
            } catch (Exception e) {
                Toast.makeText(context, "Exception! " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        return jsonArray;
    }

    private static JSONObject createSingleItem(JSONObject jsonObject, BasketData basketData, int tableNumber, String paymentMethod) throws JSONException {

        double itemTotal = Double.parseDouble(basketData.getUnitPrice()) * basketData.getQty();

        jsonObject.put("payment_method", paymentMethod);
        jsonObject.put("item_id", String.valueOf(basketData.getItemId()));
        jsonObject.put("shop_id", String.valueOf(basketData.getShopId()));
        jsonObject.put("unit_price", String.valueOf(basketData.getUnitPrice()));
        jsonObject.put("discount_percent", basketData.getDiscountPercent());
        jsonObject.put("name", basketData.getName());
        jsonObject.put("qty", basketData.getQty());
        jsonObject.put("user_id", basketData.getUserId());
        jsonObject.put("payment_trans_id", "");
        jsonObject.put("delivery_address", "");
        jsonObject.put("billing_address", "");
        jsonObject.put("total_amount", String.valueOf(itemTotal));
        jsonObject.put("basket_item_attribute", createCustomAttributesString(basketData));
        jsonObject.put("email", Cache.mMerchant.getUserEmail());
        jsonObject.put("phone", "");
        jsonObject.put("coupon_discount_amount", basketData.getDiscountPercent());
        jsonObject.put("flat_rate_shipping", "");
        jsonObject.put("table_number", String.valueOf(tableNumber));

        return  jsonObject;
    }

    private static String createCustomAttributesString(BasketData basketData) {
        String ret = "";


        String[] attributeNames = basketData.getSelectedAttributeNames().split(";");
        String[] detailNames = basketData.getSelectedDetailNames().split(";");

        for(int i = 0; i < attributeNames.length; i++) {
            ret += attributeNames[i] + ":" + detailNames[i] + ";";
        }

        return ret;
    }

    private static JSONObject createSingleItemCustomEmail(String email, JSONObject jsonObject, BasketData basketData, int tableNumber, String paymentMethod) throws JSONException {

        double itemTotal = Double.parseDouble(basketData.getUnitPrice()) * basketData.getQty();

        jsonObject.put("payment_method", paymentMethod);
        jsonObject.put("item_id", String.valueOf(basketData.getItemId()));
        jsonObject.put("shop_id", String.valueOf(basketData.getShopId()));
        jsonObject.put("unit_price", String.valueOf(basketData.getUnitPrice()));
        jsonObject.put("discount_percent", basketData.getDiscountPercent());
        jsonObject.put("name", basketData.getName());
        jsonObject.put("qty", basketData.getQty());
        jsonObject.put("user_id", basketData.getUserId());
        jsonObject.put("payment_trans_id", "");
        jsonObject.put("delivery_address", "");
        jsonObject.put("billing_address", "");
        jsonObject.put("total_amount", String.valueOf(itemTotal));
        jsonObject.put("basket_item_attribute", createCustomAttributesString(basketData));
        jsonObject.put("email", email);
        jsonObject.put("phone", "");
        jsonObject.put("coupon_discount_amount", basketData.getDiscountPercent());
        jsonObject.put("flat_rate_shipping", "");
        jsonObject.put("table_number", String.valueOf(tableNumber));

        return  jsonObject;
    }

    public static JSONArray createOrderObjectCustomEmail(String email, Context context, int tableNumber, String paymentMethod) {
        JSONArray jsonArray = new JSONArray();
        DBHandler db = new DBHandler(context);

        final List<BasketData> basket = db.getAllBasketDataByShopId(Cache.mMerchant.getShopID());
        for (BasketData basketData : basket) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject = SubmitObjectFactory.createSingleItemCustomEmail(email, jsonObject, basketData, tableNumber, paymentMethod);
                jsonArray.put(jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Exception! " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        return jsonArray;
    }

}
