package com.bclould.tocotalk.ui.activity;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tocotalk.Presenter.ReceiptPaymentPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.model.TransferListInfo;
import com.bclould.tocotalk.ui.adapter.PayManageGVAdapter;
import com.bclould.tocotalk.ui.adapter.PayRecordRVAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tocotalk.R.style.BottomDialog;

/**
 * Created by GA on 2018/3/20.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class PayRecordActivity extends BaseActivity {
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_filtrate)
    TextView mTvFiltrate;
    @Bind(R.id.tv_date)
    TextView mTvDate;
    @Bind(R.id.rl_date_selector)
    RelativeLayout mRlDateSelector;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private PayRecordRVAdapter mPayRecordRVAdapter;
    private Dialog mBottomDialog;
    String mPage = "1";
    String mPageSize = "1000";
    String mType = "全部";
    String mDate = "2018-03";
    private Map<String, Integer> mMap = new HashMap<>();
    private ReceiptPaymentPresenter mReceiptPaymentPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_record);
        ButterKnife.bind(this);
        mReceiptPaymentPresenter = new ReceiptPaymentPresenter(this);
        initRecycler();
        initMap();
        initData();
    }

    private void initMap() {
        mMap.put("筛选", 0);
    }

    List<TransferListInfo.DataBean> mDataBeanList = new ArrayList<>();

    private void initData() {
        mDataBeanList.clear();
        mReceiptPaymentPresenter.transRecord(mPage, mPageSize, mType, mDate, new ReceiptPaymentPresenter.CallBack4() {
            @Override
            public void send(List<TransferListInfo.DataBean> data) {
                mDataBeanList.addAll(data);
                mPayRecordRVAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initRecycler() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mPayRecordRVAdapter = new PayRecordRVAdapter(this, mDataBeanList);
        mRecyclerView.setAdapter(mPayRecordRVAdapter);
    }

    @OnClick({R.id.bark, R.id.rl_date_selector, R.id.tv_filtrate})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.rl_date_selector:

                break;
            case R.id.tv_filtrate:
                showFiltrateDialog();
                break;
        }
    }

    //显示账单筛选dialog
    String[] mFiltrateArr = {"全部", "红包", "转账", "收付款", "充币", "提币", "银行卡", "入账", "其他"};

    private void showFiltrateDialog() {
        mBottomDialog = new Dialog(this, R.style.BottomDialog2);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_bill, null);
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
        GridView gridView = (GridView) mBottomDialog.findViewById(R.id.grid_view);
        Button cancel = (Button) mBottomDialog.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomDialog.dismiss();
            }
        });
        gridView.setAdapter(new PayManageGVAdapter(this, mFiltrateArr, mMap, new PayManageGVAdapter.CallBack() {
            //接口回调
            @Override
            public void send(int position, String typeName) {
                mType = typeName;
                initData();
                mMap.put("筛选", position);
                mBottomDialog.dismiss();
            }
        }));
    }
}
