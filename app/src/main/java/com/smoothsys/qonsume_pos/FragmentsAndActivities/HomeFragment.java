package com.smoothsys.qonsume_pos.FragmentsAndActivities;


import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smoothsys.qonsume_pos.Models.ItemClasses.Item;
import com.smoothsys.qonsume_pos.Models.ItemClasses.ItemResponseObject;
import com.smoothsys.qonsume_pos.Models.RestaurantClasses.Category;
import com.smoothsys.qonsume_pos.Models.RestaurantClasses.Restaurant;
import com.smoothsys.qonsume_pos.Models.RestaurantClasses.SingleRestaurantResponseObject;
import com.smoothsys.qonsume_pos.Models.RestaurantClasses.SubCategory;
import com.smoothsys.qonsume_pos.R;
import com.smoothsys.qonsume_pos.Retrofit.ISnabbOrderAPI;
import com.smoothsys.qonsume_pos.Retrofit.RetrofitManager;
import com.smoothsys.qonsume_pos.StateManagement.ScreenState;
import com.smoothsys.qonsume_pos.StateManagement.StateChanger;
import com.smoothsys.qonsume_pos.Utilities.BundleKeys;
import com.smoothsys.qonsume_pos.Utilities.Cache;
import com.smoothsys.qonsume_pos.Utilities.NotificationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.smoothsys.qonsume_pos.Utilities.Cache.mMerchant;
import static com.smoothsys.qonsume_pos.Utilities.Cache.mRestaurant;
import static com.smoothsys.qonsume_pos.Utilities.Cache.mRestaurantItems;

public class HomeFragment extends Fragment {

    ISnabbOrderAPI ISnabbOrderAPI;
    private Call<String> dailyCountCall;
    private Call<String> dailyAmountCall;
    private Call<SingleRestaurantResponseObject> restaurantNameCall;
    private Call<ItemResponseObject> itemCall = null;

    String shopName = "";

    @Override
    public void onResume() {
        ((MainActivity) getActivity()).setTitleBarText(getString(R.string.home_name));
        initDailyTransactionData();
        super.onResume();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Cache.mMerchant == null) {
            if(savedInstanceState.getParcelable(BundleKeys.MERCHANT) != null) {
                Cache.mMerchant = savedInstanceState.getParcelable(BundleKeys.MERCHANT);
            }
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        ISnabbOrderAPI = RetrofitManager.SetupInterfaceFromCredentials(prefs);
        initDailyTransactionData();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (Cache.mMerchant != null) {
            outState.putParcelable(BundleKeys.MERCHANT, Cache.mMerchant);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (Cache.mMerchant == null) {
            Cache.mMerchant = savedInstanceState.getParcelable(BundleKeys.MERCHANT);
        }
    }

    private void initDailyTransactionData() {

        int shopId = Cache.mMerchant.getShopID();
        dailyCountCall = ISnabbOrderAPI.getTransactionCount(shopId);
        dailyCountCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                String myNumber = response.body();

                if (getView() == null) {
                    return;
                }

                TextView tv = getView().findViewById(R.id.receiptsNumberTv);
                tv.setText(myNumber);

                int count = Integer.parseInt(myNumber);

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

                int difference = count - prefs.getInt("daily_transaction_count", 0);
                NotificationUtils.createNotification(difference, getContext());

                prefs.edit().putInt("daily_transaction_count", count).apply();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {}
        });

        dailyAmountCall = ISnabbOrderAPI.getTransactionAmount(Cache.mMerchant.getShopID());
        dailyAmountCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (getView() == null) {
                    return;
                }

                String amount = response.body();
                TextView tv = getView().findViewById(R.id.salesNumberTv);
                tv.setText(amount);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {}
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        setupShopAndUserNameText(view);
        setupButtons(view);
        return view;
    }

    private void setupShopAndUserNameText(View view) {
        TextView userNameTextView = view.findViewById(R.id.userNameTextView);

        String userName = "";
        if (Cache.mMerchant.getUserName() != null) {
            userName = Cache.mMerchant.getUserName();
        }

        userNameTextView.setText(userName);

        final TextView shopNameTextView = view.findViewById(R.id.shopNameField);

        restaurantNameCall = ISnabbOrderAPI.getSingleRestaurant(Cache.mMerchant.getShopID());
        restaurantNameCall.enqueue(new Callback<SingleRestaurantResponseObject>() {
            @Override
            public void onResponse(Call<SingleRestaurantResponseObject> call, Response<SingleRestaurantResponseObject> response) {
                Restaurant r = response.body().getData();
                mRestaurant = r;
                shopName = r.getName();
                shopNameTextView.setText(shopName);

                if (getActivity() == null) {
                    return;
                }

                ((MainActivity) getActivity()).updateShopNameHeader();

                // == Loop Through All Categories ==
                List<Category> allCategories = mRestaurant.getCategories();
                for (int i = 0; i < allCategories.size(); i++) {
                    Category currentCategory = allCategories.get(i);
                    List<SubCategory> currentSubCategories = currentCategory.getSubCategories();

                    // == Loop Through All Sub Categories ==
                    for (int j = 0; j < currentSubCategories.size(); j++) {

                        SubCategory subCategory = currentSubCategories.get(j);
                        itemCall = ISnabbOrderAPI.getItems_old(mMerchant.getUserEmail(), Integer.parseInt(subCategory.getId()));

                        // == Get Items Here ==
                        itemCall.enqueue(new Callback<ItemResponseObject>() {
                            @Override
                            public void onResponse(Call<ItemResponseObject> call, Response<ItemResponseObject> response) {

                                if (response.body().getData() == null) {
                                    return;
                                }

                                List<Item> currentItems = response.body().getData();
                                // == Loop Through Items ==
                                for (int k = 0; k < currentItems.size(); k++) {
                                    Item item = currentItems.get(k);

                                    if (mRestaurantItems == null) {
                                        mRestaurantItems = new ArrayList<>();
                                    }

                                    boolean anyDuplicate = mRestaurantItems.stream().anyMatch(it -> it.getId().equals(item.getId()));

                                    if(!anyDuplicate) {
                                        mRestaurantItems.add(item);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ItemResponseObject> call, Throwable t) {
                                if(getContext() != null) {
                                    Toast.makeText(getContext(), "something went wrong when fetching items from server!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<SingleRestaurantResponseObject> call, Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Sorry, could not find your shop!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    void setupButtons(View view) {

        //Setting up transfer button
        FrameLayout goToTransferButton = view.findViewById(R.id.transaction_btn);
        goToTransferButton.setOnClickListener(v -> {
            ((MainActivity)getActivity()).screenSwitcher.goToManualPaymentFragment();
        });

        //Setting up CardInfo button
        FrameLayout goToCardInfoButton = view.findViewById(R.id.card_info_btn);
        goToCardInfoButton.setOnClickListener(v -> {
            ((MainActivity)getActivity()).screenSwitcher.goToCardInfoFragment();
        });

        FrameLayout goToOrderCreationBtn = view.findViewById(R.id.create_order_btn);
        goToOrderCreationBtn.setOnClickListener(v -> ((MainActivity) getActivity()).screenSwitcher.goToCreateOrderFragment());

        final FrameLayout goToOrderListButton = view.findViewById(R.id.order_list_btn);
        goToOrderListButton.setOnClickListener(v -> ((MainActivity) getActivity()).screenSwitcher.goToOrderTabsFragment());

        FrameLayout goToTransactionHistoryBtn = view.findViewById(R.id.history_btn);
        goToTransactionHistoryBtn.setOnClickListener(v -> {
            ((MainActivity)getActivity()).screenSwitcher.goToHistoryFragment();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        StateChanger.setState(ScreenState.onHomeScreen);
    }
}