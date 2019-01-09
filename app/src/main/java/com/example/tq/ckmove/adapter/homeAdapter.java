package com.example.tq.ckmove.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tq.ckmove.R;
import com.example.tq.ckmove.bean.DataBean;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tq on 2018/8/9.
 */

public class homeAdapter extends RecyclerView.Adapter<homeAdapter.HomeHolder> implements View.OnClickListener {
    private static final String TAG = "homeAdapter";

    private final List<DataBean> mData;
    private final Context mContext;
    private final GridLayoutManager mGridLayoutManager;
    private OnItemClickListener mItemClickListener;


    public homeAdapter(Context fragment, List<DataBean> mData, GridLayoutManager gridLayoutManager) {
        this.mContext = fragment;
        this.mData = mData;
        mGridLayoutManager = gridLayoutManager;
    }

    @Override
    public HomeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recyview, parent, false);
        HomeHolder holder = new HomeHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(HomeHolder holder, int position) {
        ViewGroup.LayoutParams params = holder.mll.getLayoutParams();
        params.width = mGridLayoutManager.getWidth() / mGridLayoutManager.getSpanCount();
        params.height = mGridLayoutManager.getHeight()/ 3;
        holder.setData(mData.get(position));
        holder.itemView.setTag(position);


    }

    @Override
    public int getItemCount() {
        if (mData != null){
            return mData.size();
        }else {
            return 0;
        }
    }

    @Override
    public void onClick(View v) {
        if (mItemClickListener!=null){
            mItemClickListener.onItemClick((Integer)v.getTag());
        }
    }

    public class HomeHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img)
        ImageView mImg;
        @BindView(R.id.name)
        TextView mName;
        @BindView(R.id.score)
        TextView mScore;
        @BindView(R.id.ll)
        LinearLayout mll;

        public HomeHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

        }

        public void setData(DataBean data) {
            Picasso.with(mContext).load(data.getDataImg()).into(mImg);
            mName.setText(data.getDataName());
            mScore.setText(data.getDataScore());
            /*Log.e(TAG, data.getDataImg());
            Log.e(TAG, mName+"");
            Log.e(TAG, mScore+"");*/
        }
    }
    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }
}
