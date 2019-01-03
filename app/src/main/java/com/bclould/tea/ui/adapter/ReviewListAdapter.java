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
import com.bclould.tea.ui.widget.DeleteCacheDialog;
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

    private final GroupPresenter mGroupPresenter;
    private  Context mContext;
    private  List<ReviewInfo.DataBean> reviewInfos;


    public ReviewListAdapter(Context reviewListActivity, ArrayList<ReviewInfo.DataBean> reviewInfos, GroupPresenter groupPresenter) {
        this.mContext=reviewListActivity;
        this.reviewInfos=reviewInfos;
        mGroupPresenter = groupPresenter;
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
            viewHolder.setData(reviewInfos.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (reviewInfos.size() != 0) {
            return reviewInfos.size();
        }
        return 0;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.btn_reject)
        Button btn_reject;
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.name)
        TextView mName;
        @Bind(R.id.btn_consent)
        Button mBtnConsent;
        private ReviewInfo.DataBean mAddRequestInfo;
        private int mPosition;

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
                                mBtnConsent.setVisibility(View.GONE);
                                btn_reject.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                                btn_reject.setText(mContext.getString(R.string.agrd_agreed));
                                btn_reject.setTextColor(mContext.getResources().getColor(R.color.gray));
                                btn_reject.setEnabled(false);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            btn_reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        // TODO: 2019/1/3 拒绝接口
                        new GroupPresenter(mContext).setReview(mAddRequestInfo.getGroup_id(),3 , mAddRequestInfo.getTo_toco_id(), new GroupPresenter.CallBack() {
                            @Override
                            public void send() {
                                mBtnConsent.setVisibility(View.GONE);
                                //拒绝操作
                                btn_reject.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                                btn_reject.setText(mContext.getString(R.string.denied));
                                btn_reject.setTextColor(mContext.getResources().getColor(R.color.gray));
                                btn_reject.setEnabled(false);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showDeleteDialog(mPosition);
                    return false;
                }
            });
        }

        public void setData(ReviewInfo.DataBean addRequestInfo, int position) {
            mPosition = position;
            mBtnConsent.setVisibility(View.VISIBLE);
            mAddRequestInfo = addRequestInfo;
            if (addRequestInfo.getTo_avatar().isEmpty()) {
                UtilTool.setCircleImg(mContext, R.mipmap.img_nfriend_headshot1, mIvTouxiang);
            } else {
                UtilTool.setCircleImg(mContext, addRequestInfo.getTo_avatar(), mIvTouxiang);
            }
            mName.setText(addRequestInfo.getTo_name());
            if (addRequestInfo.getStatus() == 1) {
                btn_reject.setEnabled(true);
                btn_reject.setBackground(mContext.getResources().getDrawable(R.drawable.bg_white_shape));
                btn_reject.setText(mContext.getString(R.string.reject));
                btn_reject.setTextColor(mContext.getResources().getColor(R.color.red));

                mBtnConsent.setVisibility(View.VISIBLE);
                mBtnConsent.setEnabled(true);
            }else if(addRequestInfo.getStatus() == 2){
                mBtnConsent.setVisibility(View.GONE);
                btn_reject.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                btn_reject.setText(mContext.getString(R.string.agrd_agreed));
                btn_reject.setTextColor(mContext.getResources().getColor(R.color.gray));
                btn_reject.setEnabled(false);
            }else if(addRequestInfo.getStatus() == 3){
                mBtnConsent.setVisibility(View.GONE);
                btn_reject.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                btn_reject.setText(mContext.getString(R.string.denied));
                btn_reject.setTextColor(mContext.getResources().getColor(R.color.gray));
                btn_reject.setEnabled(false);
            }
        }
    }

    private void showDeleteDialog(final int position) {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, mContext, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle(mContext.getString(R.string.delete_group_request_hint));
        Button confirm = (Button) deleteCacheDialog.findViewById(R.id.btn_confirm);
        confirm.setTextColor(mContext.getResources().getColor(R.color.red));
        confirm.setText(mContext.getString(R.string.delete));
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
                mGroupPresenter.deleteGroupReview(reviewInfos.get(position).getId(), reviewInfos.get(position).getGroup_id(), new GroupPresenter.CallBack() {
                    @Override
                    public void send() {
                        reviewInfos.remove(position);
                        notifyDataSetChanged();
                    }
                });
            }
        });
        Button cancel = (Button) deleteCacheDialog.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
            }
        });
    }
}
