package com.dashiji.biyun.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.dashiji.biyun.R;
import com.dashiji.biyun.base.BaseActivity;
import com.dashiji.biyun.base.MyApp;
import com.dashiji.biyun.history.DBManager;
import com.dashiji.biyun.model.ProvinceInfo;
import com.dashiji.biyun.model.UserInfo;
import com.dashiji.biyun.utils.Constants;
import com.dashiji.biyun.utils.MySharedPreferences;
import com.dashiji.biyun.utils.UtilTool;
import com.dashiji.biyun.xmpp.XmppConnection;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.luck.picture.lib.config.PictureMimeType.ofImage;

/**
 * Created by GA on 2017/9/27.
 */

public class PersonalDetailsActivity extends BaseActivity {

    private static final String GENDER = "gender";
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.touxiang)
    ImageView mTouxiang;
    @Bind(R.id.rl_touxiang)
    RelativeLayout mRlTouxiang;
    @Bind(R.id.nickname)
    TextView mNickname;
    @Bind(R.id.rl_nickname)
    RelativeLayout mRlNickname;
    @Bind(R.id.rl_gender)
    RelativeLayout mRlGender;
    @Bind(R.id.rl_region)
    RelativeLayout mRlRegion;
    @Bind(R.id.currency)
    TextView mCurrency;
    @Bind(R.id.rl_signature)
    RelativeLayout mRlSignature;
    @Bind(R.id.rl_qr_code)
    RelativeLayout mRlQrCode;
    @Bind(R.id.rl_site)
    RelativeLayout mRlSite;
    @Bind(R.id.site)
    TextView mSite;
    @Bind(R.id.gender_iv)
    ImageView mGenderIv;
    @Bind(R.id.gender_tv)
    TextView mGenderTv;
    private List<LocalMedia> selectList = new ArrayList<>();
    private DBManager mMgr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_detail);
        ButterKnife.bind(this);
        initInterface();
        MyApp.getInstance().addActivity(this);
    }

    //初始化界面
    private void initInterface() {
        DBManager mgr = new DBManager(this);
        List<UserInfo> userInfos = mgr.queryUser(Constants.MYUSER);
        if (userInfos.size() != 0) {
            String path = userInfos.get(0).getPath();
            mTouxiang.setImageBitmap(BitmapFactory.decodeFile(path));
        } else {
            mTouxiang.setImageBitmap(UtilTool.setDefaultimage(this));
        }
        //添加数据
        initOptionData();

        String gender = MySharedPreferences.getInstance().getString(GENDER);

        //性别选择器
        setGender(gender);

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

    //给地址选择器添加数据
    private ArrayList<ProvinceInfo> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();

    private void initOptionData() {

        /**
         * 注意：如果是添加JavaBean实体数据，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */

        //选项1
        options1Items.add(new ProvinceInfo(0, "广东", "描述部分", "其他数据"));
        options1Items.add(new ProvinceInfo(1, "湖南", "描述部分", "其他数据"));
        options1Items.add(new ProvinceInfo(2, "广西", "描述部分", "其他数据"));

        //选项2
        ArrayList<String> options2Items_01 = new ArrayList<>();
        options2Items_01.add("广州");
        options2Items_01.add("佛山");
        options2Items_01.add("东莞");
        options2Items_01.add("珠海");
        ArrayList<String> options2Items_02 = new ArrayList<>();
        options2Items_02.add("长沙");
        options2Items_02.add("岳阳");
        options2Items_02.add("株洲");
        options2Items_02.add("衡阳");
        ArrayList<String> options2Items_03 = new ArrayList<>();
        options2Items_03.add("桂林");
        options2Items_03.add("玉林");
        options2Items.add(options2Items_01);
        options2Items.add(options2Items_02);
        options2Items.add(options2Items_03);

        /*--------数据源添加完毕---------*/
    }

    @OnClick({R.id.bark, R.id.rl_touxiang, R.id.rl_nickname, R.id.rl_gender, R.id.rl_region, R.id.rl_signature, R.id.rl_qr_code, R.id.rl_site})
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
            case R.id.rl_nickname:

                startActivity(new Intent(this, NickNameActivity.class));

                break;
            case R.id.rl_gender:

                final List<String> gender1Items = new ArrayList();

                gender1Items.add("男");
                gender1Items.add("女");
                gender1Items.add("保密");

                OptionsPickerView gender = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int option2, int options3, View v) {


                        String tx = gender1Items.get(options1);

                        setGender(tx);

                    }
                }).build();
                gender.setPicker(gender1Items);
                gender.show();

                break;
            case R.id.rl_region:


                OptionsPickerView region = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int options2, int options3, View v) {
                        //返回的分别是三个级别的选中位置

                        String tx = options1Items.get(options1).getPickerViewText() + "/"
                                + options2Items.get(options1).get(options2);
                        mSite.setText(tx);
                    }
                }).build();

                region.setPicker(options1Items, options2Items);
                region.show();

                break;
            case R.id.rl_signature:

                //跳转签名页面
                startActivity(new Intent(this, SignatureActivity.class));

                break;
            case R.id.rl_qr_code:

                //跳转二维码页面
                startActivity(new Intent(this, QRCodeActivity.class));

                break;
            case R.id.rl_site:

                //跳转地址页面
                startActivity(new Intent(this, SiteActivity.class));

                break;
        }
    }

    //设置性别
    private void setGender(String tx) {
        if (tx.equals("男")) {

            mGenderTv.setText(tx);

            mGenderTv.setTextColor(getResources().getColor(R.color.blue));

            mGenderIv.setImageResource(R.mipmap.icon_nfriend_boys);

        } else if (tx.equals("女")) {

            mGenderTv.setText(tx);

            mGenderTv.setTextColor(getResources().getColor(R.color.red));

            mGenderIv.setImageResource(R.mipmap.icon_nfriend_igirls);

        } else {

            mGenderTv.setText(tx);

            mGenderTv.setTextColor(Color.BLACK);

            mGenderIv.setImageResource(R.mipmap.icon_full_selected);

        }

        //存储性别sp
        MySharedPreferences.getInstance().setString(GENDER, tx);

    }
}
