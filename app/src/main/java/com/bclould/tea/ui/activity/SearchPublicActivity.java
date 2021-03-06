package com.bclould.tea.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import com.bclould.tea.Presenter.PublicPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.model.PublicInfo;
import com.bclould.tea.ui.adapter.SearchPublicAdapter;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchPublicActivity extends BaseActivity {

    @Bind(R.id.et_content)
    EditText mEtContent;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.tv_no_data)
    TextView mTvNoData;

    private SearchPublicAdapter mSearchPublicAdapter;
    private List<PublicInfo.DataBean> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_public);
        ButterKnife.bind(this);
        init();
        initRecyclerView();
    }


    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSearchPublicAdapter = new SearchPublicAdapter(this, mList);
        mRecyclerView.setAdapter(mSearchPublicAdapter);
    }

    private void init() {
        mEtContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 隐藏键盘
                    ((InputMethodManager) mEtContent.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                    search(mEtContent.getText().toString());
                }
                return false;
            }
        });
    }

    private void search(String content) {
        new PublicPresenter(this).searchList(content, new PublicPresenter.CallBack() {
            @Override
            public void send(PublicInfo dataBean) {
                if (dataBean.getData().size() > 0) {
                    mTvNoData.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                }else{
                    mTvNoData.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                }
                mList.clear();
                mList.addAll(dataBean.getData());
                mSearchPublicAdapter.notifyDataSetChanged();
            }

            @Override
            public void error() {

            }
        });
    }

    @OnClick({R.id.bark})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
        }
    }
}
