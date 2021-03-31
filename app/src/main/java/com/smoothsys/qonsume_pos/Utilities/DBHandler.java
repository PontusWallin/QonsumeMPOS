package com.smoothsys.qonsume_pos.Utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.smoothsys.qonsume_pos.Models.BasketData;

import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "moketsdb";
    private static final String TABLE_ORDERID = "OrderID";
    // Contacts table name
    private static final String TABLE_BASKET = "BasketData";

    // Basket Table Columns names
    private static final String KEY_ID            = "id";
    private static final String KEY_ITEM_ID       = "item_id";
    private static final String KEY_SHOP_ID       = "shop_id";
    private static final String KEY_USER_ID       = "user_id";
    private static final String KEY_NAME          = "name";
    private static final String KEY_DESC          = "desc";
    private static final String KEY_UNIT_PRICE    = "unit_price";
    private static final String KEY_VAT           = "vat";

    private static final String KEY_DISCOUNT_PERCENT    = "discount_percent";
    private static final String KEY_QTY           = "qty";
    private static final String KEY_IMAGE_PATH    = "image_path";
    private static final String KEY_CURRENCY_SYMBOL    = "currency_symbol";
    private static final String KEY_CURRENCY_SHORT_FORM   = "currency_short_form";
    private static final String KEY_SELECTED_ATTRIBUTE_NAMES   = "selected_attribute_names";
    private static final String KEY_SELECTED_ATTRIBUTE_IDS   = "selected_attribute_ids";
    private static final String KEY_SELECTED_DETAIL_IDS   = "selected_detail_ids";
    private static final String KEY_SELECTED_DETAIL_NAMES   = "selected_detail_names";

    //Order ID Table Column names
    private static final String ORDER_ID = "order_id";
    private static final String CUSTOMER_EMAIL = "customer_email";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_BASKET_TABLE = "CREATE TABLE " + TABLE_BASKET + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_ITEM_ID + " INTEGER,"
                + KEY_SHOP_ID + " INTEGER,"
                + KEY_USER_ID + " INTEGER,"
                + KEY_NAME + " TEXT,"
                + KEY_DESC + " TEXT,"
                + KEY_UNIT_PRICE + " TEXT,"
                + KEY_VAT + " TEXT,"
                + KEY_DISCOUNT_PERCENT + " TEXT,"
                + KEY_QTY + " INTEGER,"
                + KEY_IMAGE_PATH + " TEXT,"
                + KEY_CURRENCY_SYMBOL + " TEXT,"
                + KEY_CURRENCY_SHORT_FORM + " TEXT,"
                + KEY_SELECTED_ATTRIBUTE_NAMES + " TEXT,"
                + KEY_SELECTED_ATTRIBUTE_IDS + " TEXT,"
                + KEY_SELECTED_DETAIL_IDS + " TEXT,"
                + KEY_SELECTED_DETAIL_NAMES + " TEXT" + ")";

        db.execSQL(CREATE_BASKET_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BASKET);
        // Creating tables again
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    // Adding new basket item
    public void addBasket(BasketData basketData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ITEM_ID, basketData.getItemId());
        values.put(KEY_SHOP_ID, basketData.getShopId());
        values.put(KEY_USER_ID, basketData.getUserId());
        values.put(KEY_NAME, basketData.getName());
        values.put(KEY_DESC, basketData.getDesc());
        values.put(KEY_UNIT_PRICE, basketData.getUnitPrice());
        values.put(KEY_VAT, basketData.getVATRate());
        values.put(KEY_DISCOUNT_PERCENT, basketData.getDiscountPercent());
        values.put(KEY_QTY, basketData.getQty());
        values.put(KEY_IMAGE_PATH, basketData.getImagePath());
        values.put(KEY_CURRENCY_SYMBOL, basketData.getCurrencySymbol());
        values.put(KEY_CURRENCY_SHORT_FORM, basketData.getCurrencyShortForm());
        values.put(KEY_SELECTED_ATTRIBUTE_NAMES, basketData.getSelectedAttributeNames());
        values.put(KEY_SELECTED_ATTRIBUTE_IDS, basketData.getSelectedAttributeIds());
        values.put(KEY_SELECTED_DETAIL_IDS, basketData.getSelectedDetailIds());
        values.put(KEY_SELECTED_DETAIL_NAMES, basketData.getSelectedDetailNames());

        // Inserting Row
        db.insert(TABLE_BASKET, null, values);
        db.close(); // Closing database connection
    }

    // Getting one basket
    public BasketData getBasketById(int itemId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_BASKET, new String[]{KEY_ID,
                        KEY_ITEM_ID, KEY_SHOP_ID, KEY_USER_ID, KEY_NAME, KEY_DESC, KEY_UNIT_PRICE, KEY_VAT,
                        KEY_DISCOUNT_PERCENT, KEY_QTY, KEY_IMAGE_PATH, KEY_CURRENCY_SYMBOL, KEY_CURRENCY_SHORT_FORM,
                        KEY_SELECTED_ATTRIBUTE_NAMES, KEY_SELECTED_ATTRIBUTE_IDS, KEY_SELECTED_DETAIL_IDS, KEY_SELECTED_ATTRIBUTE_NAMES}, KEY_ITEM_ID + "=?",
                new String[]{String.valueOf(itemId)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        BasketData basket = new BasketData(
                Integer.parseInt(cursor.getString(0)),
                Integer.parseInt(cursor.getString(1)),
                Integer.parseInt(cursor.getString(2)),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                Float.parseFloat(cursor.getString(6)),
                cursor.getString(7),
                Integer.parseInt(cursor.getString(8)),
                cursor.getString(9),
                cursor.getString(10),
                cursor.getString(11),
                cursor.getString(12),
                cursor.getString(13),
                cursor.getString(14),
                cursor.getString(15));
        return basket;
    }

    public BasketData getBasketByIdAndAttribute(int itemId, String attributeIds, String detailIds) {

        // if no attribute - ignore Attribute Id parameter
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_BASKET, new String[]{KEY_ID,
                            KEY_ITEM_ID, KEY_SHOP_ID, KEY_USER_ID, KEY_NAME, KEY_DESC, KEY_UNIT_PRICE, KEY_VAT,
                            KEY_DISCOUNT_PERCENT, KEY_QTY, KEY_IMAGE_PATH, KEY_CURRENCY_SYMBOL, KEY_CURRENCY_SHORT_FORM,
                            KEY_SELECTED_ATTRIBUTE_NAMES, KEY_SELECTED_ATTRIBUTE_IDS, KEY_SELECTED_DETAIL_IDS, KEY_SELECTED_DETAIL_NAMES},
                        KEY_ITEM_ID + "=? and " + KEY_SELECTED_ATTRIBUTE_IDS + "=? AND " + KEY_SELECTED_DETAIL_IDS + "=?",
                    new String[]{String.valueOf(itemId), attributeIds, detailIds}, null, null, null, null);

        if(cursor.getCount() < 1) {
            return null;
        }

        if (cursor != null)
            cursor.moveToFirst();

        BasketData basket = new BasketData(
                Integer.parseInt(cursor.getString(0)),
                Integer.parseInt(cursor.getString(1)),
                Integer.parseInt(cursor.getString(2)),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                Float.parseFloat(cursor.getString(6)),
                cursor.getString(7),
                getDiscountPercent(cursor),
                cursor.getString(9),
                cursor.getString(10),
                cursor.getString(11),
                cursor.getString(12),
                cursor.getString(13),
                cursor.getString(14),
                cursor.getString(15));
        return basket;
    }

    private int getDiscountPercent(Cursor cursor) {

        String disc = cursor.getString(8);

        try {
            int dsc  = Integer.parseInt(disc);
            return dsc;
        } catch (NumberFormatException e) {
            
            return 0;
        }
    }

    // Getting All Basket
    public List<BasketData> getAllBasketData() {
        List<BasketData> basketList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_BASKET;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                BasketData basketData = new BasketData();
                basketData.setId(Integer.parseInt(cursor.getString(0)));
                basketData.setItemId(Integer.parseInt(cursor.getString(1)));
                basketData.setShopId(Integer.parseInt(cursor.getString(2)));
                basketData.setUserId(Integer.parseInt(cursor.getString(3)));
                basketData.setName(cursor.getString(4));
                basketData.setDesc(cursor.getString(5));
                basketData.setUnitPrice(cursor.getString(6));
                basketData.setVAT(Float.parseFloat(cursor.getString(7)));
                basketData.setDiscountPercent(cursor.getString(8));
                basketData.setQty(Integer.parseInt(cursor.getString(9)));
                basketData.setImagePath(cursor.getString(10));
                basketData.setCurrencySymbol(cursor.getString(11));
                basketData.setCurrencyShortForm(cursor.getString(12));
                basketData.setSelectedAttributeNames(cursor.getString(13));
                basketData.setSelectedAttributeIds(cursor.getString(14));
                basketData.setSelectedDetailIds(cursor.getString(15));
                basketData.setSelectedDetailNames(cursor.getString(16));

                // Adding basket to list
                basketList.add(basketData);
            } while (cursor.moveToNext());
        }

        // return basket list
        return basketList;
    }

    // Getting All Basket
    public List<BasketData> getAllBasketDataByShopId(int shopId) {
        List<BasketData> basketList = new ArrayList<BasketData>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_BASKET + " Where " + KEY_SHOP_ID + " = " + shopId;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                BasketData basketData = new BasketData();
                basketData.setId(Integer.parseInt(cursor.getString(0)));
                basketData.setItemId(Integer.parseInt(cursor.getString(1)));
                basketData.setShopId(Integer.parseInt(cursor.getString(2)));
                basketData.setUserId(Integer.parseInt(cursor.getString(3)));
                basketData.setName(cursor.getString(4));
                basketData.setDesc(cursor.getString(5));
                basketData.setUnitPrice(cursor.getString(6));
                basketData.setVAT(Float.parseFloat(cursor.getString(7)));
                basketData.setDiscountPercent(cursor.getString(8));
                basketData.setQty(Integer.parseInt(cursor.getString(9)));
                basketData.setImagePath(cursor.getString(10));
                basketData.setCurrencySymbol(cursor.getString(11));
                basketData.setCurrencyShortForm(cursor.getString(12));
                basketData.setSelectedAttributeNames(cursor.getString(13));
                basketData.setSelectedAttributeIds(cursor.getString(14));
                basketData.setSelectedDetailIds(cursor.getString(15));
                basketData.setSelectedDetailNames(cursor.getString(16));

                basketList.add(basketData);
            } while (cursor.moveToNext());
        }

        // return basket list
        return basketList;
    }

    //Getting QTY By Item ID and Shop ID
    public int getQTYByIds(int itemId, int shopId) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_BASKET, new String[]{KEY_ID,
                        KEY_ITEM_ID, KEY_SHOP_ID, KEY_USER_ID, KEY_NAME, KEY_DESC, KEY_UNIT_PRICE,
                        KEY_DISCOUNT_PERCENT, KEY_QTY, KEY_IMAGE_PATH, KEY_CURRENCY_SYMBOL, KEY_CURRENCY_SHORT_FORM,
                        KEY_SELECTED_ATTRIBUTE_NAMES, KEY_SELECTED_ATTRIBUTE_IDS}, KEY_ITEM_ID + "=? AND " + KEY_SHOP_ID + " = ?",
                new String[]{String.valueOf(itemId), String.valueOf(shopId)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        if(cursor.getCount() == 0) {
            return 0;
        } else {
            return Integer.parseInt(cursor.getString(8));
        }
    }

    public void resetDataBase() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BASKET);
        onCreate(db);
    }

    public void createDataBase() {
        SQLiteDatabase db = this.getReadableDatabase();
        onCreate(db);
    }

    public int getQTYByItemAttributeAndDetailIds(int itemId, String attributeIds, String detailIds) {

        SQLiteDatabase db = this.getReadableDatabase();


        if(detailIds == null) {
            detailIds = "";
        }
        Cursor cursor = db.query(TABLE_BASKET, new String[]{KEY_ID,
                        KEY_ITEM_ID, KEY_SHOP_ID, KEY_USER_ID, KEY_NAME, KEY_DESC, KEY_UNIT_PRICE, KEY_VAT,
                        KEY_DISCOUNT_PERCENT, KEY_QTY, KEY_IMAGE_PATH, KEY_CURRENCY_SYMBOL, KEY_CURRENCY_SHORT_FORM,
                        KEY_SELECTED_ATTRIBUTE_NAMES, KEY_SELECTED_ATTRIBUTE_IDS, KEY_SELECTED_DETAIL_IDS, KEY_SELECTED_DETAIL_NAMES},
                    KEY_ITEM_ID + "=? AND " + KEY_SELECTED_ATTRIBUTE_IDS + " =? AND " + KEY_SELECTED_DETAIL_IDS + "=?",
                new String[]{String.valueOf(itemId), attributeIds, detailIds}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        if(cursor.getCount() == 0) {
            return 0;
        } else {
            return Integer.parseInt(cursor.getString(9));
        }
    }

    public double getPriceByItemAttributeAndDetailIds(int itemId, String attributeIds, String detailIds) {

        SQLiteDatabase db = this.getReadableDatabase();


        if(detailIds == null) {
            detailIds = "";
        }
        Cursor cursor = db.query(TABLE_BASKET, new String[]{KEY_ID,
                        KEY_ITEM_ID, KEY_SHOP_ID, KEY_USER_ID, KEY_NAME, KEY_DESC, KEY_UNIT_PRICE, KEY_VAT,
                        KEY_DISCOUNT_PERCENT, KEY_QTY, KEY_IMAGE_PATH, KEY_CURRENCY_SYMBOL, KEY_CURRENCY_SHORT_FORM,
                        KEY_SELECTED_ATTRIBUTE_NAMES, KEY_SELECTED_ATTRIBUTE_IDS, KEY_SELECTED_DETAIL_IDS, KEY_SELECTED_DETAIL_NAMES},
                KEY_ITEM_ID + "=? AND " + KEY_SELECTED_ATTRIBUTE_IDS + " =? AND " + KEY_SELECTED_DETAIL_IDS + "=? ",
                new String[]{String.valueOf(itemId), attributeIds, detailIds}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        
        if(cursor.getCount() == 0) {
            return -1;
        } else {
            return Double.parseDouble(cursor.getString(6));
        }
    }

    // Getting basket Count
    public int getBasketCount() {
        String countQuery = "SELECT * FROM " + TABLE_BASKET;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }

    // Getting basket Count By Item ID
    public int getBasketCountByItemId(int itemId) {
        String countQuery = "SELECT * FROM " + TABLE_BASKET + " Where " + KEY_ITEM_ID + " = " + itemId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    // Get basket count by item, attribute, and detail - id.
    public int getBasketCountByAttriubteIds(String attributeIds, int detailId) {
        String countQuery = "SELECT * FROM " + TABLE_BASKET + " Where " + KEY_SELECTED_ATTRIBUTE_IDS + " = '" + attributeIds + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    // Get basket count by item, attribute, and detail - id.
    public int getBasketCountByItemIdAndAttriubteIdsAndDetailIds(int itemId, String attributeIds, String detailIds) {

        String countQuery;

        countQuery = "SELECT * FROM " + TABLE_BASKET + " Where " + KEY_ITEM_ID + " = '" + itemId + "' and " +
                KEY_SELECTED_ATTRIBUTE_IDS + " = '" + attributeIds + "' and " + KEY_SELECTED_DETAIL_IDS + " ='" + detailIds + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    public void clearDatabase() {

        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM " + TABLE_BASKET);
        return;
    }

    private List<String> debugDump() {
        String debugQuery = "SELECT * FROM " + TABLE_BASKET;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(debugQuery, null);

        List<String> ret = new ArrayList<>();
        while(cursor.moveToNext()) {

            String line = "";
            for(int i = 0; i < cursor.getColumnCount(); i++) {
                line += cursor.getString(i);
            }
            ret.add(line);
        }
        return ret;
    }

    private String debugDump(Cursor cursor) {

        String ret = "";
        while(cursor.moveToNext()) {

            for(int i = 0; i < cursor.getColumnCount(); i++) {
                ret += cursor.getString(i);
            }
        }
        return ret;
    }

    // Getting basket Count By shop ID
    public int getBasketCountByShopId(int shopId) {
        String countQuery = "SELECT * FROM " + TABLE_BASKET + " Where " + KEY_SHOP_ID + " = " + shopId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    public int updateBasketByIdsAndAttributes(BasketData basketData, int itemId, int shopId, String attributeIds) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ITEM_ID, basketData.getItemId());
        values.put(KEY_SHOP_ID, basketData.getShopId());
        values.put(KEY_USER_ID, basketData.getUserId());
        values.put(KEY_NAME, basketData.getName());
        values.put(KEY_DESC, basketData.getDesc());
        values.put(KEY_UNIT_PRICE, basketData.getUnitPrice());
        values.put(KEY_DISCOUNT_PERCENT, basketData.getDiscountPercent());
        values.put(KEY_QTY, basketData.getQty());
        values.put(KEY_IMAGE_PATH, basketData.getImagePath());
        values.put(KEY_CURRENCY_SYMBOL, basketData.getCurrencySymbol());
        values.put(KEY_CURRENCY_SHORT_FORM, basketData.getCurrencyShortForm());
        values.put(KEY_SELECTED_ATTRIBUTE_NAMES, basketData.selected_attribute_names);
        values.put(KEY_SELECTED_ATTRIBUTE_IDS, basketData.selected_attribute_ids);
        values.put(KEY_SELECTED_DETAIL_IDS, basketData.selected_detail_ids);
        values.put(KEY_SELECTED_DETAIL_NAMES, basketData.selected_detail_names);

        // updating row
        String clause = KEY_ITEM_ID + " = ? AND " + KEY_SHOP_ID + " = ? AND " + KEY_SELECTED_ATTRIBUTE_IDS + " = ? AND " + KEY_SELECTED_DETAIL_IDS + "= ? ";
        return db.update(TABLE_BASKET, values, clause,
                new String[]{String.valueOf(itemId), String.valueOf(shopId), attributeIds, basketData.getSelectedDetailIds()});
    }

    // Updating a basket
    public int updateBasketByIds(BasketData basketData, int itemId, int shopId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ITEM_ID, basketData.getItemId());
        values.put(KEY_SHOP_ID, basketData.getShopId());
        values.put(KEY_USER_ID, basketData.getUserId());
        values.put(KEY_NAME, basketData.getName());
        values.put(KEY_DESC, basketData.getDesc());
        values.put(KEY_UNIT_PRICE, basketData.getUnitPrice());
        values.put(KEY_DISCOUNT_PERCENT, basketData.getDiscountPercent());
        values.put(KEY_QTY, basketData.getQty());
        values.put(KEY_IMAGE_PATH, basketData.getImagePath());
        values.put(KEY_CURRENCY_SYMBOL, basketData.getCurrencySymbol());
        values.put(KEY_CURRENCY_SHORT_FORM, basketData.getCurrencyShortForm());
        values.put(KEY_SELECTED_ATTRIBUTE_NAMES, basketData.getSelectedAttributeNames());
        values.put(KEY_SELECTED_ATTRIBUTE_IDS, basketData.getSelectedAttributeIds());
        values.put(KEY_SELECTED_DETAIL_IDS, basketData.getSelectedDetailIds());
        values.put(KEY_SELECTED_DETAIL_NAMES, basketData.getSelectedDetailNames());

        // updating row
        return db.update(TABLE_BASKET, values, KEY_ITEM_ID + " = ? AND " + KEY_SHOP_ID + " = ? AND " + KEY_SELECTED_DETAIL_IDS + " = ? ",
                new String[]{String.valueOf(itemId), String.valueOf(shopId), basketData.getSelectedDetailIds()});
    }

    // Deleting a basket
    public void deleteBasket(BasketData basketData) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BASKET, KEY_ID + " = ?",
                new String[] { String.valueOf(basketData.getId()) });
        db.close();
    }

    // Deleting a basket by IDs
    public void deleteBasketByIds(int itemId, int shopId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BASKET, KEY_ITEM_ID + " = ? AND " + KEY_SHOP_ID + " =? ",
                new String[] { String.valueOf(itemId), String.valueOf(shopId)  });
        db.close();
    }

    public void deleteBasketByItemAndAttributeIdsAndDetailIds(int itemId, String attributeIds, String detailIds) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BASKET, KEY_ITEM_ID + " = ? AND " + KEY_SELECTED_ATTRIBUTE_IDS + " = ? and " + KEY_SELECTED_DETAIL_IDS + " = ? ",
                new String[] { String.valueOf(itemId), attributeIds, detailIds});
        db.close();
    }

    // Deleting a basket by Shop Id
    public void deleteBasketByShopId(int shopId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BASKET, KEY_SHOP_ID + " =? ",
                new String[] {String.valueOf(shopId)});
        db.close();
    }

    // Add orderid/email to database table
    public void addOrderId(String email, int order) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ORDER_ID, order);
        values.put(CUSTOMER_EMAIL, email);

        // Inserting Row
        db.insert(TABLE_ORDERID, null, values);
        db.close(); // Closing database connection

    }

    // Get email and order
    public ArrayList<Integer> getOrderIdsByEmail(String email) {

        // Select all order ids by email query
        String selectQuery = "SELECT " + ORDER_ID + " FROM " + TABLE_ORDERID + " Where " + CUSTOMER_EMAIL + " = '" + email + "'";
        ArrayList<Integer> orderIds = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                orderIds.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }

        // return basket list
        return orderIds;
    }
}
