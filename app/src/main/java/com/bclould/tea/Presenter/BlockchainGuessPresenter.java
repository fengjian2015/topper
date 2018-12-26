package com.bclould.tea.Presenter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.bclould.tea.R;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.model.BetInfo;
import com.bclould.tea.model.GuessInfo;
import com.bclould.tea.model.GuessListInfo;
import com.bclould.tea.network.RetrofitUtil;
import com.bclould.tea.ui.widget.LoadingProgressDialog;
import com.bclould.tea.utils.UtilTool;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by GA on 2018/4/23.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class BlockchainGuessPresenter {

    private final Context mContext;
    private LoadingProgressDialog mProgressDialog;

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

    public BlockchainGuessPresenter(Context context) {
        mContext = context;
    }

    public void getGuessList(int page, int page_size, int type, String user, final CallBack callBack) {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .GuessList(UtilTool.getToken(), page, page_size, type, user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<GuessListInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GuessListInfo guessListInfo) {
                        if (guessListInfo.getStatus() == 1) {
                            callBack.send(guessListInfo.getData());
                        }else{
                            callBack.finishRefresh();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.error();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getGuessHistory(int page, int page_size, final CallBack callBack) {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .getGuessHistory(UtilTool.getToken(), page, page_size)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<GuessListInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GuessListInfo guessListInfo) {
                        if (guessListInfo.getStatus() == 1) {
                            callBack.send(guessListInfo.getData());
                        }else{
                            callBack.finishRefresh();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.error();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void pushingGuess(int id, String title, String count, String timeMinute, String sum, String second_password, String singleCount, String password, final CallBack2 callBack2) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .publishGuess(UtilTool.getToken(), title, count, singleCount, id + "", timeMinute, second_password, password)
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
                            callBack2.send();
                        } else {
                            Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        UtilTool.Log("發佈競猜", baseInfo.getMessage());
                    }

                    @Override
                    public void onError(Throwable e) {
                        UtilTool.Log("發佈競猜", e.getMessage());
                        hideDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getGuessInfo(int bet_id, int period_qty, final CallBack3 callBack3) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .GuessInfo(UtilTool.getToken(), bet_id, period_qty)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<GuessInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GuessInfo guessInfo) {
                        hideDialog();
                        if (guessInfo.getStatus() == 1) {
                            callBack3.send(guessInfo.getData());
                        }
                        UtilTool.Log("發佈競猜", guessInfo.getMessage());
                    }

                    @Override
                    public void onError(Throwable e) {
                        UtilTool.Log("發佈競猜", e.getMessage());
                        hideDialog();
                        callBack3.error();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getRandom(final CallBack4 callBack4) {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .getRandom(UtilTool.getToken())
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
                            callBack4.send(baseInfo.getData().getBet_coin());
                        } else {
                            Toast.makeText(mContext, baseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        UtilTool.Log("發佈競猜", e.getMessage());
                        hideDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void bet(int bet_id, int period_qty, int coin_id, String random, String password, final CallBack5 callBack5) {
        showDialog();
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .bet(UtilTool.getToken(), bet_id, period_qty, coin_id + "", random, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<BetInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BetInfo betInfo) {
                        hideDialog();
                        if (betInfo.getStatus() == 1) {
                            callBack5.send(betInfo.getData());
                            Toast.makeText(mContext, mContext.getString(R.string.bet_succeed), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, betInfo.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        UtilTool.Log("發佈競猜", e.getMessage());
                        hideDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getMyStart(int page, int page_size, int status, final CallBack callBack) {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .getMyStart(UtilTool.getToken(), page, page_size, status)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<GuessListInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GuessListInfo guessListInfo) {
                        hideDialog();
                        if (guessListInfo.getStatus() == 1) {
                            callBack.send(guessListInfo.getData());
                        } else {
                            callBack.finishRefresh();
                            Toast.makeText(mContext, guessListInfo.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        UtilTool.Log("發佈競猜", e.getMessage());
                        hideDialog();
                        callBack.error();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getMyJoin(int page, int page_size, int status, final CallBack callBack) {
        RetrofitUtil.getInstance(mContext)
                .getServer()
                .getMyJoin(UtilTool.getToken(), page, page_size, status)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//请求完成后在主线程更显UI
                .subscribe(new Observer<GuessListInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GuessListInfo guessListInfo) {
                        hideDialog();
                        if (guessListInfo.getStatus() == 1) {
                            callBack.send(guessListInfo.getData());
                        } else {
                            callBack.finishRefresh();
                            Toast.makeText(mContext, guessListInfo.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        UtilTool.Log("發佈競猜", e.getMessage());
                        hideDialog();
                        callBack.error();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //定义接口
    public interface CallBack {
        void send(List<GuessListInfo.DataBean> data);

        void error();
        void finishRefresh();
    }

    //定义接口
    public interface CallBack2 {
        void send();
    }

    //定义接口
    public interface CallBack3 {
        void send(GuessInfo.DataBean data);

        void error();
    }

    //定义接口
    public interface CallBack4 {
        void send(String data);
    }

    //定义接口
    public interface CallBack5 {
        void send(BetInfo.DataBean data);
    }
}
