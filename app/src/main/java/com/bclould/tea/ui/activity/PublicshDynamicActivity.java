package com.bclould.tea.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.Presenter.DynamicPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.service.ImageUpService;
import com.bclould.tea.ui.adapter.PublicshDynamicGVAdapter;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
import com.bclould.tea.utils.FullyGridLayoutManager;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.permissions.RxPermissions;
import com.luck.picture.lib.tools.PictureFileUtils;

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

    private static final int LOCATION = 1;
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
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.tv_location)
    TextView mTvLocation;
    @Bind(R.id.rl_site)
    RelativeLayout mRlSite;


    private List<LocalMedia> selectList = new ArrayList<>();
    private PublicshDynamicGVAdapter adapter;
    private int maxSelectNum = 9;
    private DynamicPresenter mDynamicPresenter;
    private boolean mType = false;
    private LoadingProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_dynamic);
        mDynamicPresenter = new DynamicPresenter(this);
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
                    .isGif(false)// 是否显示gif图片
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
                        Toast.makeText(this, getString(R.string.selecotr_video_hint), Toast.LENGTH_SHORT).show();
                        selectList.clear();
                        selectList.add(localMedia);
                    } else {
                        adapter.setList(selectList);
                        adapter.notifyDataSetChanged();
                    }
                    break;
                case LOCATION:
                    String location = data.getStringExtra("location");
                    mTvLocation.setText(location);
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

    @OnClick({R.id.bark, R.id.publish, R.id.rl_site})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.rl_site:
                Intent intent = new Intent(this, LocationActivity.class);
                intent.putExtra("type", 2);
                startActivityForResult(intent, LOCATION);
                break;
            case R.id.publish:
                String text = mTextEt.getText().toString();
                if (selectList.size() != 0 || !text.isEmpty()) {
                    try {
                        checkFile();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, getString(R.string.no_null), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    ArrayList<String> mPathList = new ArrayList<>();

    private void checkFile() {
        String text = mTextEt.getText().toString();
        String location = mTvLocation.getText().toString();
        Intent intent = new Intent(this, ImageUpService.class);
        Bundle bundle = new Bundle();
        for (LocalMedia localMedia : selectList) {
            if (UtilTool.getPostfix(selectList.get(0).getPath()).equals("Image")) {
                mType = false;
                int degree = UtilTool.readPictureDegree(localMedia.getCompressPath());
                UtilTool.Log("圖片信息", degree + "");
                if (degree != 0) {
                    UtilTool.toturn(localMedia.getCompressPath(), BitmapFactory.decodeFile(localMedia.getCompressPath()), degree);
                }
                mPathList.add(localMedia.getCompressPath());
            } else {
                mType = true;
                mPathList.add(localMedia.getPath());
            }
        }
        if (!UtilTool.isServiceRunning(this, "com.bclould.tea.service.ImageUpService")) {
            bundle.putStringArrayList("imageList", mPathList);
            bundle.putString("text", text);
            bundle.putString("location", location);
            bundle.putBoolean("type", mType);
            intent.putExtras(bundle);
            startService(intent);
            finish();
        } else {
            ToastShow.showToast2(this, getString(R.string.dynamic_underway_uploading));
        }
    }
}
