package com.bclould.tea.ui.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bclould.tea.Presenter.SubscribeCoinPresenter;
import com.bclould.tea.R;
import com.bclould.tea.ui.activity.BankCardActivity;
import com.bclould.tea.ui.activity.BlockchainGambleActivity;
import com.bclould.tea.ui.activity.CoinExchangeActivity;
import com.bclould.tea.ui.activity.FinancingActivity;
import com.bclould.tea.ui.activity.MyAssetsActivity;
import com.bclould.tea.ui.activity.OtcActivity;
import com.bclould.tea.ui.activity.PawnActivity;
import com.bclould.tea.ui.activity.PayRecordActivity;
import com.bclould.tea.ui.activity.ReceiptPaymentActivity;
import com.bclould.tea.ui.activity.SafeActivity;
import com.bclould.tea.utils.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by GA on 2017/9/19.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class WalletFragment extends Fragment {

    public static WalletFragment instance = null;
    @Bind(R.id.iv_more)
    ImageView mIvMore;
    @Bind(R.id.ll_inout)
    LinearLayout mLlInout;
    @Bind(R.id.ll_usdt)
    LinearLayout mLlUsdt;
    @Bind(R.id.ll_bank_card)
    LinearLayout mLlBankCard;
    @Bind(R.id.ll_asserts)
    LinearLayout mLlAsserts;
    @Bind(R.id.ll_exchange)
    LinearLayout mLlExchange;
    @Bind(R.id.ll_otc)
    LinearLayout mLlOtc;
    @Bind(R.id.ll_financing)
    LinearLayout mLlFinancing;
    @Bind(R.id.ll_pawn)
    LinearLayout mLlPawn;
    @Bind(R.id.ll_safe)
    LinearLayout mLlSafe;
    @Bind(R.id.tv_total)
    TextView mTvTotal;


    public static WalletFragment getInstance() {
        if (instance == null) {

            instance = new WalletFragment();

        }

        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_cloud_coin, container, false);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(getString(R.string.transfer))) {
            initData();
        }
    }


    private void initData() {
        if (isAdded()) {
            SubscribeCoinPresenter subscribeCoinPresenter = new SubscribeCoinPresenter(getContext());
            subscribeCoinPresenter.getUSDT(new SubscribeCoinPresenter.CallBack3() {
                @Override
                public void send(String data) {
                    if (data != null && mTvTotal != null) {
                        mTvTotal.setText(data);
                    }
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.ll_blockchain_gamble, R.id.iv_more, R.id.ll_inout, R.id.ll_usdt, R.id.ll_bank_card, R.id.ll_asserts, R.id.ll_exchange, R.id.ll_otc, R.id.ll_financing, R.id.ll_pawn, R.id.ll_safe})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_more:
                Intent intent = new Intent(getActivity(), PayRecordActivity.class);
                intent.putExtra("type", "0");
                startActivity(intent);
                break;
            case R.id.ll_inout:
                startActivity(new Intent(getActivity(), ReceiptPaymentActivity.class));
                break;
            case R.id.ll_usdt:
                initData();
                break;
            case R.id.ll_bank_card:
                startActivity(new Intent(getActivity(), BankCardActivity.class));
                break;
            case R.id.ll_asserts:
                startActivity(new Intent(getActivity(), MyAssetsActivity.class));
                break;
            case R.id.ll_exchange:
                startActivity(new Intent(getActivity(), CoinExchangeActivity.class));
                break;
            case R.id.ll_otc:
                startActivity(new Intent(getActivity(), OtcActivity.class));
                break;
            case R.id.ll_financing:
                startActivity(new Intent(getActivity(), FinancingActivity.class));
                break;
            case R.id.ll_pawn:
                startActivity(new Intent(getActivity(), PawnActivity.class));
                break;
            case R.id.ll_safe:
                startActivity(new Intent(getActivity(), SafeActivity.class));
                break;
            case R.id.ll_blockchain_gamble:
                startActivity(new Intent(getActivity(), BlockchainGambleActivity.class));
                break;
        }
    }
}
