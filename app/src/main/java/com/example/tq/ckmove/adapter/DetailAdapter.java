package com.example.tq.ckmove.adapter;

import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.tq.ckmove.R;
import com.example.tq.ckmove.View.activity.DetailsActivity;
import com.example.tq.ckmove.bean.DetailsBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tq on 2018/8/16.
 */

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.DetailHolder> implements View.OnClickListener {


    private final DetailsActivity activity;
    private final List<DetailsBean> mData;
    private final GridLayoutManager gridLayoutManager;
    private OnItemClickListener mItemClickListener;


    public DetailAdapter(DetailsActivity activity, List<DetailsBean> data, GridLayoutManager gridLayoutManager) {
        this.activity = activity;
        this.mData = data;
        this.gridLayoutManager = gridLayoutManager;
    }

    @Override
    public DetailHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_datail, parent, false);
        DetailHolder holder = new DetailHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(DetailHolder holder, int position) {
        ViewGroup.LayoutParams params = holder.mLayout.getLayoutParams();
        params.height = gridLayoutManager.getHeight()/4;
        holder.setData(mData.get(position));
        holder.itemView.setTag(position);

    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        } else {

            return 0;
        }
    }

    @Override
    public void onClick(View v) {
        if (mItemClickListener!=null){
            mItemClickListener.onItemClick((Integer)v.getTag());
            v.setBackgroundColor(Color.parseColor("#FF0000"));
        }
    }

    public class DetailHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.TextView)
        TextView mTextView;
        @BindView(R.id.rr_item_de)
        RelativeLayout mLayout;

        public DetailHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(DetailsBean bean) {
            mTextView.setText(bean.getTitle());
        }
    }
    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    public void setItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }
}
