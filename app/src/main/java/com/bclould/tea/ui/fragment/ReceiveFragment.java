package com.bclould.tea.ui.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bclould.tea.Presenter.RedRecordPresenter;
import com.bclould.tea.R;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.model.RedRecordInfo;
import com.bclould.tea.ui.adapter.ReceiveRVAdapter;
import com.bclould.tea.ui.widget.ChangeTextSpaceView;
import com.bclould.tea.utils.UtilTool;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/9/22.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class ReceiveFragment extends Fragment {

    public static ReceiveFragment instance = null;
    @Bind(R.id.iv_touxiang)
    ImageView mIvTouxiang;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.tv_count)
    ChangeTextSpaceView mTvCount;
    @Bind(R.id.tv_coin)
    TextView mTvCoin;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.tv_red_count)
    TextView mTvRedCount;
    @Bind(R.id.tv_max_money)
    TextView mTvMaxMoney;
    @Bind(R.id.tv_most_coin)
    TextView mTvMostCoin;
    @Bind(R.id.ll_total)
    LinearLayout mLlTotal;
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.ll_no_data)
    LinearLayout mLlNoData;
    private DBManager mDbManager;

    public static ReceiveFragment getInstance() {

        if (instance == null) {

            instance = new ReceiveFragment();

        }

        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_receive, container, false);

        ButterKnife.bind(this, view);
        mDbManager = new DBManager(getContext());
        initData();

        return view;
    }

    private void initData() {
        RedRecordPresenter redRecordPresenter = new RedRecordPresenter(getActivity());
        redRecordPresenter.log("get", new RedRecordPresenter.CallBack() {
            @Override
            public void send(RedRecordInfo.DataBean data) {
                if (!getActivity().isDestroyed()) {
                    if (data.getLog().size() == 0) {
                        mLlTotal.setVisibility(View.GONE);
                    } else {

                        mLlTotal.setVisibility(View.VISIBLE);
                        mLlNoData.setVisibility(View.GONE);
                    }
                    UtilTool.getImage(mDbManager, UtilTool.getTocoId(), getContext(), mIvTouxiang);

//                    mIvTouxiang.setImageBitmap(UtilTool.getImage(mDbManager, UtilTool.getTocoId(), getContext()));
                    mTvName.setText(data.getName() + "共收到");
                    mTvCount.setText(data.getTotal_money() + "");
                    mTvRedCount.setText(data.getRp_number() + "");
                    mTvMaxMoney.setText(data.getMax_money());
                    mTvMostCoin.setText(data.getMost_coin());
                    initRecyclerView(data.getLog());
                }
            }
        });
    }

    private void initRecyclerView(List<RedRecordInfo.DataBean.LogBean> logBeanList) {

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mRecyclerView.setAdapter(new ReceiveRVAdapter(getContext(), logBeanList, true));

        mRecyclerView.setNestedScrollingEnabled(false);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
