package com.smoothsys.qonsume_pos.Utilities;

import android.view.View;
import android.widget.RadioButton;

import com.smoothsys.qonsume_pos.Models.ItemClasses.Attribute;
import com.smoothsys.qonsume_pos.Models.ItemClasses.Detail;
import com.smoothsys.qonsume_pos.Models.ItemClasses.Item;
import com.smoothsys.qonsume_pos.Models.AttributeDetailPair;

import java.util.ArrayList;
import java.util.List;

public class ActivePairsHelper {

    public static List<AttributeDetailPair> getActivePairs(View itemView, Item item) {

        List<AttributeDetailPair> activeAttributeDetailPairs = new ArrayList<>();
        List<Attribute> allAttributes = item.getAttributes();

        for(int i = 0; i<allAttributes.size(); i++) {
            Attribute currentAttribute = allAttributes.get(i);

            List<Detail> activeDetails = getActiveDetails(itemView, currentAttribute);
            if(activeDetails.size() > 0) {
                activeAttributeDetailPairs.add(new AttributeDetailPair(currentAttribute, activeDetails));
            }
        }
        return activeAttributeDetailPairs;
    }

    // This method will grab the radio button from itemView and use it find out wich attribute detail is active
    public static List<Detail> getActiveDetails(View itemView, Attribute attribute) {
        List<Detail> details = attribute.getDetails();
        List<Detail> activeDetails = new ArrayList<>();
        for(int i = 0; i<details.size(); i++) {
            Detail currentDetail = details.get(i);
            RadioButton btn = itemView.findViewWithTag(currentDetail);

            if(btn == null) {
                continue;
            }
            if(btn.isChecked()) {
                activeDetails.add(currentDetail);
            }
        }

        // If no detail is selected - select the first one by default
        if(activeDetails.size() == 0) {
            activeDetails.add(details.get(0));
        }

        return activeDetails;
    }

    public static List<AttributeDetailPair> getDefaultPairs(Item item) {

        List<AttributeDetailPair> activeAttributeDetailPairs = new ArrayList<>();
        List<Attribute> allAttributes = item.getAttributes();

        for(int i = 0; i<allAttributes.size(); i++) {
            Attribute currentAttribute = allAttributes.get(i);

            List<Detail> activeDetails = new ArrayList<>();
            activeDetails.add(currentAttribute.getDetails().get(0));

            if(activeDetails.size() > 0) {
                activeAttributeDetailPairs.add(new AttributeDetailPair(currentAttribute, activeDetails));
            }
        }
        return activeAttributeDetailPairs;

    }

    public static String getAttributeIds(List<AttributeDetailPair> pairs) {
        String ret = "";
        for(int i = 0; i<pairs.size(); i++) {
            Attribute currentAttribute = pairs.get(i).attribute;
            ret += currentAttribute.getId() + ";";
        }
        return ret;
    }

    public static String getDetailIds(List<AttributeDetailPair> pairs) {
        String ret = "";
        for(int i = 0; i<pairs.size(); i++) {
            List<Detail> details = pairs.get(i).detailList;
            for(int j = 0; j < details.size(); j++) {
                ret += details.get(j).getId() + ";";
            }
        }
        return ret;
    }

    public static String getDetailName(int detailId) {
        String ret = "";
        for(int i = 0; i<Cache.mRestaurantItems.size(); i++) {

            Item currentItem = Cache.mRestaurantItems.get(i);
            List<Attribute> attributes = currentItem.getAttributes();
            for(int j = 0; j< attributes.size(); j++) {
                Attribute cAttribute = attributes.get(j);

                List<Detail> details = cAttribute.getDetails();
                for(int k = 0; k < details.size(); k++) {
                    Detail cDetail = details.get(k);

                    if(Integer.parseInt(cDetail.getId()) == detailId) {
                        return cDetail.getName();
                    }

                }
            }

        }
        return "No detail name";
    }

    public static String getDetailNames(List<AttributeDetailPair> pairs) {
        String ret = "";
        for(int i = 0; i < pairs.size(); i++) {
            List<Detail> cDetails = pairs.get(i).detailList;

            for(int j = 0; j < cDetails.size(); j++) {
                Detail currentDetail = cDetails.get(j);

                ret += currentDetail.getName() + ";";
            }
        }
        return ret;
    }

    public static String getAttributeNames(List<AttributeDetailPair> pairs) {
        String ret = "";
        for(int i = 0; i< pairs.size(); i++) {
            Attribute currentAttribute = pairs.get(i).attribute;
            ret += currentAttribute.getName() + ";";
        }

        return ret;
    }
}