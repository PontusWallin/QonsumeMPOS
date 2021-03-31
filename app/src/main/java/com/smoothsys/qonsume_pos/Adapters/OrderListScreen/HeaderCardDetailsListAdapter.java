package com.smoothsys.qonsume_pos.Adapters.OrderListScreen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.smoothsys.qonsume_pos.Models.OrderClasses.Detail;
import com.smoothsys.qonsume_pos.R;

import java.util.List;

/**
 * Created by Pontu on 2018-03-21.
 */

public class HeaderCardDetailsListAdapter extends ArrayAdapter<Detail> {

    private List<Detail> details;

    public HeaderCardDetailsListAdapter(Context context, List<Detail> data) {

        super(context, R.layout.simple_list_row_dynamic_text_size ,data);
        this.details= data;
    }

    public View getView(int position, View converterView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.simple_list_row_dynamic_text_size, parent,false);

        Detail currentDetail = details.get(position);

        TextView textView = customView.findViewById(R.id.textView);

        String detailName = currentDetail.getItem_name();
        String andMore = getContext().getResources().getString(R.string.andmore);
        String qty = "";
        if(!detailName.equals(andMore)) {
            qty = " - " + String.valueOf(currentDetail.getQty());
        }

        textView.setText(detailName  + qty);
        return customView;
    }

    public Boolean andMoreIncluded = false;
    public void editSingleEntry(int position, String text) {

        if(!andMoreIncluded) {

            for(int i = 0; i < details.size(); i++) {

                String andMore = getContext().getResources().getString(R.string.andmore);
                if(details.get(i).getItem_name().equals(andMore)){
                    return;
                }
            }

            details.add(position, new Detail(text, ""));
            andMoreIncluded = true;
        }
    }
}
