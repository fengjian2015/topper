package com.bclould.tea.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tea.Presenter.CoinPresenter;
import com.bclould.tea.Presenter.FinanciaPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.model.CoinListInfo;
import com.bclould.tea.model.FinancialInfo;
import com.bclould.tea.ui.adapter.BottomDialogRVAdapter4;
import com.bclould.tea.ui.adapter.FinancialAdapter;
import com.bclould.tea.ui.adapter.FinancialGridAdapter;
import com.bclould.tea.ui.widget.ManagementFundsDialog;
import com.bclould.tea.ui.widget.MyGridView;
import com.bclould.tea.ui.widget.PWDDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.EventBusUtil;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.UtilTool;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.R.style.BottomDialog;

public class FinancialActivity extends BaseActivity {

    @Bind(R.id.tv_all_money)
    TextView mTvAllMoney;
    @Bind(R.id.view_centent)
    View mViewCentent;
    @Bind(R.id.tv_available)
    TextView mTvAvailable;
    @Bind(R.id.tv_yesterday_earnings)
    TextView mTvYesterdayEarnings;
    @Bind(R.id.rl_income_breakdown)
    RelativeLayout mRlIncomeBreakdown;
    @Bind(R.id.rl_financial_record)
    RelativeLayout mRlFinancialRecord;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;
    @Bind(R.id.tv_type)
    TextView mTvType;
    @Bind(R.id.tv_asset_transfer)
    TextView mTvAssetTransfer;
    @Bind(R.id.tv_transfer_assets)
    TextView mTvTransferAssets;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    @Bind(R.id.my_gridview)
    MyGridView mMyGridview;

    private FinancialGridAdapter mFinancialGridAdapter;
    private  List<FinancialInfo.DataBean.IncomeListsBean> income_lists=new ArrayList<>();
    private FinancialAdapter mFinancialAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<FinancialInfo.DataBean.ProductListsBean> mHashMapList = new ArrayList<>();
    private int page = 1;
    private Dialog mBottomDialog;
    private String mCoinName = "";
    private CoinListInfo.DataBean mDataBean = new CoinListInfo.DataBean();
    private FinancialInfo baseInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financial);
        ButterKnife.bind(this);
        setTitle(getString(R.string.manage_money),R.mipmap.icon_transfer);
        mTvTitleTop.setCompoundDrawablePadding(10);
        mTvTitleTop.setCompoundDrawablesWithIntrinsicBounds(null,null, getResources().getDrawable(R.mipmap.icon_right),null);
        EventBus.getDefault().register(this);//初始化EventBus
        initCoin();
        initAdapter();
        setListHight();
    }

    private void initCoin() {
        MyApp.getInstance().mOtcCoinList.clear();
        if (MyApp.getInstance().mOtcCoinList.size() == 0) {
            CoinPresenter coinPresenter = new CoinPresenter(this);
            coinPresenter.coinLists("financial", new CoinPresenter.CallBack() {
                @Override
                public void send(List<CoinListInfo.DataBean> data) {
                    if (ActivityUtil.isActivityOnTop(FinancialActivity.this)) {
                        mLlError.setVisibility(View.GONE);
                        mRefreshLayout.setVisibility(View.VISIBLE);
                        UtilTool.Log(getString(R.string.coins), data.size() + "");
                        if (data.size() == 0) {
                            return;
                        }
                        if (MyApp.getInstance().mOtcCoinList.size() == 0) {
                            MyApp.getInstance().mOtcCoinList.addAll(data);
                            mCoinName = MyApp.getInstance().mOtcCoinList.get(0).getName();
                            setCoinName(mCoinName);
                            mDataBean = MyApp.getInstance().mOtcCoinList.get(0);
                            initHttp(true, 1);
                        }
                    }
                }

                @Override
                public void error() {
                    if (ActivityUtil.isActivityOnTop(FinancialActivity.this)) {
                        mLlError.setVisibility(View.VISIBLE);
                        mRefreshLayout.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    private void initHttp(final boolean isRefresh, int page) {
        new FinanciaPresenter(this).assets(mDataBean.getId(), new FinanciaPresenter.CallBack() {
            @Override
            public void send(FinancialInfo baseInfo) {
                resetRefresh(isRefresh);
                initData(baseInfo, isRefresh);
            }

            @Override
            public void error() {
                resetRefresh(isRefresh);
            }
        });
    }


    private void initData(FinancialInfo baseInfo, boolean isRefresh) {
        this.baseInfo = baseInfo;
        mTvAllMoney.setText(baseInfo.getData().getTotal());
        mTvAvailable.setText(baseInfo.getData().getOver_num());
        mTvYesterdayEarnings.setText(baseInfo.getData().getYesterday_income());

        if (isRefresh) {
            mHashMapList.clear();
            income_lists.clear();
        }
        income_lists.addAll(baseInfo.getData().getIncome_lists()) ;
        mHashMapList.addAll(baseInfo.getData().getProduct_lists());
        mFinancialAdapter.notifyDataSetChanged();
        mFinancialGridAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.bark, R.id.tv_title_top, R.id.tv_asset_transfer, R.id.tv_transfer_assets, R.id.rl_income_breakdown, R.id.rl_financial_record, R.id.iv_more, R.id.ll_error})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_title_top:
                showCoinDialog();
                break;
            case R.id.tv_asset_transfer:
                showTransferDialog(1);
                break;
            case R.id.tv_transfer_assets:
                showTransferDialog(2);
                break;
            case R.id.rl_income_breakdown:
                //  2018/9/30 收益明细
                goHistory(0);
                break;
            case R.id.rl_financial_record:
                // 2018/9/30 理财记录
                goHistory(1);
                break;
            case R.id.iv_more:
                // 2018/10/11 转账记录
                goTransferRecord();
                break;
            case R.id.ll_error:
                initCoin();
                break;
        }
    }

    private void goTransferRecord() {
        Intent intent = new Intent(this, TransferRecordActivity.class);
        intent.putExtra("coin_name", mDataBean.getName());
        intent.putExtra("coin_id", mDataBean.getId());
        startActivity(intent);
    }

    private void goHistory(int type) {
        Intent intent = new Intent(this, FinancialHistoryActivity.class);
        intent.putExtra("coin_name", mDataBean.getName());
        intent.putExtra("coin_id", mDataBean.getId());
        intent.putExtra("type", type);
        startActivity(intent);
    }

    private void showTransferDialog(final int type) {
        if (baseInfo == null) return;
        if(!ActivityUtil.isActivityOnTop(this))return;
        ManagementFundsDialog fundsDialog = new ManagementFundsDialog(this);
        fundsDialog.show();
        if (type == 1) {
            fundsDialog.setContent(type, mDataBean.getName(), mDataBean.getLogo(), mDataBean.getCoin_over());
        } else {
            fundsDialog.setContent(type, mDataBean.getName(), mDataBean.getLogo(), baseInfo.getData().getOver_num());
        }

        fundsDialog.setOnClickListener(new ManagementFundsDialog.OnClickListener() {
            @Override
            public void onClick(String number) {
                pay(number, type);
            }
        });
    }


    private void pay(final String number, final int type) {
        PWDDialog pwdDialog = new PWDDialog(this);
        pwdDialog.setOnPWDresult(new PWDDialog.OnPWDresult() {
            @Override
            public void success(String password) {
                //密码输入成功调用支付
                payHttp(password, type, number);
            }
        });
        if (type == 1) {
            pwdDialog.showDialog(UtilTool.removeZero(number + ""), mDataBean.getName(), getString(R.string.asset_transfer), null, null);
        } else {
            pwdDialog.showDialog(UtilTool.removeZero(number + ""), mDataBean.getName(), getString(R.string.transfer_assets), null, null);
        }
    }

    private void payHttp(String password, int type, String number) {
        if (type == 1) {
            new FinanciaPresenter(this).productIn(mDataBean.getId(), number, password, new FinanciaPresenter.CallBack1() {
                @Override
                public void send(BaseInfo baseInfo) {
                    initHttp(true, 1);
                }

                @Override
                public void error() {

                }
            });
        } else {
            new FinanciaPresenter(this).productOut(mDataBean.getId(), number, password, new FinanciaPresenter.CallBack1() {
                @Override
                public void send(BaseInfo baseInfo) {
                    initHttp(true, 1);
                }

                @Override
                public void error() {

                }
            });
        }
    }


    private void showCoinDialog() {
        mBottomDialog = new Dialog(this, R.style.BottomDialog2);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_bottom, null);
        //获得dialog的window窗口
        Window window = mBottomDialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //将设置好的属性set回去
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(BottomDialog);
        mBottomDialog.setContentView(contentView);
        mBottomDialog.show();
        RecyclerView recyclerView = (RecyclerView) mBottomDialog.findViewById(R.id.recycler_view);
        TextView tvTitle = (TextView) mBottomDialog.findViewById(R.id.tv_title);
        Button addCoin = (Button) mBottomDialog.findViewById(R.id.btn_add_coin);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Button cancel = (Button) mBottomDialog.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomDialog.dismiss();
            }
        });
        recyclerView.setAdapter(new BottomDialogRVAdapter4(this, MyApp.getInstance().mOtcCoinList));
        addCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FinancialActivity.this, MyAssetsActivity.class));
                mBottomDialog.dismiss();
            }
        });
        tvTitle.setText(getString(R.string.coins));
    }

    public void hideDialog(CoinListInfo.DataBean data) {
        mBottomDialog.dismiss();
        mDataBean = data;
        setCoinName(data.getName());
        initHttp(true, 1);
        MessageEvent messageEvent = new MessageEvent(getString(R.string.coin_switchover));
        messageEvent.setCoinName(data.getName());
        EventBus.getDefault().post(messageEvent);
    }

    private void setCoinName(String coinName) {
        mCoinName = coinName;
        mTvTitleTop.setText(coinName + getString(R.string.manage_money));
        mTvType.setText(getString(R.string.total_assets1) + "(" + coinName + ")");
    }

    private void setListHight() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int myHeigth = dm.heightPixels;
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mRecyclerView.getLayoutParams();
        layoutParams.height = (int) (myHeigth - getResources().getDimensionPixelSize(R.dimen.y200) - UtilTool.getStateBar3(this));
        mRecyclerView.setLayoutParams(layoutParams);
    }


    private void resetRefresh(boolean isRefresh) {
        if (isRefresh) {
            mRefreshLayout.finishRefresh();
        } else {
            mRefreshLayout.finishLoadMore();
        }
    }

    private void initAdapter() {
        mRecyclerView.setFocusable(false);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mFinancialAdapter = new FinancialAdapter(this, mHashMapList);
        mRecyclerView.setAdapter(mFinancialAdapter);
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initHttp(true, 1);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
//                initHttp(false,page++);
            }
        });
        mFinancialAdapter.setOnClickListener(new FinancialAdapter.OnclickListener() {
            @Override
            public void onclick(int position) {
                Intent intent = new Intent(FinancialActivity.this, FinancialDetailActivity.class);
                intent.putExtra("coinName", mDataBean.getName());
                intent.putExtra("coinOver", baseInfo.getData().getOver_num());
                intent.putExtra("coinLogo", mDataBean.getLogo());
                intent.putExtra("lock_day", baseInfo.getData().getProduct_lists().get(position).getLock_day());
                intent.putExtra("income_rate", baseInfo.getData().getProduct_lists().get(position).getIncome_rate_value());
                intent.putExtra("product_id", baseInfo.getData().getProduct_lists().get(position).getId());
                intent.putExtra("rate_value", baseInfo.getData().getProduct_lists().get(position).getRate_value());
                intent.putExtra("coin_id", mDataBean.getId());
                intent.putExtra("title",baseInfo.getData().getProduct_lists().get(position).getTitle());
                startActivity(intent);
            }
        });

        //grid
        mFinancialGridAdapter=new FinancialGridAdapter(this,income_lists);
        mMyGridview.setAdapter(mFinancialGridAdapter);
    }


    //接受通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String msg = event.getMsg();
        if (msg.equals(EventBusUtil.refresh_financial)) {
           initHttp(true,1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//初始化EventBus
    }
}
