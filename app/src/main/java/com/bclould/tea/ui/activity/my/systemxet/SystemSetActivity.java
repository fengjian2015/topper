package com.bclould.tea.ui.activity.my.systemxet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tea.Presenter.GroupPresenter;
import com.bclould.tea.Presenter.LogoutPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.ui.activity.ProblemFeedBackActivity;
import com.bclould.tea.ui.activity.SelectorLanguageActivity;
import com.bclould.tea.ui.activity.SerchImageActivity;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.ui.widget.MenuListPopWindow;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.ui.activity.SerchImageActivity.TYPE_BACKGROUND;
import static com.bclould.tea.utils.MySharedPreferences.SETTING;
import static com.luck.picture.lib.config.PictureMimeType.ofImage;

/**
 * Created by GA on 2017/9/22.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class SystemSetActivity extends BaseActivity implements SystemSetContacts.View{
    public static final String INFORM = "inform";
    public static final String PRIVATE = UtilTool.getUserId() + "private";
    public static final String AUTOMATICALLY_DOWNLOA = UtilTool.getUserId() + "Dautomatically_download";
    @Bind(R.id.tv_inform)
    TextView mTvInform;
    @Bind(R.id.on_off_inform)
    ImageView mOnOffInform;
    @Bind(R.id.rl_inform)
    RelativeLayout mRlInform;
    @Bind(R.id.tv_private)
    TextView mTvPrivate;
    @Bind(R.id.on_off_private)
    ImageView mOnOffPrivate;
    @Bind(R.id.rl_private)
    RelativeLayout mRlPrivate;
    @Bind(R.id.tv_help)
    TextView mTvHelp;
    @Bind(R.id.rl_help)
    RelativeLayout mRlHelp;
    @Bind(R.id.tv_cache)
    TextView mTvCache;
    @Bind(R.id.tv_cache_count)
    TextView mTvCacheCount;
    @Bind(R.id.rl_cache)
    RelativeLayout mRlCache;
    @Bind(R.id.btn_brak)
    Button mBtnBrak;
    @Bind(R.id.tv_language)
    TextView mTvLanguage;
    @Bind(R.id.tv_language_hint)
    TextView mTvLanguageHint;
    @Bind(R.id.rl_language)
    RelativeLayout mRlLanguage;
    @Bind(R.id.on_off_download)
    ImageView mOnOffDownload;



    private SystemSetContacts.Presenter mPresenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_set);
        ButterKnife.bind(this);
        mPresenter=new SystemSetPresenter();
        mPresenter.bindView(this);
        mPresenter.start(this);
    }
    @Override
    public void initView() {
        setTitle(getString(R.string.system_set));
    }

    @Override
    public void setOnOffDownloadSelected(boolean istrue) {
        mOnOffDownload.setSelected(istrue);
    }

    @Override
    public void setTvCacheCount(String content) {
        mTvCacheCount.setText(content);
    }

    @Override
    public void setOnOffInformSelected(boolean isTrue) {
        mOnOffInform.setSelected(isTrue);
    }

    @Override
    public void setTvLanguageHint(String content) {
        mTvLanguageHint.setText(content);
    }

    @Override
    public void setOnOffPrivateSelected(boolean isTrue) {
        mOnOffPrivate.setSelected(isTrue);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mPresenter.onMyNewIntent(intent);
    }


    @OnClick({R.id.btn_brak, R.id.bark, R.id.rl_inform, R.id.rl_private, R.id.rl_help, R.id.rl_cache, R.id.rl_language, R.id.rl_backgound, R.id.rl_download, R.id.on_off_download})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_brak:
                mPresenter.showDialog();
                break;
            case R.id.rl_language:
                mPresenter.goSelectorLanguageActivity();
                break;
            case R.id.bark:
                finish();
                break;
            case R.id.rl_inform:
                mPresenter.rlInformClick();
                break;
            case R.id.rl_private:
                mPresenter.rlPrivateClick();
                break;
            case R.id.rl_help:
                startActivity(new Intent(this, ProblemFeedBackActivity.class));
                break;
            case R.id.rl_cache:
                mPresenter.rlCache();
                break;
            case R.id.rl_backgound:
                mPresenter.showBackgoundDialog();
                break;
            case R.id.rl_download:
            case R.id.on_off_download:
                mPresenter.changeDownOnOff();
                break;
        }
    }



    //拿到选择的图片
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.onActivityResult(requestCode,resultCode,data);
    }
}
