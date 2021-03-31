package com.smoothsys.qonsume_pos.DataDump;

import com.smoothsys.qonsume_pos.Models.OrderClasses.Order;


public class OrderManager {

    // This object stores all orders ==
    private OrderListContainer allOrders;

    // This stores active orders - for example, all orders AFTER a search filter
    private OrderListContainer activeOrders;

    public OrderManager() {
        allOrders = new OrderListContainer();
        activeOrders = new OrderListContainer();
    }

    public void sortActiveOrdersByHeaderString(String query) {

        activeOrders = new OrderListContainer();
        for(int i = 0; i< allOrders.getSize(); i++) {

            Order cOrder = allOrders.getOrder(i);
            String header = allOrders.getHeaderList().get(i);

            if(!isValidHeaderString(header)) {
                continue;
            }

            String headerEmail = grabEmailFromHeaderString(header);
            if (headerEmail.trim().equals(query)) {
                activeOrders.putOrder(header,cOrder);
            }
        }
    }

    public void resetFiltering() {
        activeOrders = allOrders;
    }

    private String grabEmailFromHeaderString(String header) {

        String comps[] = header.split("\\.");

        return comps[1] + "." + comps[2];
    }


    private boolean isValidHeaderString(String header) {
        String comps[] = header.split("\\.");
        return comps.length >= 3;
    }

    public void putOrder(String header, Order order) {
        allOrders.putOrder(header,order);
        activeOrders.putOrder(header,order);
    }

    public OrderListContainer getAllOrders() {
        return allOrders;
    }

    public void setAllOrders(OrderListContainer allOrders) {
        this.allOrders = allOrders;
    }

    public OrderListContainer getActiveOrders() {
        return activeOrders;
    }

    public void setActiveOrders(OrderListContainer activeOrders) {
        this.activeOrders = activeOrders;
    }

    public void addToActiveOrders(OrderListContainer orders) {
        activeOrders.addAllOrders(orders);
    }
}
