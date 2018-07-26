package com.bclould.tea.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.Presenter.IndividualDetailsPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.model.IndividualInfo;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2017/9/28.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class RemarkActivity extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.save)
    TextView mSave;
    @Bind(R.id.et_remark)
    EditText mEtRemark;

    private String mName;
    private DBManager mMgr;
    private String mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remark);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        mMgr = new DBManager(this);
        init();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, newBase.getString(R.string.language_pref_key)));
    }

    private void init() {
        mName=getIntent().getStringExtra("name");
        mUser=getIntent().getStringExtra("user");
        String remark=getIntent().getStringExtra("remark");
        mEtRemark.setText(remark);
        mEtRemark.setSelection(mEtRemark.getText().length());
    }


    @OnClick({R.id.bark, R.id.save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.save:
                preserve();
                break;
        }
    }

    private void preserve() {
        String remarkName = mEtRemark.getText().toString().trim();
        if(remarkName.isEmpty()){
            Toast.makeText(this, getString(R.string.toast_remark), Toast.LENGTH_SHORT).show();
        }else{
            IndividualDetailsPresenter presenter = new IndividualDetailsPresenter(this);
            presenter.getChangeRemark(mUser,remarkName, new IndividualDetailsPresenter.CallBack() {
                @Override
                public void send(IndividualInfo.DataBean data) {
                    mMgr.updateRemark(data.getRemark(), mUser);
                    EventBus.getDefault().post(new MessageEvent(getString(R.string.change_friend_remark)));
                    Intent intent = new Intent(RemarkActivity.this, IndividualDetailsActivity.class);
                    intent.putExtra("remark", data.getRemark());
                    setResult(RESULT_OK, intent);
                    finish();
                }

                @Override
                public void error() {

                }
            });
        }
    }
}
