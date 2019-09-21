package com.momodupi.piggybank;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabFragmentAdapter extends FragmentPagerAdapter {
    private Context context;
    private Robot robot;

    TabFragmentAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        this.context = context;
        this.robot =  new Robot(this.context, DatabaseHelper.BOOKNAME);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MonthFragment(this.context, this.robot);
            case 1:
                return new YearFragment(this.context, this.robot);
            case 2:
                return new OthersFragment(this.context, this.robot);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return this.context.getString(R.string.tabmonth);
            case 1:
                return this.context.getString(R.string.tabyear);
            case 2:
                return this.context.getString(R.string.tabtype);
            default:
                return null;
        }
    }
}
