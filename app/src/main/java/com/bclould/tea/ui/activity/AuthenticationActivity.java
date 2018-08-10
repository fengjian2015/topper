package com.bclould.tea.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.LoginBaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.topperchat.WsConnection;
import com.bclould.tea.ui.widget.ConfirmDialog;
import com.bclould.tea.ui.widget.RoundImageView;
import com.bclould.tea.utils.MessageEvent;
import com.bclould.tea.utils.UtilTool;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@RequiresApi(api = Build.VERSION_CODES.N)
public class AuthenticationActivity extends LoginBaseActivity {

    @Bind(R.id.iv_move)
    ImageView mIvMove;
    @Bind(R.id.iv_target)
    ImageView mIvTarget;

    private int oldleft;
    private int oldtop;
    private int oldright;
    private int oldbottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.getInstance().addActivity(this);
        setContentView(R.layout.activity_authentication);
        ButterKnife.bind(this);
        initMove();
    }

    private void initMove() {
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
                oldbottom= oldtop + mIvMove.getMeasuredHeight();
            }
        });

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
                        final ConfirmDialog confirmDialog=new ConfirmDialog(AuthenticationActivity.this);
                        confirmDialog.show();
                        if(isTouchPointInView(mIvMove,mIvTarget)){
                            confirmDialog.setTvTitle(getString(R.string.security_verification_success));
                            confirmDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    success();
                                }
                            });
                        }else{
                            confirmDialog.setTvTitle(getString(R.string.security_verification_failure));
                            confirmDialog.setTvContent(getString(R.string.security_verification_failure_hint));
                            mIvMove.layout(oldleft, oldtop,oldright, oldbottom);
                        }
                        break;
                }
                return true;
            }
        });
    }

    private void success(){
        Intent intent=new Intent(this,LoginActivity.class);
        intent.putExtra("isState",true);
        startActivity(intent);
        finish();
    }

    private boolean isTouchPointInView(View view,View target) {
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
        int bottom = top + view.getMeasuredHeight()-20;

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
