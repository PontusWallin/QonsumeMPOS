package com.smoothsys.qonsume_pos.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.smoothsys.qonsume_pos.R;

import java.util.ArrayList;

/**
 * Created by Pontu on 2018-03-10.
 */

public class CardInfoTransactionListAdapter extends ArrayAdapter<String> {

    public CardInfoTransactionListAdapter(Context context, String[] listItem) {
        super(context, R.layout.card_info_list_item,listItem);
    }

    public CardInfoTransactionListAdapter(Context context, ArrayList<String> listItem) {
        super(context, R.layout.card_info_list_item,listItem);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View myView = inflater.inflate(R.layout.transaction_list_item, parent, false);

        String item = getItem(position);
        TextView textView =  myView.findViewById(R.id.transaction_id_tv);
        textView.setText(item);
        return myView;
    }
}
