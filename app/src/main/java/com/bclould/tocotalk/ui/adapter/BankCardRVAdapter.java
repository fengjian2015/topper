package com.bclould.tocotalk.ui.adapter;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tocotalk.Presenter.BankCardPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.CardListInfo;
import com.bclould.tocotalk.ui.activity.BankCardActivity;
import com.bclould.tocotalk.ui.widget.DeleteCacheDialog;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/9/26.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class BankCardRVAdapter extends RecyclerView.Adapter {

    private final BankCardActivity mActivity;
    private final List<CardListInfo.DataBean> mCardList;
    private final BankCardPresenter mBankCardPresenter;

    //构造方法
    public BankCardRVAdapter(BankCardActivity activity, List<CardListInfo.DataBean> cardList, BankCardPresenter bankCardPresenter) {
        mActivity = activity;
        mCardList = cardList;
        mBankCardPresenter = bankCardPresenter;
    }

    //绑定item
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_bank_card, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    //绑定ViewHolder
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mCardList.get(position));
    }

    //条目数量
    @Override
    public int getItemCount() {
        if (mCardList.size() != 0)
            return mCardList.size();
        return 0;
    }

    //ViewHolder控制条目
    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.image)
        ImageView mImage;
        @Bind(R.id.bank_name)
        TextView mBankName;
        @Bind(R.id.card_type)
        TextView mCardType;
        @Bind(R.id.bank_card_number)
        TextView mBankCardNumber;
        @Bind(R.id.tv_delete)
        TextView mTvDelete;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final CardListInfo.DataBean data) {
            String bankName = data.getBank_name();
            String[] split = bankName.split("-");
            mBankName.setText(split[0]);
            mCardType.setText(split[split.length - 1]);
            mBankCardNumber.setText(data.getCard_number());
            mTvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog(data);
                }
            });
            mActivity.setOnItemDeleteListener(new BankCardActivity.OnItemDeleteListener() {
                @Override
                public void onDelete(boolean isDelete) {
                    if (isDelete) {
                        mTvDelete.setVisibility(View.VISIBLE);
                    } else {
                        mTvDelete.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    private void showDialog(final CardListInfo.DataBean data) {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, mActivity);
        deleteCacheDialog.show();
        Button cancel = (Button) deleteCacheDialog.findViewById(R.id.btn_cancel);
        TextView title = (TextView) deleteCacheDialog.findViewById(R.id.tv_title);
        title.setText("确定要删除银行卡吗？");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
            }
        });
        Button confirm = (Button) deleteCacheDialog.findViewById(R.id.btn_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
                mBankCardPresenter.unBindBankCard(data.getId(), new BankCardPresenter.CallBack3() {
                    @Override
                    public void send(int status) {
                        if (status == 1) {
                            Toast.makeText(mActivity, "解绑成功", Toast.LENGTH_SHORT).show();
                            mCardList.remove(data);
                            notifyDataSetChanged();
                        } else {
                            Toast.makeText(mActivity, "解绑失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
