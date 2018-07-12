package com.bclould.tea.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bclould.tea.Presenter.GroupPresenter;
import com.bclould.tea.R;
import com.bclould.tea.model.ReviewInfo;
import com.bclould.tea.utils.UtilTool;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/10/12.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class ReviewListAdapter extends RecyclerView.Adapter {

    private  Context mContext;
    private  List<ReviewInfo.DataBean> reviewInfos;


    public ReviewListAdapter(Context reviewListActivity, ArrayList<ReviewInfo.DataBean> reviewInfos) {
        this.mContext=reviewListActivity;
        this.reviewInfos=reviewInfos;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_new_friend, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        if (reviewInfos.size() != 0 )
            viewHolder.setData(reviewInfos.get(position));
    }

    @Override
    public int getItemCount() {
        if (reviewInfos.size() != 0) {
            return reviewInfos.size();
        }
        return 0;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.name)
        TextView mName;
        @Bind(R.id.btn_consent)
        Button mBtnConsent;
        private ReviewInfo.DataBean mAddRequestInfo;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            mBtnConsent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        new GroupPresenter(mContext).setReview(mAddRequestInfo.getGroup_id(), 2, mAddRequestInfo.getTo_toco_id(), new GroupPresenter.CallBack() {
                            @Override
                            public void send() {
                                //同意操作
                                mBtnConsent.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                                mBtnConsent.setText(mContext.getString(R.string.agrd_agreed));
                                mBtnConsent.setTextColor(mContext.getResources().getColor(R.color.gray));
                                mBtnConsent.setEnabled(false);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        public void setData(ReviewInfo.DataBean addRequestInfo) {
            mBtnConsent.setVisibility(View.VISIBLE);
            mAddRequestInfo = addRequestInfo;
            if (addRequestInfo.getTo_avatar().isEmpty()) {
                UtilTool.setCircleImg(mContext, R.mipmap.img_nfriend_headshot1, mIvTouxiang);
            } else {
                UtilTool.setCircleImg(mContext, addRequestInfo.getTo_avatar(), mIvTouxiang);
            }
            mName.setText(addRequestInfo.getTo_name());
            if (addRequestInfo.getStatus() == 1) {
                mBtnConsent.setEnabled(true);
                mBtnConsent.setBackgroundColor(mContext.getResources().getColor(R.color.green));
                mBtnConsent.setText(mContext.getString(R.string.consent));
                mBtnConsent.setTextColor(mContext.getResources().getColor(R.color.white));
            }else if(addRequestInfo.getStatus() == 2){
                mBtnConsent.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                mBtnConsent.setText(mContext.getString(R.string.agrd_agreed));
                mBtnConsent.setTextColor(mContext.getResources().getColor(R.color.gray));
                mBtnConsent.setEnabled(false);
            }
        }
    }
}
