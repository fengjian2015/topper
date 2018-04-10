package com.bclould.tocotalk.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.model.UserInfo;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.MySharedPreferences;
import com.bclould.tocotalk.utils.UtilTool;
import com.bclould.tocotalk.xmpp.XmppConnection;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tocotalk.Presenter.LoginPresenter.STATE;
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

    private List<LocalMedia> selectList = new ArrayList<>();
    private DBManager mMgr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_detail);
        mMgr = new DBManager(this);
        ButterKnife.bind(this);
        initInterface();
        MyApp.getInstance().addActivity(this);
    }

    //初始化界面
    private void initInterface() {
        String state = MySharedPreferences.getInstance().getString(STATE);
        mTvLocation.setText(state);
        DBManager mgr = new DBManager(this);
        List<UserInfo> userInfos = mgr.queryUser(Constants.MYUSER);
        if (userInfos.size() != 0) {
            String path = userInfos.get(0).getPath();
            mTouxiang.setImageBitmap(BitmapFactory.decodeFile(path));
        } else {
            mTouxiang.setImageBitmap(UtilTool.setDefaultimage(this));
        }
        String jid = UtilTool.getJid();
        String myName = jid.substring(0, jid.indexOf("@"));
        mTvUsername.setText(myName);
    }

    //拿到选择的图片
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    Bitmap bitmap = BitmapFactory.decodeFile(selectList.get(0).getCompressPath());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] bytes = baos.toByteArray();
                    boolean type = XmppConnection.getInstance().changeImage(bytes);
                    if (type) {
                        List<UserInfo> userInfos = mMgr.queryUser(UtilTool.getJid());
                        mTouxiang.setImageBitmap(BitmapFactory.decodeFile(userInfos.get(0).getPath()));
                        EventBus.getDefault().post(new MessageEvent("修改头像"));
                        Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }

    @OnClick({R.id.bark, R.id.rl_touxiang, R.id.rl_qr_card})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.rl_touxiang:
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
                        .isGif(true)// 是否显示gif图片 true or false
                        .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                        .circleDimmedLayer(false)// 是否圆形裁剪 true or false
                        .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                        .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                        .openClickSound(true)// 是否开启点击声音 true or false
                        .selectionMedia(selectList)// 是否传入已选图片 List<LocalMedia> list
                        .rotateEnabled(true) // 裁剪是否可旋转图片 true or false
                        .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
                        .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
                break;
            case R.id.rl_qr_card:
                startActivity(new Intent(this, QRCodeActivity.class));
                break;
        }
    }
}
