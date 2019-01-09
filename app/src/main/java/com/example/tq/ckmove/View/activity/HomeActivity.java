package com.example.tq.ckmove.View.activity;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.tq.ckmove.R;
import com.example.tq.ckmove.View.fragment.CartoonFragment;
import com.example.tq.ckmove.View.fragment.HomeFragment;
import com.example.tq.ckmove.View.fragment.MoveFragment;
import com.example.tq.ckmove.View.fragment.TVplayFragment;
import com.hjm.bottomtabbar.BottomTabBar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tq on 2018/8/9.
 */

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.tabBar)
    BottomTabBar mTabBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制竖屏
        ButterKnife.bind(this);
        mTabBar.init(getSupportFragmentManager())
                .setImgSize(50, 50)
                .setFontSize(10)
                .setTabPadding(4, 6, 10)
                .setChangeColor(Color.RED, Color.DKGRAY)
                .addTabItem("首页", R.mipmap.ic_launcher_round, HomeFragment.class)
                .addTabItem("电影", R.mipmap.ic_launcher_round, MoveFragment.class)
                .addTabItem("电视剧", R.mipmap.ic_launcher_round, TVplayFragment.class)
                .addTabItem("动漫", R.mipmap.ic_launcher_round, CartoonFragment.class);
    }


}
