package com.smoothsys.qonsume_pos.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;

import com.smoothsys.qonsume_pos.FragmentsAndActivities.CategoryFragment;
import com.smoothsys.qonsume_pos.Models.RestaurantClasses.Category;

import java.util.List;

/**
 * Created by Pontu on 2017-07-28.
 */

public class DynamicPagerAdapter extends FragmentStatePagerAdapter
{

    private List<Category> mCategories;
    private BadgeUpdateInterface buttonListener;
    private static SwipeRefreshLayout refreshLayout;
    public  DynamicPagerAdapter(List<Category> categories, FragmentManager fm, BadgeUpdateInterface badgeUpdateInterface, SwipeRefreshLayout rl) {
        super(fm);
        mCategories = categories;
        buttonListener = badgeUpdateInterface;
        refreshLayout = rl;
    }

    public void setData(List<Category> categories) {
        mCategories = categories;
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object o) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        return CategoryFragment.newInstance(mCategories.get(position), buttonListener, refreshLayout);

    }
    @Override
    public int getCount() {
        return mCategories.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mCategories.get(position).getName();
    }

    public void add(int position, Category category) {
        mCategories.add(position,category);
        notifyDataSetChanged();
    }
}