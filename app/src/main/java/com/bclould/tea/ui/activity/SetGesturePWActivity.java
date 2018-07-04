package com.bclould.tea.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.ui.widget.GestureLockViewGroup;
import com.bclould.tea.utils.UtilTool;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GA on 2018/5/22.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class SetGesturePWActivity extends BaseActivity {

    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.rl_title)
    RelativeLayout mRlTitle;
    @Bind(R.id.gesture_view)
    GestureLockViewGroup mGestureView;
    private String mAnswer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.getInstance().addActivity(this);
        setContentView(R.layout.activity_set_gesture);
        ButterKnife.bind(this);
        initGesture();
    }

    private void initGesture() {
        mGestureView.setUnMatchExceedBoundary(5);
        mGestureView.setOnGestureLockViewListener(new GestureLockViewGroup.OnGestureLockViewListener() {
            @Override
            public void onBlockSelected(int position) {
                UtilTool.Log("手勢", position + "   ：onBlockSelected");
                mAnswer += position;
            }

            @Override
            public void onGestureEvent(boolean matched) {
                UtilTool.Log("手勢", matched + "   ：onGestureEvent");
            }

            @Override
            public void onUnmatchedExceedBoundary() {
                UtilTool.Log("手勢", "onUnmatchedExceedBoundary");
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureView.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @OnClick(R.id.bark)
    public void onViewClicked() {
        finish();
    }
}
