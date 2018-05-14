package com.bclould.tocotalk.ui.adapter;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tocotalk.Presenter.BankCardPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.CardListInfo;
import com.bclould.tocotalk.ui.activity.BankCardActivity;
import com.bclould.tocotalk.ui.widget.DeleteCacheDialog;
import com.bclould.tocotalk.utils.MessageEvent;

import org.greenrobot.eventbus.EventBus;

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
        final ViewHolder viewHolder = (ViewHolder) holder;
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
        @Bind(R.id.tv_logo)
        TextView mTvLogo;
        @Bind(R.id.bank_name)
        TextView mBankName;
        @Bind(R.id.card_type)
        TextView mCardType;
        @Bind(R.id.bank_card_number)
        TextView mBankCardNumber;
        @Bind(R.id.iv_delete)
        ImageView mIvDelete;
        @Bind(R.id.rl_card)
        RelativeLayout mRlCard;
        private CardListInfo.DataBean mData;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog(mData, 1);
                }
            });
        }

        public void setData(final CardListInfo.DataBean data) {
            mData = data;
            String bankName = data.getBank_name();
            String[] split = bankName.split("-");
            mBankName.setText(split[0]);
            String logo = split[0].substring(0, 1);
            mTvLogo.setText(logo);
            mCardType.setText(split[split.length - 1]);
            mBankCardNumber.setText(data.getCard_number());
            if (data.getIs_default() == 1) {
                mRlCard.setBackground(mActivity.getDrawable(R.drawable.bg_bank_card_shape2));
            } else {
                mRlCard.setBackground(mActivity.getDrawable(R.drawable.bg_bank_card_shape));
            }
            mIvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog(data, 0);
                }
            });

        }
    }

    private void showDialog(final CardListInfo.DataBean data, final int type) {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, mActivity, R.style.dialog);
        deleteCacheDialog.show();
        Button cancel = (Button) deleteCacheDialog.findViewById(R.id.btn_cancel);
        TextView title = (TextView) deleteCacheDialog.findViewById(R.id.tv_title);
        if (type == 0) {
            title.setText(mActivity.getString(R.string.delete_bank_card_hint));
        } else {
            title.setText(mActivity.getString(R.string.set_the_default_bank_card_hint));
        }
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
                if (type == 0) {
                    mBankCardPresenter.unBindBankCard(data.getId(), new BankCardPresenter.CallBack3() {
                        @Override
                        public void send(int status) {
                            if (status == 1) {
                                Toast.makeText(mActivity, mActivity.getString(R.string.unbinding_succeed), Toast.LENGTH_SHORT).show();
                                mCardList.remove(data);
                                notifyDataSetChanged();
                            } else {
                                Toast.makeText(mActivity, mActivity.getString(R.string.unbinding_error), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    mBankCardPresenter.setDefaultBankCard(data.getId(), new BankCardPresenter.CallBack3() {
                        @Override
                        public void send(int status) {
                            if (status == 1) {
                                EventBus.getDefault().post(new MessageEvent(mActivity.getString(R.string.set_the_default_bank_card)));
                            }
                        }
                    });
                }
            }
        });
    }
}
