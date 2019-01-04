package com.bclould.tea.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.bclould.tea.Presenter.DillDataPresenter;
import com.bclould.tea.R;
import com.bclould.tea.model.TransferInfo;
import com.bclould.tea.ui.adapter.BillDataRVAapter;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2017/9/22.
 */

public class BillDataFragment extends Fragment {

    public static BillDataFragment instance = null;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.ll_no_data)
    LinearLayout mLlNoData;
    private BillDataRVAapter mBillDataRVAapter;

    //对外提供方法获取对象
    public static BillDataFragment getInstance() {

        if (instance == null) {

            instance = new BillDataFragment();

        }

        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_bill_data, container, false);
        ButterKnife.bind(this, view);
        initListView();
        initData();
        return view;
    }

    List<TransferInfo.DataBean> mDataList = new ArrayList<>();

    private void initData() {
        DillDataPresenter dillDataPresenter = new DillDataPresenter(getContext());
        dillDataPresenter.getTransfer(new DillDataPresenter.CallBack() {
            @Override
            public void send(List<TransferInfo.DataBean> data) {
                if (mRecyclerView != null) {
                    if (data.size() != 0) {
                        mDataList.clear();
                        mDataList.addAll(data);
                        mBillDataRVAapter.notifyDataSetChanged();
                        mLlNoData.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.GONE);
                    } else {
                        mLlNoData.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void error() {

            }
        });
    }

    //初始化条目
    private void initListView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBillDataRVAapter = new BillDataRVAapter(getActivity(), mDataList);
        mRecyclerView.setAdapter(mBillDataRVAapter);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
