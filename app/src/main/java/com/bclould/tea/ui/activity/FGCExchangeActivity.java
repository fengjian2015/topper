package com.bclould.tea.ui.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bclould.tea.Presenter.FinanciaPresenter;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseActivity;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.model.BaseInfo;
import com.bclould.tea.model.FGCInfo;
import com.bclould.tea.ui.adapter.FGCAdapter;
import com.bclould.tea.ui.widget.MyListView;
import com.bclould.tea.ui.widget.PWDDialog;
import com.bclould.tea.utils.AnimatorTool;
import com.bclould.tea.utils.UtilTool;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@RequiresApi(api = Build.VERSION_CODES.N)
public class FGCExchangeActivity extends BaseActivity implements OnChartGestureListener, OnChartValueSelectedListener {

    @Bind(R.id.et_count)
    EditText mEtCount;
    @Bind(R.id.tv_coin)
    TextView mTvCoin;
    @Bind(R.id.tv_price)
    TextView mTvPrice;
    @Bind(R.id.btn_float)
    Button mBtnFloat;
    @Bind(R.id.tv_available)
    TextView mTvAvailable;
    @Bind(R.id.btn_exchange)
    Button mBtnExchange;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.ll_exchange)
    LinearLayout mLlExchange;
    @Bind(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;
    @Bind(R.id.mLineChar)
    LineChart mLineChar;
    private int page = 1;
    private FGCAdapter mFGCAdapter;
    private List<FGCInfo.DataBean.RecordBean> mHashMapList = new ArrayList<>();
    private double maxMoney = 0;
    private double rate;
    private PWDDialog pwdDialog;
    private LineDataSet set1;
    protected List<String> mMonths =new ArrayList<>();
    private LinearLayoutManager mLinearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fgcexchange);
        ButterKnife.bind(this);
        setTitle(getString(R.string.exchange_fgc));
        MyApp.getInstance().addActivity(this);
        initRecylerView();
        setOnClick();
        setChat();
        initHttp(true, 1);
    }


    private void initRecylerView() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mFGCAdapter = new FGCAdapter(this, mHashMapList);
        mRecyclerView.setAdapter(mFGCAdapter);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initHttp(true, 1);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                initHttp(false, page + 1);
            }
        });
    }

    private void initHttp(final boolean isRefresh, int p) {
        new FinanciaPresenter(this).exchangeFGC(p, new FinanciaPresenter.CallBack6() {
            @Override
            public void send(FGCInfo baseInfo) {
                resetRefresh(isRefresh);
                if (baseInfo.getData().getRecord().size() == 10) {
                    mRefreshLayout.setEnableLoadMore(true);
                } else {
                    mRefreshLayout.setEnableLoadMore(false);
                }
                setViewData(baseInfo, isRefresh);
            }

            @Override
            public void error() {
                resetRefresh(isRefresh);
            }
        });
    }

    private void setViewData(FGCInfo baseInfo, boolean isRefresh) {
        if(baseInfo.getData().getX()==null)return;
        if (isRefresh) {
            page = 1;
            mBtnFloat.setText(baseInfo.getData().getRate() + ":1");
            mTvAvailable.setText(getString(R.string.available_fgc) + baseInfo.getData().getFgc_num());
            mEtCount.setHint(baseInfo.getData().getUsd_num() + "");
            maxMoney = baseInfo.getData().getUsd_num();
            rate = baseInfo.getData().getRate();
            mHashMapList.clear();

            //设置数据
            setChatData(baseInfo,baseInfo.getData().getY());
            //默认动画
            mLineChar.animateXY(1000,1000);
            // 得到这个文字
            Legend l = mLineChar.getLegend();
            // 修改文字 ...
            l.setForm(Legend.LegendForm.LINE);
        } else {
            page++;
        }
        mHashMapList.addAll(baseInfo.getData().getRecord());
        mFGCAdapter.notifyDataSetChanged();
    }

    private void setChat(){
        //设置手势滑动事件
        mLineChar.setOnChartGestureListener(this);
        //设置数值选择监听
        mLineChar.setOnChartValueSelectedListener(this);
        //后台绘制
        mLineChar.setDrawGridBackground(false);
        //设置描述文本
        mLineChar.getDescription().setEnabled(false);
        //设置支持触控手势
        mLineChar.setTouchEnabled(true);
        //设置缩放
        mLineChar.setDragEnabled(true);
        //设置推动
        mLineChar.setScaleEnabled(true);
        //如果禁用,扩展可以在x轴和y轴分别完成
        mLineChar.setPinchZoom(true);
        mLineChar.setDescription(null);

        XAxis xAxis = mLineChar.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//位置
        //设置x轴的最大值
//        xAxis.setAxisMaximum(300f);
        //设置x轴的最小值
        xAxis.setAxisMinimum(0f);
        xAxis.setTextColor(getResources().getColor(R.color.secondary_text_color));
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mMonths.get((int) value % mMonths.size());
            }
        });
        YAxis leftAxis = mLineChar.getAxisLeft();
        //重置所有限制线,以避免重叠线
        leftAxis.removeAllLimitLines();
        //y轴最大
//        leftAxis.setAxisMaximum(200f);
        //y轴最小
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextColor(getResources().getColor(R.color.secondary_text_color));
//        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);
        // 限制数据(而不是背后的线条勾勒出了上面)
        leftAxis.setDrawLimitLinesBehindData(true);
        mLineChar.getAxisRight().setEnabled(false);
    }

    private void setChatData(FGCInfo baseInfo, List<Float> ylist){
        try {
            if(ylist==null||ylist.size()==0)return;
            ArrayList<Entry> values = new ArrayList<Entry>();
            //这里我模拟一些数据
            Float max = Collections.max(ylist);//获取最大值
            Float min = Collections.min(ylist);
            YAxis leftAxis = mLineChar.getAxisLeft();
//            if(min>3000) {
//                min=min-500;
//                leftAxis.setAxisMinimum(min);
//            }else if(min>1000){
//                min=min-100;
//            }else if(min>10){
//                min=min-10;
//            }
            min=min-((max+min)/2-min);

            leftAxis.setAxisMinimum(min);
            mMonths.clear();
            values.add(new Entry(0,  min));
            mMonths.add("");
            for(int i=0;i<baseInfo.getData().getX().size();i++){
                mMonths.add(baseInfo.getData().getX().get(i));
                Entry entry = new Entry(i+1,baseInfo.getData().getY().get(i));
                values.add(entry);
            }

            set1 = new LineDataSet(values, null);
            set1.setHighlightEnabled(false);
            set1.setDrawFilled(true);
            set1.setDrawValues(false);
            set1.setDrawCircles(true);  //设置有圆点
            if (Utils.getSDKInt() >= 18) {
                set1.setFillDrawable(getResources().getDrawable(R.drawable.fgc_xian));
            }
            set1.setColor(getResources().getColor(R.color.blue3));
            set1.setValueTextColor(getResources().getColor(R.color.secondary_text_color));
            set1.setLineWidth(2f);
            set1.setDrawHighlightIndicators(false);
            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            //添加数据集
            dataSets.add(set1);
            set1.setDrawValues(!set1.isDrawValuesEnabled());
            //创建一个数据集的数据对象
            LineData data = new LineData(dataSets);

            //谁知数据
            mLineChar.setData(data);
            mLineChar.setVisibleXRangeMaximum(7);//设置x轴的显示数值间距
            mLineChar.invalidate();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void resetRefresh(boolean isRefresh) {
        if (isRefresh) {
            mRefreshLayout.finishRefresh();
        } else {
            mRefreshLayout.finishLoadMore();
        }
    }


    private void setOnClick() {
        mEtCount.setFilters(new InputFilter[]{lengthFilter});
        mEtCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                double counts = UtilTool.parseDouble(mEtCount.getText().toString());
//                if (counts > maxMoney) {
//                    mEtCount.setText(maxMoney + "");
//                    mEtCount.setSelection(mEtCount.getText().length());
//                }
                setTvPrice(mEtCount.getText().toString());
            }
        });
    }

    private void setTvPrice(String money) {
        String format = UtilTool.changeMoney1(UtilTool.parseDouble(money) / rate);
        mTvPrice.setText(format);
    }

    private InputFilter lengthFilter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            // source:当前输入的字符
            // start:输入字符的开始位置
            // end:输入字符的结束位置
            // dest：当前已显示的内容
            // dstart:当前光标开始位置
            // dent:当前光标结束位置
            if (dest.length() == 0 && source.equals(".")) {
                return "0.";
            }
            String dValue = dest.toString();
            String[] splitArray = dValue.split("\\.");
            if (splitArray.length > 1) {
                String dotValue = splitArray[1];
                if (dotValue.length() == 8) {
                    return "";
                }
            }
            return null;
        }

    };

    @OnClick({R.id.bark, R.id.btn_exchange})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.btn_exchange:
                if (checkEidt()) {
                    showPWDialog();
                }
                break;

        }
    }

    private void showPWDialog() {
        pwdDialog = new PWDDialog(this);
        pwdDialog.setOnPWDresult(new PWDDialog.OnPWDresult() {
            @Override
            public void success(String password) {
                exchange(password);
            }
        });
        String count = mEtCount.getText().toString();
        pwdDialog.showDialog(count, "USDT", getString(R.string.exchange) + "GC", null, null);
    }

    private void exchange(String password) {
        new FinanciaPresenter(this).exchange(mEtCount.getText().toString(), password, new FinanciaPresenter.CallBack1() {
            @Override
            public void send(BaseInfo baseInfo) {
                initHttp(true, 1);
            }

            @Override
            public void error() {

            }
        });
    }

    private boolean checkEidt() {
        if (UtilTool.parseDouble(mEtCount.getText().toString()) <= 0) {
            AnimatorTool.getInstance().editTextAnimator(mEtCount);
            Toast.makeText(this, getString(R.string.toast_count), Toast.LENGTH_SHORT).show();
        } else {
            return true;
        }
        return false;
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        // 完成之后停止晃动
        if (lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            mLineChar.highlightValues(null);
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
