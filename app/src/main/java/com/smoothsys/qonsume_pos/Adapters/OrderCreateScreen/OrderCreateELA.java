package com.smoothsys.qonsume_pos.Adapters.OrderCreateScreen;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smoothsys.qonsume_pos.Adapters.BadgeUpdateInterface;
import com.smoothsys.qonsume_pos.Models.AttributeDetailPair;
import com.smoothsys.qonsume_pos.Models.BasketData;
import com.smoothsys.qonsume_pos.Models.ItemClasses.Attribute;
import com.smoothsys.qonsume_pos.Models.ItemClasses.Detail;
import com.smoothsys.qonsume_pos.Models.ItemClasses.Item;
import com.smoothsys.qonsume_pos.Models.RestaurantClasses.SubCategory;
import com.smoothsys.qonsume_pos.R;
import com.smoothsys.qonsume_pos.Utilities.ActivePairsHelper;
import com.smoothsys.qonsume_pos.Utilities.BitmapTransform;
import com.smoothsys.qonsume_pos.Utilities.Cache;
import com.smoothsys.qonsume_pos.Utilities.Config;
import com.smoothsys.qonsume_pos.Utilities.DBHandler;
import com.smoothsys.qonsume_pos.Utilities.RenderUtils;
import com.smoothsys.qonsume_pos.Utilities.Utils;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.smoothsys.qonsume_pos.R.mipmap.ic_pepsi;

public class OrderCreateELA extends BaseExpandableListAdapter {

    private Context context;
    private static BadgeUpdateInterface buttonListener;

    private static SharedPreferences pref;
    private Picasso p;

    private List<SubCategory> listDataHeader;
    private HashMap<SubCategory, List<Item>> listHashMap;

    static DBHandler db;

    public OrderCreateELA(Context context, List<SubCategory> listDataHeader, HashMap<SubCategory, List<Item>> listHashMap, Picasso picasso, BadgeUpdateInterface buttonListener) {

        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listHashMap = listHashMap;
        this.buttonListener = buttonListener;

        db = new DBHandler(this.context);

        pref = PreferenceManager.getDefaultSharedPreferences(context);
        p = picasso;
    }

    @Override
    public int getGroupCount() {
        return listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int i) {
        if (listHashMap.get(listDataHeader.get(i)) == null) {
            return 0;
        }
        return listHashMap.get(listDataHeader.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return listDataHeader.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return listHashMap.get(listDataHeader.get(i)).get(i1); // i = Group Item, i1 = ChildItem
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {

        SubCategory currentSubCategory = (SubCategory) getGroup(i);

        view = initializeIfNull(view);

        String headerTitle = currentSubCategory.getName();
        setupHeaderText(view, headerTitle);

        // finally - change header background
        ImageView bgImage = view.findViewById(R.id.header_bg);
        ImageView arrowImage = view.findViewById(R.id.down_arrow);
        TextView text = view.findViewById(R.id.lblListHeader);

        if (b) {
            bgImage.setBackgroundResource(R.drawable.item_list_header_active);
            arrowImage.setBackgroundResource(R.drawable.small_arrow_blue);
            text.setTextColor(Color.BLACK);
        } else {
            bgImage.setBackgroundResource(R.drawable.item_list_header);
            arrowImage.setBackgroundResource(R.drawable.small_arrow);
            text.setTextColor(Color.WHITE);
        }

        return view;
    }

    private View initializeIfNull(View view) {

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(R.layout.itemlist_group, null);
        }
        return view;
    }

    private void setupHeaderText(View view, String headerTitle) {

        TextView lblListHeader = view.findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
    }

    public static class ItemViewHolder {

        Context context;
        Item currentItem;

        RelativeLayout baseLayout;

        // == Item Layout ==
        ConstraintLayout itemLayout;
        // == TextViews ==
        TextView nameTv_firstLine;
        TextView priceTv;
        TextView currencyTv;
        TextView quantityTv;

        // == ImageViews ==
        ImageView decrementBtn;
        ImageView incrementBtn;
        ImageView arrow;
        ImageView itemImage;


        // == Attribute layout ==
        LinearLayout attributesLayout;

        private View itemView;

        private void setupTextviews(View v) {

            nameTv_firstLine = v.findViewById(R.id.firstLineTv);
            currencyTv = v.findViewById(R.id.currencyTv);
            priceTv = v.findViewById(R.id.priceTv);
            quantityTv = v.findViewById(R.id.quantityTv);
        }

        private void setupButtons(final View itemView) {
            decrementBtn = itemView.findViewById(R.id.minus_btn);
            incrementBtn = itemView.findViewById(R.id.plus_btn);

            decrementBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    decrementBtnClick(itemView);
                }
            });

            incrementBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    incrementBtnClick(itemView);
                }
            });
        }

        ItemViewHolder(View v, Item i, Context context) {

            currentItem = i;
            this.context = context;
            this.itemView = v;
            setupTextviews(v);
            baseLayout = v.findViewById(R.id.baseLayout);
            itemLayout = v.findViewById(R.id.itemLayout);
            attributesLayout = v.findViewById(R.id.attributesLayout);
            itemImage = v.findViewById(R.id.itemImage);
            arrow = v.findViewById(R.id.arrow);
            setupButtons(v);
        }

        private void incrementBtnClick(View itemView) {

            List<AttributeDetailPair> pairs = ActivePairsHelper.getActivePairs(itemView, currentItem);

            int quantity = db.getQTYByItemAttributeAndDetailIds(Integer.parseInt(currentItem.getId()), ActivePairsHelper.getAttributeIds(pairs), ActivePairsHelper.getDetailIds(pairs));
            quantity++;
            quantityTv.setText(Integer.toString(quantity));

            String attributeIds = ActivePairsHelper.getAttributeIds(pairs);
            String detailIds = ActivePairsHelper.getDetailIds(pairs);
            int item_qty;
            item_qty = db.getQTYByItemAttributeAndDetailIds(Integer.parseInt(currentItem.getId()), attributeIds, detailIds);

            item_qty++;
            doAddToCart(currentItem, pairs, item_qty);
        }

        private void decrementBtnClick(View itemView) {
            List<AttributeDetailPair> pairs = ActivePairsHelper.getActivePairs(itemView, currentItem);
            int quantity = db.getQTYByItemAttributeAndDetailIds(Integer.parseInt(currentItem.getId()), ActivePairsHelper.getAttributeIds(pairs), ActivePairsHelper.getDetailIds(pairs));

            if (quantity > 0) {
                reduceQuantity(quantity, itemView);
            }

            if (quantity == 1) {
                removeRecord(itemView);
            }
        }

        private void removeRecord(View itemView) {

            List<AttributeDetailPair> pairs = ActivePairsHelper.getActivePairs(itemView, currentItem);

            // get qty for sub item here
            String attributeids = ActivePairsHelper.getAttributeIds(pairs);
            String detailIds = ActivePairsHelper.getDetailIds(pairs);

            db.deleteBasketByItemAndAttributeIdsAndDetailIds(Integer.parseInt(currentItem.getId()), attributeids, detailIds);
            buttonListener.onItemCountChanged();
        }

        private void reduceQuantity(int quantity, View itemView) {

            quantity--;
            quantityTv.setText(Integer.toString(quantity));

            List<AttributeDetailPair> pairs = ActivePairsHelper.getActivePairs(itemView, currentItem);

            // get qty for sub item here
            String attributeids = ActivePairsHelper.getAttributeIds(pairs);
            String detailIds = ActivePairsHelper.getDetailIds(pairs);

            int item_qty;
            item_qty = db.getQTYByItemAttributeAndDetailIds(Integer.parseInt(currentItem.getId()), attributeids, detailIds);

            item_qty--;
            doAddToCart(currentItem, pairs, item_qty);
        }
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {

        final Item childItem = (Item) getChild(i, i1);
        final ItemViewHolder holder;

        // == Inflate view holder ==
        if (view == null) {
            // == Create new View holder ==
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.itemlist_row, null);
            holder = new ItemViewHolder(view, childItem, context);
            view.setTag(holder);
        } else {
            // == Recycle old View holder ==
            holder = (ItemViewHolder) view.getTag();
        }

        setDataForView(holder, childItem);
        fetchImage(holder);

        return view;
    }

    private void fetchImage(ItemViewHolder holder) {

        int MAX_WIDTH = 500;
        int MAX_HEIGHT = 500;
        try {
            p.load(Config.getAppImagesUrl() + holder.currentItem.getImages().get(0).getPath()).transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
                    .into(holder.itemImage);

        } catch (Exception e) {
            holder.itemImage.setImageResource(ic_pepsi);
            e.printStackTrace();
        }
    }

    private double getAdditionalPrice(List<AttributeDetailPair> pairs) {

        double additionalPrice = 0;
        for (AttributeDetailPair pair: pairs) {
            for (Detail detail: pair.detailList) {
                additionalPrice += Double.parseDouble(detail.getAdditionalPrice());
            }
        }
        return additionalPrice;
    }

    private void setupPriceOfItem(final Item childItem, final ItemViewHolder holder) {

        List<AttributeDetailPair> pairs = ActivePairsHelper.getDefaultPairs(childItem);

        // == Setup Price ==
        double unitPrice = Double.parseDouble(childItem.getUnitPrice());
        double additionalPrice = getAdditionalPrice(pairs);
        DecimalFormat df = Utils.getDecimalFormat();
        holder.priceTv.setText(df.format(unitPrice + additionalPrice));
        holder.currencyTv.setText(childItem.getCurrencySymbol());
    }

    private void setDataForView(final ItemViewHolder holder, final Item childItem) {

        holder.nameTv_firstLine.setText(childItem.getName());
        holder.currentItem = childItem;

        setupPriceOfItem(childItem,holder);
        setQtyOfItem(childItem, holder);
        setupAttributes(holder, childItem);

        String tag = (String) holder.arrow.getTag();
        if (tag.equals("folded")) {
            holder.attributesLayout.removeAllViews();

        } else {
            createAddonLayout(holder, childItem);
        }
    }

    private void setPriceOfItem(Item childItem, ItemViewHolder holder) {

        DecimalFormat df = Utils.getDecimalFormat();

        List<AttributeDetailPair> pairs = ActivePairsHelper.getDefaultPairs(childItem);

        String attributeIds = ActivePairsHelper.getAttributeIds(pairs);
        String detailIds = ActivePairsHelper.getDetailIds(pairs);

        if (getQtyOfItem(Integer.parseInt(childItem.getId())) > 0) {

            double unitPrice = Double.parseDouble(childItem.getUnitPrice());
            double additionalPrice = getAdditionalPrice(pairs);
            holder.priceTv.setText(df.format(unitPrice + additionalPrice));
        } else {

            double unitPrice = db.getPriceByItemAttributeAndDetailIds(Integer.parseInt(childItem.getId()), attributeIds, detailIds);
            holder.priceTv.setText(df.format(unitPrice));
        }
    }

    private void setQtyOfItem(Item childItem, ItemViewHolder holder) {
        if (getQtyOfItem(Integer.parseInt(childItem.getId())) > 0) {

            int itemId = Integer.parseInt(childItem.getId());

            List<AttributeDetailPair> pairs = ActivePairsHelper.getDefaultPairs(childItem);

            // get qty for sub item here
            String attributeids = ActivePairsHelper.getAttributeIds(pairs);
            String detailIds = ActivePairsHelper.getDetailIds(pairs);

            String quantity = Integer.toString(db.getQTYByItemAttributeAndDetailIds(itemId, attributeids, detailIds));
            holder.quantityTv.setText(quantity);
        } else {
            holder.quantityTv.setText("0");
        }
    }

    // == UI Creation Methods ==
    private void setupAttributes(final ItemViewHolder holder, final Item childItem) {

        List<Attribute> attributes = childItem.getAttributes();
        if (attributes.size() > 0) {
            holder.arrow.setVisibility(View.VISIBLE);

            //1. When arrow is clicked - Create Attribute layout
            holder.arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // == Change arrow rotation ==
                    ImageView arrow = holder.arrow;
                    arrow.setScaleY(arrow.getScaleY() * -1);

                    // == Change tag ==
                    String tag = (String) arrow.getTag();

                    if (tag.equals("folded")) {
                        arrow.setTag("expanded");
                        createAddonLayout(holder, childItem);
                    } else {
                        arrow.setTag("folded");
                        holder.attributesLayout.removeAllViews();

                        setQtyOfItem(childItem, holder);
                        setPriceOfItem(childItem, holder);
                    }
                }
            });

        } else {
            holder.arrow.setVisibility(View.GONE);
        }
    }

    private Item gItem;

    private void createAddonLayout(ItemViewHolder holder, Item childItem) {

        gItem = childItem;

        // == For each attribute ==
        holder.attributesLayout.removeAllViews();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, holder.itemLayout.getId());
        holder.attributesLayout.setLayoutParams(params);
        holder.attributesLayout.setTag(childItem);

        List<Attribute> attributes = childItem.getAttributes();
        for (int i = 0; i < attributes.size(); i++) {
            Attribute currentAttribute = attributes.get(i);
            createAddonRow(holder, currentAttribute);
        }
    }

    private void createAddonRow(ItemViewHolder holder, Attribute currentAttribute) {

        // == Create Addon Row Layout ==
        LinearLayout currentAddOnRow = new LinearLayout(context);
        currentAddOnRow.setTag(currentAttribute);
        currentAddOnRow.setOrientation(LinearLayout.HORIZONTAL);
        currentAddOnRow.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // == Create attributes text for view ==
        TextView currentTextView = new TextView(context);
        currentTextView.setText(currentAttribute.getName());

        currentTextView.setTextColor(0xFF000000);
        currentTextView.setTextSize(16);
        currentAddOnRow.addView(currentTextView);

        LinearLayout detailsLayout = createDetailsLayout(holder, currentAttribute);
        currentAddOnRow.addView(detailsLayout);

        RenderUtils.renderBorder(0xFF000000, currentAddOnRow);
        // == Add to base attributes layout ==
        holder.attributesLayout.addView(currentAddOnRow);
    }

    private LinearLayout createDetailsLayout(ItemViewHolder holder, Attribute currentAttribute) {

        LinearLayout detailsLayout = new LinearLayout(context);
        detailsLayout.setOrientation(LinearLayout.VERTICAL);
        detailsLayout.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        detailsLayout.setPadding(0, 0, 64, 0);

        // == Create sub layout for radio group ==
        LinearLayout subLayout = new LinearLayout(context);
        subLayout.setOrientation(LinearLayout.VERTICAL);
        subLayout.setLayoutParams(new ConstraintLayout.LayoutParams(250, ViewGroup.LayoutParams.WRAP_CONTENT));

        RadioGroup group = createDetailsRadioGroup(holder, currentAttribute);

        group.setGravity(Gravity.START);
        subLayout.setGravity(Gravity.START);
        subLayout.addView(group);

        detailsLayout.setGravity(Gravity.END);
        detailsLayout.addView(subLayout);

        return detailsLayout;
    }

    private RadioGroup createDetailsRadioGroup(final ItemViewHolder holder, Attribute currentAttribute) {

        RadioGroup group = new RadioGroup(context);
        group.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        group.setTag(currentAttribute);

        List<Detail> currentDetails = currentAttribute.getDetails();
        for (int j = 0; j < currentDetails.size(); j++) {
            Detail detail = currentDetails.get(j);
            RadioButton rb = new RadioButton(context);
            String additionPriceString = "";

            if (Double.parseDouble(detail.getAdditionalPrice()) > 0) {
                additionPriceString = " (+" + detail.getAdditionalPrice() + gItem.getCurrencySymbol() + ")";
            }

            rb.setText(detail.getName() + additionPriceString);
            rb.setTag(detail);
            group.addView(rb);

            // if first radio button - select it.
            if(j == 0) {
                rb.toggle();
            }
        }

        group.setGravity(Gravity.START);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                updateQtyAndPrice(holder);
            }
        });
        return group;
    }

    private List<AttributeDetailPair> getActivePairs(View baseview, Item item) {

        List<AttributeDetailPair> activeAttributeDetailPairs = new ArrayList<>();
        List<Attribute> allAttributes = item.getAttributes();

        for (int i = 0; i < allAttributes.size(); i++) {
            Attribute currentAttribute = allAttributes.get(i);

            List<Detail> activeDetails = ActivePairsHelper.getActiveDetails(baseview, currentAttribute);
            if (activeDetails.size() > 0) {
                activeAttributeDetailPairs.add(new AttributeDetailPair(currentAttribute, activeDetails));
            }
        }
        return activeAttributeDetailPairs;
    }

    private void updateQtyAndPrice(ItemViewHolder holder) {

        // get item
        View baseview = holder.attributesLayout;
        Item item = (Item) baseview.getTag();
        List<AttributeDetailPair> pairs = getActivePairs(baseview, item);

        // == Quantity ==
        int qty = db.getQTYByItemAttributeAndDetailIds(Integer.parseInt(item.getId()), ActivePairsHelper.getAttributeIds(pairs), ActivePairsHelper.getDetailIds(pairs));
        holder.quantityTv.setText(Integer.toString(qty));

        // == Price ==
        int currentItemID = Integer.parseInt(item.getId());
        String attributeIds = ActivePairsHelper.getAttributeIds(pairs);
        String detailIds = ActivePairsHelper.getDetailIds(pairs);

        double price = db.getPriceByItemAttributeAndDetailIds(currentItemID, attributeIds, detailIds);

        // -1 means the item has not been stored in the local database yet.
        if (price == -1) {
            detailIds = ActivePairsHelper.getDetailIds(pairs);
            double detailPrice = getAttributeAmount(detailIds);
            price = Double.parseDouble(item.getUnitPrice()) + detailPrice;
        }

        DecimalFormat df = Utils.getDecimalFormat();
        holder.priceTv.setText(df.format(price));
        return;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private static String attributeNames;
    private static String detailNames;
    private static String attributeIds;
    private static String detailIds;

    private static void doAddToCart(Item item, List<AttributeDetailPair> pairs, int quantity) {
        //If logged in:
        int itemId = Integer.parseInt(item.getId());
        int userId = Cache.mUserId;

        // == If we have a user log in ==
        if (isUserLoggedIn()) {

            String imagePath;
            if (getImagePath(item) == null) {
                imagePath = "";
            } else {
                imagePath = getImagePath(item);
            }

            attributeNames = ActivePairsHelper.getAttributeNames(pairs);
            detailNames = ActivePairsHelper.getDetailNames(pairs);
            attributeIds = ActivePairsHelper.getAttributeIds(pairs);
            detailIds = ActivePairsHelper.getDetailIds(pairs);

            // If details and attributes are empty - set up default attributes and details - if available
            setupDefaultDetail(item);

            int qty = getQtyOfItemByAttributeIdAndDetailId(itemId, attributeIds, detailIds);
            if (qty > 0) {
                updateBasketEntry(item, userId, imagePath, attributeNames, attributeIds, detailIds, detailNames, itemId, quantity);
            } else {
                addBasketEntry(item, userId, imagePath, attributeNames, attributeIds, detailIds, detailNames, itemId, quantity);
            }
        } else {
            //Else show an error message
        }

        return;
    }

    private static void setupDefaultDetail(Item item) {

        if(attributeNames.equals("")) {

            if(item.getAttributes().size() > 0) {

                Attribute firstAttribute = item.getAttributes().get(0);
                attributeNames = firstAttribute.getName();
                attributeIds = firstAttribute.getId();

                if(firstAttribute.getDetails().size() > 0) {

                    Detail firstDetail = firstAttribute.getDetails().get(0);
                    detailNames = firstDetail.getName();
                    detailIds = firstDetail.getId();
                }
            }
        }

    }

    private static void updateBasketEntry(Item item, int userId, String imagePath, String attributeNames, String attributeIds, String detailIds, String detailNames, int itemId, int quantity) {

        BasketData basketData;
        basketData = db.getBasketByIdAndAttribute(itemId, attributeIds, detailIds);

        double itemP = Double.parseDouble(item.getUnitPrice()) + getAttributeAmount(detailIds);
        String itemPrice = Double.toString(itemP);

        if (basketData.getQty() != quantity) {

            db.updateBasketByIdsAndAttributes(new BasketData(
                    itemId,
                    Integer.parseInt(item.getShopId()),
                    userId,
                    item.getName(),
                    item.getDescription(),
                    itemPrice,
                    item.parseVAT(),
                    item.getDiscountPercent(),
                    quantity,
                    imagePath,
                    item.getCurrencySymbol(),
                    item.getCurrencyShortForm(),
                    attributeNames,
                    attributeIds,
                    detailNames,
                    detailIds
            ), itemId, Integer.parseInt(item.getShopId()), attributeIds);
        }
    }

    private static double getAttributeAmount(String detailIds) {

        List<Detail> details = Cache.getAllActiveDetails(detailIds);

        double sumOfDetails = 0;
        for (int i = 0; i < details.size(); i++) {
            Detail cDetail = details.get(i);
            sumOfDetails += Double.parseDouble(cDetail.getAdditionalPrice());
        }
        return sumOfDetails;
    }

    private static void addBasketEntry(Item item, int userId, String imagePath, String attributeNames, String attributeIds, String detailIds, String detailNames, int itemId, int quantity) {

        double itemP = Double.parseDouble(item.getUnitPrice()) + getAttributeAmount(detailIds);
        String itemPrice = Double.toString(itemP);
        db.addBasket(new BasketData(
                itemId,
                Integer.parseInt(item.getShopId()),
                userId,
                item.getName(),
                item.getDescription(),
                itemPrice,
                item.parseVAT(),
                item.getDiscountPercent(),
                quantity,
                imagePath,
                item.getCurrencySymbol(),
                item.getCurrencyShortForm(),
                attributeNames,
                attributeIds,
                detailNames,
                detailIds

        ));
        buttonListener.onItemCountChanged();
    }

    private static String getImagePath(Item item) {
        if (item.getImages().size() > 0) {
            return item.getImages().get(0).getPath();
        }
        return "";
    }

    private static Boolean isUserLoggedIn() {
        return pref.getString("_user_name", "") != "";
    }

    public static int getQtyOfItem(int itemId) {
        if (pref.getString("_user_name", "") != "") {
            return db.getBasketCountByItemId(itemId);
        }
        return 0;
    }

    public static int getQtyOfItemByAttributeIdAndDetailId(int itemId, String attributeIds, String detailIds) {
        if (pref.getString("_user_name", "") != "") {
            return db.getBasketCountByItemIdAndAttriubteIdsAndDetailIds(itemId, attributeIds, detailIds);
        }
        return 0;
    }

    private void showCartEmpty() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(context, R.style.Theme_AppCompat_Dialog_Alert);
        builder.setTitle(R.string.sorry_title);
        builder.setMessage(R.string.cart_empty);
        builder.setPositiveButton(R.string.OK, null);
        builder.show();
    }
}
