package com.example.tq.ckmove.View.fragment;

import android.view.View;

/**
 * Created by tq on 2018/8/9.
 */

public class MoveFragment extends HomeFragment implements View.OnClickListener {

    @Override
    protected void intitView() {
        hostUrl = "http://m.ckck.vip/dianying/";
        super.intitView();
        mThisPage = 1;
        listTpye = "List_15_";
        mTvThisPage.setVisibility(View.VISIBLE);


    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected String setTitle() {
        return "电影";
    }

    @Override
    protected void setViibilly() {
        page.setVisibility(View.VISIBLE);
    }


}
