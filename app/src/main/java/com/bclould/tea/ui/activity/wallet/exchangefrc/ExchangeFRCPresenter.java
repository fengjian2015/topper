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
import com.bclould.tea.Presenter.FinanciaPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseView;
import com.bclould.tea.model.base.BaseInfoConstants;
import com.bclould.tea.model.base.BaseListInfo;
import com.bclould.tea.model.base.BaseMapInfo;
import com.bclould.tea.ui.activity.my.taskrecord.TaskRecordContacts;
import com.bclould.tea.ui.adapter.ExchangeFRCAdapter;
import com.bclould.tea.ui.adapter.TaskRecordAdapter;
import com.bclould.tea.ui.widget.ConfirmDialog;
import com.bclould.tea.ui.widget.PWDDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.TimeSelectUtil;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fengjian on 2019/1/7.
 */

public class ExchangeFRCPresenter implements ExchangeFRCContacts.Presenter {
    private ExchangeFRCContacts.View mView;
    private Activity mActivity;

    private ExchangeFRCAdapter mExchangeFRCAdapter;
    List<Map> mInOutList = new ArrayList<>();
    private int page = 1;
    private int page_size=10;
    private String [] rate;
    private PWDDialog pwdDialog;

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
        new FinanciaPresenter(mActivity).exchangeRecordFRC(p, page_size, new FinanciaPresenter.CallBack7() {
            @Override
            public void send(BaseMapInfo data) {
                mView.resetRefresh(isRefresh);
                if (((List)data.getData().get(BaseInfoConstants.EXCHANGE)).size() == page_size) {
                    mView.setEnableLoadMore(true);
                } else {
                    mView.setEnableLoadMore(false);
                }
                if (isRefresh) {
                    page = 1;
                    mInOutList.clear();
                } else {
                    page++;
                }
                setView(data);
                if (data.getData().size() != 0) {
                    mInOutList.addAll((List)data.getData().get(BaseInfoConstants.EXCHANGE));
                    mExchangeFRCAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void error() {
                mView.resetRefresh(isRefresh);
            }
        });
    }

    public void setView(BaseMapInfo data){
        rate=((String)data.getData().get(BaseInfoConstants.RATE)).split(":");
        mView.setmTvProportion("1GC = "+UtilTool.changeMoney1(getRateMoney(1))+"FRC");
        mView.setmTvExchangeRate(mActivity.getResources().getString(R.string.current_rate)+data.getData().get(BaseInfoConstants.RATE));
        String result= String.format(mActivity.getString(R.string.echange_frc_help) ,data.getData().get(BaseInfoConstants.NUMBER_CONDITION));
        mView.setmTvEchangeFrcHelp(result);
        mView.setmTvBalance(mActivity.getString(R.string.available)+data.getData().get(BaseInfoConstants.OVER_NUM)+"GC");
        String  no=UtilTool.subZeroAndDot(data.getData().get(BaseInfoConstants.USER_NO)+"");
        if(UtilTool.parseDouble(data.getData().get(BaseInfoConstants.USER_NO)+"")==0){
            mView.setmRlSuccessShow(View.GONE);
        }else{
            mView.setmRlSuccessShow(View.VISIBLE);
            String content= String.format(mActivity.getString(R.string.echange_frc_success) ,UtilTool.subZeroAndDot(data.getData().get(BaseInfoConstants.USER_NO)+"")+"");
            mView.setmTvSuccess(content);
        }
    }

    private double getRateMoney(double number){
        if(rate==null||rate.length>=2) {
            return (number / UtilTool.parseDouble(rate[0]) * UtilTool.parseDouble(rate[1]));
        }else{
            return 0;
        }
    }

    @Override
    public void exchange(String money) {
        if(StringUtils.isEmpty(money)){
            ToastShow.showToast2(mActivity,mActivity.getString(R.string.toast_count));
            return;
        }
        showPWDialog(money);
    }

    private void showPWDialog(final String money) {
        pwdDialog = new PWDDialog(mActivity);
        pwdDialog.setOnPWDresult(new PWDDialog.OnPWDresult() {
            @Override
            public void success(String password) {
                setPay(money,password);
            }
        });
        pwdDialog.showDialog(money, "GC", mActivity.getString(R.string.exchange) + "FRC", null, null);
    }

    public void setPay(String money,String password){
        new FinanciaPresenter(mActivity).exchangeActionFRC(money, password, new FinanciaPresenter.CallBack7() {
            @Override
            public void send(BaseMapInfo baseInfo) {
                showDiolag(baseInfo.getData().get(BaseInfoConstants.USER_NO)+"");
            }

            @Override
            public void error() {

            }
        });
    }

    private void showDiolag(String user_no){
        if(!ActivityUtil.isActivityOnTop(mActivity))return;
        if(UtilTool.parseDouble(user_no)==0){
            initHttp(true);
            return;
        }
        user_no=UtilTool.subZeroAndDot(user_no);
        ConfirmDialog diaolg = new ConfirmDialog(mActivity);
        diaolg.show();
        diaolg.setTvTitleColor(mActivity.getResources().getColor(R.color.btn_bg_color));
        diaolg.setTvContentColor(mActivity.getResources().getColor(R.color.app_bg_color));
        diaolg.setTvTitle(mActivity.getString(R.string.exchange_succeed));
        String result= String.format(mActivity.getString(R.string.frc_congratulations) ,user_no);
        SpannableStringBuilder ssb = new SpannableStringBuilder(result);
        int star=result.indexOf(user_no);
        ssb.setSpan(new ForegroundColorSpan(mActivity.getResources().getColor(R.color.btn_bg_color)),star,star+user_no.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        diaolg.setTvContent(ssb);
        diaolg.setBtnConfirm(mActivity.getString(R.string.got_it));
        diaolg.setOnClickListener(new ConfirmDialog.OnClickListener() {
            @Override
            public void onClick() {
                initHttp(true);
            }
        });
    }

    public String exchangeMoeny(String money){
        String change= UtilTool.changeMoney1(getRateMoney(UtilTool.parseDouble(money)));
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
