package com.bclould.tocotalk.ui.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tocotalk.Presenter.CurrencyInOutPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.model.BaseInfo;
import com.bclould.tocotalk.utils.UtilTool;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/3/15.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class InCoinActivity extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_record)
    TextView mTvRecord;
    @Bind(R.id.tv_count)
    TextView mTvCount;
    @Bind(R.id.iv_site_qr)
    ImageView mIvSiteQr;
    @Bind(R.id.tv_site)
    TextView mTvSite;
    @Bind(R.id.card_view)
    CardView mCardView;
    @Bind(R.id.btn_copy)
    Button mBtnCopy;
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_desc)
    TextView mTvDesc;
    private int mId;
    private String mCoinName;
    private String mOver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_coin);
        ButterKnife.bind(this);
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
                    if (data.getDesc() != null && data.getAddress() != null && data.getTitle() != null) {
                        String desc = data.getDesc().replace("\\n", "\n");
                        mTvTitle.setText(data.getTitle());
                        mTvDesc.setText(desc);
                        JSONObject object = new JSONObject();//创建一个总的对象，这个对象对整个json串
                        object.put("coin", mCoinName);
                        object.put("address", data.getAddress());
                        String jsonresult = object.toString();//生成返回字符串
                        Bitmap qrImage = UtilTool.createQRImage(jsonresult);
                        mIvSiteQr.setImageBitmap(qrImage);
                        mTvSite.setText(data.getAddress());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @OnClick({R.id.bark, R.id.tv_record, R.id.btn_copy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_record:
                Intent intent = new Intent(this, BillDetailsActivity.class);
                intent.putExtra("type", 0);
                intent.putExtra("coin_id", mId + "");
                intent.putExtra("coin_name", mCoinName);
                startActivity(intent);
                break;
            case R.id.btn_copy:
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(mTvSite.getText().toString());
                Toast.makeText(this, "复制成功", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
