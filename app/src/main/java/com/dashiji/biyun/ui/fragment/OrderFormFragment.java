package com.dashiji.biyun.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dashiji.biyun.R;

import butterknife.ButterKnife;


/**
 * Created by GA on 2018/1/15.
 */

public class OrderFormFragment extends android.support.v4.app.Fragment {
    private static OrderFormFragment instance;

    public static OrderFormFragment getInstance() {

        if (instance == null) {

            instance = new OrderFormFragment();

        }

        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_order_form, container, false);

        ButterKnife.bind(this, view);

        initRecyclerView();
        return view;
    }

    private void initRecyclerView() {

    }
}
