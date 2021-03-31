package com.smoothsys.qonsume_pos.DataDump;

import com.smoothsys.qonsume_pos.Models.OrderClasses.Order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Pontus on 2018-03-27.
 */

public class OrderListContainer {
    private List<String> headerList;
    private HashMap<String, Order> orderMap;

    public  OrderListContainer() {
        headerList = new ArrayList<>();
        orderMap = new HashMap<>();
    }

    public OrderListContainer(List<String> headers, HashMap<String, Order> orders) {
        headerList = headers;
        orderMap = orders;
    }

    public String getHeader(int i) {
        return headerList.get(i);
    }

    private void addHeader(String h) {
        headerList.add(h);
    }

    public void setHeaderList(List<String> h) {
        headerList = h;
    }

    public List<String> getHeaderList() {
        return headerList;
    }

    public void putOrder(String header, Order order) {
        headerList.add(header);
        orderMap.put(header,order);
    }

    public void addAllOrders(OrderListContainer ct) {
        headerList.addAll(ct.getHeaderList());
        orderMap.putAll(ct.orderMap);
    }

    public int getSize() {
        return headerList.size();
    }

    public void bringToFrontOfList(int i) {
        // Bring header to front of list

        String correctHeader = headerList.get(i);
        headerList.remove(i);
        headerList.add(0, correctHeader);

    }

    public Order getOrder(int i) {
        return orderMap.get(headerList.get(i));
    }


    public void setOrderMap(HashMap<String, Order> o) {
        orderMap = o;
    }

    public HashMap<String,Order> getOrderMap() {
        return orderMap;
    }


}
