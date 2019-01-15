package com.bclould.tea.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tea.Presenter.OutCoinSitePresenter;
import com.bclould.tea.R;
import com.bclould.tea.model.OutCoinSiteInfo;
import com.bclould.tea.ui.activity.OutCoinSiteActivity;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.utils.ActivityUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/11/3.
 */

public class OutCoinSiteRVAdapter extends RecyclerView.Adapter {

    private final List<OutCoinSiteInfo.MessageBean> mSiteBeanList;
    private final OutCoinSitePresenter mOutCoinSitePresenter;
    private final OutCoinSiteActivity mOutCoinSiteActivity;
    private final int mId;

    public OutCoinSiteRVAdapter(OutCoinSiteActivity outCoinSiteActivity, List<OutCoinSiteInfo.MessageBean> siteBeanList, OutCoinSitePresenter outCoinSitePresenter, int id) {
        mOutCoinSiteActivity = outCoinSiteActivity;
        mSiteBeanList = siteBeanList;
        mOutCoinSitePresenter = outCoinSitePresenter;
        mId = id;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mOutCoinSiteActivity).inflate(R.layout.item_add_site, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mSiteBeanList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mSiteBeanList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_site)
        TextView mTvSite;
        @Bind(R.id.tv_label)
        TextView mTvLabel;
        @Bind(R.id.iv_delete)
        ImageView mIvDelete;
        private OutCoinSiteInfo.MessageBean mMessageBean;
        private int mPosition;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOutCoinSiteActivity.setSite(mMessageBean.getAddress(), mMessageBean.getId());
                }
            });

            mIvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!ActivityUtil.isActivityOnTop(mOutCoinSiteActivity))return;
                    final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, mOutCoinSiteActivity, R.style.dialog);
                    deleteCacheDialog.show();
                    deleteCacheDialog.setTitle(mOutCoinSiteActivity.getString(R.string.delete_site_hint));
                    dialogClick(deleteCacheDialog);
                }

                private void dialogClick(final DeleteCacheDialog deleteCacheDialog) {
                    Button cancel = (Button) deleteCacheDialog.findViewById(R.id.btn_cancel);

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deleteCacheDialog.dismiss();

                        }
                    });
                    Button confirm = (Button) deleteCacheDialog.findViewById(R.id.btn_confirm);
                    confirm.setText(mOutCoinSiteActivity.getString(R.string.delete));
                    confirm.setTextColor(mOutCoinSiteActivity.getResources().getColor(R.color.red));
                    confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mOutCoinSitePresenter.deleteSite(mId, mMessageBean.getId(), new OutCoinSitePresenter.CallBack2() {
                                @Override
                                public void send() {
                                    mSiteBeanList.remove(mPosition);
                                    notifyDataSetChanged();
                                    deleteCacheDialog.dismiss();
                                }
                            });
                        }
                    });
                }
            });

        }

        public void setData(OutCoinSiteInfo.MessageBean messageBean, int position) {
            mPosition = position;
            mMessageBean = messageBean;
            mTvSite.setText(messageBean.getAddress());
            mTvLabel.setText(messageBean.getLabel());
        }
    }
}
