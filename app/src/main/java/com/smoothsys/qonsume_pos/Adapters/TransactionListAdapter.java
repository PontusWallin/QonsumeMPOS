package com.smoothsys.qonsume_pos.Adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.smoothsys.qonsume_pos.Models.OrderClasses.Order;
import com.smoothsys.qonsume_pos.R;

import java.util.List;


public class TransactionListAdapter extends ArrayAdapter<Order> {

    public TransactionListAdapter(@NonNull Context context, @LayoutRes int resource, List<Order> transactions) {
        super(context, resource, transactions);
    }

    @Override
    public View getView(int position, View converterView, ViewGroup parent) {

        View v = converterView;

        if(v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_item_transaction, null);
        }

        Order currentTransaction = (Order) getItem(position);
        if(currentTransaction != null) {

            TextView custName = v.findViewById(R.id.custName);
            custName.setText("Customer Email: " + currentTransaction.getEmail());

            TextView trxType = v.findViewById(R.id.transactionType);
            trxType.setText("Payment Type: " + currentTransaction.getPaymentMethod());

            TextView idRow = v.findViewById(R.id.IdRow);
            idRow.setText("Order ID: " + currentTransaction.getReceipt_number());

            TextView trIdRow = v.findViewById(R.id.tansaction_idRow);
            String trNo = "0";
            if(currentTransaction.getTransactionRefNo() != null) {
                trNo = currentTransaction.getTransactionRefNo().toString();
            }
            trIdRow.setText("Transaction Reference: " + trNo);

            TextView dateRow = v.findViewById(R.id.dateRow);
            dateRow.setText("Date: " + currentTransaction.getPaymentDate());

            TextView amountRow = v.findViewById(R.id.amountRow);
            amountRow.setText("Amount: " + currentTransaction.getTotalAmount().toString() + currentTransaction.getCurrencySymbol());
        }
        return v;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}