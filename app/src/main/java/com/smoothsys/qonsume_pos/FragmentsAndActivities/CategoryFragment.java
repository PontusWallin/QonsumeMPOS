package com.smoothsys.qonsume_pos.FragmentsAndActivities;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.smoothsys.qonsume_pos.Adapters.BadgeUpdateInterface;
import com.smoothsys.qonsume_pos.Adapters.OrderCreateScreen.OrderCreateELA;
import com.smoothsys.qonsume_pos.Models.ItemClasses.Attribute;
import com.smoothsys.qonsume_pos.Models.ItemClasses.Detail;
import com.smoothsys.qonsume_pos.Models.ItemClasses.Item;
import com.smoothsys.qonsume_pos.Models.ItemClasses.ItemResponseObject;
import com.smoothsys.qonsume_pos.Models.RestaurantClasses.Category;
import com.smoothsys.qonsume_pos.Models.RestaurantClasses.SubCategory;
import com.smoothsys.qonsume_pos.R;
import com.smoothsys.qonsume_pos.Retrofit.ISnabbOrderAPI;
import com.smoothsys.qonsume_pos.Retrofit.RetrofitManager;
import com.smoothsys.qonsume_pos.Utilities.Cache;
import com.smoothsys.qonsume_pos.Utilities.Utils;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Pontus on 2017-06-06.
 */

public class CategoryFragment extends Fragment {

    private ExpandableListView listView;
    private OrderCreateELA listAdapter;
    private Category mCategory;

    private List<SubCategory> subCategories;
    Picasso picasso;

    SharedPreferences prefs = null;
    static BadgeUpdateInterface buttonListener;
    private static SwipeRefreshLayout refreshLayout;
    public static HashMap<SubCategory,List<Item>> itemsMap;

    public static CategoryFragment newInstance(Category c, BadgeUpdateInterface badgeUpdateInterface, SwipeRefreshLayout rl) {

        CategoryFragment fragment = new CategoryFragment();
        buttonListener = badgeUpdateInterface;
        refreshLayout = rl;
        fragment.setCategory(c);
        return fragment;
    }

    public void setCategory(Category c) {
        mCategory = c;
        subCategories = mCategory.getSubCategories();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        itemsMap = new HashMap<>();
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        picasso = new Picasso.Builder(getContext()).build();
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_create_items, container, false);

        final ExpandableListView list = view.findViewById(R.id.itemList);
        list.setOnTouchListener((v, event) -> {

            if(listIsAtTop(list)){
                refreshLayout.setEnabled(true);
            } else {
                refreshLayout.setRefreshing(false);
                refreshLayout.setEnabled(false);
            }
            return false;
        });

        return view;
    }

    private boolean listIsAtTop(ListView listView)   {
        if(listView.getChildCount() == 0) return true;
        return listView.getChildAt(0).getTop() == 0;
    }

    @Override
    public void onStart() {

        initData();

        listAdapter = new OrderCreateELA(getContext(), subCategories,itemsMap, picasso, buttonListener);
        listView = getView().findViewById(R.id.itemList);
        if(subCategories != null) {
            listView.setAdapter(listAdapter);
        }

        super.onStart();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    List<Item> items;
    private void initData(){

        if (subCategories == null) {
            return;
        }

        for(int i = 0; i<subCategories.size(); i++) {

            ISnabbOrderAPI snabbpayInterface = RetrofitManager.SetupInterfaceFromCredentials(prefs);
            int subCatId = Integer.parseInt(subCategories.get(i).getId());
            final SubCategory currentSubCat = subCategories.get(i);
            Call<ItemResponseObject> itemCall = snabbpayInterface.getItems(Cache.mMerchant.getShopID(),subCatId);
            final int finalI = i;
            itemCall.enqueue(new Callback<ItemResponseObject>() {
                @Override
                public void onResponse(Call<ItemResponseObject> call, Response<ItemResponseObject> response) {

                    if(!isValidResponse(response)) {
                        return;
                    }

                    ItemResponseObject itemResponse = response.body();
                    items = itemResponse.getData();

                    setDefaultAttribute();

                    itemsMap.put(currentSubCat, items);

                    // if this is the last subCategory to be loaded - update adapter.
                    if(finalI == subCategories.size()-1) {
                        listAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<ItemResponseObject> call, Throwable t) {
                    Utils.makeToast("Something went wrong when fetching items from the server.",getActivity());
                }
            });
        }
    }

    private void setDefaultAttribute() {

        for (Item item: items) {

            List<Attribute> attributes = item.getAttributes();
            if(attributes.size() > 0) {

                Attribute firstAttribute = item.getAttributes().get(0);
                List<Detail> details = firstAttribute.getDetails();
                if(details.size() > 0) {
                    Detail firstDetail = firstAttribute.getDetails().get(0);
                }
            }
        }

    }

    private boolean isValidResponse(Response<ItemResponseObject> response) {
        if(response.code() != 200) {
            Utils.makeToast("Failed to load items for one or more subcategories." + response.message(), getActivity());
            return false;
        }
        ItemResponseObject itemResponse = response.body();

        if(itemResponse == null) {
            return false;
        }

        if(itemResponse.getData() == null) {
            return false;
        }

        if(!itemResponse.getStatus().equals("success")) {
            Utils.makeToast("Something went wrong when fetching items.", getActivity());
            return false;
        }

        items = itemResponse.getData();
        if(items.size() < 1) {
            return false;
        }

        return true;
    }

    public void onResume() {

        ((MainActivity)getActivity()).setTitleBarText(getString(R.string.itemsScreen_name));
        super.onResume();
    }
}
