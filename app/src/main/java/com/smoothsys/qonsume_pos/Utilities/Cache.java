package com.smoothsys.qonsume_pos.Utilities;


import android.util.Log;

import com.smoothsys.qonsume_pos.Models.ItemClasses.Attribute;
import com.smoothsys.qonsume_pos.Models.ItemClasses.Detail;
import com.smoothsys.qonsume_pos.Models.ItemClasses.Item;
import com.smoothsys.qonsume_pos.Models.Merchant;
import com.smoothsys.qonsume_pos.Models.RestaurantClasses.Category;
import com.smoothsys.qonsume_pos.Models.RestaurantClasses.Restaurant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pontu on 2017-04-07.
 */

public class Cache {

    public static boolean SingedIn = false;

    public static Merchant mMerchant;
    public static Restaurant mRestaurant;
    public static List<Category> mCategories;
    public static List<Item> mRestaurantItems = new ArrayList<>();


    public static int mUserId;
    public static int mShopId;
    public static String mEmail;
    public Cache() { }

    public static List<Detail> getAllActiveDetails(String detailIds) {

        if(detailIds.equals("")) {
            return new ArrayList<>();
        }

        List<Detail> details = new ArrayList<>();

        String[] splitted = detailIds.split(";");
        for(int i = 0; i < splitted.length; i++) {

            int currentDetailId = Integer.parseInt(splitted[i]);

            // == Go through all items until we find it ==

            for(int j = 0; j < mRestaurantItems.size(); j++) {

                Item cItem = mRestaurantItems.get(j);
                for(int k = 0; k <cItem.getAttributes().size(); k++) {
                    Attribute cAttribute = cItem.getAttributes().get(k);

                    for(int l = 0; l < cAttribute.getDetails().size(); l++) {
                        Detail cDetail = cAttribute.getDetails().get(l);

                        int loopedDetailId = (Integer.parseInt(cDetail.getId()));
                        if(loopedDetailId == currentDetailId) {
                            details.add(cDetail);
                        }
                    }
                }
            }
        }
        return details;
    }

    public static void clear() {

        Log.d("Cache", "Cache cleared!");

        mMerchant = null;
        mRestaurant = null;
        mCategories = null;
        mUserId = -1;
        mShopId = -1;
        mEmail = null;
        mRestaurantItems = null;
    }
}