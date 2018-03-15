package com.bclould.tocotalk.ui.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bclould.tocotalk.Presenter.SubscribeCoinPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.model.MyAssetsInfo;
import com.bclould.tocotalk.ui.adapter.MyWalletRVAapter;
import com.bclould.tocotalk.utils.UtilTool;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by GA on 2017/9/22.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class MyAssetsActivity extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_subscription)
    TextView mTvSubscription;
    @Bind(R.id.et_coin_name)
    EditText mEtCoinName;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    List<MyAssetsInfo.DataBean> mDataList = new ArrayList();
    private MyWalletRVAapter mMyWalletRVAapter;
    private ViewGroup mPopupWindowView;
    private PopupWindow mPopupWindow;

    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_assets);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        initRecyclerView();
        initData();
    }

    private void initData() {
        SubscribeCoinPresenter subscribeCoinPresenter = new SubscribeCoinPresenter(this);
        subscribeCoinPresenter.getMyAssets(new SubscribeCoinPresenter.CallBack() {
            @Override
            public void send(List<MyAssetsInfo.DataBean> info) {
                mDataList.addAll(info);
                mMyWalletRVAapter.notifyDataSetChanged();
            }
        });
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(15));
        mMyWalletRVAapter = new MyWalletRVAapter(this, mDataList);
        mRecyclerView.setAdapter(mMyWalletRVAapter);
        mMyWalletRVAapter.setOnItemClickListener(new MyWalletRVAapter.OnItemClickListener() {
            @Override
            public void onClick(int position, MyAssetsInfo.DataBean dataBean) {
                View childAt = mRecyclerView.getChildAt(position);
                initPopupWindow(childAt, dataBean);
            }
        });
    }

    private void initPopupWindow(View view, final MyAssetsInfo.DataBean dataBean) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int widthPixels = dm.widthPixels;
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        UtilTool.Log("坐标", "x" + location[0] + ":y" + location[1]);
        mPopupWindowView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.pop_assets, null);
        mPopupWindow = new PopupWindow(mPopupWindowView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setAnimationStyle(R.style.AnimationRightFade);
        mPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, widthPixels - mPopupWindow.getWidth(), location[1]);
        final Button inCoin = (Button) mPopupWindowView.findViewById(R.id.btn_in_coin);
        inCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyAssetsActivity.this, InCoinActivity.class);
                intent.putExtra("id", dataBean.getId());
                intent.putExtra("coinName", dataBean.getName());
                intent.putExtra("over", dataBean.getOver());
                startActivity(intent);
            }
        });
        Button outCoin = (Button) mPopupWindowView.findViewById(R.id.btn_out_coin);
        outCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyAssetsActivity.this, OutCoinActivity.class);
                intent.putExtra("id", dataBean.getId());
                intent.putExtra("coinName", dataBean.getName());
                intent.putExtra("over", dataBean.getOver());
                startActivity(intent);
            }
        });
        Button transferAccounts = (Button) mPopupWindowView.findViewById(R.id.btn_transfer_accounts);
        transferAccounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyAssetsActivity.this, TransferAccountsActivity.class);
                intent.putExtra("id", dataBean.getId());
                intent.putExtra("coinName", dataBean.getName());
                intent.putExtra("over", dataBean.getOver());
                startActivity(intent);
            }
        });
    }

    @OnClick({R.id.bark, R.id.tv_subscription, R.id.et_coin_name})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_subscription:
                startActivity(new Intent(this, SubscribeCoinActivity.class));
                break;
            case R.id.et_coin_name:

                break;
        }
    }

    class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        int mSpace;

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.bottom = mSpace;
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = mSpace;
            }
        }

        public SpaceItemDecoration(int space) {
            this.mSpace = space;
        }
    }
}
