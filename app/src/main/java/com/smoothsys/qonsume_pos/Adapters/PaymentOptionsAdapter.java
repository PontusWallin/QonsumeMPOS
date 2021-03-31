package com.smoothsys.qonsume_pos.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Guideline;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smoothsys.qonsume_pos.DataDump.PaymentOptions;
import com.smoothsys.qonsume_pos.Models.PaymentOption;
import com.smoothsys.qonsume_pos.R;

import java.util.List;

public class PaymentOptionsAdapter extends ArrayAdapter<PaymentOption> {

    List<PaymentOption> paymentOptions;
    Activity activity;
    public PaymentOptionsAdapter(@NonNull Context context, List<PaymentOption> paymentOptions, Activity activity) {

        super(context, R.layout.payment_option_list_item, paymentOptions);
        this.paymentOptions = paymentOptions;
        this.activity = activity;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.payment_option_list_item, parent, false);

        PaymentOption paymentOption = getItem(position);

        customView.setTag(paymentOption);

        TextView optionNameTv = customView.findViewById(R.id.paymentOptionNameTv);
        optionNameTv.setText(paymentOption.getOptionName());

        TextView descriptionTv = customView.findViewById(R.id.paymentOptionDescriptionTv);
        descriptionTv.setText(paymentOption.getDescription());

        ImageView paymentIconIv = customView.findViewById(R.id.paymentOptionIv);
        String iconName = paymentOption.getIconName();
        paymentIconIv.setImageDrawable(PaymentOptions.getPaymentIcon(iconName,activity));

        // If credit card icon - make imageView wider, but changing the right side guideline
        if(iconName.equals("credit_card_icon")) {
            Guideline gl = customView.findViewById(R.id.icon_width);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) gl.getLayoutParams();
            params.guidePercent = 0.3f;
            gl.setLayoutParams(params);
        }

        return customView;
    }
}
