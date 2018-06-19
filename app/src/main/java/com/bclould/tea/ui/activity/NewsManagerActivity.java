package com.bclould.tea.ui.activity;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.ui.adapter.NewsManagerPVAdapter;
import com.bclould.tea.ui.adapter.PayManageGVAdapter;
import com.bclould.tea.utils.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bclould.tea.R.style.BottomDialog;

/**
 * Created by GA on 2018/5/8.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class NewsManagerActivity extends BaseActivity {


    @Bind(R.id.bark)
    ImageView mBark;
    @Bind(R.id.tv_filtrate)
    TextView mTvFiltrate;
    @Bind(R.id.tv_empty)
    TextView mTvEmpty;
    @Bind(R.id.xx)
    TextView mXx;
    @Bind(R.id.xx2)
    TextView mXx2;
    @Bind(R.id.xx3)
    TextView mXx3;
    @Bind(R.id.ll_menu)
    LinearLayout mLlMenu;
    @Bind(R.id.view_pager)
    ViewPager mViewPager;
    private Dialog mBottomDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_manager);
        ButterKnife.bind(this);
        initFiltrate();
        mViewPager.setCurrentItem(0);
        mTvFiltrate.setVisibility(View.VISIBLE);
        setSelector(0);
        initTopMenu();
        initViewPager();
    }

    private void initFiltrate() {
        mType = "0";
        mMap.put(getString(R.string.filtrate), 0);
        mFiltrateList.add(getString(R.string.all));
        mFiltrateList.add(getString(R.string.check_pending));
        mFiltrateList.add(getString(R.string.pass));
        mFiltrateList.add(getString(R.string.no_pass));
    }

    private void setSelector(int index) {
        switch (index) {
            case 0:
                mXx.setVisibility(View.VISIBLE);
                mXx2.setVisibility(View.INVISIBLE);
                mXx3.setVisibility(View.INVISIBLE);
                break;
            case 1:
                mXx.setVisibility(View.INVISIBLE);
                mXx2.setVisibility(View.VISIBLE);
                mXx3.setVisibility(View.INVISIBLE);
                break;
            case 2:
                mXx.setVisibility(View.INVISIBLE);
                mXx2.setVisibility(View.INVISIBLE);
                mXx3.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void initTopMenu() {
        for (int i = 0; i < mLlMenu.getChildCount(); i++) {
            View childAt = mLlMenu.getChildAt(i);
            childAt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = mLlMenu.indexOfChild(view);
                    setSelector(index);
                    mViewPager.setCurrentItem(index);
                }
            });
        }
    }

    private void initViewPager() {
        NewsManagerPVAdapter newsManagerPVAdapter = new NewsManagerPVAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(newsManagerPVAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setSelector(position);
                if (position == 0) {
                    mTvFiltrate.setVisibility(View.VISIBLE);
                    mTvEmpty.setVisibility(View.GONE);
                } else if (position == 1) {
                    mTvFiltrate.setVisibility(View.GONE);
                    mTvEmpty.setVisibility(View.VISIBLE);
                } else {
                    mTvFiltrate.setVisibility(View.GONE);
                    mTvEmpty.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick({R.id.bark, R.id.tv_filtrate, R.id.tv_empty})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.tv_filtrate:
                showFiltrateDialog();
                break;
            case R.id.tv_empty:
                EventBus.getDefault().post(new MessageEvent(getString(R.string.empty_news_browsing_history_callback)));

                break;
        }
    }

    String mType = "";
    private Map<String, Integer> mMap = new HashMap<>();
    private List<String> mFiltrateList = new ArrayList<>();

    private void showFiltrateDialog() {
        mBottomDialog = new Dialog(this, R.style.BottomDialog2);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_bill, null);
        //获得dialog的window窗口
        Window window = mBottomDialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //将设置好的属性set回去
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(BottomDialog);
        mBottomDialog.setContentView(contentView);
        mBottomDialog.show();
        GridView gridView = (GridView) mBottomDialog.findViewById(R.id.grid_view);
        Button cancel = (Button) mBottomDialog.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomDialog.dismiss();
            }
        });
        gridView.setAdapter(new PayManageGVAdapter(this, mFiltrateList, mMap, new PayManageGVAdapter.CallBack() {
            //接口回调
            @Override
            public void send(int position, String typeName) {
                if (typeName.equals(getString(R.string.all))) {
                    mType = "6";
                } else if (typeName.equals(getString(R.string.check_pending))) {
                    mType = "1";
                } else if (typeName.equals(getString(R.string.pass))) {
                    mType = "2";
                } else if (typeName.equals(getString(R.string.no_pass))) {
                    mType = "5";
                }
                MessageEvent messageEvent = new MessageEvent(getString(R.string.news_filtrate));
                messageEvent.setFiltrate(mType);
                EventBus.getDefault().post(messageEvent);
                mMap.put(getString(R.string.filtrate), position);
                mBottomDialog.dismiss();
            }
        }));
    }
}
