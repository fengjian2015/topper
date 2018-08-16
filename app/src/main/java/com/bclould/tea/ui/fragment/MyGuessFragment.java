package com.bclould.tea.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bclould.tea.R;
import com.bclould.tea.ui.activity.MyJoinActivity;
import com.bclould.tea.ui.activity.MyStartActivity;
import com.bclould.tea.ui.activity.StartGuessActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/4/23.
 */

public class MyGuessFragment extends Fragment {


    @Bind(R.id.rl_start_guess)
    RelativeLayout mRlStartGuess;
    @Bind(R.id.rl_my_join)
    RelativeLayout mRlMyJoin;
    @Bind(R.id.rl_my_start)
    RelativeLayout mRlMyStart;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_my_guess, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.rl_start_guess, R.id.rl_my_join, R.id.rl_my_start})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_start_guess:
                startActivity(new Intent(getContext(), StartGuessActivity.class));
                break;
            case R.id.rl_my_join:
                startActivity(new Intent(getContext(), MyJoinActivity.class));
                break;
            case R.id.rl_my_start:
                startActivity(new Intent(getContext(), MyStartActivity.class));
                break;
        }
    }
}
