package com.dashiji.biyun.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dashiji.biyun.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/10/13.
 */

public class CommentRVAdapter extends RecyclerView.Adapter {

    private final Context mContext;

    public CommentRVAdapter(Context context) {
        mContext = context;
    }

    @Override

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;

        RecyclerView.ViewHolder viewHolder = null;

        if (viewType == 0) {

            view = LayoutInflater.from(mContext).inflate(R.layout.item_comment, parent, false);

            viewHolder = new ViewHolder(view);

        } else {

            view = LayoutInflater.from(mContext).inflate(R.layout.item_comment2, parent, false);

            viewHolder = new ViewHolder2(view);

        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemViewType(int position) {

        if (position % 2 == 0) {

            return 0;

        } else {

            return 1;
        }

    }

    @Override
    public int getItemCount() {
        return 20;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.touxiang)
        ImageView mTouxiang;
        @Bind(R.id.name)
        TextView mName;
        @Bind(R.id.time)
        TextView mTime;
        @Bind(R.id.pinglun)
        TextView mPinglun;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    class ViewHolder2 extends RecyclerView.ViewHolder {
        @Bind(R.id.touxiang)
        ImageView mTouxiang;
        @Bind(R.id.name)
        TextView mName;
        @Bind(R.id.time)
        TextView mTime;
        @Bind(R.id.pinglun)
        ImageView mPinglun;

        ViewHolder2(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
