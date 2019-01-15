package com.bclould.tea.ui.activity.ftc.acccountbinding;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import com.bclould.tea.Presenter.DistributionPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseView;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.ui.widget.NodePayDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.ToastShow;

import static com.bclould.tea.Presenter.LoginPresenter.BIND_FTC;

/**
 * Created by GIjia on 2018/12/25.
 */

public class AccountBindingPresenter implements AccountBindingContacts.Presenter{
    private AccountBindingContacts.View mView;
    private Activity mActivity;
    @Override
    public void bindView(BaseView view) {
        mView= (AccountBindingContacts.View) view;
        mView.initView();
    }

    @Override
    public <T extends Context> void start(T context) {
        mActivity= (Activity) context;
        desc();
    }

    @Override
    public void release() {

    }

    private void desc() {
        new DistributionPresenter(mActivity).bindDesc(new DistributionPresenter.CallBack() {
            @Override
            public void send(BaseInfo baseInfo) {
                mView.setDesc(baseInfo.getData().getDesc());
            }

            @Override
            public void error() {

            }
        });
    }

    @Override
    public void bind(String email,String password) {
        if (StringUtils.isEmpty(email)) {
            ToastShow.showToast(mActivity, mActivity.getString(R.string.toast_email_or_phone));
        }
        if (StringUtils.isEmpty(password)) {
            ToastShow.showToast(mActivity, mActivity.getString(R.string.toast_password));
        }

        new DistributionPresenter(mActivity).bindTeam(email, password, new DistributionPresenter.CallBack() {
            @Override
            public void send(BaseInfo baseInfo) {
                MySharedPreferences.getInstance().setBoolean(BIND_FTC, true);
                showDialog();
            }

            @Override
            public void error() {

            }
        });
    }

    private void showDialog() {
        if(!ActivityUtil.isActivityOnTop(mActivity))return;
        NodePayDialog nodePayDialog = new NodePayDialog(mActivity);
        nodePayDialog.show();
        nodePayDialog.setTvTitle(mActivity.getString(R.string.binding_succeed));
        nodePayDialog.setIvImage(R.mipmap.icon_node_dui);
        nodePayDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mActivity.finish();
            }
        });

    }

}
