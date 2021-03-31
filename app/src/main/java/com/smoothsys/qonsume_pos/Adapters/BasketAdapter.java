package com.smoothsys.qonsume_pos.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smoothsys.qonsume_pos.FragmentsAndActivities.BasketFragment;
import com.smoothsys.qonsume_pos.Models.BasketData;
import com.smoothsys.qonsume_pos.R;
import com.smoothsys.qonsume_pos.Utilities.DBHandler;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class BasketAdapter extends BaseAdapter implements ListAdapter {

    private Activity activity;
    private BasketFragment parentFrag;
    private LayoutInflater inflater;
    private List<BasketData> mBasketData;

    private ItemViewHolder holder = null;

    private int itemQty = 0;
    private int loginUserId = 0;
    private DBHandler db;
    private Double totalAmount = 0.0;
    public static float totalVAT = 0;
    private Picasso picasso;
    private Context mContext;

    public BasketAdapter(Activity activity, BasketFragment pFrag, List<BasketData> basketData, int loginUserId, DBHandler dbHandler, Picasso picasso, Context context) {

        this.activity = activity;
        this.parentFrag = pFrag;
        this.mBasketData = basketData;
        this.db = dbHandler;
        this.loginUserId = loginUserId;
        this.picasso = picasso;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        if (mBasketData != null) {
            return mBasketData.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return mBasketData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        initializeInflater();
        convertView = inflateView(convertView);

        createViewholder(convertView, position);
        updateTotalAmount();
        return convertView;
    }

    private void initializeInflater() {

        if (inflater == null) {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
    }

    private View inflateView(View convertView) {

        if (convertView == null) {
            return inflater.inflate(R.layout.basket_item, null);
        }
        return convertView;
    }

    //region Can this be separated into a new class?
    private void createViewholder(View convertView, final int position) {

        holder = new ItemViewHolder();
        setupLayout(holder, convertView, position);
        bindView(holder, position);
    }

    private void setupLayout(final ItemViewHolder holder, View convertView, final int position) {

        // ==  Setup header part ==
        setupTextViews(holder, convertView);
        setupQTYButtons(holder, convertView, position);

        // == Set up addon part ==
        holder.addonLayout = convertView.findViewById(R.id.addon_section);
        holder.arrowImage = convertView.findViewById(R.id.arrow);

        // if no attributes - remove addon layout - and arrow
        if(mBasketData.size() < 1) {
            return;
        }

        BasketData basketData = mBasketData.get(position);
        if(basketData.getSelectedAttributeIds().equals("")) {
            holder.addonLayout.setVisibility(View.GONE);
            holder.arrowImage.setVisibility(View.GONE);
        } else {
            holder.addonLayout.setVisibility(View.VISIBLE);
            holder.arrowImage.setVisibility(View.VISIBLE);
        }

        holder.arrowImage.setTag("expanded");
        holder.arrowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // flip arrow
                ImageView arrow = holder.arrowImage;
                arrow.setScaleY(arrow.getScaleY() * -1);

                // if folded
                String tag = (String) arrow.getTag();
                if(tag.equals("folded")) {

                    // expand layout
                    holder.addonLayout.setVisibility(View.VISIBLE);
                    arrow.setTag("expanded");

                } else {

                    // remove layout
                    holder.addonLayout.setVisibility(View.GONE);
                    arrow.setTag("folded");
                }
            }
        });
    }

    private Boolean hasAttribute(int position) {

        if(mBasketData.get(position).getSelectedAttributeIds() == null || mBasketData.get(position).getSelectedAttributeIds().equals("")) {
            return false;
        } else {
            return !mBasketData.get(position).getSelectedAttributeIds().equals("No id");
        }
    }

    private void setupTextViews(final ItemViewHolder holder, View convertView) {

        holder.txtItemTitle = convertView.findViewById(R.id.firstLineTv);
        holder.txtItemPrice = convertView.findViewById(R.id.priceTv);
        holder.txtItemQty = convertView.findViewById(R.id.quantityTv);
    }

    private void setupQTYButtons(final ItemViewHolder holder, View convertView, final int position) {

        holder.btnIncrease = convertView.findViewById(R.id.plus_btn);
        holder.btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addToBasket(holder,position);
            }
        });

        holder.btnDecrease = convertView.findViewById(R.id.minus_btn);
        holder.btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                removeFromBasket(holder, position);
            }
        });
    }

    private void bindView(final ItemViewHolder holder, final int position) {

        // grab everything from db directly
        mBasketData = db.getAllBasketData();

        if(mBasketData.size() < 1) {
            return;
        }

        BasketData basketData = mBasketData.get(position);
        populateViewWithBasketData(holder, basketData, position);
    }

    private void populateViewWithBasketData(final ItemViewHolder holder, BasketData basket, final int position ) {

        holder.txtItemTitle.setText(basket.getName());


        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(2);
        df.toLocalizedPattern();

        float unitPrice = Float.parseFloat(basket.getUnitPrice());

        holder.txtItemPrice.setText(df.format(unitPrice) + basket.getCurrencySymbol());

        BasketData basketData = mBasketData.get(position);
        itemQty = db.getQTYByItemAttributeAndDetailIds(mBasketData.get(position).getItemId(), basketData.getSelectedAttributeIds(), basketData.getSelectedDetailIds());
        holder.txtItemQty.setText(Integer.toString(itemQty));

        if(hasAttribute(position)) {

            // remove old views first
            holder.addonLayout.removeAllViews();
            List<String> attributeNames = Arrays.asList(basket.getSelectedAttributeNames().split(";"));

            if(attributeNames.size() < 1) {
                holder.arrowImage.setVisibility(View.GONE);
                holder.addonLayout.setVisibility(View.GONE);
            }

            for(int i = 0; i < attributeNames.size(); i++) {

                // remove ; from string
                String currentAttributeName = attributeNames.get(i);
                currentAttributeName = currentAttributeName.replace(";","");

                // == Base Layout ==
                RelativeLayout baseLayout = new RelativeLayout(mContext);
                RelativeLayout.LayoutParams paramsForLine = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                baseLayout.setLayoutParams(paramsForLine);

                TextView tvAttribute = createTextView(currentAttributeName, RelativeLayout.ALIGN_PARENT_LEFT);

                String detailText = "No detail name!";
                if(basket.getSelectedDetailNames() != null) {
                    String allDets = basket.getSelectedDetailNames();
                    String[] detArray  = allDets.split(";");

                    detailText = detArray[i];
                }

                TextView tvDetail = createTextView(detailText, RelativeLayout.ALIGN_PARENT_RIGHT);
                baseLayout.addView(tvAttribute);
                baseLayout.addView(tvDetail);

                holder.addonLayout.addView(baseLayout);
            }
        }
    }

    private TextView createTextView(String text, int alignmentRule) {

        TextView textView = new TextView(mContext);
        RelativeLayout.LayoutParams paramsForAttributeTv = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsForAttributeTv.addRule(alignmentRule);
        textView.setLayoutParams(paramsForAttributeTv);

        int horizontalPadding = 24;
        textView.setPadding(horizontalPadding,0,horizontalPadding,0);
        textView.setText(text);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(16);

        return textView;
    }

    void addToBasket(ItemViewHolder holder, int position) {

        BasketData currentBasketData = mBasketData.get(position);
        itemQty = db.getQTYByItemAttributeAndDetailIds(currentBasketData.getItemId(), currentBasketData.getSelectedAttributeIds(), currentBasketData.getSelectedDetailIds()) + 1;

        holder.txtItemQty.setText(Integer.toString(itemQty));

        db.updateBasketByIds(new BasketData(
                currentBasketData.getItemId(),
                currentBasketData.getShopId(),
                loginUserId,
                currentBasketData.getName(),
                currentBasketData.getDesc(),
                String.valueOf(currentBasketData.getUnitPrice()),
                currentBasketData.getVATRate(),
                currentBasketData.getDiscountPercent(),
                itemQty,
                currentBasketData.getImagePath(),
                currentBasketData.getCurrencySymbol(),
                currentBasketData.getCurrencyShortForm(),
                currentBasketData.getSelectedAttributeNames(),
                currentBasketData.getSelectedAttributeIds(),
                currentBasketData.getSelectedDetailNames(),
                currentBasketData.getSelectedDetailIds()
        ), currentBasketData.getItemId(), currentBasketData.getShopId());

        currentBasketData.setQty(itemQty);
        updateTotalAmount();
    }
    //endregion

    private Boolean moreThanOneItemInBasket(int itemId) {
        BasketData basketData = mBasketData.get(itemId);
        return db.getQTYByItemAttributeAndDetailIds(basketData.getItemId(), basketData.getSelectedAttributeIds(), basketData.getSelectedDetailIds()) > 1;
    }

    private void removeFromBasket(ItemViewHolder holder, int position) {

        BasketData currentBasketData = mBasketData.get(position);
        if (moreThanOneItemInBasket(position)) {
            decrementItemCountFromBasketEntry(holder, currentBasketData);
        } else {
            removeBasketEntry(currentBasketData, position);
        }
    }

    private void decrementItemCountFromBasketEntry(ItemViewHolder holder, BasketData basketData) {

        // update item QTY
        itemQty = db.getQTYByItemAttributeAndDetailIds(basketData.getItemId(),basketData.getSelectedAttributeIds(), basketData.getSelectedDetailIds()) - 1;
        holder.txtItemQty.setText(Integer.toString(itemQty));

        // set basket DB with this new QTY
        db.updateBasketByIdsAndAttributes(new BasketData(
                basketData.getItemId(),
                basketData.getShopId(),
                loginUserId,
                basketData.getName(),
                basketData.getDesc(),
                String.valueOf(basketData.getUnitPrice()),
                basketData.getVATRate(),
                basketData.getDiscountPercent(),
                itemQty,
                basketData.getImagePath(),
                basketData.getCurrencySymbol(),
                basketData.getCurrencyShortForm(),
                basketData.getSelectedAttributeNames(),
                basketData.getSelectedAttributeIds(),
                basketData.getSelectedDetailNames(),
                basketData.getSelectedDetailIds()
        ), basketData.getItemId(), basketData.getShopId(), basketData.getSelectedAttributeIds());

        // If there are still items in the Basket - we update total amount
        if (mBasketData.size() > 0) {
            basketData.setQty(itemQty);
            updateTotalAmount();
        }
    }

    private void removeBasketEntry(BasketData basketData, int position) {

        String attrIds =  basketData.getSelectedAttributeIds();
        String detailIds = basketData.getSelectedDetailIds();

        db.deleteBasketByItemAndAttributeIdsAndDetailIds(basketData.getItemId(),attrIds, detailIds);

        mBasketData.remove(position);
        notifyDataSetChanged();
        updateTotalAmount();

        return;
    }

    void updateTotalAmount() {

        totalAmount = 0d;
        totalVAT = 0;

        for (int i = 0; i < mBasketData.size(); i++) {

            BasketData currentItem = mBasketData.get(i);
            double qty = currentItem.getQty();
            double price = Double.parseDouble(currentItem.getUnitPrice());

            double vat_rate = currentItem.getVATRate();
            totalVAT += price * vat_rate * qty;

            totalAmount += qty * price;
        }
        parentFrag.updateTotal(totalAmount, totalVAT);
    }

    private static class ItemViewHolder {

        // == Header part ==
        private TextView txtItemTitle;
        private TextView txtItemPrice;
        private TextView txtItemQty;
        private ImageView btnIncrease;
        private ImageView btnDecrease;

        // == Add-on part ==
        private ImageView arrowImage;
        private LinearLayout addonLayout;
    }
}