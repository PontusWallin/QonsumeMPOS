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
import com.smoothsys.qonsume_pos.FragmentsAndActivities.OrdersFragment;
import com.smoothsys.qonsume_pos.Models.OrderClasses.Detail;
import com.smoothsys.qonsume_pos.Models.OrderClasses.Order;
import com.smoothsys.qonsume_pos.R;
import com.smoothsys.qonsume_pos.StateManagement.ScreenSwitcher;

import java.util.ArrayList;
import java.util.List;

public class PendingDetailsAdapter extends ArrayAdapter<Detail> implements IOrderChange {

    private static IOrderChange orderChangeDelegate;
    private static Order parentOrder;
    private static Context context;
    private static SharedPreferences prefs;
    private static ScreenSwitcher screenSwitcher;
    private static IRefresh mRefresh;
    public PendingDetailsAdapter(List<Detail> data, Order parentOrder, Context c, SharedPreferences prefs, ScreenSwitcher ss, IRefresh refresh) {
        super(c, R.layout.details_row_pending,data);
        this.parentOrder = parentOrder;
        this.prefs = prefs;
        this.context = c;
        orderChangeDelegate = this;
        screenSwitcher = ss;
        mRefresh  = refresh;
    }


    private ToggleButton pendingBtn;
    private ToggleButton completedBtn;
    private ToggleButton deliveredBtn;
    private ToggleButton cancelledBtn;

    public View getView(int position, View converterView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.details_row_pending, parent, false);

        Detail currentDetail = getItem(position);
        setupTexts(customView, currentDetail);
        setupButtons(customView, currentDetail);

        return customView;
    }

    private void setupTexts(View customView, Detail currentDetail) {

        // == Item Name ==
        TextView itemName = customView.findViewById(R.id.itemNameTv);
        itemName.setText(currentDetail.getItem_name());

        // == Add-On Name ==
        TextView addOnName = customView.findViewById(R.id.addOnTv);
        addOnName.setText( getAddonName(currentDetail.getItem_attribute()) );

        TextView itemQty = customView.findViewById(R.id.qtyTv);
        itemQty.setText(String.valueOf(currentDetail.getQty()));
    }

    private void setupButtons(View customView, Detail currentDetail) {

        grabButtonsFromView(customView);
        setCheckedButtons(currentDetail.getItem_state());
        setupPaymentStatusIcon(customView);
        setupButtonEvents(customView, currentDetail);
    }

    private void grabButtonsFromView(View customView) {
        pendingBtn = customView.findViewById(R.id.pendingBtn);
        completedBtn = customView.findViewById(R.id.completedBtn);
        deliveredBtn = customView.findViewById(R.id.deliveredBtn);
        cancelledBtn = customView.findViewById(R.id.cancelledBtn);
    }

    private void setupPaymentStatusIcon(View customView) {
        ImageView paymentIv = customView.findViewById(R.id.paymentStatus);
        if(parentOrder.getPaymentDate() == null) {
            paymentIv.setAlpha(0.5f);
        } else {
            paymentIv.setAlpha(1f);
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

    private List<Detail> deleteDetail(int detailId) {
        List<Detail> details = parentOrder.getDetails();
        List<Detail> newList = new ArrayList<>();

        for (Detail detail: details) {
            if(detail.getId() != detailId) {
                newList.add(detail);
            }
        }
        return newList;
    }

    @Override
    public void updateDetailStatus(int detailId, int itemStatus, int positionInList) {

        Detail currentDetail = getCorrectDetail(detailId);

        setItemState(currentDetail, itemStatus);


        // 1. Check if all items are same status

        List<Integer> itemStates = new ArrayList<>();
        for (Detail detail: parentOrder.getDetails()) {
            itemStates.add(detail.getItem_state());
        }

        if(allEqual(itemStates)) {

            int item_state = currentDetail.getItem_state();
            int order_state = OrderStatusChanger.convertOrderStatusStringToInt(parentOrder.getTransactionStatus());

            if(item_state != order_state) {
                OrderStatusChanger.changeOrderStatus(parentOrder, item_state, prefs, getContext(), orderChangeDelegate);
            }
        }

        mRefresh.reSelectOrder(parentOrder);
        notifyDataSetChanged();
    }

    private List<Detail> removeDetail(int detailId) {

        List<Detail> newList = new ArrayList<>();

        List<Detail> details = parentOrder.getDetails();
        for (Detail detail: details) {
            if(detail.getId() != detailId) {
                newList.add(detail);
            }
        }
        return newList;
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

    @Override
    public void updateOrderStatus(String orderStatus) {

        parentOrder.setTransactionStatus(orderStatus);

        if(context instanceof MainActivity) {
            ((MainActivity)context).orderELAChanged();
        }
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

    private void setupSingleClickEvent(Detail detail, int itemStatus, View v) {

        // DO nothing - if already set to same status.
        if(detail.getItem_state() == itemStatus) {
            notifyDataSetChanged();
            return;
        }
        int receiptNo = parentOrder.getReceipt_number();
        OrderStatusChanger.changeItemStatus(detail, detail.getId() , receiptNo ,itemStatus, getPosition(detail),orderChangeDelegate, context, v);
    }

    private void setupButtonEvents(final View view, final Detail currentDetail) {

        cancelledBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setupSingleClickEvent(currentDetail, ItemStates.CANCELLED, view);
            }
        });

        pendingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupSingleClickEvent(currentDetail, ItemStates.PENDING, view);
            }
        });

        deliveredBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupSingleClickEvent(currentDetail, ItemStates.DELIVERED, view);
            }
        });

        completedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setupSingleClickEvent(currentDetail, ItemStates.COMPLETED, view);
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
                    Toast.makeText(getContext(), "Order has already been paid for.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}