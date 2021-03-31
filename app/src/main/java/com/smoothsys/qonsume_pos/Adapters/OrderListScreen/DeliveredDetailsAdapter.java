package com.smoothsys.qonsume_pos.Adapters.OrderListScreen;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.smoothsys.qonsume_pos.DataDump.ItemStates;
import com.smoothsys.qonsume_pos.FragmentsAndActivities.MainActivity;
import com.smoothsys.qonsume_pos.Models.OrderClasses.Detail;
import com.smoothsys.qonsume_pos.Models.OrderClasses.Order;
import com.smoothsys.qonsume_pos.R;
import com.smoothsys.qonsume_pos.StateManagement.ScreenSwitcher;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pontu on 2018-03-20.
 */

public class DeliveredDetailsAdapter extends ArrayAdapter<Detail> implements IOrderChange {

    private static Order parentOrder;
    private static Context context;
    private static SharedPreferences prefs;
    private static IOrderChange orderChangeDelegate;
    private static ScreenSwitcher screenSwitcher;
    private static IRefresh mRefresh;
    public DeliveredDetailsAdapter(List<Detail> data, Order parentOrder, Context c, SharedPreferences prefs, ScreenSwitcher ss, IRefresh refresh) {

        super(c, R.layout.details_row_delivered,data);
        this.parentOrder = parentOrder;
        this.context = c;
        this.prefs = prefs;
        mRefresh = refresh;
        screenSwitcher = ss;
        orderChangeDelegate = this;
    }

    private ToggleButton completedBtn;
    private ToggleButton pendingBtn;
    private ToggleButton deliveredBtn;
    private ToggleButton canceledBtn;

    public View getView(int position, View converterView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.details_row_delivered, parent, false);

        Detail currentDetail = getItem(position);

        TextView itemName = customView.findViewById(R.id.itemNameTv);
        TextView itemQty = customView.findViewById(R.id.qtyTv);
        TextView itemPrice = customView.findViewById(R.id.price);

        itemName.setText(currentDetail.getItem_name());
        itemQty.setText(String.valueOf(currentDetail.getQty()));

        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(2);
        df.toLocalizedPattern();

        float unitPrice = currentDetail.getUnit_price();
        itemPrice.setText(df.format(unitPrice) + " " + parentOrder.getCurrencySymbol());

        // == Setup Buttons ==
        completedBtn = customView.findViewById(R.id.completedBtn);
        pendingBtn = customView.findViewById(R.id.pendingBtn);
        deliveredBtn = customView.findViewById(R.id.deliveredBtn);
        canceledBtn = customView.findViewById(R.id.canceledBtn);

        setCheckedButtons(currentDetail.getItem_state());

        // == Setup payment status icon ==
        ImageView paymentIv = customView.findViewById(R.id.paymentStatus);
        if(parentOrder.getPaymentDate() == null) {
            paymentIv.setAlpha(0.5f);
        } else {
            paymentIv.setAlpha(1f);
        }

        setupEvents(customView, currentDetail);
        return customView;
    }

    private void setupEvents(View view, final Detail currentDetail) {

        completedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupSingleEvent(currentDetail, ItemStates.COMPLETED, v);
            }
        });

        pendingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupSingleEvent(currentDetail, ItemStates.PENDING ,v);
            }
        });

        deliveredBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupSingleEvent(currentDetail, ItemStates.DELIVERED ,v);
            }
        });

        canceledBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupSingleEvent(currentDetail, ItemStates.CANCELLED ,v);
            }
        });

        final ImageView paymentIcon = view.findViewById(R.id.paymentStatus);
        paymentIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // go to pay options
                if(parentOrder.getPaymentDate() == null) {
                    //screenSwitcher.goToPayOptionsFragment(parentOrder, context);
                }
                else {
                    Toast.makeText(getContext(), "Order has already been paid for.", Toast.LENGTH_SHORT).show();
                }
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
        canceledBtn.setChecked(false);
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
            canceledBtn.setChecked(true);
        }
    }
}