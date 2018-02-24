package com.dashiji.biyun.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dashiji.biyun.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/10/18.
 */

public class AllProblemRVAdapter extends RecyclerView.Adapter {

    private final Context mContext;

    public AllProblemRVAdapter(Context context) {

        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_all_problem, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


    }

    @Override
    public int getItemCount() {
        return 20;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.problem_content)
        TextView mProblemContent;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
