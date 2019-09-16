package com.cs.ping.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.cs.ping.fragment.PingFragment;
import com.cs.ping.fragment.SettingFragment;
import com.cs.ping.fragment.TelnetFragment;


public class MainFragmentPagerAdapter extends FragmentPagerAdapter {

    private String[] mTitles = new String[]{"Ping", "Telnet", "设置"};

    public MainFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return new TelnetFragment();
            case 2:
                return new SettingFragment();
            default:
                return new PingFragment();
        }

    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    // ViewPager与TabLayout绑定后，这里获取到PageTitle就是Tab的Text
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
