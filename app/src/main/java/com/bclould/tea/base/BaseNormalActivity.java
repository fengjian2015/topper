package com.bclould.tea.base;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.StringUtils;

import butterknife.ButterKnife;

/**
 * Created by fengjian on 2018/12/28.
 */


@RequiresApi(api = Build.VERSION_CODES.N)
public class BaseNormalActivity extends AppCompatActivity {
    protected TextView mTvTitleTop;
    protected TextView mTvAdd;
    protected TextView mTvAdd1;
    protected ImageView mImageView;
    protected ImageView mIvFinish;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.getInstance().addActivity(this);
    }

    protected void setTitle(String content){
        mTvTitleTop=(TextView)findViewById(R.id.tv_title_top);
        mTvTitleTop.setText(content);
    }

    protected void setHtmlTitle(String content){
        mTvTitleTop=(TextView)findViewById(R.id.tv_title_top);
        mIvFinish=(ImageView)findViewById(R.id.iv_finish);
        mTvTitleTop.setText(content);
    }

    protected void setTitle(String content, String rightContent){
        mTvTitleTop=(TextView)findViewById(R.id.tv_title_top);
        mTvAdd=(TextView)findViewById(R.id.tv_add);
        mTvTitleTop.setText(content);
        mTvAdd.setText(rightContent);
        mTvAdd.setVisibility(View.VISIBLE);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, MySharedPreferences.getInstance().getString(newBase.getString(R.string.language_pref_key))));
    }

    /**
     * rightContent1红色右边字体
     * @param content
     * @param rightContent
     * @param rightContent1
     */
    protected void setTitle(String content, String rightContent,String rightContent1){
        mTvTitleTop=(TextView)findViewById(R.id.tv_title_top);
        mTvAdd=(TextView)findViewById(R.id.tv_add);
        mTvAdd1=(TextView)findViewById(R.id.tv_add1);
        mTvTitleTop.setText(content);
        if(!StringUtils.isEmpty(rightContent)) {
            mTvAdd.setText(rightContent);
            mTvAdd.setVisibility(View.VISIBLE);
        }
        if(!StringUtils.isEmpty(rightContent1)) {
            mTvAdd1.setVisibility(View.VISIBLE);
            mTvAdd1.setText(rightContent1);
        }
    }

    protected void setTitle(String content,int id){
        mTvTitleTop=(TextView)findViewById(R.id.tv_title_top);
        mTvAdd=(TextView)findViewById(R.id.tv_add);
        mTvAdd1=(TextView)findViewById(R.id.tv_add1);
        mTvTitleTop.setText(content);
        mImageView=(ImageView)findViewById(R.id.iv_more);
        mImageView.setVisibility(View.VISIBLE);
        mImageView.setImageResource(id);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.finish();
        MyApp.getInstance().removeActivity(this);
        ButterKnife.unbind(this);
    }
}
