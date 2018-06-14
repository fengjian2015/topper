package com.bclould.tocotalk.ui.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.bclould.tocotalk.Presenter.RealNamePresenter;
import com.bclould.tocotalk.R;
import com.bclould.tocotalk.network.OSSupload;
import com.bclould.tocotalk.ui.adapter.ProblemFeedBackRVAdapter;
import com.bclould.tocotalk.ui.widget.LoadingProgressDialog;
import com.bclould.tocotalk.utils.AnimatorTool;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static android.app.Activity.RESULT_OK;


/**
 * Created by GA on 2017/10/17.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class ProblemFeedBackFragment extends Fragment {

    public static ProblemFeedBackFragment instance = null;
    @Bind(R.id.feedback_content)
    EditText mFeedbackContent;
    @Bind(R.id.text_count)
    TextView mTextCount;
    @Bind(R.id.text)
    TextView mText;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.finish)
    Button mFinish;

    private List<LocalMedia> mSelectList = new ArrayList<>();
    private ProblemFeedBackRVAdapter adapter;
    private LoadingProgressDialog mProgressDialog;
    private RealNamePresenter mRealNamePresenter;
    private OSSAsyncTask<PutObjectResult> mTask;

    public static ProblemFeedBackFragment getInstance() {

        if (instance == null) {

            instance = new ProblemFeedBackFragment();

        }

        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_problem_feedback, container, false);

        ButterKnife.bind(this, view);

        mRealNamePresenter = new RealNamePresenter(getContext());

        initRecyclerView();

        ListenerEditText();

        return view;
    }

    private void ListenerEditText() {

        mFeedbackContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String feedbackContent = mFeedbackContent.getText().toString().trim();

                mTextCount.setText(feedbackContent.length() + "/150");

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }


    private void initRecyclerView() {

        FullyGridLayoutManager manager = new FullyGridLayoutManager(getActivity(), 6, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        adapter = new ProblemFeedBackRVAdapter(getActivity(), onAddPicClickListener);
        adapter.setList(mSelectList);
        adapter.setSelectMax(maxSelectNum);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new ProblemFeedBackRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (mSelectList.size() > 0) {
                    LocalMedia media = mSelectList.get(position);
                    String pictureType = media.getPictureType();
                    int mediaType = PictureMimeType.pictureToVideo(pictureType);
                    switch (mediaType) {
                        case 1:
                            // 预览图片 可自定长按保存路径
                            //PictureSelector.create(MainActivity.this).externalPicturePreview(position, "/custom_file", selectList);
                            PictureSelector.create(ProblemFeedBackFragment.this).externalPicturePreview(position, mSelectList);
                            break;
                        case 2:
                            // 预览视频
                            PictureSelector.create(ProblemFeedBackFragment.this).externalPictureVideo(media.getPath());
                            break;
                        case 3:
                            // 预览音频
                            PictureSelector.create(ProblemFeedBackFragment.this).externalPictureAudio(media.getPath());
                            break;
                    }
                }
            }
        });

        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        RxPermissions permissions = new RxPermissions(getActivity());
        permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    PictureFileUtils.deleteCacheDirFile(getActivity());
                } else {
                    Toast.makeText(getActivity(),
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

    private int maxSelectNum = 3;
    private ProblemFeedBackRVAdapter.onAddPicClickListener onAddPicClickListener = new ProblemFeedBackRVAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {
            // 进入相册 以下是例子：不需要的api可以不写
            PictureSelector.create(ProblemFeedBackFragment.this)
                    .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
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
                    .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                    .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示
                    .isGif(true)// 是否显示gif图片
                    .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
                    .circleDimmedLayer(false)// 是否圆形裁剪
                    .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                    .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                    .openClickSound(false)// 是否开启点击声音
                    .selectionMedia(mSelectList)// 是否传入已选图片
                    .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
        }

    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    mSelectList = PictureSelector.obtainMultipleResult(data);
                    adapter.setList(mSelectList);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    private void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = LoadingProgressDialog.createDialog(getContext());
            mProgressDialog.setMessage(getString(R.string.uploading));
        }

        mProgressDialog.show();
    }

    private void hideDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    private String keyList;
    private int count;
    private String mType;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {

            } else {
            }
        }
    };

    @OnClick(R.id.finish)
    public void onViewClicked() {
        if (checkEdit()) {
            if (mSelectList.size() != 0) {
                upImage();
            } else {
                submit("");
            }
        }
    }

    private void upImage() {
        showDialog();
        OSSClient ossClient = OSSupload.getInstance().visitOSS();
        for (LocalMedia info : mSelectList) {
            final String key = UtilTool.getUserId() + UtilTool.createtFileName() + UtilTool.getPostfix2(info.getCompressPath());

            PutObjectRequest put = new PutObjectRequest(Constants.BUCKET_NAME, key, info.getCompressPath());
            mTask = ossClient.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                @Override
                public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {
                    String key = putObjectRequest.getObjectKey();
                    if (count == 1) {
                        keyList = key;
                    } else {
                        keyList += "," + key;
                    }
                    count++;
                    if (count == mSelectList.size()) {
                        submit(keyList);
                    }
                }

                @Override
                public void onFailure(PutObjectRequest putObjectRequest, ClientException e, ServiceException e1) {
                    hideDialog();
                    Toast.makeText(getContext(), getString(R.string.up_error), Toast.LENGTH_SHORT).show();
                    UtilTool.Log("oss", e.getMessage());
                    UtilTool.Log("oss", e1.getMessage());
                }
            });
        }
    }

    private boolean checkEdit() {
        if (mFeedbackContent.getText().toString().isEmpty()) {
            AnimatorTool.getInstance().editTextAnimator(mFeedbackContent);
            Toast.makeText(getContext(), getString(R.string.toast_content), Toast.LENGTH_SHORT).show();
        } else {
            return true;
        }
        return false;
    }

    private void submit(String keyList) {
        String content = mFeedbackContent.getText().toString();
        mRealNamePresenter.UpFeedBackImage(content, keyList, new RealNamePresenter.CallBack() {
            @Override
            public void send(int status) {
                if (status == 1) {
                    Toast.makeText(getActivity(), getString(R.string.up_succeed), Toast.LENGTH_SHORT).show();
                    mSelectList.clear();
                    adapter.notifyDataSetChanged();
                    mFeedbackContent.setText("");
                } else {
                    Toast.makeText(getActivity(), getString(R.string.up_error), Toast.LENGTH_SHORT).show();
                }
                hideDialog();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTask != null) {
            mTask.cancel();
        }
        ButterKnife.unbind(this);
    }
}
