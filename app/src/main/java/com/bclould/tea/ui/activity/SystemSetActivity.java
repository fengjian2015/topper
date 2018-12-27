package com.bclould.tea.ui.activity;

import android.content.Context;
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
import com.bclould.tea.base.MyApp;
import com.bclould.tea.ui.widget.DeleteCacheDialog;
import com.bclould.tea.ui.widget.MenuListPopWindow;
import com.bclould.tea.utils.AppLanguageUtils;
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
public class SystemSetActivity extends BaseActivity {
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

    private long mFolderSize;
    private List<LocalMedia> selectList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_set);
        ButterKnife.bind(this);
        setTitle(getString(R.string.system_set));
        init();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, MySharedPreferences.getInstance().getString(newBase.getString(R.string.language_pref_key))));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String path = intent.getStringExtra("path");
        if (!StringUtils.isEmpty(path)) {
            upload(path);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private void init() {
        boolean privateStatus = MySharedPreferences.getInstance().getBoolean(PRIVATE);
        String language = MySharedPreferences.getInstance().getString(getString(R.string.language_pref_key));
        if (language.equals("")) {
            mTvLanguageHint.setText(getString(R.string.follow_the_system));
        } else if (language.equals("zh-cn")) {
            mTvLanguageHint.setText(getString(R.string.simplified_chinese));
        } else if (language.equals("zh-hk")) {
            mTvLanguageHint.setText(getString(R.string.chinese_traditional));
        } else if (language.equals("en")) {
            mTvLanguageHint.setText(getString(R.string.english));
        }
        mOnOffPrivate.setSelected(privateStatus);
        isOnOff2 = privateStatus;
        SharedPreferences sp = getSharedPreferences(SETTING, 0);
        if (sp.contains(INFORM)) {
            boolean informStatus = MySharedPreferences.getInstance().getBoolean(INFORM);
            mOnOffInform.setSelected(informStatus);
            isOnOff = informStatus;
        } else {
            mOnOffInform.setSelected(true);
            isOnOff = true;
        }
        setDownOnOff();
        countCache();
    }

    private void countCache() {
        mFolderSize = UtilTool.getFolderSize(new File(Constants.LOG_DIR));
        String fileSize = UtilTool.FormetFileSize(mFolderSize);
        mTvCacheCount.setText(fileSize);
    }


    //显示退出弹框
    private void showDialog() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, this, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle(getString(R.string.logout_hint));
        Button cancel = (Button) deleteCacheDialog.findViewById(R.id.btn_cancel);
        Button confirm = (Button) deleteCacheDialog.findViewById(R.id.btn_confirm);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCacheDialog.dismiss();
                logout();
            }
        });

    }

    //退出登录
    private void logout() {
        LogoutPresenter logoutPresenter = new LogoutPresenter(this);
        logoutPresenter.logout();
    }

    boolean isOnOff = false;
    boolean isOnOff2 = false;

    @OnClick({R.id.btn_brak, R.id.bark, R.id.rl_inform, R.id.rl_private, R.id.rl_help, R.id.rl_cache, R.id.rl_language, R.id.rl_backgound, R.id.rl_download, R.id.on_off_download})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_brak:
                showDialog();
                break;
            case R.id.rl_language:
                startActivity(new Intent(this, SelectorLanguageActivity.class));
//                ToastShow.showToast2(this, getString(R.string.hint_unfinished));
                break;
            case R.id.bark:
                finish();
                break;
            case R.id.rl_inform:
                isOnOff = !isOnOff;
                setOnOff(INFORM, isOnOff);
                break;
            case R.id.rl_private:
                isOnOff2 = !isOnOff2;
                setOnOff(PRIVATE, isOnOff2);
                break;
            case R.id.rl_help:
                startActivity(new Intent(this, ProblemFeedBackActivity.class));
                break;
            case R.id.rl_cache:
                if (mFolderSize != 0) {
                    showCacheDialog();
                } else {
                    ToastShow.showToast2(this, getString(R.string.no_cache));
                }
                break;
            case R.id.rl_backgound:
                showBackgoundDialog();
                break;
            case R.id.rl_download:
            case R.id.on_off_download:
                changeDownOnOff();
                break;
        }
    }

    private void setDownOnOff() {
        if (MySharedPreferences.getInstance().getBoolean(AUTOMATICALLY_DOWNLOA)) {
            mOnOffDownload.setSelected(true);
        } else {
            mOnOffDownload.setSelected(false);
        }
    }

    private void changeDownOnOff() {
        if (MySharedPreferences.getInstance().getBoolean(AUTOMATICALLY_DOWNLOA)) {
            MySharedPreferences.getInstance().setBoolean(AUTOMATICALLY_DOWNLOA, false);
            mOnOffDownload.setSelected(false);
        } else {
            MySharedPreferences.getInstance().setBoolean(AUTOMATICALLY_DOWNLOA, true);
            mOnOffDownload.setSelected(true);
        }

    }


    private void showBackgoundDialog() {
        List<String> list = new ArrayList<>();
        list.add(getString(R.string.image));
        list.add(getString(R.string.network_image));
        list.add(getString(R.string.reduction_background));
        final MenuListPopWindow menu = new MenuListPopWindow(this, list);
        menu.setListOnClick(new MenuListPopWindow.ListOnClick() {
            @Override
            public void onclickitem(int position) {
                switch (position) {
                    case 0:
                        menu.dismiss();
                        break;
                    case 1:
                        menu.dismiss();
                        goImage();
                        break;
                    case 2:
                        menu.dismiss();
                        Intent intent = new Intent(SystemSetActivity.this, SerchImageActivity.class);
                        intent.putExtra("type", TYPE_BACKGROUND);
                        startActivity(intent);
                        break;
                    case 3:
                        menu.dismiss();
                        new GroupPresenter(SystemSetActivity.this).deleteBackgound(new GroupPresenter.CallBack() {
                            @Override
                            public void send() {
                                MySharedPreferences.getInstance().setString("backgroundu_url" + UtilTool.getTocoId(), "");
                                MySharedPreferences.getInstance().setString("backgroundu_file" + UtilTool.getTocoId(), "");
                                EventBus.getDefault().post(new MessageEvent(getString(R.string.conversation_backgound)));
                            }
                        });
                        break;
                }
            }
        });
        menu.setColor(Color.BLACK);
        menu.showAtLocation();
    }

    private void goImage() {
        PictureSelector.create(this)
                .openGallery(ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
//                        .theme(R.style.picture_white_style)
                .maxSelectNum(1)// 最大图片选择数量 int
                .imageSpanCount(3)// 每行显示个数 int
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .previewImage(true)// 是否可预览图片 true or false
                .previewVideo(true)// 是否可预览视频 true or false
                .enablePreviewAudio(true) // 是否可播放音频 true or false
                .compressGrade(Luban.THIRD_GEAR)// luban压缩档次，默认3档 Luban.THIRD_GEAR、Luban.FIRST_GEAR、Luban.CUSTOM_GEAR
                .isCamera(true)// 是否显示拍照按钮 true or false
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
                .enableCrop(false)// 是否裁剪 true or false
                .compress(true)// 是否压缩 true or false
                .compressMode(PictureConfig.SYSTEM_COMPRESS_MODE)//系统自带 or 鲁班压缩 PictureConfig.SYSTEM_COMPRESS_MODE or LUBAN_COMPRESS_MODE
                .glideOverride(160, 160)// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .withAspectRatio(1, 1)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示 true or false
                .isGif(false)// 是否显示gif图片 true or false
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                .circleDimmedLayer(true)// 是否圆形裁剪 true or false
                .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                .openClickSound(true)// 是否开启点击声音 true or false
                .selectionMedia(selectList)// 是否传入已选图片 List<LocalMedia> list
                .rotateEnabled(true) // 裁剪是否可旋转图片 true or false
                .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    //拿到选择的图片
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    try {
                        selectList = PictureSelector.obtainMultipleResult(data);
                        upload(selectList.get(0).getPath());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    private void upload(String path) {
        final File file = new File(path);
        final String key = UtilTool.getUserId() + UtilTool.createtFileName() + UtilTool.getPostfix2(file.getName());
        final File newFile = new File(Constants.BACKGOUND + key);
        Bitmap cutImg = BitmapFactory.decodeFile(path);
        UtilTool.comp1(cutImg, newFile);
        final Bitmap bitmap = BitmapFactory.decodeFile(newFile.getAbsolutePath());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = UtilTool.getFileToByte(newFile);
        String Base64Image = Base64.encodeToString(bytes, Base64.DEFAULT);
        UtilTool.Log("編碼", Base64Image.length() + "");
        new GroupPresenter(this).changeBackgound(Base64Image, new GroupPresenter.CallBack2() {
            @Override
            public void send(String url) {
                String fileurl = MySharedPreferences.getInstance().getString("backgroundu_file" + UtilTool.getTocoId());
                if (!StringUtils.isEmpty(fileurl) && new File(fileurl).exists()) {
                    new File(fileurl).delete();
                }
                MySharedPreferences.getInstance().setString("backgroundu_url" + UtilTool.getTocoId(), url);
                MySharedPreferences.getInstance().setString("backgroundu_file" + UtilTool.getTocoId(), key);
                EventBus.getDefault().post(new MessageEvent(getString(R.string.conversation_backgound)));
            }
        });
    }

    //显示Dialog
    private void showCacheDialog() {

        DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, this, R.style.dialog);

        deleteCacheDialog.show();

        dialogClick(deleteCacheDialog);

    }

    //Dialog的点击事件处理
    private void dialogClick(final DeleteCacheDialog dialog) {

        Button cancel = (Button) dialog.findViewById(R.id.btn_cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });

        Button confirm = (Button) dialog.findViewById(R.id.btn_confirm);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UtilTool.deleteFolderFile(Constants.LOG_DIR, false);
                mTvCacheCount.setText("0M");
                ToastShow.showToast2(SystemSetActivity.this, getString(R.string.delete_cache_succeed));
                dialog.dismiss();
            }
        });

    }

    private void setOnOff(String key, boolean status) {
        if (key.equals(PRIVATE)) {
            if (status) {
                mOnOffPrivate.setSelected(true);
            } else {
                mOnOffPrivate.setSelected(false);
            }
            MySharedPreferences.getInstance().setBoolean(PRIVATE, status);
        } else {
            if (status) {
                mOnOffInform.setSelected(true);
            } else {
                mOnOffInform.setSelected(false);
            }
            MySharedPreferences.getInstance().setBoolean(INFORM, status);
        }
    }
}
