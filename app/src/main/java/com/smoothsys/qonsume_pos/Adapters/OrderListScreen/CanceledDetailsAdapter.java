package com.smoothsys.qonsume_pos.Adapters.OrderListScreen;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.smoothsys.qonsume_pos.DataDump.ItemStates;
import com.smoothsys.qonsume_pos.FragmentsAndActivities.MainActivity;
import com.smoothsys.qonsume_pos.Models.OrderClasses.Detail;
import com.smoothsys.qonsume_pos.Models.OrderClasses.Order;
import com.smoothsys.qonsume_pos.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pontu on 2018-03-20.
 */

public class CanceledDetailsAdapter extends ArrayAdapter<Detail> implements IOrderChange {

    private static IOrderChange orderChangeDelegate;
    private static Order parentOrder;
    private static Context context;
    private static SharedPreferences prefs;
    private static IRefresh mRefresh;
    public CanceledDetailsAdapter(List<Detail> data, Order parentOrder, Context c, SharedPreferences prefs, IRefresh refresh) {
        super(c, R.layout.details_row_pending,data);
        this.parentOrder = parentOrder;
        this.prefs = prefs;
        this.context = c;
        orderChangeDelegate = this;
        mRefresh = refresh;
    }

    private ToggleButton pendingBtn;
    private ToggleButton completedBtn;
    private ToggleButton deliveredBtn;
    private ToggleButton cancelledBtn;

    public View getView(int position, View converterView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.details_row_canceled, parent, false);

        Detail currentDetail = getItem(position);

        // == Setup texts ==
        TextView itemName = customView.findViewById(R.id.itemNameTv);
        TextView addOnName = customView.findViewById(R.id.addOnTv);
        TextView itemQty = customView.findViewById(R.id.qtyTv);

        itemName.setText(currentDetail.getItem_name());
        addOnName.setText(getAddonName(currentDetail.getItem_attribute()));
        itemQty.setText(String.valueOf(currentDetail.getQty()));

        // == Setup buttons ==
        pendingBtn = customView.findViewById(R.id.pendingBtn);
        completedBtn = customView.findViewById(R.id.completedBtn);
        deliveredBtn = customView.findViewById(R.id.deliveredBtn);
        cancelledBtn = customView.findViewById(R.id.cancelBtn);

        setCheckedButtons(currentDetail.getItem_state());

        setupEvents(currentDetail);

        return customView;
    }

    private String getAddonName(String item_attribute) {
        String extracted = "";

        String[] splitted = item_attribute.split(";");

        for(int i = 0; i < splitted.length; i++) {
            String detailRow = splitted[i];

            extracted += detailRow.replace(":", " - ");

            if(i != splitted.length-1) {
                extracted += "\n";
            }
        }

        return extracted;
    }

    private void setupEvents(final Detail currentDetail) {

        cancelledBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupSingleEvent(currentDetail, ItemStates.CANCELLED, v);
            }
        });

        pendingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupSingleEvent(currentDetail, ItemStates.PENDING, v);
            }
        });

        deliveredBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupSingleEvent(currentDetail, ItemStates.DELIVERED, v);
            }
        });

        completedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupSingleEvent(currentDetail, ItemStates.COMPLETED, v);
            }
        });
    }

    private void setupSingleEvent(Detail detail, int itemStatus, View v) {
        // DO nothing - if already set to same status.
        if(detail.getItem_state() == itemStatus) {
            notifyDataSetChanged();
            return;
        }
        int receiptNo = parentOrder.getReceipt_number();
        OrderStatusChanger.changeItemStatus(detail, detail.getId() , receiptNo, itemStatus, getPosition(detail),orderChangeDelegate, context, v);
    }

    @Override
    public void updateOrderStatus(String orderStatus) {

        parentOrder.setTransactionStatus(orderStatus);
        if(context instanceof MainActivity) {
            ((MainActivity)context).orderELAChanged();
        }
    }

    private Detail getCorrectDetail(int detailId) {

        List<Detail> details = parentOrder.getDetails();
        for (Detail detail: details) {
            if(detail.getId() == detailId) {
                return detail;
            }
        }

        return null;
    }

    private boolean allEqual(List<Integer> itemStates) {
        boolean allEqual = true;
        for (int state : itemStates) {
            if(state != itemStates.get(0)){
                allEqual = false;
            }
        }
        return allEqual;
    }

    @Override
    public void updateDetailStatus(int detailId, int itemStatus, int position) {

        Detail currentDetail = getCorrectDetail(detailId);
        setItemState(currentDetail, itemStatus);

        // 1. Check if all items are same status
        List<Integer> itemStates = new ArrayList<>();
        for (Detail detail: parentOrder.getDetails()) {
            itemStates.add(detail.getItem_state());
        }

        if(allEqual(itemStates)) {

            if(currentDetail.getItem_state() != OrderStatusChanger.convertOrderStatusStringToInt(parentOrder.getTransactionStatus())) {
                OrderStatusChanger.changeOrderStatus(parentOrder, currentDetail.getItem_state(), prefs, getContext(), orderChangeDelegate);
            }
        }

        mRefresh.reSelectOrder(parentOrder);
        notifyDataSetChanged();
    }

    private void setItemState(Detail currentDetail, int itemStatus) {

        // == Set item State ==
        if(currentDetail != null) {
            currentDetail.setItem_state(itemStatus);
        } else {
            Toast.makeText(context, "Current detail is null!", Toast.LENGTH_SHORT).show();
        }
    }

    private void uncheckAll() {

        pendingBtn.setChecked(false);
        completedBtn.setChecked(false);
        deliveredBtn.setChecked(false);
        cancelledBtn.setChecked(false);
    }

    private void setCheckedButtons(int itemState) {

        uncheckAll();
        if(itemState == ItemStates.PENDING) {
            pendingBtn.setChecked(true);
        } else if(itemState == ItemStates.COMPLETED) {
            completedBtn.setChecked(true);
        } else if(itemState == ItemStates.DELIVERED) {
            deliveredBtn.setChecked(true);
        } else if(itemState == ItemStates.CANCELLED) {
            cancelledBtn.setChecked(true);
        }
    }
}