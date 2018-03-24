package com.bclould.tocotalk.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.ui.activity.BankCardActivity;
import com.bclould.tocotalk.ui.activity.MyAssetsActivity;
import com.bclould.tocotalk.ui.activity.OtcActivity;
import com.bclould.tocotalk.ui.activity.PayCentreActivity;
import com.bclould.tocotalk.ui.activity.ReceiptPaymentActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by GA on 2017/9/19.
 */

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

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.iv_more, R.id.ll_inout, R.id.ll_usdt, R.id.ll_bank_card, R.id.ll_asserts, R.id.ll_exchange, R.id.ll_otc, R.id.ll_financing, R.id.ll_pawn, R.id.ll_safe})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_more:
                startActivity(new Intent(getActivity(), PayCentreActivity.class));
                break;
            case R.id.ll_inout:
                startActivity(new Intent(getActivity(), ReceiptPaymentActivity.class));
                break;
            case R.id.ll_usdt:
                break;
            case R.id.ll_bank_card:
                startActivity(new Intent(getActivity(), BankCardActivity.class));
                break;
            case R.id.ll_asserts:
                startActivity(new Intent(getActivity(), MyAssetsActivity.class));
                break;
            case R.id.ll_exchange:
                break;
            case R.id.ll_otc:
                startActivity(new Intent(getActivity(), OtcActivity.class));
                break;
            case R.id.ll_financing:
                break;
            case R.id.ll_pawn:
                break;
            case R.id.ll_safe:
                break;
        }
    }
}