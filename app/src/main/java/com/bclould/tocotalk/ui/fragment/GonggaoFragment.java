package com.bclould.tocotalk.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bclould.tocotalk.R;

import butterknife.ButterKnife;

/**
 * Created by GA on 2018/5/7.
 */

public class GonggaoFragment extends Fragment {
    private static GonggaoFragment instance;

    public static GonggaoFragment getInstance() {
        if (instance == null) {
            instance = new GonggaoFragment();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_gonggao, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
