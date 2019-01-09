package com.example.tq.ckmove.View.fragment;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.tq.ckmove.R;
import com.example.tq.ckmove.View.activity.DetailsActivity;
import com.example.tq.ckmove.adapter.homeAdapter;
import com.example.tq.ckmove.bean.DataBean;
import com.example.tq.ckmove.searchview.ICallBack;
import com.example.tq.ckmove.searchview.SearchView;
import com.example.tq.ckmove.searchview.bCallBack;
import com.example.tq.ckmove.until.MyStringRequest;
import com.example.tq.ckmove.until.RequestQueueSingleton;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.Unbinder;

import static android.content.ContentValues.TAG;

/**
 * Created by tq on 2018/8/9.
 */

public class HomeFragment extends BaseFragment implements View.OnClickListener {
    // 初始化搜索框变量
    @BindView(R.id.search_view)
    SearchView searchView;
    @BindView(R.id.ll_page)
    LinearLayout page;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.up)
    Button mUp;
    @BindView(R.id.next)
    Button mNext;
    @BindView(R.id.tv_thisPage)
    TextView mTvThisPage;
    @BindView(R.id.home_page)
    Button mHomePage;
    Unbinder unbinder;
    private List<DataBean> mData = new ArrayList<>();
    private Matcher mMatcher;
    private GridLayoutManager mGridLayoutManager;
    public int mThisPage = 1;
    public String hostUrl = "http://m.ckck.vip";
    public String requsetUrl = "";
    public String listTpye = "";
    private homeAdapter mHomeAdapter;

    @Override
    protected void intitView() {
        setViibilly();
        requsetUrl = hostUrl;
        title.setText(setTitle());
        mTvThisPage.setVisibility(View.GONE);
        mGridLayoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mUp.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mHomePage.setOnClickListener(this);

        // 4. 设置点击键盘上的搜索按键后的操作（通过回调接口）
        // 参数 = 搜索框输入的内容
        searchView.setOnClickSearch(new ICallBack() {
            @Override
            public void SearchAciton(String string) {
                System.out.println("我收到了" + string);
            }
        });

        // 5. 设置点击返回按键后的操作（通过回调接口）
        searchView.setOnClickBack(new bCallBack() {
            @Override
            public void BackAciton() {

            }
        });
    }

    protected void setViibilly() {
        page.setVisibility(View.GONE);
    }

    protected String setTitle() {
        return "最近更新";
    }

    @Override
    protected void initData() {
        //RequestQueue queue = Volley.newRequestQueue(getActivity());
        RequestQueueSingleton queue = RequestQueueSingleton.getInstance(this.getActivity());
        queue.requestAsyn(getHosturl(), 0, null, new RequestQueueSingleton.ReqCallBack<String>() {
            /**
             * 响应成功
             *
             * @param result
             */
            @Override
            public void onReqSuccess(String result) {
                String regEx = "<li><div class=li-box><div class=img-box></div><a href=\"(.+?)\"><img src=\"(.+?)\" onerror=\".+?\"><span class=back></span><span>(.+?)</span></div><P><a href=\".+?\" target=\"_blank\">(.+?)</a></P></li>";
                //<li><div class=li-box><div class=img-box></div><a href="/dhp_lianzai/54866.html"><img src="http://88.meenke.com/img_buyhi/201704/2017040573906281.jpg" onerror="this.src='/nopic.gif'"><span class=back></span><span>8.2</span></div><P><a href="/dhp_lianzai/54866.html" target="_blank">²©ÈË´«-»ðÓ°¡­</a></P></li>
                //<div class=li-box>.+?<a href="(.+?)"><img src="(.+?)".+?>(.+?)</a></P>
                Pattern pattern = Pattern.compile(regEx);
                mMatcher = pattern.matcher(result);
                if (mData != null) {
                    mData.clear();
                }
                while (mMatcher.find()) {
                    //Log.e(TAG, matcher.group());
                    Log.e(TAG, mMatcher.group(1));
                    Log.e(TAG, mMatcher.group(2));
                    Log.e(TAG, mMatcher.group(3));
                    Log.e(TAG, mMatcher.group(4));
                    DataBean dataBean = new DataBean();
                    dataBean.setDataNetWork(mMatcher.group(1));
                    dataBean.setDataImg(mMatcher.group(2));
                    dataBean.setDataName(mMatcher.group(4));
                    dataBean.setDataScore(mMatcher.group(3));
                    mData.add(dataBean);
                }
               /* new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (mMatcher.find()) {
                            //Log.e(TAG, matcher.group());
                            Log.e(TAG, mMatcher.group(1));
                            Log.e(TAG, mMatcher.group(2));
                            Log.e(TAG, mMatcher.group(3));
                            Log.e(TAG, mMatcher.group(4));
                            DataBean dataBean = new DataBean();
                            dataBean.setDataNetWork(mMatcher.group(1));
                            dataBean.setDataImg(mMatcher.group(2));
                            dataBean.setDataName(mMatcher.group(4));
                            dataBean.setDataScore(mMatcher.group(3));
                            mData.add(dataBean);
                        }
                    }
                }).start();*/

                if (mHomeAdapter == null) {
                    mHomeAdapter = new homeAdapter(getContext(), mData, mGridLayoutManager);
                }
                mRecyclerView.setAdapter(mHomeAdapter);
                mHomeAdapter.notifyDataSetChanged();
                mTvThisPage.setText(""+mThisPage);
                Toast.makeText(getContext(), "第" + mThisPage + "页", Toast.LENGTH_SHORT).show();
                mHomeAdapter.setItemClickListener(new homeAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        String url = mData.get(position).getDataNetWork();
                        String title = mData.get(position).getDataName();
                        String img_url = mData.get(position).getDataImg();
                        String requestUrl = getHosturl() + url;
                        //Toast.makeText(getContext(), requestUrl, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContext(), DetailsActivity.class);
                        intent.putExtra("title", title);
                        intent.putExtra("img_url", img_url);
                        intent.putExtra("requestUrl", requestUrl);
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
                Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
        /*MyStringRequest stringRequest = new MyStringRequest(getHosturl(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String regEx = "<li><div class=li-box><div class=img-box></div><a href=\"(.+?)\"><img src=\"(.+?)\" onerror=\".+?\"><span class=back></span><span>(.+?)</span></div><P><a href=\".+?\" target=\"_blank\">(.+?)</a></P></li>";
                //<li><div class=li-box><div class=img-box></div><a href="/dhp_lianzai/54866.html"><img src="http://88.meenke.com/img_buyhi/201704/2017040573906281.jpg" onerror="this.src='/nopic.gif'"><span class=back></span><span>8.2</span></div><P><a href="/dhp_lianzai/54866.html" target="_blank">²©ÈË´«-»ðÓ°¡­</a></P></li>
                //<div class=li-box>.+?<a href="(.+?)"><img src="(.+?)".+?>(.+?)</a></P>
                Pattern pattern = Pattern.compile(regEx);
                mMatcher = pattern.matcher(response);
                if (mData != null) {
                    mData.clear();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (mMatcher.find()) {
                            //Log.e(TAG, matcher.group());
                            *//*Log.e(TAG, mMatcher.group(1));
                            Log.e(TAG, mMatcher.group(2));
                            Log.e(TAG, mMatcher.group(3));
                            Log.e(TAG, mMatcher.group(4));*//*
                            DataBean dataBean = new DataBean();
                            dataBean.setDataNetWork(mMatcher.group(1));
                            dataBean.setDataImg(mMatcher.group(2));
                            dataBean.setDataName(mMatcher.group(4));
                            dataBean.setDataScore(mMatcher.group(3));
                            mData.add(dataBean);
                        }
                    }
                }).start();
                if (mHomeAdapter == null) {
                    mHomeAdapter = new homeAdapter(getContext(), mData, mGridLayoutManager);
                }
                mRecyclerView.setAdapter(mHomeAdapter);
                mHomeAdapter.notifyDataSetChanged();
                mTvThisPage.setText("第"+mThisPage+"页");
                Toast.makeText(getContext(), "第" + mThisPage + "页", Toast.LENGTH_SHORT).show();
                mHomeAdapter.setItemClickListener(new homeAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        String url = mData.get(position).getDataNetWork();
                        String title = mData.get(position).getDataName();
                        String img_url = mData.get(position).getDataImg();
                        String requestUrl = getHosturl() + url;
                        //Toast.makeText(getContext(), requestUrl, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContext(), DetailsActivity.class);
                        intent.putExtra("title", title);
                        intent.putExtra("img_url", img_url);
                        intent.putExtra("requestUrl", requestUrl);
                        startActivity(intent);
                    }
                });

                //Log.e(TAG, response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.getMessage(), error);
                mThisPage--;
                Toast.makeText(getContext(), "网络不稳定!加载失败!请稍后重试!", Toast.LENGTH_SHORT).show();

            }
        });*/
    }

    protected String getHosturl() {
        return requsetUrl;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_home;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.up:
                mThisPage--;
                if (mThisPage > 1) {
                    requsetUrl = hostUrl + listTpye + mThisPage + ".html";
                    initData();
                }
                break;
            case R.id.next:
                mThisPage++;
                if (mThisPage > 1) {
                    requsetUrl = hostUrl + listTpye + mThisPage + ".html";
                    initData();
                }
                break;
            case R.id.home_page:
                mThisPage = 1;
                requsetUrl = hostUrl;
                initData();
                break;
            default:
                break;
        }
    }

}
