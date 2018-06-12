package com.bclould.tocotalk.Presenter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.BaseInfo;
import com.bclould.tocotalk.model.DynamicListInfo;
import com.bclould.tocotalk.model.LikeInfo;
import com.bclould.tocotalk.model.ReviewListInfo;
import com.bclould.tocotalk.network.RetrofitUtil;
import com.bclould.tocotalk.ui.widget.LoadingProgressDialog;
import com.bclould.tocotalk.utils.Constants;
import com.bclould.tocotalk.utils.MessageEvent;
import com.bclould.tocotalk.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by GA on 2018/3/22.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class DynamicPresenter {
    private final Context mContext;
    private LoadingProgressDialog mProgressDialog;

    public DynamicPresenter(Context context) {
        mContext = context;
    }

    private void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = LoadingProgressDialog.createDialog(mContext);
            mProgressDialog.setMessage(mContext.getString(R.string.loading));
        }

        mProgressDialog.show();
    }

    private void hideDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public void publicsh(String text, String type, String keyList, String keyCompressList, String position, final CallBack callBack) {
        if (UtilTool.isNetworkAvailable(mContext)) {
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .publishDynamic(UtilTool.getToken(), text, type, keyList, keyCompressList, position)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<BaseInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseInfo baseInfo) {
                            if (baseInfo.getStatus() == 1) {
                                callBack.send();
                            }
                            Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideDialog();
                            Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    public void dynamicList(int page, int pageSize, String userList, final CallBack2 callBack2) {
        userList = userList.replace("@" + Constants.DOMAINNAME, "");
        userList = userList.replace("@" + Constants.DOMAINNAME2, "");
        UtilTool.Log("动态", userList);
        if (UtilTool.isNetworkAvailable(mContext)) {
//            showDialog();
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .dynamicList(UtilTool.getToken(), page, pageSize, userList)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<DynamicListInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(DynamicListInfo dynamicListInfo) {
//                            hideDialog();
                            if (dynamicListInfo.getStatus() == 1) {
                                callBack2.send(dynamicListInfo.getData());
                            } else {
                                Toast.makeText(mContext, mContext.getString(R.string.loading_error), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
//                            hideDialog();
                            UtilTool.Log("动态", e.getMessage());
                            Toast.makeText(mContext, mContext.getString(R.string.loading_error), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    public void taDynamicList(int page, int pageSize, String user, final CallBack2 callBack2) {
        if (UtilTool.isNetworkAvailable(mContext)) {
//            showDialog();
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .taDynamicList(UtilTool.getToken(), page, pageSize, user)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<DynamicListInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(DynamicListInfo dynamicListInfo) {
//                            hideDialog();
                            if (dynamicListInfo.getStatus() == 1) {
                                callBack2.send(dynamicListInfo.getData());
                            } else {
                                Toast.makeText(mContext, mContext.getString(R.string.loading_error), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
//                            hideDialog();
                            UtilTool.Log("動態", e.getMessage());
                            Toast.makeText(mContext, mContext.getString(R.string.loading_error), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    public void reviewList(String id, final CallBack3 callBack3) {
        if (UtilTool.isNetworkAvailable(mContext)) {
//            showDialog();
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .reviewList(UtilTool.getToken(), id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<ReviewListInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(ReviewListInfo reviewListInfo) {
//                            hideDialog();
                            if (reviewListInfo.getStatus() == 1) {
                                callBack3.send(reviewListInfo.getData());
                            } else {
                                Toast.makeText(mContext, mContext.getString(R.string.loading_error), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
//                            hideDialog();
                            Toast.makeText(mContext, mContext.getString(R.string.loading_error), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    public void like(String id, final CallBack4 callBack4) {
        if (UtilTool.isNetworkAvailable(mContext)) {
//            showDialog();
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .like(UtilTool.getToken(), id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<LikeInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(LikeInfo likeInfo) {
//                            hideDialog();
                            if (likeInfo.getStatus() == 1) {
                                callBack4.send(likeInfo);
                            } else {
                                Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
//                            hideDialog();
                            Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    public void sendComment(String id, String comment, String reply_id, String key, int key_type, final CallBack5 callBack5) {
        if (UtilTool.isNetworkAvailable(mContext)) {
//            showDialog();
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .publishReview(UtilTool.getToken(), id, comment, reply_id, key, key_type)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<ReviewListInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(ReviewListInfo reviewListInfo) {
//                            hideDialog();
                            if (reviewListInfo.getStatus() == 1) {
                                callBack5.send(reviewListInfo.getData().getList());
                                Toast.makeText(mContext, reviewListInfo.getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, reviewListInfo.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
//                            hideDialog();
                            UtilTool.Log("動態", e.getMessage());
                            Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    public void reviewLike(String id, final CallBack4 callBack4) {
        if (UtilTool.isNetworkAvailable(mContext)) {
            showDialog();
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .reviewLike(UtilTool.getToken(), id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<LikeInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(LikeInfo likeInfo) {
                            hideDialog();
                            callBack4.send(likeInfo);
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideDialog();
                            Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteDynamic(final String id) {
        if (UtilTool.isNetworkAvailable(mContext)) {
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .deleteDynamic(UtilTool.getToken(), id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<BaseInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseInfo baseInfo) {
                            if (baseInfo.getStatus() == 1) {
                                /*if (mContext instanceof DynamicDetailActivity) {
                                    DynamicDetailActivity activity = (DynamicDetailActivity) mContext;
                                    activity.finish();
                                }*/
                                MessageEvent messageEvent = new MessageEvent(mContext.getString(R.string.delete_dynamic));
                                messageEvent.setId(id);
                                EventBus.getDefault().post(messageEvent);
                            }
                            Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    public void reward(int dynamic_id, String count, int id, String password, final CallBack callBack) {
        if (UtilTool.isNetworkAvailable(mContext)) {
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .giveReward(UtilTool.getToken(), dynamic_id, count, id, password)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<BaseInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseInfo baseInfo) {
                            if (baseInfo.getStatus() == 1) {
                                callBack.send();
                            } else {
                                Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    //定义接口
    public interface CallBack {
        void send();
    }

    //定义接口
    public interface CallBack2 {
        void send(List<DynamicListInfo.DataBean> data);
    }

    //定义接口
    public interface CallBack3 {
        void send(ReviewListInfo.DataBean data);
    }

    //定义接口
    public interface CallBack4 {
        void send(LikeInfo data);
    }

    //定义接口
    public interface CallBack5 {
        void send(List<DynamicListInfo.DataBean.ReviewListBean> data);
    }

    //定义接口
    public interface CallBack6 {
        void send();
    }

}
