package com.dashiji.biyun.ui.fragment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dashiji.biyun.R;
import com.dashiji.biyun.history.DBManager;
import com.dashiji.biyun.ui.activity.GuanYuMeActivity;
import com.dashiji.biyun.ui.activity.MyAssetsActivity;
import com.dashiji.biyun.ui.activity.QRCodeActivity;
import com.dashiji.biyun.ui.activity.ShenFenVerifyActivity;
import com.dashiji.biyun.ui.activity.SystemSetActivity;
import com.dashiji.biyun.ui.widget.DeleteCacheDialog;
import com.dashiji.biyun.utils.UtilTool;
import com.dashiji.biyun.xmpp.XmppConnection;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.ByteArrayOutputStream;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static com.luck.picture.lib.config.PictureMimeType.ofImage;

/**
 * Created by GA on 2017/9/19.
 */

public class MyFragment extends Fragment {

    public static MyFragment instance = null;
    @Bind(R.id.touxiang)
    ImageView mTouxiang;
    @Bind(R.id.name)
    TextView mName;
    @Bind(R.id.id)
    TextView mId;
    @Bind(R.id.imageview2)
    ImageView mImageview2;
    @Bind(R.id.rl_my_assets)
    RelativeLayout mRlMyAssets;
    @Bind(R.id.imageview3)
    ImageView mImageview3;
    @Bind(R.id.rl_shenfen_verify)
    RelativeLayout mRlShenfenVerify;
    @Bind(R.id.imageview4)
    ImageView mImageview4;
    @Bind(R.id.rl_guanyu_me)
    RelativeLayout mRlGuanyuMe;
    @Bind(R.id.imageview5)
    ImageView mImageview5;
    @Bind(R.id.rl_system_set)
    RelativeLayout mRlSystemSet;
    @Bind(R.id.imageview6)
    ImageView mImageview6;
    @Bind(R.id.cache_count)
    TextView mCacheCount;
    @Bind(R.id.rl_delete_cache)
    RelativeLayout mRlDeleteCache;


    private DBManager mMgr;
    private List<LocalMedia> selectList;

    public static MyFragment getInstance() {

        if (instance == null) {

            instance = new MyFragment();

        }

        return instance;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_my, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        mName.setText(UtilTool.getMyUser().substring(0, UtilTool.getMyUser().lastIndexOf("@")));
        mMgr = new DBManager(getContext());
        Bitmap bitmap = UtilTool.getMyImage(mMgr, UtilTool.getMyUser());
        if (bitmap != null)
            mTouxiang.setImageBitmap(bitmap);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    //点击事件处理
    @OnClick({R.id.iv_my_qr, R.id.touxiang, R.id.rl_my_assets, R.id.rl_shenfen_verify, R.id.rl_guanyu_me, R.id.rl_system_set, R.id.rl_delete_cache})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_my_qr:
                startActivity(new Intent(getActivity(), QRCodeActivity.class));
                break;
            case R.id.touxiang:

//                startActivity(new Intent(getActivity(), LoginActivity.class));

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


                break;/*
            case R.id.edit:

//                setSkip(PersonalDetailsActivity.class);

                break;
            case R.id.rl_my_move:

                setSkip(FriendDataActivity.class);

                break;*/
            case R.id.rl_my_assets:

                setSkip(MyAssetsActivity.class);

                break;
            case R.id.rl_shenfen_verify:

                setSkip(ShenFenVerifyActivity.class);

                break;
            case R.id.rl_guanyu_me:

                setSkip(GuanYuMeActivity.class);

                break;
            case R.id.rl_system_set:

                setSkip(SystemSetActivity.class);

                break;
            case R.id.rl_delete_cache:

                showDialog();

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    Bitmap bitmap = BitmapFactory.decodeFile(selectList.get(0).getPath());
//                    UtilTool.saveImages(bitmap, Constants.MYUSER, this, mMgr);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] bytes = baos.toByteArray();
                    boolean b = XmppConnection.getInstance().changeImage(bytes);
                    UtilTool.Log("日志", b + "");
                    break;
            }
        }
    }


    //显示Dialog
    private void showDialog() {

        DeleteCacheDialog deleteCacheDialog = new DeleteCacheDialog(R.layout.dialog_delete_cache, getContext());

        deleteCacheDialog.show();

        dialogClick(deleteCacheDialog);

    }

    //Dialog的点击事件处理
    private void dialogClick(final DeleteCacheDialog dialog) {

        Button cancel = (Button) dialog.findViewById(R.id.btn_cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Toast.makeText(getActivity(), "点击了取消", Toast.LENGTH_SHORT).show();
                dialog.dismiss();

            }
        });

        Button confirm = (Button) dialog.findViewById(R.id.btn_confirm);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCacheCount.setText("0");
//                Toast.makeText(getActivity(), "点击了确定", Toast.LENGTH_SHORT).show();
                dialog.dismiss();

            }
        });

    }

    //跳转事件处理
    private void setSkip(Class clazz) {

        startActivity(new Intent(getActivity(), clazz));

    }
}
