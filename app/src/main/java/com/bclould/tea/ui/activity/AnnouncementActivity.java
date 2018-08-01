package com.bclould.tea.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.bclould.tea.Presenter.GroupPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.history.DBRoomManage;
import com.bclould.tea.network.OSSupload;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
import com.bclould.tea.utils.Constants;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.UtilTool;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.luck.picture.lib.config.PictureMimeType.ofImage;

@RequiresApi(api = Build.VERSION_CODES.N)
public class AnnouncementActivity extends BaseActivity {

    @Bind(R.id.et)
    EditText mEt;
    @Bind(R.id.tv_hint)
    TextView mTvHint;
    @Bind(R.id.addImage)
    Button mAddImage;
    @Bind(R.id.view)
    View mView;

    private List<LocalMedia> selectList = new ArrayList<>();
    private String roomId;
    private DBRoomManage mDBRoomManage;
    private SpannableString mSpannableString;
    private List<HashMap> mStringList=new ArrayList<>();//記錄選擇的圖片key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);
        mDBRoomManage = new DBRoomManage(this);
        initGetIntent();
        initView();
    }

    private void initGetIntent() {
        roomId = getIntent().getStringExtra("roomId");

    }

    private void initView() {
        setEtCompile(false);
        setAnnouncement();
        if (isOwner()) {
            mTvHint.setVisibility(View.VISIBLE);
        } else {
            mTvHint.setVisibility(View.GONE);
        }
    }

    private void setAnnouncement(){
        String content=mDBRoomManage.findRoomDescription(roomId);
        if (!StringUtils.isEmpty(content)) {
            mSpannableString=new SpannableString(content);
            mEt.setText(mSpannableString);
            searchImage(content);
        }else{
            mSpannableString=new SpannableString("");
        }
    }

    private void setEtCompile(boolean isCompile) {
        if (isCompile) {
            mEt.setFocusableInTouchMode(true);
            mEt.setFocusable(true);
            mEt.requestFocus();
        } else {
            mEt.setFocusable(false);
            mEt.setFocusableInTouchMode(false);
        }
    }

    private boolean isOwner() {
        if (UtilTool.getTocoId().equals(mDBRoomManage.findRoomOwner(roomId))) {
            return true;
        } else {
            return false;
        }
    }

    @OnClick({R.id.bark, R.id.tv_hint, R.id.addImage})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_hint:
                if (getString(R.string.edit).equals(mTvHint.getText().toString())) {
                    //開始編輯模式
                    mAddImage.setVisibility(View.VISIBLE);
                    mView.setVisibility(View.VISIBLE);
                    mTvHint.setText(getString(R.string.finish));
                    setEtCompile(true);
                } else if (getString(R.string.finish).equals(mTvHint.getText().toString())) {
                    //完成提交
                    if(mStringList.size()>0){
                        uploadImage();
                    }else{
                        commit();
                    }
                }
                break;
            case R.id.addImage:
                goImage();
                break;

        }
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
                .circleDimmedLayer(false)// 是否圆形裁剪 true or false
                .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
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
                    // 图片选择结果回调
                    try {
                        selectList = PictureSelector.obtainMultipleResult(data);
                        Bitmap originalBitmap = BitmapFactory.decodeFile(selectList.get(0).getCutPath());
                        insertCompilePic(originalBitmap, selectList.get(0).getCutPath());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    /**
     * 編輯的方法
     * 插入图片
     */
    private void insertCompilePic(Bitmap bitmap, final String url) {
        HashMap map=new HashMap();
        final String postfixs = UtilTool.getPostfix3(url);
        final String key = UtilTool.getUserId() + UtilTool.createtFileName() + ".AN." +postfixs;//命名aws文件名
        map.put("url",url);
        map.put("key",key);
        mStringList.add(map);

        // 根据Bitmap对象创建ImageSpan对象
        ImageSpan imageSpan = new ImageSpan(AnnouncementActivity.this, bitmap);
        // 创建一个SpannableString对象，以便插入用ImageSpan对象封装的图像
        String tempUrl = "<img src=\"" + key + "\"/>";
        SpannableString spannableString = new SpannableString(tempUrl);
        // 用ImageSpan对象替换你指定的字符串
        spannableString.setSpan(imageSpan, 0, tempUrl.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 将选择的图片追加到EditText中光标所在位置
        int index = mEt.getSelectionStart(); // 获取光标所在位置
        Editable edit_text = mEt.getEditableText();
        if (index < 0 || index >= edit_text.length()) {
            edit_text.append(spannableString);
        } else {
            edit_text.insert(index, spannableString);
        }
        System.out.println("插入的图片：" + edit_text.toString());
    }

      /**
     * 僅僅顯示的方法
     * 插入图片
     */
    private void insertPic(final Bitmap bitmap, final String imageurl, final int start) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 根据Bitmap对象创建ImageSpan对象
                ImageSpan imageSpan = new ImageSpan(AnnouncementActivity.this, bitmap);
                mSpannableString.setSpan(imageSpan, start, start+imageurl.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                mEt.setText(mSpannableString);
            }
        });
    }

    private void searchImage(String content){
        Pattern p = Pattern.compile("<img src=\"([^：]*?)\"/>");
        final Matcher m = p.matcher(content);
        while (m.find()) {
            String url=m.group().substring(10,m.group().length()-3);
            UtilTool.Log("公告",m.group()+"   "+m.start()+"     "+m.group().length()+"   "+url+"    "+url.length()+"   "+content.length());
            String imageUrl= downFile(url);
            Glide.with( this ) // could be an issue!
                    .load(imageUrl)
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                            BitmapDrawable bd = (BitmapDrawable) resource;
                            Bitmap bm = bd.getBitmap();
                            insertPic(bm,m.group(),m.start());
                        }
                    });
        }
    }

    private void commit() {
        final String content=mEt.getText().toString();
        new GroupPresenter(this).updateBullet(Integer.parseInt(roomId), content, new GroupPresenter.CallBack() {
            @Override
            public void send() {
                mDBRoomManage.updateDescription(roomId, content);
                MessageEvent messageEvent = new MessageEvent(getString(R.string.modify_group_announcement));
                messageEvent.setName(content);
                EventBus.getDefault().post(messageEvent);
                finish();
            }
        });
    }

    private void uploadImage(){
        showDialog();
        String content=mEt.getText().toString();
        final List<HashMap> hashMaps=new ArrayList<>();
        hashMaps.clear();
        hashMaps.addAll(mStringList);
        for(final HashMap hashMap:mStringList){
            if(!content.contains((String)hashMap.get("key"))){
                hashMaps.remove(hashMap);
                return;
            }
            OSSClient ossClient = OSSupload.getInstance().visitOSS();
            PutObjectRequest put = new PutObjectRequest(Constants.BUCKET_NAME2, (String) hashMap.get("key"), (String) hashMap.get("url"));
            ossClient.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                @Override
                public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {
                    hashMaps.remove(hashMap);
                    if(hashMaps.size()==0){
                        commit();
                        hideDialog();
                    }
                }

                @Override
                public void onFailure(PutObjectRequest putObjectRequest, ClientException e, ServiceException e1) {
                    UtilTool.Log("aws", "错误");
                    hideDialog();
                    mStringList.addAll(hashMaps);
                }
            });
        }
    }

    private LoadingProgressDialog mProgressDialog;
    private void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = LoadingProgressDialog.createDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
        }
        mProgressDialog.show();
    }

    private void hideDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    private String downFile(String key){
        OSSClient ossClient = OSSupload.getInstance().visitOSS();
        String url = null;
        url = ossClient.presignPublicObjectURL(Constants.BUCKET_NAME2, key);
        return url;
    }
}
