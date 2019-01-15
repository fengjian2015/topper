package com.bclould.tea.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tea.Presenter.DistributionPresenter;
import com.bclould.tea.R;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.model.UpgradeInfo;
import com.bclould.tea.ui.widget.NodePayDialog;
import com.bclould.tea.ui.widget.PWDDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.EventBusUtil;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GIjia on 2018/8/22.
 */
public class UpgradeFragment extends LazyFragment {
    @Bind(R.id.iv_remove)
    ImageView mIvRemove;
    @Bind(R.id.tv_money)
    TextView mTvMoney;
    @Bind(R.id.tv_unit)
    TextView mTvUnit;
    @Bind(R.id.iv_add)
    ImageView mIvAdd;
    @Bind(R.id.rl_add)
    RelativeLayout mRlAdd;
    @Bind(R.id.tv_content)
    TextView mTvContent;
    @Bind(R.id.btn_confirm)
    Button mBtnConfirm;
    @Bind(R.id.rl_select_money)
    LinearLayout mRlSelectMoney;
    @Bind(R.id.tv_un_title)
    TextView mTvUnTitle;
    @Bind(R.id.ll_not_reached)
    LinearLayout mLlNotReached;
    @Bind(R.id.tv_prompt)
    TextView mTvPrompt;
    private View view;
    // 标志位，标志已经初始化完成。
    private boolean isPrepared;
    private Context context;
    private PWDDialog pwdDialog;
    private int type = 1;
    private UpgradeInfo mUpgradeInfo;

    private int max=0;
    private int min=1;
    private int addMin=1;
    private int currentMoney=0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = View.inflate(getActivity(), R.layout.fragment_upgrade, null);
        context = getActivity();
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        isPrepared = true;
        lazyLoad();
        super.onActivityCreated(savedInstanceState);
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setUpgradeInfo(UpgradeInfo upgradeInfo){
        mUpgradeInfo=upgradeInfo;
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }
        init();
    }

    private void init() {
        if(mUpgradeInfo==null||mRlSelectMoney==null){
            return;
        }
        if(type==1){
            if(mUpgradeInfo.getData().getType()>=1){
                showSelectMonry(true);
            }else {
                showSelectMonry(false);
            }
            setPrompt(mUpgradeInfo.getData().getConsensus_node_text());
            setmTvUnit(mUpgradeInfo.getData().getConsensus_node().getCoin_name());
            setmTvContent(mUpgradeInfo.getData().getConsensus_node().getDesc());
            setmTvUnTitle(mUpgradeInfo.getData().getConsensus_node().getDesc());
            setmTvMoney(mUpgradeInfo.getData().getConsensus_node().getNumber()+"");
            min=mUpgradeInfo.getData().getConsensus_node().getMin();
            max=2147483647;
            addMin=mUpgradeInfo.getData().getConsensus_node().getNumber();
        }else if(type==2){
            if(mUpgradeInfo.getData().getType()>=2){
                showSelectMonry(true);
            }else {
                showSelectMonry(false);
            }
            setPrompt(mUpgradeInfo.getData().getSuper_node_text());
            setmTvUnit(mUpgradeInfo.getData().getSuper_node().getCoin_name());
            setmTvContent(mUpgradeInfo.getData().getSuper_node().getDesc());
            setmTvUnTitle(mUpgradeInfo.getData().getSuper_node().getDesc());
            setmTvMoney(mUpgradeInfo.getData().getSuper_node().getNumber()+"");
            min=mUpgradeInfo.getData().getSuper_node().getMin();
            max=mUpgradeInfo.getData().getSuper_node().getMax();
            addMin=mUpgradeInfo.getData().getSuper_node().getNumber();
        }else if(type==3){
            if(mUpgradeInfo.getData().getType()>=3){
                showSelectMonry(true);
            }else {
                showSelectMonry(false);
            }
            setPrompt(mUpgradeInfo.getData().getMain_node_text());
            setmTvUnit(mUpgradeInfo.getData().getMain_node().getCoin_name());
            setmTvContent(mUpgradeInfo.getData().getMain_node().getDesc());
            setmTvUnTitle(mUpgradeInfo.getData().getMain_node().getDesc());
            setmTvMoney(mUpgradeInfo.getData().getMain_node().getNumber()+"");
            min=mUpgradeInfo.getData().getMain_node().getMin();
            max=mUpgradeInfo.getData().getMain_node().getMax();
            addMin=mUpgradeInfo.getData().getMain_node().getNumber();

        }
    }

    private void setmTvMoney(String content){
        currentMoney= UtilTool.parseInt(content);
        mTvMoney.setText(content);
    }

    private void setmTvUnTitle(String content){
        mTvUnTitle.setText(Html.fromHtml(content));
    }

    private void setmTvContent(String content){
        mTvContent.setText(Html.fromHtml(content));
    }

    private void setmTvUnit(String content){
        mTvUnit.setText(content);
    }

    private void setPrompt(String content){
        mTvPrompt.setText(Html.fromHtml(content));
    }

    private void showSelectMonry(boolean isshow){
        if(isshow){
            mRlSelectMoney.setVisibility(View.VISIBLE);
            mLlNotReached.setVisibility(View.GONE);
        }else {
            mRlSelectMoney.setVisibility(View.GONE);
            mLlNotReached.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.btn_confirm,R.id.iv_remove,R.id.iv_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_confirm:
                pay();
                break;
            case R.id.iv_remove:
                remove();
                break;
            case R.id.iv_add:
                add();
                break;
        }
    }

    private void add(){
        currentMoney+=addMin;
        setmTvMoney(currentMoney+"");
    }

    private void remove(){
        if(currentMoney<=min||(currentMoney-addMin)<=0){
            return;
        }
        currentMoney-=addMin;
        setmTvMoney(currentMoney+"");
    }

    private void pay() {
        pwdDialog = new PWDDialog(getActivity());
        pwdDialog.setOnPWDresult(new PWDDialog.OnPWDresult() {
            @Override
            public void success(String password) {
                //密码输入成功调用支付
                payHttp(password);
            }
        });
        if(type==2||type==3) {
            pwdDialog.showDialog(UtilTool.removeZero(currentMoney*10000 + ""), "FTC", getString(R.string.upgrade_node), null, null);
        }else{
            pwdDialog.showDialog(UtilTool.removeZero(currentMoney + ""), "FTC", getString(R.string.upgrade_node), null, null);
        }
    }

    private void payHttp(String password){
        new DistributionPresenter(getActivity()).nodeBuyAction(password,currentMoney+"",type, new DistributionPresenter.CallBack5() {
            @Override
            public void send(BaseInfo baseInfo) {
                resultDialog(true,baseInfo.getMessage());
            }

            @Override
            public void error(String message) {
                resultDialog(false,message);
            }
        });
    }

    private void resultDialog(boolean isSuccess,String message) {
        if(!ActivityUtil.isActivityOnTop(getContext()))return;
        NodePayDialog nodePayDialog = new NodePayDialog(getActivity());
        nodePayDialog.show();
        if(isSuccess){
            nodePayDialog.setTvTitle(message);
            nodePayDialog.setIvImage(R.mipmap.icon_node_dui);
            EventBus.getDefault().post(new MessageEvent(EventBusUtil.refresh_upgrade));
        }else{
            nodePayDialog.setTvContent(message);
            nodePayDialog.setIvImage(R.mipmap.icon_node_cuo);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
