package com.smoothsys.qonsume_pos.Adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smoothsys.qonsume_pos.FragmentsAndActivities.BasketFragment;
import com.smoothsys.qonsume_pos.Models.BasketData;
import com.smoothsys.qonsume_pos.R;
import com.smoothsys.qonsume_pos.Utilities.DBHandler;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Pontu on 2018-03-21.
 */

public class BasketRecyclerAdapter extends RecyclerView.Adapter<BasketRecyclerAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView itemName;
        TextView priceTv;

        public ViewHolder(View view) {

            super(view);
            itemName = view.findViewById(R.id.firstLineTv);
            priceTv = view.findViewById(R.id.priceTv);
        }
    }

    Activity activity;
    BasketFragment parentFrag;
    List<BasketData> mBasketData;
    DBHandler db;
    int loginUserId;
    Picasso picasso;

    public BasketRecyclerAdapter(Activity activity, BasketFragment pFrag, List<BasketData> basketData, int loginUserId, DBHandler dbHandler, Picasso picasso) {

        this.activity = activity;
        this.parentFrag = pFrag;
        this.mBasketData = basketData;
        this.db = dbHandler;
        this.loginUserId = loginUserId;
        this.picasso = picasso;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.basket_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        BasketData data = mBasketData.get(position);
        holder.itemName.setText(data.getName());
        holder.priceTv.setText(data.getUnitPrice());
    }

    @Override
    public int getItemCount() {

        if (mBasketData != null) {
            return mBasketData.size();
        } else {
            return 0;
        }
    }

    public void setData(List<BasketData> data) {
        mBasketData = data;
    }
}
