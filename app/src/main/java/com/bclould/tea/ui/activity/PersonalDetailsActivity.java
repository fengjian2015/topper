package com.bclould.tea.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tea.Presenter.PersonalDetailsPresenter;
import com.bclould.tea.R;
import com.bclould.tea.alipay.AlipayClient;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.history.DBManager;
import com.bclould.tea.ui.widget.MenuListPopWindow;
import com.bclould.tea.ui.widget.PWDDialog;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.UtilTool;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.Presenter.LoginPresenter.ALIPAY_UUID;
import static com.bclould.tea.Presenter.LoginPresenter.STATE;
import static com.bclould.tea.ui.activity.SerchImageActivity.TYPE_PERSONAL;
import static com.luck.picture.lib.config.PictureMimeType.ofImage;

/**
 * Created by GA on 2017/9/27.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class PersonalDetailsActivity extends BaseActivity {

    private static final String GENDER = "gender";
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.touxiang)
    ImageView mTouxiang;
    @Bind(R.id.rl_touxiang)
    RelativeLayout mRlTouxiang;
    @Bind(R.id.tv_username)
    TextView mTvUsername;
    @Bind(R.id.rl_username)
    RelativeLayout mRlUsername;
    @Bind(R.id.rl_qr_card)
    RelativeLayout mRlQrCard;
    @Bind(R.id.tv_location)
    TextView mTvLocation;
    @Bind(R.id.rl_location)
    RelativeLayout mRlLocation;
    @Bind(R.id.tv_email)
    TextView mTvEmail;
    @Bind(R.id.tv_id)
    TextView mTvId;
    @Bind(R.id.tv_alipay)
    TextView mTvAlipay;
    @Bind(R.id.rl_alipay)
    RelativeLayout mRlAlipay;

    private List<LocalMedia> selectList = new ArrayList<>();
    private DBManager mMgr;
    private PersonalDetailsPresenter mPersonalDetailsPresenter;
    private PWDDialog pwdDialog;
    private String mUserId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_detail);
        mPersonalDetailsPresenter = new PersonalDetailsPresenter(this);
        mMgr = new DBManager(this);
        ButterKnife.bind(this);
        initInterface();
        MyApp.getInstance().addActivity(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, MySharedPreferences.getInstance().getString(newBase.getString(R.string.language_pref_key))));
    }

    //初始化界面
    private boolean isAlipay = false;

    private void initInterface() {
        if (MySharedPreferences.getInstance().getSp().contains(STATE)) {
            mTvLocation.setText(MySharedPreferences.getInstance().getString(STATE));
        }
        /*Bitmap bitmap = UtilTool.getImage(mMgr, UtilTool.getTocoId(), PersonalDetailsActivity.this);
        if (bitmap != null)
            mTouxiang.setImageBitmap(bitmap);*/
        UtilTool.getImage(mMgr, UtilTool.getTocoId(), PersonalDetailsActivity.this, mTouxiang);
        mTvUsername.setText(UtilTool.getUser());
        mTvId.setText(UtilTool.getTocoId());
        mTvEmail.setText(UtilTool.getEmail());
        if (UtilTool.getUUID().isEmpty()) {
            mTvAlipay.setText(getString(R.string.not_bound));
            isAlipay = false;
        } else {
            mTvAlipay.setText(getString(R.string.is_binding));
            isAlipay = true;
        }
    }

    //拿到选择的图片
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    try {
                        selectList = PictureSelector.obtainMultipleResult(data);
                        upImage(selectList.get(0).getCutPath());
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    //上傳頭像
    private void upImage(String path) throws UnsupportedEncodingException {
        File file = new File(path);
        final String keyCut = UtilTool.getUserId() + UtilTool.createtFileName() + "cut" + UtilTool.getPostfix2(file.getName());
        final File newFile = new File(Constants.PUBLICDIR + keyCut);
        Bitmap cutImg = BitmapFactory.decodeFile(path);
        UtilTool.comp(cutImg, newFile);
        final Bitmap bitmap = BitmapFactory.decodeFile(newFile.getAbsolutePath());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = UtilTool.getFileToByte(newFile);
        String Base64Image = Base64.encodeToString(bytes, Base64.DEFAULT);
        UtilTool.Log("編碼", Base64Image.length() + "");
        mPersonalDetailsPresenter.upImage(Base64Image, new PersonalDetailsPresenter.CallBack() {
            @Override
            public void send() {
                UtilTool.saveImages(bitmap, UtilTool.getTocoId(), PersonalDetailsActivity.this, mMgr);
                EventBus.getDefault().post(new MessageEvent(getString(R.string.xg_touxaing)));
                UtilTool.getImage(mMgr, UtilTool.getTocoId(), PersonalDetailsActivity.this, mTouxiang);

                //                mTouxiang.setImageBitmap(UtilTool.getImage(mMgr, UtilTool.getTocoId(), PersonalDetailsActivity.this));
            }
        });
        /*boolean type = XmppConnection.getInstance().changeImage(bytes);
        if (type) {
            List<UserInfo> userInfos = mMgr.queryUser(UtilTool.getTocoId());
            mTouxiang.setImageBitmap(BitmapFactory.decodeFile(userInfos.get(0).getPath()));
            EventBus.getDefault().post(new MessageEvent(getString(R.string.xg_touxaing)));
            Toast.makeText(this, getString(R.string.xg_succeed), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.xg_error), Toast.LENGTH_SHORT).show();
        }*/
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String path = intent.getStringExtra("path");
        if (!StringUtils.isEmpty(path)) {
            try {
                upImage(path);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }


    @OnClick({R.id.bark, R.id.rl_touxiang, R.id.rl_qr_card, R.id.rl_alipay,R.id.rl_referrer})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.rl_alipay:
                if (isAlipay) {
                    showPWDialog();
                } else {
                    alipayAuth();
                }
                break;
            case R.id.rl_touxiang:
                showDialog();
                break;
            case R.id.rl_qr_card:
                intent = new Intent(this, QRCodeActivity.class);
                intent.putExtra("user", UtilTool.getTocoId());
                startActivity(intent);
                break;
            case R.id.rl_referrer:
                intent = new Intent(this, QRCodeActivity.class);
                intent.putExtra("user", UtilTool.getUser());
                intent.putExtra("type",1);
                startActivity(intent);
                break;
        }
    }

    private void alipayAuth() {
        AlipayClient.getInstance().authV2(PersonalDetailsActivity.this, new PersonalDetailsPresenter.CallBack7() {
            @Override
            public void send(String userId) {
                mUserId = userId;
                bindAlipay();
            }
        });
    }

    private void showPWDialog() {
        pwdDialog = new PWDDialog(this);
        pwdDialog.setOnPWDresult(new PWDDialog.OnPWDresult() {
            @Override
            public void success(String password) {
                unbindAlipay(password);
            }
        });
        pwdDialog.showDialog(getString(R.string.verify_pay_pw), null, null, null, null);
    }

    private void bindAlipay() {
        mPersonalDetailsPresenter.bindAlipay(mUserId, new PersonalDetailsPresenter.CallBack6() {
            @Override
            public void send() {
                mTvAlipay.setText(getString(R.string.is_binding));
                MySharedPreferences.getInstance().setString(ALIPAY_UUID, mUserId);
                isAlipay = true;
            }
        });
    }


    private void unbindAlipay(String password) {
        mPersonalDetailsPresenter.unbindAlipay(password, new PersonalDetailsPresenter.CallBack6() {
            @Override
            public void send() {
                mTvAlipay.setText(getString(R.string.not_bound));
                MySharedPreferences.getInstance().setString(ALIPAY_UUID, "");
                isAlipay = false;
            }
        });
    }

    private void showDialog() {
        List<String> list = new ArrayList<>();
        list.add(getString(R.string.image));
        list.add(getString(R.string.network_image));
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
                        Intent intent = new Intent(PersonalDetailsActivity.this, SerchImageActivity.class);
                        intent.putExtra("type", TYPE_PERSONAL);
                        startActivity(intent);
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
                .enableCrop(true)// 是否裁剪 true or false
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
}
