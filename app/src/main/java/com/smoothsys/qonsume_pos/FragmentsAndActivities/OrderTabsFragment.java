package com.smoothsys.qonsume_pos.FragmentsAndActivities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.smoothsys.qonsume_pos.Adapters.OrderListScreen.EndlessScrollListener;
import com.smoothsys.qonsume_pos.Adapters.OrderListScreen.TableListAdapter;
import com.smoothsys.qonsume_pos.DataDump.ItemStates;
import com.smoothsys.qonsume_pos.DataDump.OrderListContainer;
import com.smoothsys.qonsume_pos.DataDump.OrderManager;
import com.smoothsys.qonsume_pos.DataDump.OrderStatus;
import com.smoothsys.qonsume_pos.Models.OrderClasses.Detail;
import com.smoothsys.qonsume_pos.Models.OrderClasses.Order;
import com.smoothsys.qonsume_pos.Models.OrderClasses.OrderResponseObject;
import com.smoothsys.qonsume_pos.Models.Table;
import com.smoothsys.qonsume_pos.R;
import com.smoothsys.qonsume_pos.Retrofit.ISnabbOrderAPI;
import com.smoothsys.qonsume_pos.Retrofit.RetrofitManager;
import com.smoothsys.qonsume_pos.StateManagement.ScreenState;
import com.smoothsys.qonsume_pos.StateManagement.StateChanger;
import com.smoothsys.qonsume_pos.Utilities.Cache;
import com.smoothsys.qonsume_pos.Utilities.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderTabsFragment extends Fragment implements TableListAdapter.ITableInterface {

    OrderManager ordersManager = null;

    OrderListContainer pendingOrdersCt = null;
    OrderListContainer completedOrdersCt = null;
    OrderListContainer deliveredOrdersCt = null;
    OrderListContainer cancelledOrdersCt = null;

    ViewPager viewPager = null;
    TabLayout tabLayout = null;
        OrdersFragment ordersFragmentCompleted = null;
        OrdersFragment ordersFragmentPending = null;
        OrdersFragment ordersFragmentDelivered = null;
        OrdersFragment ordersFragmentCancelled = null;

    Call<OrderResponseObject> orderCall;
    List<Table> tableList;
    Boolean justScanned = false;

    private Context mContext;
    private View thisView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());

        if(getContext() == null) {
            Utils.psLog("Context is null!");
        } else {
            mContext = getContext();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_tabs_layout, container, false);
        thisView = view;
        return view;
    }

    @Override
    public void onResume() {

        super.onResume();
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        setupPagerTitleStrip(null);

        // == if we have an email to sort by - set the ordersManager ==
        String email = ((MainActivity) getActivity()).emailForOrderSorting;
        if(!email.equals("")) {
            ordersManager.sortActiveOrdersByHeaderString(email);
            populateTableList();
        }

        setupOrderPages(ordersManager);

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        StateChanger.setState(ScreenState.onOrderListScreen);
    }

    public void setupPagerTitleStrip(SwipeRefreshLayout swipeRefreshLayout) {

        //if(tabLayout == null) {
            tabLayout = getView().findViewById(R.id.tabLayout);
            tabLayout.setBackgroundColor(getResources().getColor(R.color.colorSecondary));
            fetchOrdersAndUpdateView(swipeRefreshLayout, false);
        //}
    }

    public void doScan() {
        if(justScanned) {
            justScanned = false;
            ((MainActivity)getActivity()).emailForOrderSorting = "";

            // == Reset UI ==
            ordersManager.resetFiltering();
            setupOrderPages(ordersManager);
            populateTableList();
        } else {
            justScanned = true;
            ((MainActivity)getActivity()).scanForOrder();
        }
    }

    public void updateOrdersFromChildren() {

        updateListsAndHeaders();
        setupOrderPages(ordersManager);
    }

    private void updateListsAndHeaders() {

        HashMap<String,Order> orderHash = new HashMap<>(completedOrdersCt.getOrderMap());
        orderHash.putAll(pendingOrdersCt.getOrderMap());
        orderHash.putAll(deliveredOrdersCt.getOrderMap());
        orderHash.putAll(cancelledOrdersCt.getOrderMap());

        List<String> headerList = new ArrayList<>(completedOrdersCt.getHeaderList());
        headerList.addAll(pendingOrdersCt.getHeaderList());
        headerList.addAll(deliveredOrdersCt.getHeaderList());
        headerList.addAll(cancelledOrdersCt.getHeaderList());

        OrderListContainer fullOrders = new OrderListContainer();
        fullOrders.setHeaderList(headerList);
        fullOrders.setOrderMap(orderHash);

        if(ordersManager == null) {
            return;
        }
        ordersManager.setAllOrders(fullOrders);
    }

    private boolean isValidResponse(OrderResponseObject orderResponse) {

        if (orderResponse == null || orderResponse.getStatus() == null) {
            return false;
        }

        if (!orderResponse.getStatus().equals("success")) {
            Toast.makeText(mContext, "Something went wrong loading orders from the server: " + orderResponse.getStatus(), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void stopRefreshIcon(SwipeRefreshLayout swipeRefreshLayout) {

        if(swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private int numberOfDaysBack = 1;
    private boolean stillEmpty = true;
    private void fetchOrdersAndUpdateView(final SwipeRefreshLayout swipeRefreshLayout, final boolean shouldFetchMore) {

        // == Check if Merchant is loaded ==
        if(Cache.mMerchant == null) {
            Toast.makeText(mContext, "No merchant loaded!", Toast.LENGTH_LONG).show();
            stopRefreshIcon(swipeRefreshLayout);
            return;
        }

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        ISnabbOrderAPI snabbpayInterface = RetrofitManager.SetupInterfaceFromCredentials(prefs);

        String start_date = Utils.getPastDate(numberOfDaysBack);
        String end_date = Utils.getCurrentDate();
        orderCall = snabbpayInterface.getOrders_old(Cache.mMerchant.getShopID());
        orderCall.enqueue(new Callback<OrderResponseObject>() {
            @Override
            public void onResponse(Call<OrderResponseObject> call, Response<OrderResponseObject> response) {

                // Change visibility of views
                TextView noOrderTv = getView().findViewById(R.id.no_orders_found);
                noOrderTv.setVisibility(View.GONE);
                stillEmpty = false;

                if(tableListView != null) {
                    tableListView.setVisibility(View.VISIBLE);
                }

                ordersManager = new OrderManager();
                OrderResponseObject orderResponse = response.body();
                if(!isValidResponse(orderResponse)){
                    stopRefreshIcon(swipeRefreshLayout);
                    return;
                }

                putAllOrdersInOrderManager(orderResponse);
                setupOrderPages(ordersManager);
                populateTableList();
                updateOrdersFromChildren();
                stopRefreshIcon(swipeRefreshLayout);
            }

            @Override
            public void onFailure(Call<OrderResponseObject> call, Throwable t) {
                if(getContext() != null) {

                    // TODO - fix this!
                    if(t.getMessage().equals("java.lang.IllegalStateException: Expected BEGIN_ARRAY but was STRING at line 1 column 27 path $.data")) {

                        // Change visibility of views
                        TextView noOrderTv = getView().findViewById(R.id.no_orders_found);
                        noOrderTv.setVisibility(View.VISIBLE);

                        noOrderTv.setOnClickListener(v -> fetchMore());

                        if(tableListView != null) {
                            tableListView.setVisibility(View.GONE);
                        }

                        if(shouldFetchMore) {
                            fetchMore();
                        }

                    } else {
                        Toast.makeText(getContext(), "Something went wrong fetching orders from the server= " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    stopRefreshIcon(swipeRefreshLayout);
                }
            }
        });
    }

    private void fetchMore() {

        if(stillEmpty & numberOfDaysBack < 20) {
            numberOfDaysBack++;
            fetchOrdersAndUpdateView(null, true);
        }
    }

    private void putAllOrdersInOrderManager(OrderResponseObject orderResponse) {
        List<Order> currentOrders = orderResponse.getData();
        for (int i = 0; i < currentOrders.size(); i++) {

            Order currentOrder = currentOrders.get(i);
            String header = currentOrder.getReceipt_number() + ". " + currentOrder.getEmail();
            ordersManager.putOrder(header,currentOrder);
        }
    }

    private void setupOrderPages(OrderManager orderManager) {

        int oldPage = 0;
        if(viewPager != null) {
            oldPage = viewPager.getCurrentItem();
        }

        if(orderManager == null) {
            orderManager = new OrderManager();
        }

        setupLists(orderManager);
        setupViewPager();

        viewPagerAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(oldPage);
    }

    private void setupLists(OrderManager orderManager) {

        // == Reset lists ==
        pendingOrdersCt = new OrderListContainer();
        completedOrdersCt = new OrderListContainer();
        deliveredOrdersCt = new OrderListContainer();
        cancelledOrdersCt = new OrderListContainer();

        // == funnel ALL orders into correct order container ==
        OrderListContainer activeOrdersCt = orderManager.getActiveOrders();
        for(int i = 0; i < activeOrdersCt.getSize(); i++) {

            Order cOrder = activeOrdersCt.getOrder(i);
            String cHeader = activeOrdersCt.getHeader(i);
            funnelOrderIntoCorrectContainer(cOrder, cHeader);
        }
    }

    // This is the new implementation - orders are sorted based on Item status
    // An order will be inside a container if at least one of it's details are of that status
    private void funnelOrderIntoCorrectContainer(Order order, String header) {

        // == Create flags ==
        boolean anyPendingDetail = false;
        boolean anyCompletedDetail = false;
        boolean anyDeliveredDetail = false;
        boolean anyCancelledDetail = false;

        // == Set Flags ==
        for(Detail detail : order.getDetails()) {

            int detailStatus = detail.getItem_state();
            if(detailStatus == ItemStates.PENDING) {
                anyPendingDetail = true;
            }

            if(detailStatus == ItemStates.COMPLETED) {
                anyCompletedDetail = true;
            }

            if(detailStatus == ItemStates.DELIVERED) {
                anyDeliveredDetail = true;
            }

            if(detailStatus == ItemStates.CANCELLED) {
                anyCancelledDetail = true;
            }
        }

        // == Finally - Put orders in correct container ==
        if(anyPendingDetail) {
            pendingOrdersCt.putOrder(header, order);
        }

        if(anyCompletedDetail) {
            completedOrdersCt.putOrder(header, order);
        }

        if(anyDeliveredDetail) {
            deliveredOrdersCt.putOrder(header,order);
        }

        if(anyCancelledDetail) {
            cancelledOrdersCt.putOrder(header, order);
        }
    }

    private ViewPagerAdapter viewPagerAdapter;

    private void setupViewPager() {

        if(thisView == null) {
            return;
        }

        if(thisView.findViewById(R.id.OrderViewPager) != null) {

            viewPager = thisView.findViewById(R.id.OrderViewPager);
            viewPager.setAdapter(viewPagerAdapter);

            viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

                @Override
                public void onPageScrollStateChanged(int state) { }

                @Override
                public void onPageSelected(int position) {

                    if(old_pos == position) {
                        return;
                    }
                    old_pos = position;

                    updateOrdersFromChildren();
                }
            });

            initPagesIfNull();
        }
    }

    private int old_pos = -1;
    private void initPagesIfNull() {
        if(ordersFragmentPending == null) {
            createPendingTab();
        }

        if(ordersFragmentCompleted == null) {
            createCompletedTab();
        }

        if(ordersFragmentDelivered == null) {
            createDeliveredTab();
        }

        if(ordersFragmentCancelled == null) {
            createCancelledTab();
        }
    }

    private void recreatePages() {
        createPendingTab();
        createCancelledTab();
        createDeliveredTab();
        createCancelledTab();
    }

    private void setFragmentDatas() {

        ordersFragmentCompleted.setData(completedOrdersCt.getHeaderList(), completedOrdersCt.getOrderMap());
        ordersFragmentPending.setData(pendingOrdersCt.getHeaderList(), pendingOrdersCt.getOrderMap());
        ordersFragmentDelivered.setData(deliveredOrdersCt.getHeaderList(), deliveredOrdersCt.getOrderMap());
        ordersFragmentCancelled.setData(cancelledOrdersCt.getHeaderList(), cancelledOrdersCt.getOrderMap());
    }

    public void updateTabs() {

        ordersFragmentCompleted.initData();
        ordersFragmentPending.initData();
        ordersFragmentDelivered.initData();
        ordersFragmentCancelled.initData();
        ((MainActivity)getActivity()).emailForOrderSorting = "";
    }

    List<String> allSearchedHeaders;
    HashMap<String, Order> allSearchedOrders;
    public void searchForOrders(String query) {

        allSearchedHeaders = new ArrayList<>();
        allSearchedOrders = new HashMap<>();

        // == Reset active orders ==
        ordersManager.setActiveOrders(new OrderListContainer());

        // == Set active orders, based on search query ==
        OrderListContainer sortedOrdersPending = searchThroughOrderList(query, pendingOrdersCt, ordersFragmentPending);
        OrderListContainer sortedOrdersCompleted = searchThroughOrderList(query, completedOrdersCt, ordersFragmentCompleted);
        OrderListContainer sortedOrdersDelivered = searchThroughOrderList(query, deliveredOrdersCt, ordersFragmentDelivered);
        OrderListContainer sortedOrdersCancelled = searchThroughOrderList(query, cancelledOrdersCt, ordersFragmentCancelled);

        ordersManager.setActiveOrders(sortedOrdersPending);
        ordersManager.addToActiveOrders(sortedOrdersCompleted);
        ordersManager.addToActiveOrders(sortedOrdersDelivered);
        ordersManager.addToActiveOrders(sortedOrdersCancelled);

        // Populates using fullOrders
        ordersManager.resetFiltering();
        populateTableList();
    }

    private OrderListContainer searchThroughOrderList(String query, OrderListContainer container, OrdersFragment frag ) {

        OrderListContainer sortedCt = new OrderListContainer();
        for(int i = 0; i < container.getSize(); i++) {
            Order cOrder = container.getOrder(i);

            String receiptNoAsString = String.valueOf(cOrder.getReceipt_number());
            if(receiptNoAsString.contains(query)) {
                String correctHeader = container.getHeader(i);
                sortedCt.putOrder(correctHeader, cOrder);
            }
        }

        frag.setData(sortedCt.getHeaderList(), sortedCt.getOrderMap());
        frag.initData();
        return sortedCt;
    }
    private ListView tableListView;
    private void populateTableList() {

        if(getView() == null) {
            return;
        }

        if(getView().findViewById(R.id.tablesList) == null) {
            return;
        }
        tableListView = getView().findViewById(R.id.tablesList);
        tableList = new ArrayList<>();

        OrderListContainer activeOrdersCt = ordersManager.getActiveOrders();
        for(int i = 0; i<activeOrdersCt.getSize(); i++) {
            Order cOrder = activeOrdersCt.getOrder(i);
            tableList.add(new Table(cOrder.getTableNo(), cOrder.getReceipt_number()));
        }

        TableListAdapter tableListAdapter = new TableListAdapter(getContext(), tableList, this);
        tableListView.setAdapter(tableListAdapter);
        tableListAdapter.notifyDataSetChanged();

        tableListView.setOnScrollListener( new EndlessScrollListener(1, this));
    }

    private void findAndScrollToPosition(Order selectedOrder, OrderListContainer container, OrdersFragment ordersFragment) {

        for(int i = 0; i<container.getSize(); i++) {

            Order cOrder = container.getOrder(i);
            int orderNo = Integer.parseInt(selectedOrder.getId());
            if(Integer.parseInt(cOrder.getId()) == orderNo) {

                ordersFragment.updateAndScrollToPosition(i);
            }
        }
        ordersFragment.selectByOrder(selectedOrder);
    }

    public void selectAndScrollToOrder(int orderNo){

        final Order selectedOrder = selectOrderById(orderNo);
        if(selectedOrder == null) {
            return;
        }

        String orderStatus = selectedOrder.getTransactionStatus();
        switch (orderStatus) {
            case OrderStatus.PENDING:

                viewPager.setCurrentItem(0);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        findAndScrollToPosition(selectedOrder, pendingOrdersCt, ordersFragmentPending);
                    }
                }, 100);
                break;

            case OrderStatus.COMPLETED:

                viewPager.setCurrentItem(1);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        findAndScrollToPosition(selectedOrder, completedOrdersCt, ordersFragmentCompleted);
                    }
                }, 100);

                break;

            case OrderStatus.DELIVERED:

                viewPager.setCurrentItem(2);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        findAndScrollToPosition(selectedOrder, deliveredOrdersCt, ordersFragmentDelivered);
                    }
                }, 100);

                break;

            case OrderStatus.CANCELED:

                viewPager.setCurrentItem(3);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        findAndScrollToPosition(selectedOrder, cancelledOrdersCt, ordersFragmentCancelled);
                    }
                }, 100);
                break;
        }
    }

    @Override
    public void onTableClick(int orderNo) {
        selectAndScrollToOrder(orderNo);
    }

    @Override
    public void onGetMoreEntries() {

        if(numberOfDaysBack < 10) {
            numberOfDaysBack++;
            //setupPagerTitleStrip(null);
        } else {
            return;
        }

    }

    private Order selectOrderById(int orderNo) {

        OrderListContainer activeOrderCt = ordersManager.getActiveOrders();
        for(int i = 0; i < activeOrderCt.getSize(); i++) {
            Order cOrder = activeOrderCt.getOrder(i);
            if(cOrder.getReceipt_number() == orderNo) {
                return cOrder;
            }
        }
        return null;
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;

            if(position == 0) {
                fragment = createPendingTab();
            }

            if(position == 1) {
                fragment = createCompletedTab();
            }

            if(position == 2) {
                fragment = createDeliveredTab();
            }

            if(position == 3) {
                fragment = createCancelledTab();
            }

            return fragment;
        }

        private Fragment createCancelledTab() {
            Bundle args = new Bundle();
            ordersFragmentCancelled = new OrdersFragment();

            args.putInt("TypeOfOrders",4);
            ordersFragmentCancelled.setArguments(args);

            ordersFragmentCancelled.setData(cancelledOrdersCt.getHeaderList(),cancelledOrdersCt.getOrderMap());
            return ordersFragmentCancelled;
        }

        private Fragment createPendingTab() {
            Bundle args = new Bundle();
            ordersFragmentPending = new OrdersFragment();

            args.putInt("TypeOfOrders", 1);
            ordersFragmentPending.setArguments(args);

            ordersFragmentPending.setData(pendingOrdersCt.getHeaderList(),pendingOrdersCt.getOrderMap());
            return ordersFragmentPending;
        }

        private Fragment createCompletedTab() {
            Bundle args = new Bundle();
            ordersFragmentCompleted = new OrdersFragment();
            ordersFragmentCompleted.setData(completedOrdersCt.getHeaderList(),completedOrdersCt.getOrderMap());

            args.putInt("TypeOfOrders", 2);
            ordersFragmentCompleted.setArguments(args);

            return ordersFragmentCompleted;
        }

        private Fragment createDeliveredTab() {
            Bundle args = new Bundle();
            ordersFragmentDelivered = new OrdersFragment();
            ordersFragmentDelivered.setData(deliveredOrdersCt.getHeaderList(), deliveredOrdersCt.getOrderMap());

            args.putInt("TypeOfOrders", 3);
            ordersFragmentDelivered.setArguments(args);

            return ordersFragmentDelivered;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position == 0) {
                return getResources().getString(R.string.pending);
            }
            if(position == 1) {
                return getResources().getString(R.string.completed);
            }
            if(position == 2) {
                return getResources().getString(R.string.delivered);
            }
            if (position ==3) {
                return getResources().getString(R.string.canceled);
            }
            return null;
        }
    }

    private Fragment createPendingTab() {
        Bundle args = new Bundle();
        ordersFragmentPending = new OrdersFragment();
        ordersFragmentPending.setData(pendingOrdersCt.getHeaderList(),pendingOrdersCt.getOrderMap());
        args.putInt("TypeOfOrders", 1);
        ordersFragmentPending.setArguments(args);
        return ordersFragmentPending;
    }

    private Fragment createCompletedTab() {
        Bundle args = new Bundle();
        ordersFragmentCompleted = new OrdersFragment();
        ordersFragmentCompleted.setData(completedOrdersCt.getHeaderList(),completedOrdersCt.getOrderMap());
        args.putInt("TypeOfOrders", 2);
        ordersFragmentCompleted.setArguments(args);
        return ordersFragmentCompleted;
    }

    private Fragment createDeliveredTab() {
        Bundle args = new Bundle();
        ordersFragmentDelivered = new OrdersFragment();
        ordersFragmentDelivered.setData(deliveredOrdersCt.getHeaderList(),deliveredOrdersCt.getOrderMap());
        args.putInt("TypeOfOrders", 3);
        ordersFragmentDelivered.setArguments(args);
        return ordersFragmentDelivered;
    }

    private Fragment createCancelledTab() {
        Bundle args = new Bundle();
        ordersFragmentCancelled = new OrdersFragment();
        ordersFragmentCancelled.setData(cancelledOrdersCt.getHeaderList(), cancelledOrdersCt.getOrderMap());
        args.putInt("TypeOfOrders", 4);
        ordersFragmentCancelled.setArguments(args);
        return ordersFragmentCancelled;
    }

    @Override
    public void onStop() {
        super.onStop();
        if(orderCall != null) {
            orderCall.cancel();
        }
        getActivity().invalidateOptionsMenu();
    }
}