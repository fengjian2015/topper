package com.bclould.tea.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bclould.tea.Presenter.GroupPresenter;
import com.bclould.tea.Presenter.PersonalDetailsPresenter;
import com.bclould.tea.R;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.model.AddRequestInfo;
import com.bclould.tea.ui.activity.ConversationActivity;
import com.bclould.tea.ui.activity.FriendVerificationActivity;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/10/12.
 */

public class NewFriendRVAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<AddRequestInfo> mAddRequestInfos;
    private final DBManager mMgr;
    private final PersonalDetailsPresenter mPersonalDetailsPresenter;

    public NewFriendRVAdapter(Context context, List<AddRequestInfo> addRequestInfos, DBManager mgr, PersonalDetailsPresenter personalDetailsPresenter) {
        mContext = context;
        mAddRequestInfos = addRequestInfos;
        mMgr = mgr;
        mPersonalDetailsPresenter = personalDetailsPresenter;
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
        if (mAddRequestInfos.size() != 0 )
            viewHolder.setData(mAddRequestInfos.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (mAddRequestInfos.size() != 0) {
            return mAddRequestInfos.size();
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
        private AddRequestInfo mAddRequestInfo;
        private int mPosition;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mMgr.findUser(mAddRequestInfo.getToUser())) {
                        Intent intent = new Intent(mContext, ConversationActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("name", mAddRequestInfo.getUserName());
                        bundle.putString("user", mAddRequestInfo.getToUser());
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                        }else {
                            ToastShow.showToast2((Activity) mContext, mContext.getString(R.string.add_friends_first));
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
            mBtnConsent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(mContext, FriendVerificationActivity.class);
                    intent.putExtra("user",mAddRequestInfo.getToUser());
                    intent.putExtra("name",mAddRequestInfo.getUserName());
                    intent.putExtra("avatar",mAddRequestInfo.getUrl());
                    mContext.startActivity(intent);
                }
            });
        }

        public void setData(AddRequestInfo addRequestInfo, int position) {
            mPosition = position;
            mBtnConsent.setVisibility(View.VISIBLE);
            mAddRequestInfo = addRequestInfo;
            if (addRequestInfo.getUrl().isEmpty()) {
                UtilTool.setCircleImg(mContext, R.mipmap.img_nfriend_headshot1, mIvTouxiang);
            } else {
                UtilTool.setCircleImg(mContext, addRequestInfo.getUrl(), mIvTouxiang);
            }
            mName.setText(addRequestInfo.getUserName());
            if (addRequestInfo.getType() == 1) {
                if(addRequestInfo.getUser().equals(UtilTool.getTocoId())){
                    mBtnConsent.setEnabled(false);
                    mBtnConsent.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                    mBtnConsent.setText(mContext.getString(R.string.waiting_agreed));
                    mBtnConsent.setTextColor(mContext.getResources().getColor(R.color.secondary_text_color));
                }else{
                    mBtnConsent.setEnabled(true);
                    mBtnConsent.setBackgroundColor(mContext.getResources().getColor(R.color.green));
                    mBtnConsent.setText(mContext.getString(R.string.view));
                    mBtnConsent.setTextColor(mContext.getResources().getColor(R.color.white));
                }
            }else if(addRequestInfo.getType() == 2){
                mBtnConsent.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                mBtnConsent.setText(mContext.getString(R.string.agrd_agreed));
                mBtnConsent.setTextColor(mContext.getResources().getColor(R.color.secondary_text_color));
                mBtnConsent.setEnabled(false);
            }else{
                mBtnConsent.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                mBtnConsent.setText(mContext.getString(R.string.denied));
                mBtnConsent.setTextColor(mContext.getResources().getColor(R.color.secondary_text_color));
                mBtnConsent.setEnabled(false);
            }
        }
    }

    private void showDeleteDialog(final int position) {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, mContext, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle(mContext.getString(R.string.delete_friend_request_hint));
        Button confirm = (Button) deleteCacheDialog.findViewById(R.id.btn_confirm);
        confirm.setTextColor(mContext.getResources().getColor(R.color.red));
        confirm.setText(mContext.getString(R.string.delete));
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
                mPersonalDetailsPresenter.deleteReview(mAddRequestInfos.get(position).getId(), new GroupPresenter.CallBack() {
                    @Override
                    public void send() {
                        mAddRequestInfos.remove(position);
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
