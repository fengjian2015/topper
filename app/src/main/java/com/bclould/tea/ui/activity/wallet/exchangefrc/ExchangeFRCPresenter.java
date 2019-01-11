package com.bclould.tea.ui.activity.wallet.exchangefrc;

import android.app.Activity;
import android.content.Context;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.bclould.tea.Presenter.DistributionPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseView;
import com.bclould.tea.model.base.BaseListInfo;
import com.bclould.tea.ui.activity.my.taskrecord.TaskRecordContacts;
import com.bclould.tea.ui.adapter.ExchangeFRCAdapter;
import com.bclould.tea.ui.adapter.TaskRecordAdapter;
import com.bclould.tea.ui.widget.ConfirmDialog;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.TimeSelectUtil;
import com.bclould.tea.utils.ToastShow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by fengjian on 2019/1/7.
 */

public class ExchangeFRCPresenter implements ExchangeFRCContacts.Presenter {
    private ExchangeFRCContacts.View mView;
    private Activity mActivity;

    private ExchangeFRCAdapter mExchangeFRCAdapter;
    List<HashMap> mInOutList = new ArrayList<>();
    private int page = 1;

    @Override
    public void bindView(BaseView view) {
        mView = (ExchangeFRCContacts.View) view;
    }

    @Override
    public <T extends Context> void start(T context) {
        mActivity = (Activity) context;
        mView.initView();
        initRecyclerView();
        initHttp(true);
    }

    @Override
    public void release() {

    }

    private void initRecyclerView() {
        mExchangeFRCAdapter = new ExchangeFRCAdapter(mActivity, mInOutList);
        mView.setAdapter(mExchangeFRCAdapter);
    }

    public void initHttp(final boolean isRefresh) {
        int p = 1;
        if (isRefresh) {
            p = 1;
        } else {
            p = page + 1;
        }
//        mDillDataPresenter.teamReward(p, mDate, new DistributionPresenter.CallBack7() {
//            @Override
//            public void send(BaseListInfo data) {
//                mView.resetRefresh(isRefresh);
//               data.getData();
//                if (data.getData().size() == 20) {
//                    mView.setEnableLoadMore(true);
//                } else {
//                    mView.setEnableLoadMore(false);
//                }
//                if (isRefresh) {
//                    page = 1;
//                    mInOutList.clear();
//                } else {
//                    page++;
//                }
//                if (data.getData().size() != 0) {
//                    mInOutList.addAll((List)data.getData());
//                    mExchangeFRCAdapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void error() {
//                mView.resetRefresh(isRefresh);
//            }
//        });
    }

    @Override
    public void exchange(String money) {
        if(StringUtils.isEmpty(money)){
            ToastShow.showToast2(mActivity,mActivity.getString(R.string.toast_count));
            return;
        }
        // TODO: 2019/1/11 兑换接口
        showDiolag();
    }

    private void showDiolag(){
        ConfirmDialog diaolg = new ConfirmDialog(mActivity);
        diaolg.show();
        diaolg.setTvTitleColor(mActivity.getResources().getColor(R.color.btn_bg_color));
        diaolg.setTvContentColor(mActivity.getResources().getColor(R.color.app_bg_color));
        diaolg.setTvTitle(mActivity.getString(R.string.exchange_succeed));
        String result= String.format(mActivity.getString(R.string.frc_congratulations) ,"99");
        SpannableStringBuilder ssb = new SpannableStringBuilder(result);
        int star=result.indexOf("99");
        ssb.setSpan(new ForegroundColorSpan(mActivity.getResources().getColor(R.color.btn_bg_color)),star,star+"99".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        diaolg.setTvContent(ssb);
        diaolg.setBtnConfirm(mActivity.getString(R.string.got_it));
        diaolg.setOnClickListener(new ConfirmDialog.OnClickListener() {
            @Override
            public void onClick() {
                mView.setmRlSuccessShow(View.VISIBLE);
            }
        });
    }

    public String exchangeMoeny(String money){
        String change=money;
        return change;
    }

    @Override
    public InputFilter getLengthFilter() {
        return lengthFilter;
    }


    public InputFilter lengthFilter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            // source:当前输入的字符
            // start:输入字符的开始位置
            // end:输入字符的结束位置
            // dest：当前已显示的内容
            // dstart:当前光标开始位置
            // dent:当前光标结束位置
            if (dest.length() == 0 && source.equals(".")) {
                return "0.";
            }
            String dValue = dest.toString();
            String[] splitArray = dValue.split("\\.");
            if (splitArray.length > 1) {
                String dotValue = splitArray[1];
                if (dotValue.length() == 8) {
                    return "";
                }
            }
            return null;
        }

    };
}
