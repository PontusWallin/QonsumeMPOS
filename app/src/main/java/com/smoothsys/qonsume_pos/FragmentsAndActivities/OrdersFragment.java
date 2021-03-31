package com.smoothsys.qonsume_pos.FragmentsAndActivities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.smoothsys.qonsume_pos.Adapters.OrderListScreen.CanceledDetailsAdapter;
import com.smoothsys.qonsume_pos.Adapters.OrderListScreen.CompleteDetailsAdapter;
import com.smoothsys.qonsume_pos.Adapters.OrderListScreen.DeliveredDetailsAdapter;
import com.smoothsys.qonsume_pos.Adapters.OrderListScreen.HeaderCardDetailsListAdapter;
import com.smoothsys.qonsume_pos.Adapters.OrderListScreen.IRefresh;
import com.smoothsys.qonsume_pos.Adapters.OrderListScreen.OrderHeaderCardAdapter;
import com.smoothsys.qonsume_pos.Adapters.OrderListScreen.PendingDetailsAdapter;
import com.smoothsys.qonsume_pos.DataDump.ItemStates;
import com.smoothsys.qonsume_pos.Models.OrderClasses.Detail;
import com.smoothsys.qonsume_pos.Models.OrderClasses.Order;
import com.smoothsys.qonsume_pos.R;
import com.smoothsys.qonsume_pos.StateManagement.ScreenSwitcher;
import com.smoothsys.qonsume_pos.Utilities.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Pontus on 2017-06-06.
 */

public class OrdersFragment extends Fragment implements OrderHeaderCardAdapter.IPopulateLists, IRefresh {

  private RecyclerView cardRecyclerView;
  private RecyclerView.LayoutManager layoutManager;
  private OrderHeaderCardAdapter headerCardAdapter;

  private ListView detailsList;

  private List<String> allHeaders;
  private HashMap<String, Order> allOrders;

  private TypeOfOrders myType;
  OrderTabsFragment parentFrag;
  SwipeRefreshLayout swipeRefreshLayout;
  @Override
  public void selectByPosition(Order order, int position) {

    if(getContext() == null) {
      return;
    }

    selectCard(position);
    populateDetailsList(order);
  }

  public void selectByOrder(Order order) {

    if(getContext() == null) {
      return;
    }
    populateDetailsList(order);
  }

  private void selectCard(final int position) {

    cardRecyclerView.post(() -> cardRecyclerView.smoothScrollToPosition(position));
  }

  private void populateDetailsList(Order order) {

    // get rid of "... and more" row
    List<Detail> details = order.getDetails();

    for(int i = 0; i < details.size(); i++) {
      Detail d = details.get(i);
      String andMore = getContext().getResources().getString(R.string.andmore);
      if(d.getItem_name().equals(andMore)) {
        details.remove(i);
      }
    }

    order.setDetails(details);

    if(myType == TypeOfOrders.PENDING) {
      populatePendingList(order);
    } else if(myType == TypeOfOrders.COMPLETED) {
      populateCompletedList(order);
    } else if(myType == TypeOfOrders.DELIVERED) {
      populateDeliveredList(order);
    } else if (myType == TypeOfOrders.CANCELLED) {
      populateCancelledList(order);
    }
  }

  @Override
  public void reSelectOrder(Order o) {
    selectByOrder(o);
  }

  public enum TypeOfOrders {
    PENDING, COMPLETED, DELIVERED, CANCELLED
  }

  public void updateAndScrollToPosition(int position){
    headerCardAdapter.notifyDataSetChanged();

    if(cardRecyclerView == null) {
      return;
    }

    headerCardAdapter.selectedPosition = position;

    RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getContext()) {
      @Override
      protected int getHorizontalSnapPreference() {
        return LinearSmoothScroller.SNAP_TO_START;
      }
    };

    smoothScroller.setTargetPosition(position);
    cardRecyclerView.getLayoutManager().startSmoothScroll(smoothScroller);
  }

  public OrdersFragment() {}

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    int typeOfOrders = getArguments().getInt("TypeOfOrders");

    switch(typeOfOrders) {
      case 1:
        myType = TypeOfOrders.PENDING;
        break;

      case 2:
        myType = TypeOfOrders.COMPLETED;
        break;

      case 3:
        myType = TypeOfOrders.DELIVERED;
        break;
      case 4:
        myType = TypeOfOrders.CANCELLED;
        break;
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    parentFrag = (OrderTabsFragment) getActivity().getSupportFragmentManager().findFragmentByTag("OrderTabsFragment");

    View view = inflater.inflate(R.layout.orders_layout, container, false);

    swipeRefreshLayout = view.findViewById(R.id.swipe_container);
    swipeRefreshLayout.setOnRefreshListener(() -> parentFrag.setupPagerTitleStrip(swipeRefreshLayout));

    return view;
  }

  @Override
  public void onStart() {
    super.onStart();
  }

  public void onResume() {
    ((MainActivity)getActivity()).setTitleBarText(getString(R.string.orderList_name));

    detailsList = getView().findViewById(R.id.detailsList);
    updateEverything();

    super.onResume();
  }

  public void updateEverything() {

    if(getView() == null) {
      return;
    }

    setUpHeaderCards();
    initData();
    addOnMoreToCards();

    if(headerCardAdapter != null) {
      headerCardAdapter.notifyDataSetChanged();
    }
  }

  public void setUpHeaderCards() {

    // == Set up recycler view ==
    cardRecyclerView = getView().findViewById(R.id.order_header_cards);
    cardRecyclerView.setHasFixedSize(true);

    // Layout
    layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false);
    cardRecyclerView.setLayoutManager(layoutManager);

    // Adapter
    cardRecyclerView.setAdapter(headerCardAdapter);

    // 1. Set on Scroll event
    cardRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

        addOnMoreToCards();

        super.onScrolled(recyclerView, dx, dy);
      }
    });
  }

  private void addOnMoreToCards() {
    LinearLayoutManager manager = (LinearLayoutManager) cardRecyclerView.getLayoutManager();
    int firstVisible = manager.findFirstVisibleItemPosition();
    int lastVisible = manager.findLastVisibleItemPosition();

    for(int i = firstVisible; i < lastVisible; i++) {

      View view = manager.findViewByPosition(i);
      ListView listView = view.findViewById(R.id.header_detailsList);
      headerCardAdapter.editListIfTooManyItems(listView, (HeaderCardDetailsListAdapter) listView.getAdapter());
    }
  }

  private void populateCancelledList(Order order) {
    List<Detail> details = order.getDetails();

    if(!showAll) {
      details = getDetailsOfStatus(details, ItemStates.CANCELLED);
    }

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

    CanceledDetailsAdapter adapter = new CanceledDetailsAdapter(details,order,getContext(),prefs, this);
    detailsList.setAdapter(adapter);
    adapter.notifyDataSetChanged();
  }

  private void populateDeliveredList(Order order) {

    List<Detail> details = order.getDetails();

    if(!showAll) {
      details = getDetailsOfStatus(details, ItemStates.DELIVERED);
    }

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
    DeliveredDetailsAdapter deliveredDetailsAdapter = new DeliveredDetailsAdapter(details, order, getContext(),
            prefs, new ScreenSwitcher(R.id.fragment_container, getActivity()), this);
    detailsList.setAdapter(deliveredDetailsAdapter);
    deliveredDetailsAdapter.notifyDataSetChanged();
  }

  boolean showAll = Config.DEBUG_SHOW_ALL_DETAILS;
  private void populateCompletedList(Order order) {

    List<Detail> details = order.getDetails();
    if(!showAll) {
      details = getDetailsOfStatus(details, ItemStates.COMPLETED);
    }

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
    CompleteDetailsAdapter completeDetailsAdapter = new CompleteDetailsAdapter(details, order, getContext(), prefs, this);
    detailsList.setAdapter(completeDetailsAdapter);
    completeDetailsAdapter.notifyDataSetChanged();
  }

  public void populatePendingList(Order order) {

    List<Detail> details = order.getDetails();
    if(!showAll) {
      details = getDetailsOfStatus(details, ItemStates.PENDING);
    }

    PendingDetailsAdapter pendingDetailsAdapter;
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
    pendingDetailsAdapter = new PendingDetailsAdapter(
            details, order, getContext(),
            prefs, new ScreenSwitcher(R.id.fragment_container, getActivity()), this);
    detailsList.setAdapter(pendingDetailsAdapter);
    pendingDetailsAdapter.notifyDataSetChanged();
  }

  private List<Detail> getDetailsOfStatus(List<Detail> details, int state) {

    List<Detail> detailsToKeep = new ArrayList<>();
    // remove details that are not "Pending"
    for (Detail detail: details) {

      if(detail.getItem_state() == state) {
        detailsToKeep.add(detail);
      }
    }
    return detailsToKeep;
  }

  public HashMap<String,Order> getData() {
    if(allOrders == null || allOrders.size() < 1) {
      return new HashMap<>();
    }
    return allOrders;
  }

  public void initData() {

    String sortingEmail = getActivity() != null ? ((MainActivity) getActivity()).emailForOrderSorting : "";
    String sortingQuery = getActivity() != null ? ((MainActivity) getActivity()).searchQuery : "";

    if(!sortingEmail.equals("")) {
      sortExistingOrdersByEmail(sortingEmail);
    } else if(!sortingQuery.equals("")) {
      sortExistingOrdersByQuery(sortingQuery);
    } else {
      sortCardsById();
    }
  }

  public void sortCardsById() {

    if(allHeaders == null) {
      return;
    }

    Collections.sort(allHeaders, new idComparator());

    if(headerCardAdapter == null) {
      headerCardAdapter = new OrderHeaderCardAdapter(getContext(), allHeaders, allOrders, this);
    } else {
      headerCardAdapter.setData(allHeaders, allOrders);
    }

    if(cardRecyclerView == null) {
      return;
    }
    cardRecyclerView.setAdapter(headerCardAdapter);
  }

  private void sortExistingOrdersByQuery(String query){
    List<String> sortedHeaders = new ArrayList<>();
    HashMap<String, Order> sortedHashMap = new HashMap<>();

    //Initialize header
    for(int i = 0; i < allHeaders.size(); i++) {

      String currentHeader = allHeaders.get(i);

      if(!isValidHeaderString(currentHeader)) {
        continue;
      }

      String headerId = grabIdFromHeader(currentHeader);

      if (headerId.trim().contains(query)) {
        sortedHeaders.add(currentHeader);
        sortedHashMap.put(currentHeader, allOrders.get(currentHeader));
      }
    }

    headerCardAdapter = new OrderHeaderCardAdapter(getContext(),sortedHeaders, sortedHashMap, this);
    if(cardRecyclerView != null) {
      cardRecyclerView.setAdapter(headerCardAdapter);
    }
    headerCardAdapter.notifyDataSetChanged();
  }

  private void sortExistingOrdersByEmail(String email) {

    List<String> sortedHeaders = new ArrayList<>();
    HashMap<String, Order> sortedHashMap = new HashMap<>();

    //Initialize header
    for(int i = 0; i < allHeaders.size(); i++) {

      String currentHeader = allHeaders.get(i);

      if(!isValidHeaderString(currentHeader)) {
        continue;
      }

      String headerEmail = grabEmailFromHeaderString(currentHeader);

      if (headerEmail.trim().equals(email)) {
        sortedHeaders.add(currentHeader);
        sortedHashMap.put(currentHeader, allOrders.get(currentHeader));
      }
    }

    headerCardAdapter = new OrderHeaderCardAdapter(getContext(),sortedHeaders, sortedHashMap, this);
    if(cardRecyclerView != null) {
      cardRecyclerView.setAdapter(headerCardAdapter);
    }
    headerCardAdapter.notifyDataSetChanged();
  }

  public void setData(List<String> header, HashMap<String,Order> map) {
    allHeaders = header;
    allOrders = map;
  }

  // Split header string by ".". Example: 100.JohnDoe@email.com -> 100 - JohnDoe@email - com
  private boolean isValidHeaderString(String header) {
    String comps[] = header.split("\\.");
    return comps.length >= 3;
  }

  private String grabEmailFromHeaderString(String header) {
    String comps[] = header.split("\\.");
    return comps[1] + "." + comps[2];
  }

  private String grabIdFromHeader(String header) {
    String comps[] = header.split("\\.");
    return comps[0];
  }

  private class idComparator implements Comparator<String> {

    public int compare(String strA, String strB) {

      //Find hashmap value for strA and get its order object
      //Find hashmap value for strB and get its order object
      //compare date on order object?
      Order order1 = allOrders.get(strA);
      Order order2 = allOrders.get(strB);

      if(order1 == null || order2 == null) {
        return 0;
      }

      int id1 = Integer.parseInt(order1.getId());
      int id2 = Integer.parseInt(order2.getId());
      return id2 - id1;
    }
  }
}