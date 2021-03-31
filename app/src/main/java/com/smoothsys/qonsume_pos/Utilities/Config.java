package com.smoothsys.qonsume_pos.Utilities;

import android.app.Application;

import java.text.SimpleDateFormat;

/**
 * Created by Pontu on 2017-04-09.
 */

public class Config extends Application {

    public static final int NOTIFICATION_UPDATE_INTERVAL = 5;

    public static final Boolean DEBUG_SHOPS_ALWAYS_OPEN = false;
    public static final Boolean DEBUG_DISABLE_PAY_OPTIONS = false;

    public static final String APP_IMAGES_LIVE = "https://f7d63b4b-d7bf-413e-a1f2-a84d181d78c0.mock.pstmn.io//uploads/";
    public static final String APP_URL_LIVE = "https://f7d63b4b-d7bf-413e-a1f2-a84d181d78c0.mock.pstmn.io/";

    public static final String APP_IMAGES_DEV = "https://f7d63b4b-d7bf-413e-a1f2-a84d181d78c0.mock.pstmn.io//uploads/";
    public static final String URL_DEV = "https://f7d63b4b-d7bf-413e-a1f2-a84d181d78c0.mock.pstmn.io/";

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static String getAppImagesUrl() {
        return APP_IMAGES_URL;
    }

    private static String BASE_URL = APP_URL_LIVE;
    private static String APP_IMAGES_URL = APP_IMAGES_LIVE;

    private static final boolean useLive = false;
    static {
        if(useLive) {

            BASE_URL = APP_URL_LIVE;
            APP_IMAGES_URL = APP_IMAGES_LIVE;

        } else{

            BASE_URL = URL_DEV;
            APP_IMAGES_URL = APP_IMAGES_DEV;
        }
    }

    public static final boolean DEBUG_ENABLE_QPAY = false;
    public static final boolean DEBUG_SHOW_ALL_DETAILS = false;

    public static final int MSG_PER_ITEM = 1;
    public static final int MSG_PER_ORDER= 2;
    public static final int NOTIFICATION_MSG_TYPE = MSG_PER_ITEM;

    public static final SimpleDateFormat STANDARD_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public static final int orderListRefreshTime = 5;
}