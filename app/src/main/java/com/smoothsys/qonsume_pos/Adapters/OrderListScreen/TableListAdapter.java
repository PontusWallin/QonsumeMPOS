package com.smoothsys.qonsume_pos.Adapters.OrderListScreen;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.smoothsys.qonsume_pos.Models.Table;
import com.smoothsys.qonsume_pos.R;

import java.util.List;

/**
 * Created by Pontu on 2018-03-20.
 */

public class TableListAdapter extends ArrayAdapter<Table> {

    private ConstraintLayout base;

    public interface ITableInterface {
        void onTableClick(int orderNo);
        void onGetMoreEntries();
    }

    private ITableInterface tableClick;
    public TableListAdapter(Context context, List<Table> data, ITableInterface tableClick) {
        super(context, R.layout.table_list_row ,data);
        this.tableClick = tableClick;
    }

    public View getView(int position, View converterView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.table_list_row, parent, false);

        final Table currentTable = getItem(position);

        TextView orderNoText = customView.findViewById(R.id.order_no_tv);

        if (currentTable != null) {
            orderNoText.setText(String.valueOf(currentTable.getOrderNo()));
        }

        base = customView.findViewById(R.id.baseLayout);
        base.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tableClick.onTableClick(currentTable.getOrderNo());
            }
        });

        return customView;
    }
}