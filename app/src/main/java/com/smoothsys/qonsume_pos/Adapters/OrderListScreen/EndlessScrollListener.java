package com.smoothsys.qonsume_pos.Adapters.OrderListScreen;

import android.widget.AbsListView;

public class EndlessScrollListener implements AbsListView.OnScrollListener {

    private int visibleThreshold = 5;
    private int currentPage = 0;
    private int previousTotal = 0;
    private boolean loading = true;

    TableListAdapter.ITableInterface iTableInterface;

    public EndlessScrollListener() {
    }
    public EndlessScrollListener(int visibleThreshold, TableListAdapter.ITableInterface tableInterface) {
        this.visibleThreshold = visibleThreshold;
        iTableInterface = tableInterface;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
                currentPage++;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            // I load the next page of gigs using a background task,
            // but you can call any function here.
            iTableInterface.onGetMoreEntries();
            loading = true;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }
}