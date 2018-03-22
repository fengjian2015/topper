package com.bclould.tocotalk.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.AmazonClientException;
import com.amazonaws.Request;
import com.amazonaws.Response;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.handlers.RequestHandler2;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.bclould.tocotalk.Presenter.PublicshDynamicPresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.base.BaseActivity;
import com.bclould.tocotalk.base.MyApp;
import com.bclould.tocotalk.ui.adapter.PublicshDynamicGVAdapter;
import com.bclould.tocotalk.ui.widget.LoadingProgressDialog;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.FullyGridLayoutManager;
import com.bclould.tocotalk.utils.UtilTool;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.permissions.RxPermissions;
import com.luck.picture.lib.tools.PictureFileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by GA on 2017/9/28.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class PublicshDynamicActivity extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.publish)
    TextView mPublish;
    @Bind(R.id.text_et)
    EditText mTextEt;
    @Bind(R.id.scrollView)
    ScrollView mScrollView;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.ll_site)
    LinearLayout mLlSite;
    @Bind(R.id.ll_synchronization)
    LinearLayout mLlSynchronization;


    private List<LocalMedia> selectList = new ArrayList<>();
    private PublicshDynamicGVAdapter adapter;
    private int maxSelectNum = 9;
    private PublicshDynamicPresenter mPublicshDynamicPresenter;
    private boolean mType = false;
    private LoadingProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_dynamic);
        mPublicshDynamicPresenter = new PublicshDynamicPresenter(this);
        ButterKnife.bind(this);
        initRecyclerView();
        MyApp.getInstance().addActivity(this);
    }

    private void initRecyclerView() {
        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(20));
        adapter = new PublicshDynamicGVAdapter(this, onAddPicClickListener);
        adapter.setList(selectList);
        adapter.setSelectMax(maxSelectNum);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new PublicshDynamicGVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (selectList.size() > 0) {
                    LocalMedia media = selectList.get(position);
                    String pictureType = media.getPictureType();
                    int mediaType = PictureMimeType.pictureToVideo(pictureType);
                    switch (mediaType) {
                        case 1:
                            // 预览图片 可自定长按保存路径
                            //PictureSelector.create(MainActivity.this).externalPicturePreview(position, "/custom_file", selectList);
                            PictureSelector.create(PublicshDynamicActivity.this).externalPicturePreview(position, selectList);
                            break;
                        case 2:
                            // 预览视频
                            PictureSelector.create(PublicshDynamicActivity.this).externalPictureVideo(media.getPath());
                            break;
                        case 3:
                            // 预览音频
                            PictureSelector.create(PublicshDynamicActivity.this).externalPictureAudio(media.getPath());
                            break;
                    }
                }
            }
        });

        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        RxPermissions permissions = new RxPermissions(this);
        permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    PictureFileUtils.deleteCacheDirFile(PublicshDynamicActivity.this);
                } else {
                    Toast.makeText(PublicshDynamicActivity.this,
                            getString(R.string.picture_jurisdiction), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });

    }

    private PublicshDynamicGVAdapter.onAddPicClickListener onAddPicClickListener = new PublicshDynamicGVAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {

            // 进入相册 以下是例子：不需要的api可以不写
            PictureSelector.create(PublicshDynamicActivity.this)
                    .openGallery(PictureMimeType.ofAll())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
//                    .theme(R.style.picture_white_style)
                    .maxSelectNum(maxSelectNum)// 最大图片 选择数量
                    .minSelectNum(1)// 最小选择数量
                    .imageSpanCount(3)// 每行显示个数
                    .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
                    .previewImage(true)// 是否可预览图片
                    .previewVideo(true)// 是否可预览视频
                    .enablePreviewAudio(true) // 是否可播放音频
                    .compressGrade(Luban.THIRD_GEAR)// luban压缩档次，默认3档 Luban.FIRST_GEAR、Luban.CUSTOM_GEAR
                    .isCamera(true)// 是否显示拍照按钮
                    .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                    //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径
                    .enableCrop(false)// 是否裁剪
                    .compress(true)// 是否压缩
                    .compressMode(PictureConfig.SYSTEM_COMPRESS_MODE)//系统自带 or 鲁班压缩 PictureConfig.SYSTEM_COMPRESS_MODE or LUBAN_COMPRESS_MODE
                    //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                    .glideOverride(200, 200)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                    .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示
                    .isGif(true)// 是否显示gif图片
                    .videoQuality(0)
                    .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
                    .circleDimmedLayer(false)// 是否圆形裁剪
                    .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                    .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                    .openClickSound(false)// 是否开启点击声音
                    .selectionMedia(selectList)// 是否传入已选图片
                    .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code

        }

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    LocalMedia localMedia = selectList.get(0);
                    String postfix = UtilTool.getPostfix(localMedia.getPath());
                    if (postfix.equals("Video") && selectList.size() != 1) {
                        Toast.makeText(this, "只能选择一个视频", Toast.LENGTH_SHORT).show();
                        mType = true;
                        selectList.clear();
                        selectList.add(localMedia);
                    }
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的
                    adapter.setList(selectList);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    //动态设置条目间隔
    class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        int mSpace;

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            if (parent.getChildAdapterPosition(view) != 0) {
                outRect.top = mSpace;
            }
        }

        public SpaceItemDecoration(int space) {
            this.mSpace = space;
        }
    }

    private void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = LoadingProgressDialog.createDialog(this);
            mProgressDialog.setMessage("上传中...");
        }

        mProgressDialog.show();
    }

    private void hideDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    @OnClick({R.id.bark, R.id.publish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.publish:
                String text = mTextEt.getText().toString();
                if (selectList.size() != 0 || !text.isEmpty()) {
                    try {
                        checkFile(text);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    List<String> mPathList = new ArrayList<>();

    private void checkFile(String text) {
        showDialog();
        if (selectList.size() != 0) {
            if (mType) {
                File file = new File(selectList.get(0).getPath());
                String fileName = file.getName();
                String name = fileName.substring(0, fileName.lastIndexOf("."));
                String jid = UtilTool.getMyUser();
                String myName = jid.substring(0, jid.indexOf("@"));
                Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(selectList.get(0).getPath()
                        , MediaStore.Video.Thumbnails.MINI_KIND);
                //缩略图储存路径
                final String key = myName + UtilTool.createtFileName() + file.getName();
                final String keyCompress = myName + UtilTool.createtFileName() + "compress" + name + ".jpg";
                final File newFile = new File(Constants.PUBLICDIR + keyCompress);
                UtilTool.comp(bitmap, newFile);//压缩图片
                upVoide(key, file, true);
                upVoide(keyCompress, newFile, false);
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < selectList.size(); i++) {
                            mPathList.add(selectList.get(i).getCompressPath());
                            File file = new File(selectList.get(i).getCompressPath());
                            String jid = UtilTool.getMyUser();
                            String myName = jid.substring(0, jid.indexOf("@"));
                            final String key = myName + UtilTool.createtFileName() + file.getName();
                            final String keyCompress = myName + UtilTool.createtFileName() + "compress" + file.getName();
                            //缩略图储存路径
                            final File newFile = new File(Constants.PUBLICDIR + keyCompress);
                            UtilTool.comp(BitmapFactory.decodeFile(selectList.get(i).getCompressPath()), newFile);//压缩图片
                            upImage(key, file, true);
                            upImage(keyCompress, newFile, false);
                        }
                    }
                }).start();
            }
        } else {
            publicshDynamic("0", mKeyList, mkeyCompressList);
        }
    }


    private void upImage(final String key, File file, final boolean type) {
        try {
            BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(
                    Constants.ACCESS_KEY_ID,
                    Constants.SECRET_ACCESS_KEY,
                    Constants.SESSION_TOKEN);
            AmazonS3Client s3Client = new AmazonS3Client(
                    sessionCredentials);
            Regions regions = Regions.fromName("ap-northeast-2");
            Region region = Region.getRegion(regions);
            s3Client.setRegion(region);
            s3Client.addRequestHandler(new RequestHandler2() {
                @Override
                public void beforeRequest(Request<?> request) {

                }

                @Override
                public void afterResponse(Request<?> request, Response<?> response) {
                    Message message = new Message();
                    message.obj = key;
                    if (type) {
                        message.arg1 = 0;
                    } else {
                        message.arg1 = 1;
                    }
                    message.what = 1;
                    handler.sendMessage(message);
                }

                @Override
                public void afterError(Request<?> request, Response<?> response, Exception e) {

                }
            });
            PutObjectRequest por = new PutObjectRequest(Constants.BUCKET_NAME, key, file);
            s3Client.putObject(por);
        } catch (AmazonClientException e) {
            e.printStackTrace();
        }
    }

    String mKeyList = "";
    String mkeyCompressList = "";
    private int count = 0;
    private int keyCount = 0;
    private int keyCompress = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    count++;
                    if (count == 2) {
                        publicshDynamic("3", mKeyList, mkeyCompressList);
                    }
                    break;
                case 1:
                    count++;
                    if (msg.arg1 == 0) {
                        keyCount++;
                        String key = (String) msg.obj;
                        if (keyCount >= 2)
                            mKeyList += "," + key;
                        else
                            mKeyList = key;
                    } else {
                        keyCompress++;
                        String key = (String) msg.obj;
                        if (keyCompress >= 2)
                            mkeyCompressList += "," + key;
                        else
                            mkeyCompressList = key;
                    }
                    if (count == selectList.size() * 2) {
                        publicshDynamic("1", mKeyList, mkeyCompressList);
                    }
                    break;
            }
        }
    };


    private void publicshDynamic(String type, String keyList, String mkeyCompressList) {
        String text = mTextEt.getText().toString();
        mPublicshDynamicPresenter.publicsh(text, type, keyList, mkeyCompressList, "", new PublicshDynamicPresenter.CallBack() {
            @Override
            public void send() {
                hideDialog();
                finish();
            }
        });
    }

    private void upVoide(final String key, final File file, final boolean type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(
                            Constants.ACCESS_KEY_ID,
                            Constants.SECRET_ACCESS_KEY,
                            Constants.SESSION_TOKEN);
                    AmazonS3Client s3Client = new AmazonS3Client(
                            sessionCredentials);
                    Regions regions = Regions.fromName("ap-northeast-2");
                    Region region = Region.getRegion(regions);
                    s3Client.setRegion(region);
                    s3Client.addRequestHandler(new RequestHandler2() {
                        @Override
                        public void beforeRequest(Request<?> request) {

                        }

                        @Override
                        public void afterResponse(Request<?> request, Response<?> response) {
                            Message message = new Message();
                            message.what = 0;
                            if (type) {
                                mKeyList = key;
                            } else {
                                mkeyCompressList = key;
                            }
                            handler.sendMessage(message);
                        }

                        @Override
                        public void afterError(Request<?> request, Response<?> response, Exception e) {

                        }
                    });
                    PutObjectRequest por = new PutObjectRequest(Constants.BUCKET_NAME, key, file);
                    s3Client.putObject(por);
                } catch (AmazonClientException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
