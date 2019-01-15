package com.bclould.tea.ui.fragment;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.model.CardInfo;
import com.bclould.tea.topperchat.WsConnection;
import com.bclould.tea.ui.activity.AddFriendActivity;
import com.bclould.tea.ui.activity.BlockchainGambleActivity;
import com.bclould.tea.ui.activity.CoinExchangeActivity;
import com.bclould.tea.ui.activity.FGCExchangeActivity;
import com.bclould.tea.ui.activity.FinancialActivity;
import com.bclould.tea.ui.activity.GroupListActivity;
import com.bclould.tea.ui.activity.InitialActivity;
import com.bclould.tea.ui.activity.MyFriendActivity;
import com.bclould.tea.ui.activity.NewsManagerActivity;
import com.bclould.tea.ui.activity.OtcActivity;
import com.bclould.tea.ui.activity.PayRecordActivity;
import com.bclould.tea.ui.activity.PublicActivity;
import com.bclould.tea.ui.activity.ReceiptPaymentActivity;
import com.bclould.tea.ui.activity.ScanQRCodeActivity;
import com.bclould.tea.ui.activity.SendQRCodeRedActivity;
import com.bclould.tea.ui.activity.wallet.exchangefrc.ExchangeFRCActivity;
import com.bclould.tea.ui.adapter.WalletPVAdapter;
import com.bclould.tea.ui.widget.MenuListPopWindow2;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.ShadowTransformer;
import com.bclould.tea.utils.StatusBarCompat;
import com.bclould.tea.utils.permissions.AuthorizationUserTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by GA on 2017/9/19.
 */

public class WalletFragment extends Fragment {

    public static WalletFragment instance = null;
    @Bind(R.id.status_bar_fix)
    View mStatusBarFix;
    @Bind(R.id.viewPager)
    ViewPager mViewPager;
    @Bind(R.id.iv4)
    ImageView mIv4;
    @Bind(R.id.rl_otc)
    RelativeLayout mRlOtc;
    @Bind(R.id.iv5)
    ImageView mIv5;
    @Bind(R.id.rl_guess)
    RelativeLayout mRlGuess;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.rl_exchange)
    RelativeLayout mRlExchange;
    @Bind(R.id.iv3)
    ImageView mIv3;
    @Bind(R.id.tv)
    TextView mTv;
    @Bind(R.id.rl_my_ad)
    RelativeLayout mRlMyAd;
    @Bind(R.id.iv_bill)
    ImageView mIvBill;
    @Bind(R.id.rl_menu)
    RelativeLayout mRlMenu;
    private WalletPVAdapter mWalletPVAdapter;
    private ShadowTransformer mCardShadowTransformer;

    public static WalletFragment getInstance() {
        if (instance == null) {

            instance = new WalletFragment();

        }

        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_wallet, container, false);
        ButterKnife.bind(this, view);
        mStatusBarFix.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, StatusBarCompat.getStateBarHeight(getActivity())));
        initViewPage();
        return view;
    }



    private void initViewPage() {
        initData();
        mWalletPVAdapter = new WalletPVAdapter(getContext(), mViews, mDataList);

        mCardShadowTransformer = new ShadowTransformer(mViewPager, mWalletPVAdapter);
        mCardShadowTransformer.enableScaling(true);
        mViewPager.setAdapter(mWalletPVAdapter);
        mViewPager.setCurrentItem(1);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private List<CardView> mViews = new ArrayList<>();
    private List<CardInfo> mDataList = new ArrayList<>();

    private void initData() {
        if (mDataList.size() == 0) {
            mViews.add(null);
            mViews.add(null);
            mViews.add(null);
            mDataList.add(new CardInfo(R.mipmap.icon_receiving, getString(R.string.receipt_payment), R.mipmap.icon_receiving_bg));
            mDataList.add(new CardInfo(R.mipmap.icon_assetscard, getString(R.string.my_address), R.mipmap.icon_assetscard_bg));
            mDataList.add(new CardInfo(R.mipmap.icon_bankcard, getString(R.string.bank_card), R.mipmap.icon_bankcard_bg));
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViews.clear();
        mDataList.clear();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.rl_otc, R.id.rl_guess, R.id.rl_exchange, R.id.rl_my_ad, R.id.rl_financial_management, R.id.rl_fgc_exchange, R.id.iv_menu,R.id.rl_exchange_frc})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_otc:
                if (!WsConnection.getInstance().getOutConnection()) {
                    startActivity(new Intent(getActivity(), OtcActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), InitialActivity.class));
                }
                break;
            case R.id.rl_guess:
                if (!WsConnection.getInstance().getOutConnection()) {
                    startActivity(new Intent(getActivity(), BlockchainGambleActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), InitialActivity.class));
                }
                break;
            case R.id.rl_exchange:
                if (!WsConnection.getInstance().getOutConnection()) {
                    startActivity(new Intent(getActivity(), CoinExchangeActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), InitialActivity.class));
                }
                break;
            case R.id.rl_my_ad:
                if (!WsConnection.getInstance().getOutConnection()) {
                    startActivity(new Intent(getActivity(), NewsManagerActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), InitialActivity.class));
                }
                break;
            case R.id.rl_financial_management:
                if (!WsConnection.getInstance().getOutConnection()) {
                    startActivity(new Intent(getActivity(), FinancialActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), InitialActivity.class));
                }
                break;
            case R.id.rl_fgc_exchange:
                if (!WsConnection.getInstance().getOutConnection()) {
                    startActivity(new Intent(getActivity(), FGCExchangeActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), InitialActivity.class));
                }
                break;
            case R.id.iv_menu:
                initPopWindow();
                break;
            case R.id.rl_exchange_frc:
                if (!WsConnection.getInstance().getOutConnection()) {
                    startActivity(new Intent(getActivity(), ExchangeFRCActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), InitialActivity.class));
                }
                break;
        }
    }

    private void initPopWindow() {
        List<HashMap> list=new ArrayList<>();
        list.add(MenuListPopWindow2.setHashMapData(getContext(),R.string.receipt_payment,R.mipmap.icon_news_receiving));
        list.add(MenuListPopWindow2.setHashMapData(getContext(),R.string.qr_code_red_package,R.mipmap.icon_news_codeenvelope));
        list.add(MenuListPopWindow2.setHashMapData(getContext(),R.string.add_friend,R.mipmap.icon_talk_add));
        final MenuListPopWindow2 menuListPopWindow2=new MenuListPopWindow2(getActivity(),(int) (getResources().getDimension(R.dimen.y300)),list);
        if(!ActivityUtil.isActivityOnTop(getContext()))return;
        menuListPopWindow2.showAsDropDown(mRlMenu,30, 0);
        menuListPopWindow2.setListOnClick(new MenuListPopWindow2.ListOnClick() {
            @Override
            public void onclickitem(int position) {
                switch (position) {
                    case 0:
                        if (!WsConnection.getInstance().getOutConnection()) {
                            startActivity(new Intent(getActivity(), ReceiptPaymentActivity.class));
                        } else {
                            startActivity(new Intent(getActivity(), InitialActivity.class));
                        }
                        menuListPopWindow2.dismiss();
                        break;
                    case 1:
                        if (!WsConnection.getInstance().getOutConnection()) {
                            startActivity(new Intent(getActivity(), SendQRCodeRedActivity.class));
                        } else {
                            startActivity(new Intent(getActivity(), InitialActivity.class));
                        }
                        menuListPopWindow2.dismiss();
                        break;
                    case 2:
                        if (!WsConnection.getInstance().getOutConnection()) {
                            startActivity(new Intent(getActivity(), AddFriendActivity.class));
                        } else {
                            startActivity(new Intent(getActivity(), InitialActivity.class));
                        }
                        menuListPopWindow2.dismiss();
                        break;
                }
            }
        });
    }


    @OnClick(R.id.iv_bill)
    public void onViewClicked() {
        Intent intent = new Intent(getActivity(), PayRecordActivity.class);
        intent.putExtra("type", "0");
        startActivity(intent);
    }
}
