package com.smoothsys.qonsume_pos.FragmentsAndActivities;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.smoothsys.qonsume_pos.Adapters.BasketFragmentInterface;
import com.smoothsys.qonsume_pos.Adapters.CardInfoListAdapter;
import com.smoothsys.qonsume_pos.Adapters.CardInfoTransactionListAdapter;
import com.smoothsys.qonsume_pos.Bambora.BamboraManager;
import com.smoothsys.qonsume_pos.DataDump.PaymentTypes;
import com.smoothsys.qonsume_pos.Factories.SubmitObjectFactory;
import com.smoothsys.qonsume_pos.Models.BasketData;
import com.smoothsys.qonsume_pos.Models.Credit;
import com.smoothsys.qonsume_pos.Models.Merchant;
import com.smoothsys.qonsume_pos.Models.MerchantResponseObject;
import com.smoothsys.qonsume_pos.Models.OrderClasses.Order;
import com.smoothsys.qonsume_pos.Models.OrderClasses.OrderResponseObject;
import com.smoothsys.qonsume_pos.Models.SubmissionStatusObject;
import com.smoothsys.qonsume_pos.R;
import com.smoothsys.qonsume_pos.Retrofit.ISnabbOrderAPI;
import com.smoothsys.qonsume_pos.Retrofit.RetrofitManager;
import com.smoothsys.qonsume_pos.StateManagement.ScreenState;
import com.smoothsys.qonsume_pos.StateManagement.ScreenSwitcher;
import com.smoothsys.qonsume_pos.StateManagement.StateChanger;
import com.smoothsys.qonsume_pos.Utilities.Cache;
import com.smoothsys.qonsume_pos.Utilities.Config;
import com.smoothsys.qonsume_pos.Utilities.DBHandler;
import com.smoothsys.qonsume_pos.Utilities.EncryptionServices;
import com.smoothsys.qonsume_pos.Utilities.NotificationUtils;
import com.smoothsys.qonsume_pos.Utilities.Utils;

import org.json.JSONArray;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    public ScreenSwitcher screenSwitcher;
    private NavigationView navigationView = null;
    private Toolbar toolbar = null;

    private DBHandler db;
    private BasketFragmentInterface basketInterface;
    private SharedPreferences prefs;

    String mCustomerEmail = "";

    //Declaring adapters for lists here, so they can be updated from any view (for example when a new API-call has been made)
    public String[] cardInfoList = new String[] { "No voucher loaded...","No Name Loaded...","No Credit Loaded...", "No email loaded...","No ValidTo loaded...","No ValidFrom loaded...","No voucher type loaded", "No Event loaded..."};
    public CardInfoListAdapter cardInfoListAdapter;
    public CardInfoTransactionListAdapter cardTransactionListAdapter;

    long minutesSincePreviousCache = -1;

    public float touchX = 0, touchY = 0;

    final int MY_PERMISSIONS_REQUEST_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setupResources();
        setContentView(R.layout.activity_main);

        updateCachedPrefsTime();
        askForCameraPermission();

        initUI();
        manageAutoLogin();
        handleIntent(getIntent());
    }


    private void initUI() {

        setupToolbar();
        setupDrawer();
        setupNavigationView();
        setVersionLabel();
    }

    @Override
    protected void onResume() {

        super.onResume();
        invalidateOptionsMenu();
    }

    private void manageAutoLogin() {

        if(StateChanger.getState() == ScreenState.firstTimeLaunched) {
            String password = getPassword();
            if(password.equals("")) {
                // log out - and go to log in screen if no user i cached
                doLogout();
            } else {
                String userName = prefs.getString("_user_name","");
                doLogin(userName,password);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
        }

        boolean goToOrdersScreen = intent.getBooleanExtra("goToOrdersScreen", false);

        if(goToOrdersScreen) {

            intent.removeExtra("goToOrdersScreen");
            new ScreenSwitcher(R.id.fragment_container, this).goToOrderTabsFragment();
        }
    }

    private void setupResources() {

        // == Bambora ==
        BamboraManager bm = new BamboraManager();
        bm.registerHandle(getBaseContext());

        // == DB ==
        db = new DBHandler(this);

        // == Utils ==
        new Utils(this);

        // == Prefs ==
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        // == ScreenSwitcher ==
        screenSwitcher = new ScreenSwitcher(R.id.fragment_container, this);

        setupMessageHandler();
    }

    private Handler messageHandler;

    ISnabbOrderAPI ISnabbOrderAPI;
    private Call<String> dailyCountCall;

    private void setupMessageHandler() {
        if(messageHandler == null) {
            messageHandler = new Handler();


            ISnabbOrderAPI = RetrofitManager.SetupInterfaceFromCredentials(prefs);

            final int delay = 1000 * Config.NOTIFICATION_UPDATE_INTERVAL;
            messageHandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if(Cache.mMerchant == null) {
                        if(messageHandler != null) {
                            messageHandler.postDelayed(this, delay);
                        }
                        return;
                    }

                    int shopId = Cache.mMerchant.getShopID();
                    dailyCountCall = ISnabbOrderAPI.getTransactionCount(shopId);

                    dailyCountCall.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                            if(Cache.mMerchant == null) {
                                // if log out - just return
                                return;
                            }

                            if(response == null) {
                                return;
                            }

                            if(response.body() == null) {
                                return;
                            }

                            int count;
                            try {
                                count = Integer.parseInt(response.body());
                            } catch(NumberFormatException nfe)
                            {
                                return;
                            }

                            int difference = count - prefs.getInt("daily_transaction_count", 0);
                            NotificationUtils.createNotification(difference, getBaseContext());
                            prefs.edit().putInt("daily_transaction_count", count).apply();
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {}
                    });

                    if(messageHandler != null) {
                        messageHandler.postDelayed(this, delay);
                    }

                }
            }, delay);
        }
    }

    private void updateCachedPrefsTime() {

        String previousCacheTime = prefs.getString("_cache_date", "");
        if (previousCacheTime != "") {
            updateCacheTime(previousCacheTime);
        }
    }

    private void updateCacheTime(String previousCacheTime) {

        Date previousCacheTimeDate = new Date();
        try {
            previousCacheTimeDate = Config.STANDARD_DATE_FORMAT.parse(previousCacheTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Long millisecondsSinceCached =  new Date().getTime() - previousCacheTimeDate.getTime();
        minutesSincePreviousCache = millisecondsSinceCached / (1000 * 60);
    }

    private void setupToolbar() {

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupDrawer() {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    private void setupNavigationView() {

        navigationView = findViewById(R.id.nav_view);

        updateShopNameHeader();

        if(!Cache.SingedIn)
        {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_main_drawer_log_out);
        }
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.nav_home) {
            screenSwitcher.goToHomeFragment();
        } else if(id == R.id.nav_profile) {
            screenSwitcher.goToProfilePayment();
        } else if (id == R.id.nav_transaction) {
            screenSwitcher.goToManualPaymentFragment();
        } else if (id == R.id.nav_card) {
            screenSwitcher.goToCardInfoFragment();
        } else if (id == R.id.nav_orderList){
            screenSwitcher.goToOrderTabsFragment();
        } else if(id == R.id.nav_categoryTabs) {
            screenSwitcher.goToCreateOrderFragment();
        }else if(id == R.id.nav_history) {
            screenSwitcher.goToHistoryFragment();
        }else if(id == R.id.nav_settings) {
            screenSwitcher.goToSettingsFragment();
        }else if(id == R.id.nav_reports) {
            screenSwitcher.goToReportsFragment();
        }else if (id == R.id.nav_logout) {
            doLogout();
        } else {
            StateChanger.setState(ScreenState.NULL_screenState);
        }
        closeDrawer();
        return true;
    }

    private void closeDrawer() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    void setVersionLabel() {
        Menu menu = navigationView.getMenu();
        MenuItem label = menu.getItem(menu.size()-1);
        label.setTitle(getVersionString());
    }

    private String getVersionString() {
        String versionString = "Version: ";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(),0);
            versionString += pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionString;
    }

    void doLogout() {

        setLogoutMenu();
        clearLoginInfo();
        clearBasketData();
        Cache.clear();
        screenSwitcher.goToLogInFragment();
        setVersionLabel();
    }

    private void setLogoutMenu() {

        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.activity_main_drawer_log_out);

        View headerView = navigationView.getHeaderView(0);
        TextView shopNameHeader = headerView.findViewById(R.id.drawer_shop_name_tv);
        shopNameHeader.setText("");
    }

    private void clearLoginInfo() {

        Cache.SingedIn = false;

        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("_cache_date");
        editor.remove("_user_name");
        editor.remove("_password");
        editor.remove("daily_transaction_count");
        editor.commit();
    }

    private boolean responseIsValid(Response<MerchantResponseObject> response) {

        if(response.body() == null) {
            Toast.makeText(getApplicationContext(), "Could not find merchant: " + response.raw().message(), Toast.LENGTH_SHORT).show();
            return false;
        }

        MerchantResponseObject responseObject = response.body();
        if(responseObject.getData() == null) {
            Toast.makeText(getApplicationContext(), "Could not find merchant!", Toast.LENGTH_SHORT).show();
            return false;
        }

        Merchant m = responseObject.getData();
        if(m == null) {
            Toast.makeText(getApplicationContext(), "Could not find merchant!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private SharedPreferences.Editor getPrefsEditor() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        return prefs.edit();
    }

    private void updateCacheAndPrefs(Merchant merchant, String name) {

        Cache.mMerchant = merchant;
        Cache.mUserId = Cache.mMerchant.getUserID();

        // save date and name in shared prefs.
        String currentDateString = Config.STANDARD_DATE_FORMAT.format(new Date());
        SharedPreferences.Editor editor = getPrefsEditor();
        editor.putString("_cache_date", currentDateString);
        editor.putString("_user_name", name);
        editor.commit();

        updateShopNameHeader();
    }

    public void updateShopNameHeader() {

        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView shopNameHeader = headerView.findViewById(R.id.drawer_shop_name_tv);

        String shopName = "";
        if(Cache.mRestaurant != null) {
            if(Cache.mRestaurant.getName() != null) {

                shopName = Cache.mRestaurant.getName();

            }
        }
        shopNameHeader.setText(shopName);
    }

    ProgressDialog dialog;
    @RequiresApi(api = Build.VERSION_CODES.M)
    void doLogin(final String name, final String password) {

        dialog = ProgressDialog.show(MainActivity.this, "",
                "Logging in.. Please wait...", true);

        final ISnabbOrderAPI snabbpayInterface = RetrofitManager.setupInterfaceFromParameters(name, password);
        Call<MerchantResponseObject> call = snabbpayInterface.merchantLogin(name, password);

        call.enqueue(new Callback<MerchantResponseObject>() {
            @Override
            public void onResponse(Call<MerchantResponseObject> call, Response<MerchantResponseObject> response) {
                if(!responseIsValid(response)) {
                    return;
                }

                MerchantResponseObject responseObject = response.body();
                Merchant merchant = responseObject.getData();

                updateCacheAndPrefs(merchant, name);
                EncryptionServices.encryptPassword(password, getBaseContext());

                updateMenu();
                setVersionLabel();
                Cache.SingedIn = true;

                dialog.cancel();
                screenSwitcher.goToHomeFragment();
            }

            @Override
            public void onFailure(Call<MerchantResponseObject> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Login failed! Please try again later", Toast.LENGTH_LONG).show();
                dialog.cancel();
            }
        });
    }

    private void updateMenu() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.activity_main_drawer);
    }

    String typeOfScan = "";
    String qrString = "";

    public void scanForCardInfo() {

        typeOfScan = "CARD_INFO";
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Scan Customer QR-code");
        integrator.setOrientationLocked(false);
        integrator.setCaptureActivity(OrientableCaptureActivity.class);
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
    }

    public int tableNumber = 0;
    public Order orderToBePayed;

    public void scanForPayment(Order order) {

        orderToBePayed = order;
        typeOfScan = "ORDER_PAYMENT";

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Scan Customer QR-code");
        integrator.setOrientationLocked(false);
        integrator.setCaptureActivity(OrientableCaptureActivity.class);
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    double amountToBePayed = -1;
    public void scanForPrePayment(double amount) {

        amountToBePayed = amount;
        typeOfScan = "ORDER_PRE_PAYMENT";

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Scan Customer QR-code");
        integrator.setOrientationLocked(false);
        integrator.setCaptureActivity(OrientableCaptureActivity.class);
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
    }

    public void scanForPayment() {

        typeOfScan = "ORDER_PAYMENT";
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Scan Customer QR-code");
        integrator.setOrientationLocked(false);
        integrator.setCaptureActivity(OrientableCaptureActivity.class);
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
    }

    public void scanForOrder() {

        typeOfScan = "ORDER";
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Scan Customer QR-code");
        integrator.setOrientationLocked(false);
        integrator.setCaptureActivity(OrientableCaptureActivity.class);
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (resultCode != RESULT_CANCELED) {
            qrString = intent.getStringExtra("SCAN_RESULT");
            if(typeOfScan == "CARD_INFO") {
                populateCardInfoList();
                hasJustScanned = true;
            }

            if(typeOfScan == "ORDER") {
                //MenuItem scanButton = toolbar.getMenu().findItem(R.id.scan_btn);
                //scanButton.setIcon(R.drawable.ic_cancel);
                scan_active = true;
                sortOrder();
            }

            if(typeOfScan == "ORDER_PAYMENT") {

                PaymentOptionsFragment payFrag = (PaymentOptionsFragment) getSupportFragmentManager().findFragmentByTag("PaymentOptionsFragment");
                if(payFrag != null) {

                    String custEmail = getEmailFromQR(qrString);
                    payFrag.checkAndReduceSnabbpayCredit(Double.parseDouble(orderToBePayed.getTotalAmount()), custEmail);
                } else {

                    ManualPaymentFragment manualPaymentFragment = (ManualPaymentFragment) getSupportFragmentManager().findFragmentByTag("ManualPaymentFragment");
                    if(manualPaymentFragment != null) {
                        String custEmail = getEmailFromQR(qrString);
                        manualPaymentFragment.runThisAfterScan(orderToBePayed, custEmail);
                    }
                }
            }

            if(typeOfScan == "ORDER_PRE_PAYMENT") {

                PaymentOptionsFragment payFrag = (PaymentOptionsFragment) getSupportFragmentManager().findFragmentByTag("PaymentOptionsFragment");

                if(payFrag != null) {

                    String custEmail = getEmailFromQR(qrString);
                    payFrag.checkAndReduceSnabbpayCredit(amountToBePayed, custEmail);
                }
            }
        }
    }

    private String getEmailFromQR(String qr) {

        String[] splitted = qr.split(";");
        String retEmail = splitted[2].split("=")[1];
        return retEmail;
    }

    String emailForOrderSorting = "";
    void sortOrder() {

        String[] statements = qrString.split(";");
        if(statements.length < 2) {
            Toast.makeText(getApplicationContext(), "Something went wrong - please try again.", Toast.LENGTH_SHORT).show();
            return;
        }
        String email = statements[2].split("=")[1];
        emailForOrderSorting = email;

    }

    private List<BasketData> basketDataSet = new ArrayList<>();
    Call<SubmissionStatusObject> submitOrderCall;
    private void submitOrderPayLater() {
        sendOrderObject(SubmitObjectFactory.createOrderObject(getBaseContext(), tableNumber, PaymentTypes.PAY_ON_DELIVERY));
    }

    private void sendOrderObject(JSONArray orderObject) {

        final ISnabbOrderAPI snabbpayAPII = RetrofitManager.SetupInterfaceFromCredentials(prefs);
        submitOrderCall = snabbpayAPII.submitOrder(orderObject);
        submitOrderCall.enqueue(new Callback<SubmissionStatusObject>() {
            @Override
            public void onResponse(Call<SubmissionStatusObject> call, Response<SubmissionStatusObject> response) {

                if(badResponse(response)) {
                    return;
                }

                Toast.makeText(getApplicationContext(), "Order successfully submitted to Server.", Toast.LENGTH_LONG).show();
                clearBasketData();
            }

            @Override
            public void onFailure(Call<SubmissionStatusObject> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Something went wrong submitting the order to the server: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void clearBasketData() {

        for (int i = 0; i < basketDataSet.size(); i++) {
            db.deleteBasket(basketDataSet.get(i));
        }

        if(db != null) {
            db.clearDatabase();
        }

        basketDataSet.clear();
        if(basketInterface != null) {
            basketInterface.clearDataAfterOrderSubmission();
        }
    }

    private boolean badResponse(Response<SubmissionStatusObject> response) {

        if(response.code() != 200) {
            Toast.makeText(getApplication(), "Something went wrong submitting the order to the server: " + response.code(), Toast.LENGTH_LONG).show();
            return true;
        }

        if (!response.body().getStatus().equals("success")) {
            Toast.makeText(getApplicationContext(), "Something went wrong submitting the order to the server: " + response.body().getData(), Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    String currencySymbol = "";
    private void loadBasketData() {

        try {
            if (basketDataSet != null) {
                basketDataSet.clear();
            }
            List<BasketData> basket = db.getAllBasketDataByShopId(Cache.mMerchant.getShopID());

            //totalAmount = 0.0;
            for (BasketData basketData : basket) {

                if(basketData.getQty() < 1) {
                    continue;
                }
                basketDataSet.add(basketData);
                currencySymbol = basketData.getCurrencySymbol();
                //totalAmount += basketData.getQty() * Float.parseFloat(basketData.getUnitPrice());
            }
            //totalAmount = Double.valueOf(String.format(Locale.US, "%.1f", totalAmount));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Something went wrong loading basket data from local memory.", Toast.LENGTH_LONG).show();
        }
    }

    public void setBasketInterface(BasketFragmentInterface bi) {
        basketInterface = bi;
    }

   public ArrayList<String> cardTransactionList = new ArrayList<>(100);
    public void APIGetTransactionList() {

        cardTransactionList.clear();

        String mMerchantEmail = Cache.mMerchant.getUserEmail();

        if(Cache.mMerchant == null) {
            Toast.makeText(getBaseContext(), "No merchant loaded!", Toast.LENGTH_LONG).show();
            return;
        }

        if(mCustomerEmail.equals("")) {
            Toast.makeText(getBaseContext(), "No customer loaded!", Toast.LENGTH_LONG).show();
            return;
        }

        if(mMerchantEmail.equals("")) {
            Toast.makeText(getBaseContext(), "No merchant loaded!", Toast.LENGTH_LONG).show();
            return;
        }

        ISnabbOrderAPI ISnabbOrderAPI_ = RetrofitManager.SetupInterfaceFromCredentials(prefs);

        Call<OrderResponseObject> call = ISnabbOrderAPI_.getCustomerStatement(Cache.mMerchant.getUserEmail(), mCustomerEmail);
        call.enqueue(new Callback<OrderResponseObject>() {
            @Override
            public void onResponse(Call<OrderResponseObject> call, Response<OrderResponseObject> response) {

                if(response == null) {
                    return;
                }

                OrderResponseObject responseObject = response.body();
                List<Order> data = responseObject.getData();
                if(data.size() < 1) {
                    Toast.makeText(MainActivity.this, "No transactions found!", Toast.LENGTH_SHORT).show();
                    return;
                }

                for(int i = 0; i<data.size(); i++)
                {
                    Order currentTransaction = data.get(i);
                    String currentString = "";

                    String trNo = "null";
                    String trDate = "null";
                    if(currentTransaction.getTransactionRefNo() != null) {
                        trNo =currentTransaction.getTransactionRefNo();
                    }

                    if(currentTransaction.getPaymentDate() != null) {
                        trDate =currentTransaction.getPaymentDate();
                    }
                    currentString += "Transaction ID: " + trNo + "\n";
                    currentString += "Date: " + trDate  + " Amount " + currentTransaction.getTotalAmount() + "\n";
                    cardTransactionList.add(currentString);

                    cardTransactionListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<OrderResponseObject> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getInfoFromQRString() {

        String[] statements = qrString.split(";");
        if(statements.length < 2) {
            Utils.makeToast("Something went wrong scanning QR code. Please try again.", MainActivity.this);
            return;
        }

        try {
            String voucherID = statements[0].split("=")[1];
            cardInfoList[0] = "Voucher ID: " + voucherID;

            final String customerName = statements[1].split("=")[1];
            cardInfoList[1] = "Name: " + customerName;

            cardInfoList[2] = "Loading Credit...";

            final String customerEmail = statements[2].split("=")[1];
            cardInfoList[3] = "Email: " + customerEmail;
            mCustomerEmail = customerEmail;

            String validTo = statements[3].split("=")[1];
            cardInfoList[4] = "Valid To: " + validTo;

            String validFrom = statements[4].split("=")[1];
            cardInfoList[5] = "Valid From: " + validFrom;

            String voucherType = statements[5].split("=")[1];
            cardInfoList[6] = "Voucher Type: " + voucherType;

            String eventName = statements[6].split("=")[1];
            cardInfoList[7] = "Event Name: " + eventName;

            if (cardInfoListAdapter == null) {
                cardInfoListAdapter = new CardInfoListAdapter(MainActivity.this,
                        cardInfoList);
            }

            cardInfoListAdapter.notifyDataSetChanged();
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Something is wrong with QR-code format.", Toast.LENGTH_LONG).show();
            return;
        }
    }

    private void getCredit() {

        ISnabbOrderAPI ISnabbOrderAPI = RetrofitManager.SetupInterfaceFromCredentials(prefs);

        Call<List<Credit>> getCreditCall = ISnabbOrderAPI.getCustomerCredit(mCustomerEmail);
        getCreditCall.enqueue(new Callback<List<Credit>>() {
            @Override
            public void onResponse(Call<List<Credit>> call, Response<List<Credit>> response) {

                //If no transactions in list
                if(response == null) {
                    Toast.makeText(MainActivity.this, "No transactions found", Toast.LENGTH_SHORT).show();
                    cardInfoList[2] = "No credit found";
                    cardInfoListAdapter.notifyDataSetChanged();
                    return;
                }

                if(response.body().size() < 1) {
                    Toast.makeText(MainActivity.this, "No credit found!", Toast.LENGTH_SHORT).show();
                    cardInfoList[2] = "No credit found";
                    cardInfoListAdapter.notifyDataSetChanged();
                    return;
                }

                Credit credit = response.body().get(0);
                if(credit == null) {
                    return;
                }

                cardInfoList[2] = "Credit: " + Float.toString(credit.getAmount());
                cardInfoListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Credit>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Sorry! Failed to get customer information from server", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void populateCardInfoList(){

        // catch if something went wrong with scanning
        if(qrString == "") { return; }
        getInfoFromQRString();
        getCredit();
    }

    public Boolean hasJustScanned = false;
    private String currentTitle = "";
    public void setTitleBarText(String text) {

        setTitle(text);
        currentTitle = text;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        // Event to know the user clicked point
        this.touchX = event.getX();
        this.touchY = event.getY();

        return super.dispatchTouchEvent(event);
    }

    public void orderELAChanged() {

        OrderTabsFragment orderTabs = (OrderTabsFragment) getSupportFragmentManager().findFragmentByTag("OrderTabsFragment");
        if(orderTabs != null) {
            orderTabs.updateOrdersFromChildren();
        }
    }

    private void askForCameraPermission() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
                return;
            }
        }
    }

    // conv. method, so we don't have to pass the encrypted password all over the place.
    private String getPassword() {

        String encryptedPassword = prefs.getString("_password","");
        return EncryptionServices.getPassword(encryptedPassword);
    }

    private void sortOrderByQuery(String s) {

        FragmentManager manager = getSupportFragmentManager();
        Fragment frag = manager.findFragmentByTag("OrderTabsFragment");

        if(frag != null) {
            if(frag instanceof OrderTabsFragment) {
                ((OrderTabsFragment) frag).searchForOrders(s);
            }
        }
    }

    private void sortTransactionList(String s) {

        FragmentManager manager = getSupportFragmentManager();
        Fragment frag = manager.findFragmentByTag("HistoryScreenFragment");

        if(frag != null) {
            if(frag instanceof TransactionHistoryFragment) {
                ((TransactionHistoryFragment) frag).searchForTransaction(s);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        setTitle(currentTitle);

        if(StateChanger.getState() == ScreenState.onOrderListScreen) {
            setupOrderListScreenToolbar(menu);
        } else {
            clearOrderListScreenToolbar(menu);
        }

        if(StateChanger.getState() == ScreenState.onHistoryScreen) {
            setupHistoryScreenToolbar(menu);
        } else {
            clearHistoryScreenToolbar(menu);
        }
        return true;
    }

    public String searchQuery = "";
    private void setupOrderListScreenToolbar(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_for_order_screen, menu);

        setupSearchView(menu);
        setupScanBtn(menu);
    }

    private void setupScanBtn(Menu menu) {

        MenuItem scanButton = menu.findItem(R.id.scan_btn);
        if(scan_active) {
            scanButton.setIcon(R.drawable.ic_cancel);
        } else {
            scanButton.setIcon(R.drawable.ic_scan_white);
        }

        scanButton.setOnMenuItemClickListener(item -> {

            changeScanBtnIcon();
            OrderTabsFragment orderTabsFragment = (OrderTabsFragment) getSupportFragmentManager().findFragmentByTag("OrderTabsFragment");
            orderTabsFragment.doScan();
            return false;
        });
    }

    private void setupSearchView(Menu menu) {

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.searchOrder).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setInputType(InputType.TYPE_CLASS_NUMBER);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchQuery = newText;
                sortOrderByQuery(newText);
                return true;
            }
        });
    }

    Boolean scan_active = false;
    private void changeScanBtnIcon() {

        if(scan_active) {
            MenuItem scanButton = toolbar.getMenu().findItem(R.id.scan_btn);
            scanButton.setIcon(R.drawable.ic_scan_white);
            scan_active = false;
        }
    }

    private void setupHistoryScreenToolbar(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_bar_for_history_screen, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                sortTransactionList(newText);
                return true;
            }
        });
    }

    private void clearOrderListScreenToolbar(Menu menu) {

        SearchView sV;
        if(menu.findItem(R.id.search) != null) {
            sV = (SearchView) menu.findItem(R.id.search).getActionView();
            sV.setVisibility(View.GONE);
        }
    }

    private void clearHistoryScreenToolbar(Menu menu) {

        SearchView searchView;
        if(menu.findItem(R.id.search) != null) {
            searchView = (SearchView) menu.findItem(R.id.search).getActionView();
            searchView.setVisibility(View.GONE);
        }
    }

}