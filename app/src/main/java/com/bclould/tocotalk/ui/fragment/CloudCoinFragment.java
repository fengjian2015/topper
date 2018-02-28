package com.bclould.tocotalk.ui.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tocotalk.Presenter.SubscribeCoinPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.MyAssetsInfo;
import com.bclould.tocotalk.ui.activity.SubscribeCoinActivity;
import com.bclould.tocotalk.ui.adapter.CloudCoinRVAdapter;
import com.bclould.tocotalk.ui.widget.CurrencyDialog;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.StatusBarCompat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by GA on 2017/9/19.
 */

public class CloudCoinFragment extends Fragment {

    public static CloudCoinFragment instance = null;
    @Bind(R.id.status_bar_fix)
    View mStatusBarFix;
    @Bind(R.id.tv_add_coin)
    TextView mTvAddCoin;
    @Bind(R.id.btn_refresh)
    Button mBtnRefresh;
    @Bind(R.id.btn_subscribe_coin)
    Button mBtnSubscribeCoin;
    @Bind(R.id.rl_subscribe_coin)
    RelativeLayout mRlSubscribeCoin;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.tv_conversion)
    TextView mTvConversion;
    @Bind(R.id.btn_selector_currency)
    Button mBtnSelectorCurrency;
    @Bind(R.id.xx2)
    TextView mXx2;
    @Bind(R.id.tv_exchange)
    Button mTvExchange;
    @Bind(R.id.card_view)
    CardView mCardView;
    @Bind(R.id.xx3)
    TextView mXx3;
    @Bind(R.id.et_count)
    EditText mEtCount;
    @Bind(R.id.tv_have_currency)
    TextView mTvHaveCurrency;
    @Bind(R.id.tv_all_exchange)
    TextView mTvAllExchange;
    @Bind(R.id.btn_selector_currency2)
    Button mBtnSelectorCurrency2;
    @Bind(R.id.tv_count)
    TextView mTvCount;
    @Bind(R.id.tv_have_currency2)
    TextView mTvHaveCurrency2;
    @Bind(R.id.xx4)
    TextView mXx4;
    @Bind(R.id.btn_exchange)
    Button mBtnExchange;

    public static CloudCoinFragment getInstance() {

        if (instance == null) {

            instance = new CloudCoinFragment();

        }

        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_cloud_coin, container, false);

        ButterKnife.bind(this, view);

        EventBus.getDefault().register(this);

        mStatusBarFix.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, StatusBarCompat.getStateBarHeight(getActivity())));//填充状态栏

        getMyAssets();

        init();

        return view;
    }

    private void init() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(20));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals("取消或者订阅成功")) {
            getMyAssets();
        }
    }

    //获取用户资产
    private void getMyAssets() {
        SubscribeCoinPresenter subscribeCoinPresenter = new SubscribeCoinPresenter(getContext());
        subscribeCoinPresenter.getMyAssets(new SubscribeCoinPresenter.CallBack() {
            @Override
            public void send(List<MyAssetsInfo.DataBean> info) {
                List<MyAssetsInfo.DataBean> dataBeanList = new ArrayList<>();
                for (int i = 0; i < info.size(); i++) {
                    if (info.get(i).getStatus() == 1) {
                        dataBeanList.add(info.get(i));
                    }
                }
                if (dataBeanList.size() != 0) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mRlSubscribeCoin.setVisibility(View.GONE);
                    initRecyclerView(dataBeanList);
                } else {
                    mRlSubscribeCoin.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                }
            }
        });
    }

    //初始化RecyclerView
    private void initRecyclerView(List<MyAssetsInfo.DataBean> data) {
        mRecyclerView.setAdapter(new CloudCoinRVAdapter(getActivity(), data));
    }

    @OnClick({R.id.btn_refresh, R.id.btn_subscribe_coin, R.id.btn_selector_currency, R.id.btn_selector_currency2, R.id.btn_exchange, R.id.tv_add_coin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_refresh:
                getMyAssets();
                break;
            case R.id.btn_subscribe_coin:
                //跳转订阅币种界面
                startActivity(new Intent(getActivity(), SubscribeCoinActivity.class));
                break;
            case R.id.tv_add_coin:
                startActivity(new Intent(getActivity(), SubscribeCoinActivity.class));
                break;
        }
    }

    //显示币种弹框
    private void showDialog(int index) {

        CurrencyDialog currencyDialog = new CurrencyDialog(R.layout.dialog_currency, getContext(), R.style.dialog);

        Window window = currencyDialog.getWindow();

        window.setWindowAnimations(R.style.CustomDialog);

        currencyDialog.show();

        dialogClick(currencyDialog, index);

    }

    //Dialog的点击事件处理
    private void dialogClick(final CurrencyDialog dialog, final int index) {

        final Button btc = (Button) dialog.findViewById(R.id.btc);
        final Button ltc = (Button) dialog.findViewById(R.id.ltc);
        final Button eth = (Button) dialog.findViewById(R.id.dogo);
        final Button xmr = (Button) dialog.findViewById(R.id.zec);
        final Button usdt = (Button) dialog.findViewById(R.id.lsk);
        final Button rmbx = (Button) dialog.findViewById(R.id.maid);
        final Button shc = (Button) dialog.findViewById(R.id.shc);
        final Button ans = (Button) dialog.findViewById(R.id.ans);
        final Button tpc = (Button) dialog.findViewById(R.id.tpc);

        shc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCurrencyStyle(index, shc, dialog, shc.getText());
            }
        });

        ans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCurrencyStyle(index, ans, dialog, ans.getText());
            }
        });

        tpc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCurrencyStyle(index, tpc, dialog, tpc.getText());
            }
        });

        btc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCurrencyStyle(index, btc, dialog, btc.getText());
            }
        });

        ltc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setCurrencyStyle(index, ltc, dialog, ltc.getText());
            }
        });

        eth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setCurrencyStyle(index, eth, dialog, eth.getText());
            }
        });

        xmr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setCurrencyStyle(index, xmr, dialog, xmr.getText());
            }
        });

        usdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setCurrencyStyle(index, usdt, dialog, usdt.getText());
            }
        });

        rmbx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setCurrencyStyle(index, rmbx, dialog, rmbx.getText());
            }
        });

    }

    //设置货币样式
    private void setCurrencyStyle(int index, Button btc, CurrencyDialog dialog, CharSequence text) {

        if (index == 1) {

            if (!mBtnSelectorCurrency2.getText().equals(text)) {

                mBtnSelectorCurrency.setText(text);

                Toast.makeText(getActivity(), getString(R.string.toast_selector) + btc.getText(), Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(getActivity(), getString(R.string.toast_inconvertibility), Toast.LENGTH_SHORT).show();
            }

        } else {

            if (!mBtnSelectorCurrency.getText().equals(text)) {

                mBtnSelectorCurrency2.setText(text);

                Toast.makeText(getActivity(), getString(R.string.toast_selector) + btc.getText(), Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(getActivity(), getString(R.string.toast_inconvertibility), Toast.LENGTH_SHORT).show();

            }
        }
        dialog.dismiss();
    }

    //兑换
    private void currencyExchange() {

        String count = mEtCount.getText().toString().trim();

        if (count.isEmpty()) {

            Toast.makeText(getActivity(), getString(R.string.toast_count), Toast.LENGTH_SHORT).show();

        } else {

            Toast.makeText(getActivity(), getString(R.string.toast_exchange), Toast.LENGTH_SHORT).show();

        }


    }

    public void setData(List<MyAssetsInfo.DataBean> data) {

    }

    //动态设置条目间隔
    class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        int mSpace;

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.left = mSpace;
            outRect.right = mSpace;
            outRect.top = mSpace;


        }

        public SpaceItemDecoration(int space) {
            this.mSpace = space;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
