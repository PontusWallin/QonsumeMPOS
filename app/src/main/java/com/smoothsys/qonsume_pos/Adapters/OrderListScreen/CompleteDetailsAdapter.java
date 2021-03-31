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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CompleteDetailsAdapter extends ArrayAdapter<Detail> implements IOrderChange {

    private static Order parentOrder;
    private static Context context;
    private static SharedPreferences prefs;
    private static IOrderChange orderChangeDelegate;
    private static IRefresh mRefresh;
    public CompleteDetailsAdapter(List<Detail> data, Order parentOrder, Context c, SharedPreferences prefs, IRefresh refresh) {
        super(c, R.layout.details_row_complete,data);
        this.parentOrder = parentOrder;
        this.context = c;
        this.prefs = prefs;
        mRefresh = refresh;
        orderChangeDelegate = this;
    }

    private ToggleButton completedBtn;
    private ToggleButton deliveredBtn;
    private ToggleButton pendingBtn;
    private ToggleButton cancelledBtn;

    public View getView(int position, View converterView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.details_row_complete, parent, false);

        Detail currentDetail = getItem(position);

        setupTextview(customView, currentDetail);
        setupButtons(customView, currentDetail);


        ImageView itemPicture = customView.findViewById(R.id.item_image_view);
        itemPicture.setVisibility(View.GONE);

        return customView;
    }

    private void setupButtons(View customView, Detail currentDetail) {
        grabButtonsFromView(customView);
        setCheckedButtons(currentDetail.getItem_state());
        setupEvents(currentDetail);
    }

    private void grabButtonsFromView(View customView) {
        pendingBtn = customView.findViewById(R.id.pendingBtn);
        completedBtn = customView.findViewById(R.id.completedBtn);
        deliveredBtn = customView.findViewById(R.id.deliveredBtn);
        cancelledBtn = customView.findViewById(R.id.cancelledBtn);
    }

    private void setupTextview(View customView, Detail currentDetail) {
        TextView itemName = customView.findViewById(R.id.itemNameTv);
        TextView itemQty = customView.findViewById(R.id.itemQtyTv);

        itemName.setText(currentDetail.getItem_name());
        itemQty.setText(String.valueOf(currentDetail.getQty()));

        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(2);
        df.toLocalizedPattern();

    }

    private void setupEvents(final Detail currentDetail) {

        completedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupSingleEvent(currentDetail, ItemStates.COMPLETED, v);
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

        cancelledBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupSingleEvent(currentDetail, ItemStates.CANCELLED, v);
            }
        });
    }

    @Override
    public void updateOrderStatus(String orderStatus) {

        // find order by order id - and then change status of it on local device
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

    private void setCheckedButtons(int itemState) {

        if(itemState == ItemStates.PENDING) {
            checkPending();
        } else if(itemState == ItemStates.COMPLETED) {
            checkCompleted();
        } else if(itemState == ItemStates.DELIVERED) {
            checkDelivered();
        } else if(itemState == ItemStates.CANCELLED) {
            checkCancelled();
        }
    }

    private void checkPending() {
        pendingBtn.setChecked(true);
        completedBtn.setChecked(false);
        deliveredBtn.setChecked(false);
        cancelledBtn.setChecked(false);
    }

    private void checkCompleted() {
        pendingBtn.setChecked(false);
        completedBtn.setChecked(true);
        deliveredBtn.setChecked(false);
        cancelledBtn.setChecked(false);
    }

    private void checkDelivered() {
        pendingBtn.setChecked(false);
        completedBtn.setChecked(false);
        deliveredBtn.setChecked(true);
        cancelledBtn.setChecked(false);
    }

    private void checkCancelled() {
        pendingBtn.setChecked(false);
        completedBtn.setChecked(false);
        deliveredBtn.setChecked(false);
        cancelledBtn.setChecked(true);
    }

    private void setupSingleEvent(Detail detail, int itemStatus, View v) {
        // DO nothing - if already set to same status.
        if(detail.getItem_state() == itemStatus) {
            notifyDataSetChanged();
            return;
        }
        int receiptNo = parentOrder.getReceipt_number();
        OrderStatusChanger.changeItemStatus(detail, detail.getId(), receiptNo , itemStatus, getPosition(detail),orderChangeDelegate, context, v);
    }
}