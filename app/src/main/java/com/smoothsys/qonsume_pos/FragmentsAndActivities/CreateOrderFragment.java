package com.smoothsys.qonsume_pos.FragmentsAndActivities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.smoothsys.qonsume_pos.Adapters.BadgeUpdateInterface;
import com.smoothsys.qonsume_pos.Adapters.DynamicPagerAdapter;
import com.smoothsys.qonsume_pos.Models.RestaurantClasses.Category;
import com.smoothsys.qonsume_pos.Models.RestaurantClasses.Restaurant;
import com.smoothsys.qonsume_pos.Models.RestaurantClasses.RestaurantResponseObject;
import com.smoothsys.qonsume_pos.Models.RestaurantClasses.SingleRestaurantResponseObject;
import com.smoothsys.qonsume_pos.R;
import com.smoothsys.qonsume_pos.Retrofit.ISnabbOrderAPI;
import com.smoothsys.qonsume_pos.Retrofit.RetrofitManager;
import com.smoothsys.qonsume_pos.StateManagement.ScreenState;
import com.smoothsys.qonsume_pos.StateManagement.StateChanger;
import com.smoothsys.qonsume_pos.Utilities.BundleKeys;
import com.smoothsys.qonsume_pos.Utilities.Cache;
import com.smoothsys.qonsume_pos.Utilities.DBHandler;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Pontus on 2017-07-28.
 */

public class CreateOrderFragment extends Fragment implements BadgeUpdateInterface {

    PagerTabStrip pagerTabStrip = null;
    ViewPager pager = null;
    DynamicPagerAdapter itemPagerAdapter = null;
    BadgeUpdateInterface IBadgeUpdate;

    ISnabbOrderAPI snabbpayInterface = null;
    Call<RestaurantResponseObject> restaurantsCall;
    SharedPreferences prefs = null;
    DBHandler db;
    Context mContext;
    MenuItem basketActionButton;
    SwipeRefreshLayout swipeRefreshLayout;

    public void onCreate(Bundle savedInstanceState) {
        db = new DBHandler(getContext());
        setHasOptionsMenu(true);
        mContext = getContext();

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            boolean clearDB = bundle.getBoolean("clear_basket");

            if(clearDB) {
                ((MainActivity)getActivity()).clearBasketData();
            }
        }

        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_create_order, null);
        setupSwipeLayout(view);
        return view;
    }

    private void setupSwipeLayout(View view) {

        swipeRefreshLayout = view.findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> loadCategoriesFromServer());
    }

    public void onStart() {

        super.onStart();

        // == Initialize resources and configurations ==
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        StateChanger.setState(ScreenState.onCreateOrderScreen);
        IBadgeUpdate = this;
        pager = getView().findViewById(R.id.itemViewPager);
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        setupTitleStrip();
        loadCategories();
    }

    // == Fetch cached categories, if the cache is empty, otherwise, load the viewpager  with cached categories ==
    private void loadCategories() {
        if(Cache.mCategories == null) {
            loadCategoriesFromServer();
        } else {
            initViewPager(Cache.mCategories);
        }
    }

    private void setupTitleStrip() {

        pagerTabStrip = getView().findViewById(R.id.pagerTabStrip);
        pagerTabStrip.setTextColor(getResources().getColor(R.color.colorWhite));
        pagerTabStrip.setBackgroundColor(getResources().getColor(R.color.colorSecondary));
        pagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.colorSecondary));
        pagerTabStrip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
        pagerTabStrip.setTextSpacing(150);
        pagerTabStrip.setPadding(0,0,0,50);
    }

    private void initViewPager(List<Category> categories) {

        int oldActivePage = pager.getCurrentItem();
        itemPagerAdapter = new DynamicPagerAdapter(categories, getChildFragmentManager(), IBadgeUpdate, swipeRefreshLayout);
        pager.setAdapter(itemPagerAdapter);

        pager.setCurrentItem(oldActivePage);

        itemPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(Cache.mMerchant == null) {
            Cache.mMerchant = savedInstanceState.getParcelable(BundleKeys.MERCHANT);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(Cache.mMerchant != null) {
            outState.putParcelable(BundleKeys.MERCHANT, Cache.mMerchant);
        }
    }

    Call<SingleRestaurantResponseObject> getSingleRestaurantCall;
    private void loadCategoriesFromServer() {

        snabbpayInterface = RetrofitManager.SetupInterfaceFromCredentials(prefs);
        getSingleRestaurantCall = snabbpayInterface.getSingleRestaurant(Cache.mMerchant.getShopID());
        getSingleRestaurantCall.enqueue(new Callback<SingleRestaurantResponseObject>() {
            @Override
            public void onResponse(Call<SingleRestaurantResponseObject> call, Response<SingleRestaurantResponseObject> response) {

                swipeRefreshLayout.setRefreshing(false);
                if(!errorChecksSingleRestaurantResponse(response.body())) {
                    return;
                }

                Restaurant merchantsRestaurant = response.body().getData();
                List<Category> userCategories = merchantsRestaurant.getCategories();
                Cache.mCategories = userCategories;

                if(!isAdded()) {
                    return;
                }
                initViewPager(userCategories);
            }

            @Override
            public void onFailure(Call<SingleRestaurantResponseObject> call, Throwable t) {
                if(getContext() != null && !call.isCanceled()) {
                    Toast.makeText(mContext, "Sorry, something went wrong when connecting to the server!", Toast.LENGTH_SHORT).show();
                }

                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private boolean errorChecksSingleRestaurantResponse(SingleRestaurantResponseObject rObject) {
        if(rObject == null) {
            Toast.makeText(getContext(), "Something went wrong when fetching restaurants from server", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!rObject.getStatus().equals("success")) {
            Toast.makeText(getContext(), "Something went wrong when fetching restaurants from server", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(rObject.getData() == null) {
            Toast.makeText(getContext(), "Ops - could not find any restaurant data!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void updateCartBadgeCount() {

        if(Cache.mMerchant == null) {
            Toast.makeText(mContext, "No merchant found!", Toast.LENGTH_SHORT).show();
            return;
        }

        int basketCount = db.getBasketCountByShopId(Cache.mMerchant.getShopID());
        if(basketCount == 0) {

            // Make invisible and Set badge text to 0
            basketActionButton.getActionView().setAlpha(0.5f);

            ConstraintLayout counter = basketActionButton.getActionView().findViewById(R.id.counterValuePanel);
            counter.setVisibility(View.INVISIBLE);
            TextView counterText = counter.findViewById(R.id.count);
            counterText.setText("0");

        } else {

            // Make visible and update badge count
            basketActionButton.getActionView().setAlpha(1f);

            ConstraintLayout counter = basketActionButton.getActionView().findViewById(R.id.counterValuePanel);
            counter.setVisibility(View.VISIBLE);

            TextView counterText = counter.findViewById(R.id.count);
            counterText.setText(Integer.toString(basketCount));
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.create_items_toolbar, menu);

        // Basket Button
        basketActionButton = menu.findItem(R.id.action_basket);
        basketActionButton.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                basketIconClick();
            }
        });

        updateCartBadgeCount();
    }

    private void basketIconClick() {
        int basketCount = db.getBasketCountByShopId(Cache.mMerchant.getShopID());
        if(basketCount > 0) {
            ((MainActivity)getActivity()).screenSwitcher.goToBasketFragment();
        } else {
            Toast.makeText(getContext(), "Your cart is empty.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();

        if(basketIconSelected(id)) {
            basketIconClick();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private boolean basketIconSelected(int id) {
        return (id == R.id.action_basket);
    }

    @Override
    public void onItemCountChanged()
    {
        updateCartBadgeCount();
    }

    @Override
    public void onStop() {
        super.onStop();

        if(restaurantsCall != null) {
            restaurantsCall.cancel();
        }
    }
}