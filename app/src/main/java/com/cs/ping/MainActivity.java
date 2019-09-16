package com.cs.ping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.cs.ping.adapter.MainFragmentPagerAdapter;
import com.cs.ping.event.CloseFloatingButtonEvent;
import com.google.android.material.tabs.TabLayout;

import org.greenrobot.eventbus.EventBus;


public class MainActivity extends AppCompatActivity {

    private MainFragmentPagerAdapter adapter;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private TabLayout.Tab pingTab;
    private TabLayout.Tab telnetTab;
    private TabLayout.Tab settingTab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();// 隐藏掉整个ActionBar
        }
        //初始化视图
        initViews();
    }
    private void initViews(){

        // 使用适配器将ViewPager与Fragment绑定在一起
        adapter = new MainFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.vp_container);
        mViewPager.setAdapter(adapter);

        //将TabLayout与ViewPager绑定在一起
        mTabLayout = findViewById(R.id.tabLayout);
        mTabLayout.setupWithViewPager(mViewPager);

        // 指定Tab的位置
        pingTab = mTabLayout.getTabAt(0);
        telnetTab = mTabLayout.getTabAt(1);
        settingTab = mTabLayout.getTabAt(2);



        //设置Tab的图标，假如不需要则把下面的代码删去
        pingTab.setIcon(R.drawable.ic_ping_fast);
        telnetTab.setIcon(R.drawable.ic_ping_telnet);
        settingTab.setIcon(R.drawable.ic_menu_setting);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                EventBus.getDefault().post(new CloseFloatingButtonEvent());

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }
}
