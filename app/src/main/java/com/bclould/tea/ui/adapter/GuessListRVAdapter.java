package com.bclould.tea.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.R;
import com.bclould.tea.model.GuessListInfo;
import com.bclould.tea.ui.activity.GuessDetailsActivity;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.utils.AnimatorTool;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/4/23.
 */

public class GuessListRVAdapter extends RecyclerView.Adapter {
    private final List<GuessListInfo.DataBean> mDataList;
    private final Context mContext;

    public GuessListRVAdapter(List<GuessListInfo.DataBean> dataList, Context context) {
        mDataList = dataList;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_my_guess, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mDataList.get(position));
    }

    @Override
    public int getItemCount() {
        if (mDataList.size() != 0) {
            return mDataList.size();
        }
        return 0;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_guess_status)
        ImageView mIvGuessStatus;
        @Bind(R.id.tv_kaijiang_time)
        TextView mTvKaijiangTime;
        @Bind(R.id.iv_touxiang)
        ImageView mIvTouxiang;
        @Bind(R.id.tv_guess_title)
        TextView mTvGuessTitle;
        @Bind(R.id.tv_name)
        TextView mTvName;
        @Bind(R.id.btn_bet)
        Button mBtnBet;
        @Bind(R.id.insert_coins_count)
        TextView mInsertCoinsCount;
        @Bind(R.id.rl_dangqian)
        RelativeLayout mRlDangqian;
        @Bind(R.id.tv_bonus_chi)
        TextView mTvBonusChi;
        @Bind(R.id.rl_jiangjin)
        RelativeLayout mRlJiangjin;
        @Bind(R.id.tv_count)
        TextView mTvCount;
        @Bind(R.id.rl_count)
        RelativeLayout mRlCount;
        @Bind(R.id.tv_join_time)
        TextView mTvJoinTime;
        @Bind(R.id.rl_time)
        RelativeLayout mRlTime;
        @Bind(R.id.tv_kaijiang_result)
        TextView mTvKaijiangResult;
        @Bind(R.id.rl_result)
        RelativeLayout mRlResult;
        @Bind(R.id.tv_award_status)
        TextView mTvAwardStatus;
        @Bind(R.id.ll_guess_status)
        LinearLayout mLlGuessStatus;
        @Bind(R.id.ll_all)
        LinearLayout mLlAll;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(final GuessListInfo.DataBean dataBean) {
            mTvGuessTitle.setText("(" + dataBean.getUser_name() + ")" + dataBean.getTitle());
            mInsertCoinsCount.setText(dataBean.getPeriod_qty() + mContext.getString(R.string.qi));
            mTvKaijiangTime.setText(mContext.getString(R.string.kaijiang_time) + dataBean.getLottery_time());
            mTvBonusChi.setText(dataBean.getPrize_pool_number() + "/" + dataBean.getLimit_number() + " " + dataBean.getCoin_name());
            mTvKaijiangResult.setText(dataBean.getWin_number());
            mTvName.setText(mContext.getString(R.string.fa_qi_time) + dataBean.getCreated_at());
            if (dataBean.getStatus() == 1) {
                mRlTime.setVisibility(View.GONE);
                mRlCount.setVisibility(View.GONE);
                mLlGuessStatus.setVisibility(View.GONE);
                mIvGuessStatus.setImageResource(R.mipmap.icon_jc_ing);
                mBtnBet.setVisibility(View.VISIBLE);
                mBtnBet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (dataBean.getPassword() != null) {
                            if (dataBean.getPassword().isEmpty()) {
                                Intent intent = new Intent(mContext, GuessDetailsActivity.class);
                                intent.putExtra("bet_id", dataBean.getId());
                                intent.putExtra("period_qty", dataBean.getPeriod_qty());
                                mContext.startActivity(intent);
                            } else {
                                showPWDialog(dataBean);
                            }
                        }else {
                            Intent intent = new Intent(mContext, GuessDetailsActivity.class);
                            intent.putExtra("bet_id", dataBean.getId());
                            intent.putExtra("period_qty", dataBean.getPeriod_qty());
                            mContext.startActivity(intent);
                        }
                    }
                });
                mLlAll.setEnabled(false);
            } else {
                mLlAll.setEnabled(true);
                mBtnBet.setVisibility(View.GONE);
                mLlAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, GuessDetailsActivity.class);
                        intent.putExtra("bet_id", dataBean.getId());
                        intent.putExtra("period_qty", dataBean.getPeriod_qty());
                        mContext.startActivity(intent);
                    }
                });
                if (dataBean.getStatus() == 2) {
                    mRlTime.setVisibility(View.GONE);
                    mRlCount.setVisibility(View.GONE);
                    mLlGuessStatus.setVisibility(View.GONE);
                    mIvGuessStatus.setImageResource(R.mipmap.icon_jc_wait);
                } else if (dataBean.getStatus() == 3) {
                    mTvCount.setText(dataBean.getCoin_count());
                    mTvJoinTime.setText(dataBean.getJoin_created_at());
                    if (dataBean.getLottery_status() == 0) {
                        mTvAwardStatus.setText(mContext.getString(R.string.no_join));
                        mTvAwardStatus.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                    } else if (dataBean.getLottery_status() == 2) {
                        mTvAwardStatus.setText(mContext.getString(R.string.no_zhong_jiang));
                        mTvAwardStatus.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                    } else if (dataBean.getLottery_status() == 3) {
                        mTvAwardStatus.setText(mContext.getString(R.string.zhong_jiang_le));
                        mTvAwardStatus.setTextColor(mContext.getResources().getColor(R.color.yikaijiang));
                    }
                    mRlTime.setVisibility(View.VISIBLE);
                    mRlCount.setVisibility(View.VISIBLE);
                    mLlGuessStatus.setVisibility(View.VISIBLE);
                    mIvGuessStatus.setImageResource(R.mipmap.icon_jc_pass);
                } else if (dataBean.getStatus() == 4) {
                    mRlTime.setVisibility(View.GONE);
                    mRlCount.setVisibility(View.GONE);
                    mLlGuessStatus.setVisibility(View.GONE);
                    mIvGuessStatus.setImageResource(R.mipmap.icon_jc_cancel);
                }
            }
        }
    }

    private void showPWDialog(final GuessListInfo.DataBean dataBean) {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_command, mContext,R.style.dialog);
        deleteCacheDialog.show();
        final EditText etGuessPw = (EditText) deleteCacheDialog.findViewById(R.id.et_guess_password);
        Button btnConfirm = (Button) deleteCacheDialog.findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pw = etGuessPw.getText().toString();
                if (pw.isEmpty()) {
                    AnimatorTool.getInstance().editTextAnimator(etGuessPw);
                    Toast.makeText(mContext, mContext.getString(R.string.toast_guess_pw), Toast.LENGTH_SHORT).show();
                } else {
                    if (dataBean.getPassword().equals(pw)) {
                        Intent intent = new Intent(mContext, GuessDetailsActivity.class);
                        intent.putExtra("bet_id", dataBean.getId());
                        intent.putExtra("period_qty", dataBean.getPeriod_qty());
                        intent.putExtra("guess_pw", dataBean.getPassword());
                        mContext.startActivity(intent);
                        deleteCacheDialog.dismiss();
                    } else {
                        AnimatorTool.getInstance().editTextAnimator(etGuessPw);
                        Toast.makeText(mContext, mContext.getString(R.string.toast_guess_pw_error), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
