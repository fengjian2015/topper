package com.bclould.tea.ui.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bclould.tea.Presenter.CurrencyInOutPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.UtilTool;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/3/15.
 */

public class InCoinActivity extends BaseActivity {
    @Bind(R.id.tv_count)
    TextView mTvCount;
    @Bind(R.id.iv_site_qr)
    ImageView mIvSiteQr;
    @Bind(R.id.tv_site)
    TextView mTvSite;
    @Bind(R.id.btn_copy)
    Button mBtnCopy;
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_desc)
    TextView mTvDesc;
    @Bind(R.id.ll_data)
    LinearLayout mLlData;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.ll_error)
    LinearLayout mLlError;
    private int mId;
    private String mCoinName;
    private String mOver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_coin);
        ButterKnife.bind(this);
        setTitle(getString(R.string.in_coin),getString(R.string.record));
        initIntent();
        initData();

    }

    private void initIntent() {
        Intent intent = getIntent();
        mId = intent.getIntExtra("id", 0);
        mCoinName = intent.getStringExtra("coinName");
        mOver = intent.getStringExtra("over");
        mTvCount.setText(mOver + mCoinName);

    }

    private void initData() {
        CurrencyInOutPresenter currencyInOutPresenter = new CurrencyInOutPresenter(this);
        currencyInOutPresenter.inCoin(mId, new CurrencyInOutPresenter.CallBack() {
            @Override
            public void send(BaseInfo.DataBean data) {
                try {
                    if (ActivityUtil.isActivityOnTop(InCoinActivity.this)) {
                        mLlData.setVisibility(View.VISIBLE);
                        mLlError.setVisibility(View.GONE);
                        if (data.getDesc() != null && data.getAddress() != null && data.getTitle() != null) {
                            String desc = data.getDesc().replace("\\n", "\n");
                            mTvTitle.setText(data.getTitle());
                            mTvDesc.setText(desc);

                            HashMap qrCardInfo=new HashMap();
                            qrCardInfo.put("coin",mCoinName);
                            qrCardInfo.put("address",data.getAddress());
                            qrCardInfo.put("cid",mId+"");
                            Bitmap qrImage = UtilTool.createQRImage(UtilTool.base64PetToJson( Constants.QOUCOIN,qrCardInfo ));
//                            JSONObject object = new JSONObject();//创建一个总的对象，这个对象对整个json串
//                            object.put("coin", mCoinName);
//                            object.put("address", data.getAddress());
//                            String jsonresult = object.toString();//生成返回字符串
//                            Bitmap qrImage = UtilTool.createQRImage(jsonresult);
                            mIvSiteQr.setImageBitmap(qrImage);
                            mTvSite.setText(data.getAddress());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void error() {
                if (ActivityUtil.isActivityOnTop(InCoinActivity.this)) {
                    mLlData.setVisibility(View.GONE);
                    mLlError.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @OnClick({R.id.ll_error, R.id.bark, R.id.tv_add, R.id.btn_copy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.ll_error:
                initData();
                break;
            case R.id.tv_add:
                Intent intent = new Intent(this, BillDetailsActivity.class);
                intent.putExtra("type", 0);
                intent.putExtra("coin_id", mId + "");
                intent.putExtra("coin_name", mCoinName);
                startActivity(intent);
                break;
            case R.id.btn_copy:
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(mTvSite.getText().toString());
                Toast.makeText(this, getString(R.string.copy_succeed), Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
