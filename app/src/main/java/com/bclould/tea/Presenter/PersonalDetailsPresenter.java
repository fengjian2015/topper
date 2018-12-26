package com.bclould.tea.Presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.bclould.tea.R;
import com.bclould.tea.model.AuatarListInfo;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.model.NewFriendInfo;
import com.bclould.tea.model.RemarkListInfo;
import com.bclould.tea.model.UserDataInfo;
import com.bclould.tea.network.RetrofitUtil;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by GA on 2018/4/19.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class PersonalDetailsPresenter {

    private final Context mContext;
    private LoadingProgressDialog mProgressDialog;

    public PersonalDetailsPresenter(Context context) {
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
            mProgressDialog.hideDialog();
        }
    }

    public void upImage(String base64Image, final CallBack callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .uploadAvatar(UtilTool.getToken(), base64Image)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseInfo baseInfo) {
                        hideDialog();
                        if (baseInfo.getStatus() == 1) {
                            callBack.send();
                            Toast.makeText(mContext, mContext.getString(R.string.xg_succeed), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, mContext.getString(R.string.xg_error), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getFriendImageList(String toco_id, final CallBack2 callBack2) {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .avatarList(UtilTool.getToken(), toco_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<AuatarListInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(AuatarListInfo auatarListInfo) {
                        if (auatarListInfo.getStatus() == 1) {
                            callBack2.send(auatarListInfo.getData());
                        }/*else {
                                Toast.makeText(mContext, auatarListInfo.getMessage(), Toast.LENGTH_SHORT).show();
                            }*/
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getFriendRemark(String name, final CallBack3 callBack3) {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .getRemarkList(UtilTool.getToken(), name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<RemarkListInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RemarkListInfo remarkListInfo) {
                        if (remarkListInfo.getStatus() == 1) {
                            callBack3.send(remarkListInfo.getData());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //獲取好友列表
    public void getFriendList(final CallBack2 callBack2) {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .getFriendsList(UtilTool.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<AuatarListInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(AuatarListInfo remarkListInfo) {
                        if (((Activity) mContext).isDestroyed()) return;
                        hideDialog();
                        if (remarkListInfo.getStatus() == 1) {
                            callBack2.send(remarkListInfo.getData());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideDialog();
                        callBack2.error();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //添加好友
    public void addFriend(String toco_id, String friend_label) {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .addFriend(UtilTool.getToken(), toco_id, friend_label)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseInfo baseInfo) {
                        if (baseInfo.getStatus() == 1) {
                            Toast.makeText(mContext, mContext.getString(R.string.send_request_succeed), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, mContext.getString(R.string.send_request_error), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //確認添加
    public void confirmAddFriend(String toco_id, int confirm, final CallBack callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .confirmAddFriend(UtilTool.getToken(), toco_id, confirm)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseInfo baseInfo) {
                        if (!ActivityUtil.isActivityOnTop((Activity) mContext)) return;
                        hideDialog();
                        if (baseInfo.getStatus() == 1) {
                            callBack.send();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!ActivityUtil.isActivityOnTop((Activity) mContext)) return;
                        hideDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //刪除好友
    public void deleteFriend(String toco_id, final CallBack callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .deleteFriend(UtilTool.getToken(), toco_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseInfo baseInfo) {
                        if (!ActivityUtil.isActivityOnTop((Activity) mContext)) return;
                        hideDialog();
                        if (baseInfo.getStatus() == 1) {
                            Toast.makeText(mContext, mContext.getString(R.string.delete_succeed), Toast.LENGTH_SHORT).show();
                            callBack.send();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //刪除好友
    public void getUserData(int user_id, final CallBack4 callBack4) {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .getUserData(UtilTool.getToken(), user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<UserDataInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(UserDataInfo userDataInfo) {
                        if (userDataInfo.getStatus() == 1) {
                            callBack4.send(userDataInfo.getData());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //獲取請求列表
    public void getNewFriendData(boolean isShow, final CallBack5 callBack5) {
        if (isShow)
            showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .getNewFriendData(UtilTool.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<NewFriendInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(NewFriendInfo userDataInfo) {
                        if (!ActivityUtil.isActivityOnTop((Activity) mContext)) return;
                        hideDialog();
                        if (userDataInfo.getStatus() == 1) {
                            callBack5.send(userDataInfo);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!ActivityUtil.isActivityOnTop((Activity) mContext)) return;
                        hideDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getMerchantUser(String code, final CallBack4 callBack4) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .getMerchantUser(UtilTool.getToken(), code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<UserDataInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(UserDataInfo userDataInfo) {
                        hideDialog();
                        if (userDataInfo.getStatus() == 1) {
                            callBack4.send(userDataInfo.getData());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void deleteReview(int id, final GroupPresenter.CallBack callBack) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .deleteReview(UtilTool.getToken(), id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseInfo baseInfo) {
                        hideDialog();
                        if (baseInfo.getStatus() == 1) {
                            callBack.send();
                        }
                        ToastShow.showToast2((Activity) mContext, baseInfo.getMessage());
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void bindAlipay(String uuid, final CallBack6 callBack6) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .bindAlipay(UtilTool.getToken(), uuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseInfo baseInfo) {
                        hideDialog();
                        if (baseInfo.getStatus() == 1) {
                            callBack6.send();
                        }
                        ToastShow.showToast2((Activity) mContext, baseInfo.getMessage());
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void unbindAlipay(String passwrod, final CallBack6 callBack6) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .unbindAlipay(UtilTool.getToken(), passwrod)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BaseInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseInfo baseInfo) {
                        hideDialog();
                        if (baseInfo.getStatus() == 1) {
                            callBack6.send();
                        }
                        ToastShow.showToast2((Activity) mContext, baseInfo.getMessage());
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //定义接口
    public interface CallBack {
        void send();
    }

    //定义接口
    public interface CallBack2 {
        void send(List<AuatarListInfo.DataBean> data);

        void error();

        void finishRefresh();
    }

    public interface CallBack3 {
        void send(List<RemarkListInfo.DataBean> listdata);
    }

    public interface CallBack4 {
        void send(UserDataInfo.DataBean listdata);

        void error();
    }

    public interface CallBack5 {
        void send(NewFriendInfo listdata);
    }

    public interface CallBack6 {
        void send();
    }

    public interface CallBack7 {
        void send(String userId);
    }
}
