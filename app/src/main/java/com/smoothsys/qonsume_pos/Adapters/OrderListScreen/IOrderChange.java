package com.smoothsys.qonsume_pos.Adapters.OrderListScreen;

public interface IOrderChange {
    void updateOrderStatus(String orderStatus);

    void updateDetailStatus(int itemdId, int itemStatus, int itemPosition);
}
