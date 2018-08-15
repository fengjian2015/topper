package com.bclould.tea.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bclould.tea.Presenter.LoginPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.LoginBaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.ui.widget.ConfirmDialog;
import com.bclould.tea.utils.UtilTool;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@RequiresApi(api = Build.VERSION_CODES.N)
public class AuthenticationActivity extends LoginBaseActivity {

    @Bind(R.id.iv_move)
    ImageView mIvMove;
    @Bind(R.id.iv_target)
    ImageView mIvTarget;
    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.rl_title)
    RelativeLayout mRlTitle;
    @Bind(R.id.iv_title)
    ImageView mIvTitle;
    @Bind(R.id.tv_hint)
    TextView mTvHint;
    @Bind(R.id.rl_data)
    RelativeLayout mRlData;

    private int oldleft;
    private int oldtop;
    private int oldright;
    private int oldbottom;
    private int mTargetWidth;
    private int mTargetHeight;
    private int mCount = 0;
    private int mHeightPixels;
    private int mWidthPixels;
    private LoginPresenter mLoginPresenter;
    private String mEmail;
    private String mCoordinate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.getInstance().addActivity(this);
        setContentView(R.layout.activity_authentication);
        ButterKnife.bind(this);
        mLoginPresenter = new LoginPresenter(this);
        mEmail = getIntent().getStringExtra("email");
        getPhoneSize();
        initMove();
    }

    //获取屏幕高度
    private void getPhoneSize() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (this != null)
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mHeightPixels = displayMetrics.heightPixels;
        mWidthPixels = displayMetrics.widthPixels;
        mIvMove.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                mIvMove.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int[] location = new int[2];
                mIvMove.getLocationOnScreen(location);
                oldleft = location[0];
                oldtop = location[1];
                oldright = oldleft + mIvMove.getMeasuredWidth();
                oldbottom = oldtop + mIvMove.getMeasuredHeight();
                mCount++;
                if (mCount == 2) {
                    result();
                }
            }
        });
        mIvTarget.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                mIvTarget.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mTargetWidth = mIvTarget.getMeasuredWidth();
                mTargetHeight = mIvTarget.getMeasuredHeight();
                mCount++;
                if (mCount == 2) {
                    result();
                }
            }
        });
    }

    private void result() {
        String coordinate = "0-" + (mWidthPixels - mTargetWidth) + "," + (mHeightPixels - oldbottom - mTargetHeight) + "-" + (mHeightPixels - mTargetHeight * 2);
        mLoginPresenter.coordinate(coordinate, mEmail, new LoginPresenter.CallBack4() {
            @Override
            public void send(String coordinate) {
                mCoordinate = coordinate;
                String[] location = coordinate.split(",");
                int x = Integer.parseInt(location[0]);
                int y = Integer.parseInt(location[1]);
                setLayout(mIvTarget, x, y);
                UtilTool.Log("坐标", "x--" + x + "y--" + y);
                mRlData.setVisibility(View.VISIBLE);
            }

            @Override
            public void error() {
                finish();
            }
        });
    }

    public void setLayout(View view, int x, int y) {
        ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(view.getLayoutParams());
        margin.setMargins(x, y, mWidthPixels - x - mTargetWidth, mTargetHeight - y - mTargetHeight);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
        view.setLayoutParams(layoutParams);
    }


    private void initMove() {
        mIvMove.setOnTouchListener(new View.OnTouchListener() {
            private int startY;
            private int startX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        UtilTool.Log("打印操作：", "按下了");
                        //获取当前按下的坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //获取移动后的坐标
                        int moveX = (int) event.getRawX();
                        int moveY = (int) event.getRawY();
                        //拿到手指移动距离的大小
                        int move_bigX = moveX - startX;
                        int move_bigY = moveY - startY;
                        //拿到当前控件未移动的坐标
                        int left = mIvMove.getLeft();
                        int top = mIvMove.getTop();
                        left += move_bigX;
                        top += move_bigY;
                        int right = left + mIvMove.getWidth();
                        int bottom = top + mIvMove.getHeight();
                        mIvMove.layout(left, top, right, bottom);
                        startX = moveX;
                        startY = moveY;
                        break;
                    case MotionEvent.ACTION_UP:
                        final ConfirmDialog confirmDialog = new ConfirmDialog(AuthenticationActivity.this);
                        confirmDialog.show();
                        if (isTouchPointInView(mIvMove, mIvTarget)) {
                            confirmDialog.setTvTitle(getString(R.string.security_verification_success));
                            confirmDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    success();
                                }
                            });
                        } else {
                            confirmDialog.setTvTitle(getString(R.string.security_verification_failure));
                            confirmDialog.setTvContent(getString(R.string.security_verification_failure_hint));
                            mIvMove.layout(oldleft, oldtop, oldright, oldbottom);
                        }
                        break;
                }
                return true;
            }
        });
    }

    private void success() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("isState", true);
        intent.putExtra("coordinate", mCoordinate);
        startActivity(intent);
        finish();
    }

    private boolean isTouchPointInView(View view, View target) {
        int[] locationTarget = new int[2];
        target.getLocationOnScreen(locationTarget);
        int leftTarget = locationTarget[0];
        int topTarget = locationTarget[1];
        int rightTarget = leftTarget + target.getMeasuredWidth();
        int bottomTarget = topTarget + target.getMeasuredHeight();

        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getMeasuredWidth();
        int bottom = top + view.getMeasuredHeight() - 20;

        if (left >= leftTarget && top >= topTarget && right <= rightTarget && bottom <= bottomTarget) {
            return true;
        }
        return false;
    }

    @OnClick({R.id.bark})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
        }
    }
}
