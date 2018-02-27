package com.bclould.tocotalk.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.ui.adapter.GongGaoRVAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/9/25.
 */

public class GongGaoActivity extends BaseActivity {


    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.more)
    ImageView mMore;
    @Bind(R.id.xx)
    TextView mXx;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private ViewGroup mView;
    private PopupWindow mPopupWindow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gong_gao);
        ButterKnife.bind(this);
        initListView();
        MyApp.getInstance().addActivity(this);
    }

    //初始化条目
    private void initListView() {

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.setAdapter(new GongGaoRVAdapter(this));

    }

    //点击事件的处理
    @OnClick({R.id.bark, R.id.more})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:

                finish();

                break;
            case R.id.more:

                showPop();

                break;
        }
    }

    private void showPop() {

        DisplayMetrics displayMetrics = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int heightPixels = displayMetrics.heightPixels;

        int widthPixels = displayMetrics.widthPixels;

        mView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.pop_gong_gao, null);

        mPopupWindow = new PopupWindow(mView, widthPixels / 4, heightPixels / 5, true);

        mPopupWindow.showAsDropDown(mXx, widthPixels - widthPixels / 4, 0);

        popChildClick();

    }

    private void popChildClick() {

        for (int i = 0; i < mView.getChildCount(); i++) {

            final View childAt = mView.getChildAt(i);

            childAt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int index = mView.indexOfChild(childAt);

                    childAtClick(index);


                }
            });
        }
    }

    private void childAtClick(int index) {

        Toast.makeText(this, "你点击了条目" + index, Toast.LENGTH_SHORT).show();

        mPopupWindow.dismiss();

    }
}
