package com.bclould.tocotalk.Presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.bclould.tocotalk.R;
import com.bclould.tocotalk.model.AuatarListInfo;
import com.bclould.tocotalk.model.BaseInfo;
import com.bclould.tocotalk.model.RemarkListInfo;
import com.bclould.tocotalk.network.RetrofitUtil;
import com.bclould.tocotalk.ui.activity.IndividualDetailsActivity;
import com.bclould.tocotalk.ui.widget.LoadingProgressDialog;
import com.bclould.tocotalk.utils.ToastShow;
import com.bclould.tocotalk.utils.UtilTool;

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
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public void upImage(String base64Image, final CallBack callBack) {
        if (UtilTool.isNetworkAvailable(mContext)) {
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
                            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    public void getFriendImageList(String toco_id, final CallBack2 callBack2) {
        if (UtilTool.isNetworkAvailable(mContext)) {
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
                            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    public void getFriendRemark(String name,final CallBack3 callBack3){
        if (UtilTool.isNetworkAvailable(mContext)) {
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
                            ToastShow.showToast2((Activity) mContext, e.getMessage());
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    //獲取好友列表
    public void getFriendList(final CallBack2 callBack2){

        if (UtilTool.isNetworkAvailable(mContext)) {
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
                            if(((Activity)mContext).isDestroyed())return;
                            if (remarkListInfo.getStatus() == 1) {
                                callBack2.send(remarkListInfo.getData());
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideDialog();
                            ToastShow.showToast2((Activity) mContext, e.getMessage());
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            ToastShow.showToast2((Activity) mContext, mContext.getString(R.string.toast_network_error));
        }
    }

    //添加好友
    public void addFriend(String toco_id,String friend_label){
        if (UtilTool.isNetworkAvailable(mContext)) {
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .addFriend(UtilTool.getToken(),toco_id,friend_label)
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
                            }else{
                                Toast.makeText(mContext, mContext.getString(R.string.send_request_error), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideDialog();
                            ToastShow.showToast2((Activity) mContext, e.getMessage());
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    //確認添加
    public void confirmAddFriend(String toco_id, int confirm, final CallBack callBack){
        if (UtilTool.isNetworkAvailable(mContext)) {
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .confirmAddFriend(UtilTool.getToken(),toco_id,confirm)
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
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideDialog();
                            ToastShow.showToast2((Activity) mContext, e.getMessage());
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    //刪除好友
    public void deleteFriend(String toco_id, final CallBack callBack){
        if (UtilTool.isNetworkAvailable(mContext)) {
            RetrofitUtil.getInstance(mContext)
                    .getServer()
                    .deleteFriend(UtilTool.getToken(),toco_id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                    .subscribe(new Observer<BaseInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseInfo baseInfo) {
                            if (baseInfo.getStatus() == 1) {
                                Toast.makeText(mContext, mContext.getString(R.string.delete_succeed), Toast.LENGTH_SHORT).show();
                                callBack.send();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideDialog();
                            ToastShow.showToast2((Activity) mContext, e.getMessage());
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
        void send(List<AuatarListInfo.DataBean> data);
    }

    public interface CallBack3{
        void send(List<RemarkListInfo.DataBean> listdata);
    }
}
