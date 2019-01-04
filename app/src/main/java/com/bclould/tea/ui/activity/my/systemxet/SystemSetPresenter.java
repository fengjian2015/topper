package com.bclould.tea.ui.activity.my.systemxet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.view.View;
import android.widget.Button;

import com.bclould.tea.Presenter.GroupPresenter;
import com.bclould.tea.Presenter.LogoutPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseView;
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

import static android.app.Activity.RESULT_OK;
import static com.bclould.tea.ui.activity.SerchImageActivity.TYPE_BACKGROUND;
import static com.bclould.tea.ui.activity.my.systemxet.SystemSetActivity.AUTOMATICALLY_DOWNLOA;
import static com.bclould.tea.ui.activity.my.systemxet.SystemSetActivity.INFORM;
import static com.bclould.tea.ui.activity.my.systemxet.SystemSetActivity.PRIVATE;
import static com.bclould.tea.utils.MySharedPreferences.SETTING;
import static com.luck.picture.lib.config.PictureMimeType.ofImage;

/**
 * Created by fengjian on 2018/12/28.
 */

public class SystemSetPresenter implements SystemSetContacts.Presenter {
    private SystemSetContacts.View mView;
    private Activity mActivity;

    private long mFolderSize;
    boolean isOnOff = false;
    boolean isOnOff2 = false;
    private List<LocalMedia> selectList = new ArrayList<>();

    @Override
    public void bindView(BaseView view) {
        mView = (SystemSetContacts.View) view;
    }

    @Override
    public <T extends Context> void start(T context) {
        mActivity = (Activity) context;
        mView.initView();
        init();
    }

    @Override
    public void release() {
    }


    @Override
    public void onMyNewIntent(Intent intent) {
        String path = intent.getStringExtra("path");
        if (!StringUtils.isEmpty(path)) {
            upload(path);
        }
    }

    private void init() {
        boolean privateStatus = MySharedPreferences.getInstance().getBoolean(PRIVATE);
        String language = MySharedPreferences.getInstance().getString(mActivity.getString(R.string.language_pref_key));
        if (language.equals("")) {
            mView.setTvLanguageHint(mActivity.getString(R.string.follow_the_system));
        } else if (language.equals("zh-cn")) {
            mView.setTvLanguageHint(mActivity.getString(R.string.simplified_chinese));
        } else if (language.equals("zh-hk")) {
            mView.setTvLanguageHint(mActivity.getString(R.string.chinese_traditional));
        } else if (language.equals("en")) {
            mView.setTvLanguageHint(mActivity.getString(R.string.english));
        }
        mView.setOnOffPrivateSelected(privateStatus);
        isOnOff2 = privateStatus;
        SharedPreferences sp = mActivity.getSharedPreferences(SETTING, 0);
        if (sp.contains(INFORM)) {
            boolean informStatus = MySharedPreferences.getInstance().getBoolean(INFORM);
            mView.setOnOffInformSelected(informStatus);
            isOnOff = informStatus;
        } else {
            mView.setOnOffInformSelected(true);
            isOnOff = true;
        }
        setDownOnOff();
        countCache();
    }


    private void setDownOnOff() {
        if (MySharedPreferences.getInstance().getBoolean(AUTOMATICALLY_DOWNLOA)) {
            mView.setOnOffDownloadSelected(true);
        } else {
            mView.setOnOffDownloadSelected(false);
        }
    }


    private void countCache() {
        mFolderSize = UtilTool.getFolderSize(new File(Constants.LOG_DIR));
        String fileSize = UtilTool.FormetFileSize(mFolderSize);
        mView.setTvCacheCount(fileSize);
    }

    //显示退出弹框
    @Override
    public void showDialog() {
        final DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, mActivity, R.style.dialog);
        deleteCacheDialog.show();
        deleteCacheDialog.setTitle(mActivity.getString(R.string.logout_hint));
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
        LogoutPresenter logoutPresenter = new LogoutPresenter(mActivity);
        logoutPresenter.logout();
    }

    @Override
    public void changeDownOnOff() {
        if (MySharedPreferences.getInstance().getBoolean(AUTOMATICALLY_DOWNLOA)) {
            MySharedPreferences.getInstance().setBoolean(AUTOMATICALLY_DOWNLOA, false);
            mView.setOnOffDownloadSelected(false);
        } else {
            MySharedPreferences.getInstance().setBoolean(AUTOMATICALLY_DOWNLOA, true);
            mView.setOnOffDownloadSelected(true);
        }

    }

    @Override
    public void showBackgoundDialog() {
        List<String> list = new ArrayList<>();
        list.add(mActivity.getString(R.string.image));
        list.add(mActivity.getString(R.string.network_image));
        list.add(mActivity.getString(R.string.reduction_background));
        final MenuListPopWindow menu = new MenuListPopWindow(mActivity, list);
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
                        Intent intent = new Intent(mActivity, SerchImageActivity.class);
                        intent.putExtra("type", TYPE_BACKGROUND);
                        mActivity.startActivity(intent);
                        break;
                    case 3:
                        menu.dismiss();
                        new GroupPresenter(mActivity).deleteBackgound(new GroupPresenter.CallBack() {
                            @Override
                            public void send() {
                                MySharedPreferences.getInstance().setString("backgroundu_url" + UtilTool.getTocoId(), "");
                                MySharedPreferences.getInstance().setString("backgroundu_file" + UtilTool.getTocoId(), "");
                                EventBus.getDefault().post(new MessageEvent(mActivity.getString(R.string.conversation_backgound)));
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
        PictureSelector.create(mActivity)
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

    public void upload(String path) {
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
        new GroupPresenter(mActivity).changeBackgound(Base64Image, new GroupPresenter.CallBack2() {
            @Override
            public void send(String url) {
                String fileurl = MySharedPreferences.getInstance().getString("backgroundu_file" + UtilTool.getTocoId());
                if (!StringUtils.isEmpty(fileurl) && new File(fileurl).exists()) {
                    new File(fileurl).delete();
                }
                MySharedPreferences.getInstance().setString("backgroundu_url" + UtilTool.getTocoId(), url);
                MySharedPreferences.getInstance().setString("backgroundu_file" + UtilTool.getTocoId(), key);
                EventBus.getDefault().post(new MessageEvent(mActivity.getString(R.string.conversation_backgound)));
            }
        });
    }


    //显示Dialog
    private void showCacheDialog() {

        DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, mActivity, R.style.dialog);

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
                mView.setTvCacheCount("0M");
                ToastShow.showToast2(mActivity, mActivity.getString(R.string.delete_cache_succeed));
                dialog.dismiss();
            }
        });

    }

    private void setOnOff(String key, boolean status) {
        if (key.equals(PRIVATE)) {
            if (status) {
                mView.setOnOffPrivateSelected(true);
            } else {
                mView.setOnOffPrivateSelected(false);
            }
            MySharedPreferences.getInstance().setBoolean(PRIVATE, status);
        } else {
            if (status) {
                mView.setOnOffInformSelected(true);
            } else {
                mView.setOnOffInformSelected(false);
            }
            MySharedPreferences.getInstance().setBoolean(INFORM, status);
        }
    }


    @Override
    public void rlInformClick() {
        isOnOff = !isOnOff;
        setOnOff(INFORM, isOnOff);
    }

    @Override
    public void goSelectorLanguageActivity() {
        mActivity.startActivity(new Intent(mActivity, SelectorLanguageActivity.class));
//                ToastShow.showToast2(this, getString(R.string.hint_unfinished));
    }

    @Override
    public void rlPrivateClick() {
        isOnOff2 = !isOnOff2;
        setOnOff(PRIVATE, isOnOff2);
    }

    @Override
    public void rlCache() {
        if (mFolderSize != 0) {
            showCacheDialog();
        } else {
            ToastShow.showToast2(mActivity, mActivity.getString(R.string.no_cache));
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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

}
