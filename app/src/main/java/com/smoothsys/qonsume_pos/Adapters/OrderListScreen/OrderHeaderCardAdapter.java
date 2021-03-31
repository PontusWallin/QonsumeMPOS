package com.smoothsys.qonsume_pos.Adapters.OrderListScreen;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.smoothsys.qonsume_pos.Models.OrderClasses.Detail;
import com.smoothsys.qonsume_pos.Models.OrderClasses.Order;
import com.smoothsys.qonsume_pos.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Pontu on 2018-03-20.
 */

public class OrderHeaderCardAdapter extends RecyclerView.Adapter<OrderHeaderCardAdapter.ViewHolder>{

    public interface IPopulateLists {
        void selectByPosition(Order order, int position);
        void selectByOrder(Order order);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public Order order;
        TextView headerOrderNoTv;
        TextView timeAddedTv;
        ListView detailsList;
        ConstraintLayout baseLayout;
        ImageView bgImageView;
        Context mContext;
        public ViewHolder(View view, Context context) {

            super(view);
            headerOrderNoTv = view.findViewById(R.id.header_order_no);
            timeAddedTv = view.findViewById(R.id.timeAddedTv);
            detailsList = view.findViewById(R.id.header_detailsList);
            baseLayout = view.findViewById(R.id.clickArea);
            bgImageView = view.findViewById(R.id.bg_imageView);
            mContext = context;
        }
    }

    private Context context;
    private List<String> Headers;
    private HashMap<String, Order> Orders;
    private IPopulateLists sendShitToParent;
    public OrderHeaderCardAdapter(Context ctx, List<String> headers, HashMap<String, Order> ordersMap, IPopulateLists sendShitToParent) {
        Headers = headers;
        Orders = ordersMap;
        context = ctx;
        this.sendShitToParent = sendShitToParent;
    }

    public void setData(List<String> headers, HashMap<String, Order> ordersMap) {
        Headers = headers;
        Orders = ordersMap;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_card, parent, false);
        return  new ViewHolder(view, context);
    }

    public int selectedPosition = -1;

    private ImageView previouslySelectedIv;
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        String currentHeader = Headers.get(position);

        final Order currentOrder = Orders.get(currentHeader);
        holder.headerOrderNoTv.setText("Order: " + currentOrder.getReceipt_number() + ". Table: " + currentOrder.getTableNo() + ".");

        holder.timeAddedTv.setText(currentOrder.getAdded());

        holder.order = currentOrder;
        List<Detail> currentDetails = currentOrder.getDetails();

        ArrayList<String> processedStrings = new ArrayList<>();
        for(int i = 0; i< currentDetails.size(); i++) {
            Detail d = currentDetails.get(i);
            processedStrings.add(d.getItem_name() + " - " + d.getQty());
        }

        final HeaderCardDetailsListAdapter adapter = new HeaderCardDetailsListAdapter(context, currentDetails);
        holder.detailsList.setAdapter(adapter);

        holder.baseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(previouslySelectedIv != null) {
                    ImageView prev_background = previouslySelectedIv.findViewById(R.id.bg_imageView);
                    prev_background.setColorFilter(context.getResources().getColor(notSelectedColor));
                }

                selectedPosition = position;
                sendShitToParent.selectByPosition(holder.order, position);
                holder.bgImageView.setColorFilter(context.getResources().getColor(selectedColor));

                previouslySelectedIv = holder.bgImageView;
            }
        });
        changeColorIfSelected(holder, position);
    }

    private int selectedColor = R.color.colorPrimaryDark;
    private int notSelectedColor = R.color.colorPrimary;

    public void changeColorIfSelected(ViewHolder holder, int position) {

        if(selectedPosition == position) {
            // large shadow
            holder.bgImageView.setColorFilter(context.getResources().getColor(selectedColor));
            previouslySelectedIv = holder.bgImageView;
        } else {
            // no shadow
            holder.bgImageView.setColorFilter(context.getResources().getColor(notSelectedColor));
        }
    }

    public void editAllWithinRange(int first, int last) {

        // run editList.. on all items in range
        for(int i = first; i < last; i++ ) {

            editListForCard(i);

        }
    }

    private void editListForCard(int position) {
        // get object for position ?
        String header = Headers.get(position);
        Order order = Orders.get(header);

        // get view at position


        // get detail list for view
        // get last visible item
        // editsingleEntry ?
    }


    public void editListIfTooManyItems(ListView listView, HeaderCardDetailsListAdapter adapter) {

        int last = listView.getLastVisiblePosition();
        int count = listView.getAdapter().getCount();

        if(last >= count - 1) {
            // all items fit - so do nothing
            return;
        }

        if(last == -1 || last == 0) {
            return;
        }

        String andMore = context.getResources().getString(R.string.andmore);
        adapter.editSingleEntry(last, andMore);
        adapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return Headers.size();
    }
}