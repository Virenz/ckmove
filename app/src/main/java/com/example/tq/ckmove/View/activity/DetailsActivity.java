package com.example.tq.ckmove.View.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.tq.ckmove.R;
import com.example.tq.ckmove.adapter.DetailAdapter;
import com.example.tq.ckmove.bean.DetailsBean;
import com.example.tq.ckmove.until.MyStringRequest;
import com.example.tq.ckmove.until.RequestQueueSingleton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tq on 2018/8/13.
 */

public class DetailsActivity extends AppCompatActivity {
    private static final String TAG = "DetailsActivity";
    @BindView(R.id.iv_da_img)
    ImageView mIvDaImg;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_updata)
    TextView mTvUpdata;
    @BindView(R.id.tv_time)
    TextView mTvTime;
    @BindView(R.id.tv_type)
    TextView mTvType;
    @BindView(R.id.da_recyView)
    RecyclerView mDaRecyView;
    private String mRequestUrl;
    private Matcher mMatcher;
    private List<DetailsBean> mData = new ArrayList<>();
    private GridLayoutManager mGridLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制竖屏
        ButterKnife.bind(this);
        initView();

        //Toast.makeText(this,requestUrl, Toast.LENGTH_SHORT).show();
        initData();
    }

    private void initView() {
        Intent intent = getIntent();
        mRequestUrl = intent.getStringExtra("requestUrl");
        String title = intent.getStringExtra("title");
        String img_url = intent.getStringExtra("img_url");
        mTvTitle.setText(title);
        Picasso.with(this).load(img_url).into(mIvDaImg);
        mGridLayoutManager = new GridLayoutManager(this,3);
        mDaRecyView.setLayoutManager(mGridLayoutManager);
    }

    private void initData() {

        RequestQueueSingleton queue = RequestQueueSingleton.getInstance(this);
        queue.requestAsyn(mRequestUrl, 0, null, new RequestQueueSingleton.ReqCallBack<String>() {
            /**
             * 响应成功
             *
             * @param result
             */
            @Override
            public void onReqSuccess(String result) {
                //Log.e(TAG, response );
                String regEx = "<a href=\"/(Player_.+?_9_23.+?)\" >(.+?)</a></li>";
                String regEx_info = ">(([\\u4e00-\\u9fa5\\s]*)：([\\[\\]\\w]+))</";
                //<a href="/Player_73525_23_14_1.html">BD中英双字</a>
                /*
                <div class="movie"><ul><div class="img"><div class="img-box-2"></div><img src="http://88.meenke.com/img_buyhi/201805/2018052876045761.jpg" alt="帝王攻略" border="0" onerror="this.src='/nopic.gif'"></div><h1>帝王攻略</h1><li>更新至：[17]</li><li>年　代：2018</li><li>类　型：<a href="/dhp_lianzai/Index.html" target="_blank">动画连载</a></li><li class="cksc"><a id="shoucang" href="#sc" onclick="shoucang('72893')">收藏</a></li></ul></div>
                 */

                Pattern pattern = Pattern.compile(regEx_info);
                mMatcher = pattern.matcher(result);
                ArrayList<String> infos = new ArrayList<>();
                while (mMatcher.find()) {
                    infos.add(mMatcher.group(1));
                }
                mTvUpdata.setText(infos.get(0));
                mTvTime.setText(infos.get(1));
                mTvType.setText("类    型：");

                pattern = Pattern.compile(regEx);
                mMatcher = pattern.matcher(result);
                /*new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (mMatcher.find()) {
                            //Toast.makeText(DetailsActivity.this, mMatcher.group() + "", Toast.LENGTH_SHORT).show();
                            //Log.e(TAG, mMatcher.group());
                            //Log.e(TAG, mMatcher.group(1));
                            //Log.e(TAG, mMatcher.group(2));
                            String url = mMatcher.group(1);
                            Log.e(TAG, url);
                            String title = mMatcher.group(2);
                            //Log.e(TAG, mMatcher.group(3));
                            //Log.e(TAG, mMatcher.group(4));
                            DetailsBean dataBean = new DetailsBean();
                            dataBean.setTitle(title);
                            dataBean.setUrl(url);
                            mData.add(dataBean);
                        }
                    }
                }).start();*/
                while (mMatcher.find()) {
                    //Toast.makeText(DetailsActivity.this, mMatcher.group() + "", Toast.LENGTH_SHORT).show();
                    //Log.e(TAG, mMatcher.group());
                    //Log.e(TAG, mMatcher.group(1));
                    //Log.e(TAG, mMatcher.group(2));
                    String url = mMatcher.group(1);
                    Log.e(TAG, url);
                    String title = mMatcher.group(2);
                    //Log.e(TAG, mMatcher.group(3));
                    //Log.e(TAG, mMatcher.group(4));
                    DetailsBean dataBean = new DetailsBean();
                    dataBean.setTitle(title);
                    dataBean.setUrl(url);
                    mData.add(dataBean);
                }
                DetailAdapter adapter = new DetailAdapter(DetailsActivity.this, mData, mGridLayoutManager);
                mDaRecyView.setAdapter(adapter);
                adapter.setItemClickListener(new DetailAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        String url = "http://m.ckck.vip/" + mData.get(position).getUrl();
                        String title = mTvTitle.getText().toString();
                        String episode = mData.get(position).getTitle();
                        Intent intent = new Intent(DetailsActivity.this, VideoActivity.class);
                        intent.putExtra("url", url);
                        intent.putExtra("title", title);
                        intent.putExtra("episode", episode);
                        startActivity(intent);
                    }
                });
            }

            /**
             * 响应失败
             *
             * @param errorMsg
             */
            @Override
            public void onReqFailed(String errorMsg) {
                Toast.makeText(DetailsActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }


            /*RequestQueue queue = Volley.newRequestQueue(this);
        MyStringRequest stringRequest = new MyStringRequest(mRequestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.e(TAG, response );
                String regEx = "<a href=\"/(Player_.+?_9_23.+?)\" >(.+?)</a></li>";
                String regEx_info = ">(([\\u4e00-\\u9fa5\\s]*)：([\\[\\]\\w]+))</";
                //<a href="/Player_73525_23_14_1.html">BD中英双字</a>
                *//*
                <div class="movie"><ul><div class="img"><div class="img-box-2"></div><img src="http://88.meenke.com/img_buyhi/201805/2018052876045761.jpg" alt="帝王攻略" border="0" onerror="this.src='/nopic.gif'"></div><h1>帝王攻略</h1><li>更新至：[17]</li><li>年　代：2018</li><li>类　型：<a href="/dhp_lianzai/Index.html" target="_blank">动画连载</a></li><li class="cksc"><a id="shoucang" href="#sc" onclick="shoucang('72893')">收藏</a></li></ul></div>
                 *//*

                Pattern pattern = Pattern.compile(regEx_info);
                mMatcher = pattern.matcher(response);
                ArrayList<String> infos = new ArrayList<>();
                while (mMatcher.find()){
                    infos.add(mMatcher.group(1));
                }
                mTvUpdata.setText(infos.get(0));
                mTvTime.setText(infos.get(1));
                mTvType.setText("类    型：");

                pattern = Pattern.compile(regEx);
                mMatcher = pattern.matcher(response);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (mMatcher.find()) {
                            //Toast.makeText(DetailsActivity.this, mMatcher.group() + "", Toast.LENGTH_SHORT).show();
                            //Log.e(TAG, mMatcher.group());
                            //Log.e(TAG, mMatcher.group(1));
                            //Log.e(TAG, mMatcher.group(2));
                            String url = mMatcher.group(1);
                            Log.e(TAG, url);
                            String title = mMatcher.group(2);
                            //Log.e(TAG, mMatcher.group(3));
                            //Log.e(TAG, mMatcher.group(4));
                            DetailsBean dataBean = new DetailsBean();
                            dataBean.setTitle(title);
                            dataBean.setUrl(url);
                            mData.add(dataBean);
                        }
                    }
                }).start();
                DetailAdapter adapter = new DetailAdapter(DetailsActivity.this,mData,mGridLayoutManager);
                mDaRecyView.setAdapter(adapter);
                adapter.setItemClickListener(new DetailAdapter.OnItemClickListener() {
                   @Override
                   public void onItemClick(int position) {
                       String url = "http://m.yiybb.com/" + mData.get(position).getUrl();
                       String title = mTvTitle.getText().toString();
                       String episode = mData.get(position).getTitle();
                       Intent intent = new Intent(DetailsActivity.this, VideoActivity.class);
                       intent.putExtra("url",url);
                       intent.putExtra("title", title);
                       intent.putExtra("episode", episode);
                       startActivity(intent);
                   }
               });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.getMessage(), error);
                Toast.makeText(DetailsActivity.this, "网络不稳定!", Toast.LENGTH_SHORT).show();

            }
        });
        queue.add(stringRequest);*/
        });
    }
}
