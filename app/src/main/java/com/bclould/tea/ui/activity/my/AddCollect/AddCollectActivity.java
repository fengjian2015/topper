package com.bclould.tea.ui.activity.my.AddCollect;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ImageView;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.ui.widget.ClearEditText;
import com.bclould.tea.utils.AnimatorTool;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.MySharedPreferences;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/7/13.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class AddCollectActivity extends BaseActivity implements AddCollectContacts.View{
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.et_titles)
    ClearEditText mEtTitles;
    @Bind(R.id.et_url)
    ClearEditText mEtUrl;

    private AddCollectContacts.Presenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_collect);
        ButterKnife.bind(this);
        mPresenter=new AddCollectPresenter();
        mPresenter.bindView(this);
        mPresenter.start(this);
    }

    @Override
    public void initView() {
        setTitle(getString(R.string.add_collect),getString(R.string.save));
    }

    @OnClick({R.id.bark, R.id.tv_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_add:
                mPresenter.tvAddOnClick();
                break;
        }
    }

    @Override
    public void setEtUrl(String url) {
        mEtUrl.setText(url);
    }

    @Override
    public String getEtUrl() {
        return mEtUrl.getText().toString().trim();
    }

    @Override
    public void animatorEtUrl() {
        AnimatorTool.getInstance().editTextAnimator(mEtUrl);
    }

    @Override
    public String getEtTitles() {
        return mEtTitles.getText().toString();
    }

    public void hideDialog(){
        mPresenter.hideDialog();
    }
}
