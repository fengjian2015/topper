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
import android.widget.LinearLayout;

import com.bclould.tea.Presenter.DillDataPresenter;
import com.bclould.tea.R;
import com.bclould.tea.model.InOutInfo;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GA on 2018/3/4.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class InOutDataFragment extends Fragment {

    public static InOutDataFragment instance = null;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.ll_no_data)
    LinearLayout mLlNoData;

    //对外提供方法获取对象
    public static InOutDataFragment getInstance() {

        if (instance == null) {

            instance = new InOutDataFragment();

        }

        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_bill_data, container, false);
        ButterKnife.bind(this, view);
        initData();
        return view;
    }

    private void initData() {
        DillDataPresenter dillDataPresenter = new DillDataPresenter(getContext());
        dillDataPresenter.getInOutData(getString(R.string.out_coin),"1", new DillDataPresenter.CallBack2() {
            @Override
            public void send(List<InOutInfo.DataBean> data) {
                if (mRecyclerView != null) {
                    if (data.size() == 0) {
                        mLlNoData.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.GONE);
                    } else {
                        mLlNoData.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }
                    initListView(data);
                }
            }
        });
    }

    //初始化条目
    private void initListView(List<InOutInfo.DataBean> data) {

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

//        mRecyclerView.setAdapter(new InOutDataRVAapter(getContext(), data, mType, mCoinName));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
