package com.bclould.tocotalk.ui.fragment;

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

import com.bclould.tocotalk.Presenter.RedRecordPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.RedRecordInfo;
import com.bclould.tocotalk.ui.adapter.ReceiveRVAdapter;
import com.bclould.tocotalk.ui.widget.ChangeTextSpaceView;
import com.bclould.tocotalk.utils.UtilTool;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/9/22.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class EmitFragment extends Fragment {

    public static EmitFragment instance = null;
    @Bind(R.id.iv_touxiang)
    ImageView mIvTouxiang;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.tv_count)
    ChangeTextSpaceView mTvCount;
    @Bind(R.id.tv_coin)
    TextView mTvCoin;
    @Bind(R.id.tv_red_count)
    TextView mTvRedCount;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
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
    List<RedRecordInfo.DataBean.LogBean> mLogBeanList = new ArrayList<>();
    private ReceiveRVAdapter mReceiveRVAdapter;


    public static EmitFragment getInstance() {

        if (instance == null) {

            instance = new EmitFragment();

        }

        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_emit, container, false);

        ButterKnife.bind(this, view);

        mDbManager = new DBManager(getContext());

        initRecyclerView();

        initData();

        return view;
    }

    private void initData() {
        RedRecordPresenter redRecordPresenter = new RedRecordPresenter(getActivity());
        redRecordPresenter.log("send", new RedRecordPresenter.CallBack() {
            @Override
            public void send(RedRecordInfo.DataBean data) {
                if (!getActivity().isDestroyed()) {
                    if (data.getLog().size() == 0) {
                        mLlTotal.setVisibility(View.GONE);
                        mLlNoData.setVisibility(View.VISIBLE);
                    } else {
                        mLlTotal.setVisibility(View.VISIBLE);
                        mLlNoData.setVisibility(View.GONE);
                    }
                    UtilTool.getImage(mDbManager, UtilTool.getTocoId(), getContext(), mIvTouxiang);
                    mTvName.setText(data.getName() + getString(R.string.sum_send));
                    mTvCount.setText(data.getTotal_money() + "");
                    mTvRedCount.setText(data.getRp_number() + "");
                    mTvMaxMoney.setText(data.getMax_money());
                    mTvMostCoin.setText(data.getMost_coin());
                    mLogBeanList.addAll(data.getLog());
                    mReceiveRVAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void initRecyclerView() {

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        mReceiveRVAdapter = new ReceiveRVAdapter(getContext(), mLogBeanList, false);

        mRecyclerView.setAdapter(mReceiveRVAdapter);

        mRecyclerView.setNestedScrollingEnabled(false);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
