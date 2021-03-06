package com.bclould.tea.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.bclould.tea.R;
import com.bclould.tea.base.BaseNormalActivity;
import com.bclould.tea.model.LocationInfo;
import com.bclould.tea.ui.adapter.LocationListAdapter;
import com.bclould.tea.ui.widget.AppTitle;
import com.bclould.tea.ui.widget.CenterIcon1;
import com.bclould.tea.ui.widget.ScreenListPopWindow;
import com.bclould.tea.utils.ActivityUtil;
import com.bclould.tea.utils.LoadingProgressUtil;
import com.bclould.tea.utils.StringUtils;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;
import com.bclould.tea.xmpp.RoomManage;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.tencent.lbssearch.TencentSearch;
import com.tencent.lbssearch.httpresponse.BaseObject;
import com.tencent.lbssearch.httpresponse.HttpResponseListener;
import com.tencent.lbssearch.object.Location;
import com.tencent.lbssearch.object.param.Geo2AddressParam;
import com.tencent.lbssearch.object.result.Geo2AddressResultObject;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.mapsdk.raster.model.BitmapDescriptorFactory;
import com.tencent.mapsdk.raster.model.CameraPosition;
import com.tencent.mapsdk.raster.model.LatLng;
import com.tencent.mapsdk.raster.model.Marker;
import com.tencent.mapsdk.raster.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.map.MapView;
import com.tencent.tencentmap.mapsdk.map.TencentMap;
import com.tencent.tencentmap.mapsdk.map.UiSettings;
import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import jp.wasabeef.recyclerview.animators.ScaleInBottomAnimator;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by wushange on 2016/07/13.
 */
public class LocationActivity extends BaseNormalActivity implements
        TencentLocationListener, TencentMap.OnMapCameraChangeListener {
    @ViewInject(R.id.apptitle)
    AppTitle appTitle;
    @ViewInject(R.id.ll_sel_location)
    LinearLayout ll_sel_location;
    @ViewInject(R.id.mapview)
    private MapView mapView;
    @ViewInject(R.id.poi_list)
    private EasyRecyclerView mRecyclerView;
    @ViewInject(R.id.et_search)
    private EditText mEtSearch;
    @ViewInject(R.id.rl_data)
    private RelativeLayout rl_data;
    LocationListAdapter mAdapter;
    private Marker mLocationMarker;
    private Marker mCenterMarker;
    private TencentMap tencentMap;
    private TencentLocation mLocation;
    private TencentLocationManager mLocationManager;
    private boolean isFirstEnter = true;//第一次进入动画效果移动到定位点
    private boolean isActiveMove = false;//是不是主动拖动屏幕
    TencentSearch tencentSearch = new TencentSearch(this);
    private CenterIcon1 centerIcon = null;
    private int oldClick = 0;//記錄上次點擊點
    private String mUser;
    private Context context;
    private LocationInfo locationInfo;
    private List<LocationInfo.DataBean> mList=new ArrayList<>();
    private LoadingProgressUtil mLoadingProgressUtil;
    private ScreenListPopWindow screenListPopWindow;
    private String title;

    private MyHandler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//            getWindow().setStatusBarColor(getResources().getColor(R.color.appThemeColor));
//        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location_main_view);
        x.Ext.init(this.getApplication());
        x.Ext.setDebug(true);
        x.view().inject(this);
        mHandler = new MyHandler(this);
        context = this;
        checkSelf();
        initView();
        iniRecycerView();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        startLocation();
    }

    private void checkSelf() {
        if (Build.VERSION.SDK_INT >= 23) {
            String[] permissions = {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };

            if (checkSelfPermission(permissions[0]) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permissions, 0);
            } else {
                startLocation();
            }
        } else {
            startLocation();
        }
    }

    private void startLocation() {
        LogUtil.e("开始定位" + mLocationManager);
        TencentLocationListener listener = this;
        TencentLocationRequest request = TencentLocationRequest.create();
        request.setInterval(10000);

        request.setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_NAME);
//        request.setAllowGPS(true);
        request.setAllowDirection(true);
        TencentLocationManager locationManager = TencentLocationManager.getInstance(this);
        int error = locationManager.requestLocationUpdates(request, listener);
        LogUtil.e("定位error:" + error + "   GPS:" + request.isAllowGPS());

    }


    public void initView() {
        final int type = getIntent().getIntExtra("type", 0);
        String right;
        if (type == 1) {
            mUser = getIntent().getStringExtra("user");
            right = getString(R.string.send);
        } else {
            right = getString(R.string.confirm);
        }
        mLoadingProgressUtil=new LoadingProgressUtil(this);
        mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // TODO: 2018/8/21 发送报文请求
                    if(StringUtils.isEmpty(mEtSearch.getText().toString())){
                        ToastShow.showToast2(LocationActivity.this,getString(R.string.pleass_input_location));
                        return false;
                    }
                    okHttp_synchronousGet(mEtSearch.getText().toString());
                }
                return false;
            }
        });

        appTitle.setCenterTitle(getString(R.string.location)).setLeftButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }).setRightText(right).setRightTextClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.getMap().getScreenShot(new TencentMap.OnScreenShotListener() {
                    @Override
                    public void onMapScreenShot(Bitmap arg0) {
                        // TODO Auto-generated method stub
                        if (mAdapter.getCount() == 0) return;
                        if (type == 1) {
                            RoomManage.getInstance().getRoom(mUser).sendLocationMessage(null, arg0
                                    , mAdapter.getItem(oldClick).title
                                    , mAdapter.getItem(oldClick).address
                                    , mAdapter.getItem(oldClick).location.lat
                                    , mAdapter.getItem(oldClick).location.lng);
                        } else {
                            Intent intent = new Intent(LocationActivity.this, PublicshDynamicActivity.class);
                            String address = mAdapter.getItem(oldClick).address;
                            if (address.contains("省")) {
                                String city = address.substring(address.indexOf("省") + 1, address.indexOf("市"));
                                intent.putExtra("location", city + "•" + mAdapter.getItem(oldClick).title);
                            } else {
                                intent.putExtra("location", mAdapter.getItem(oldClick).address);
                            }
                            setResult(Activity.RESULT_OK, intent);
                        }
                        finish();
                    }
                });
            }
        });
    }

    private void initMap() {
        // 初始化屏幕中心
        centerIcon = new CenterIcon1(this, mapView);
        getWindow().addContentView(
                centerIcon,
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
        tencentMap = mapView.getMap();
        tencentMap.setZoom(16);
        UiSettings uiSettings = mapView.getUiSettings();
        uiSettings.setAnimationEnabled(true);
        mLocationManager = TencentLocationManager.getInstance(this);
        tencentMap.setOnMapCameraChangeListener(this);
    }

    private void iniRecycerView() {
        DividerDecoration itemDecoration = new DividerDecoration(Color.parseColor("#E5E5E5"), dp2px(context, 0.5f), dp2px(context, 0), 0);//color & height & paddingLeft & paddingRight
        itemDecoration.setDrawLastItem(true);//sometimes you don't want draw the divider for the last item,default is true.
        itemDecoration.setDrawHeaderFooter(false);//whether draw divider for header and footer,default is false.
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setItemAnimator(new ScaleInBottomAnimator(new OvershootInterpolator(1f)));
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setEmptyView(R.layout.loading_view_layout);
        mRecyclerView.setScrollBarStyle(-1);
        mRecyclerView.setAdapterWithProgress(mAdapter = new LocationListAdapter(context));
        mAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                UtilTool.Log("fengjian----", "點擊的位置：" + position);
                if (position == -1) return;
                mAdapter.getItem(oldClick).id = "";
                oldClick = position;
                mAdapter.getItem(position).id = "true";
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void okHttp_synchronousGet(final String keyword) {
        mLoadingProgressUtil.showDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "https://apis.map.qq.com/ws/place/v1/suggestion/?region="+title+"&keyword="+keyword+"&key=VKMBZ-GAMWJ-UA5FV-KAXJS-IEHET-QABI7";
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(url).build();
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        locationInfo= JSON.parseObject(response.body().string(), LocationInfo.class);
                        mHandler.sendEmptyMessage(1);
                    } else {
                    }
                    mLoadingProgressUtil.hideDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    class MyHandler extends Handler{
        private final WeakReference<LocationActivity> mActivty;

        public MyHandler(LocationActivity activity){
            mActivty =new WeakReference<LocationActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    UtilTool.hideSoftInputFromWindow(mapView,LocationActivity.this);
                    if(locationInfo!=null&&locationInfo.getData()!=null&&locationInfo.getStatus()==0){
                        if(locationInfo.getData().size()==0){
                            ToastShow.showToast2(LocationActivity.this,getString(R.string.address_not_found));
                            return;
                        }
                        mList.clear();
                        mList.addAll(locationInfo.getData());
                        showPop();
                    }else{
                        ToastShow.showToast2(LocationActivity.this,locationInfo.getMessage());
                    }
                    break;
            }
        }

    }

    private void showPop(){
        if(screenListPopWindow!=null){
            screenListPopWindow.dismiss();
        }else {
            screenListPopWindow=new ScreenListPopWindow(this,mList);
        }
        screenListPopWindow.setListOnClick(new ScreenListPopWindow.ListOnClick() {
            @Override
            public void onclickitem(int position) {
                Location location = new Location((float) mList.get(position).getLocation().getLat(), (float) mList.get(position).getLocation().getLng());
                searchPoi(location);
                LatLng latLngLocation = new LatLng( mList.get(position).getLocation().getLat(),  mList.get(position).getLocation().getLng());
                tencentMap.animateTo(latLngLocation);
            }
        });
        if(!ActivityUtil.isActivityOnTop(this))return;
        screenListPopWindow.showAsDropDown(rl_data,0,2);
    }

    @Override
    public void onLocationChanged(TencentLocation tencentLocation, int error, String reason) {
        LogUtil.e("----onLocationChanged---" + "----" + error + "---" + reason + tencentLocation.toString());
        if (error == TencentLocation.ERROR_OK) {
            LatLng latLngLocation = null;
            String provider = tencentLocation.getProvider();//定位方式
            if (TencentLocation.GPS_PROVIDER.equals(provider)) {
                // location 是GPS定位结果
                latLngLocation = new LatLng(tencentLocation.getLatitude(), tencentLocation.getLongitude());
                UtilTool.Log("位置", tencentLocation.getAddress() + "");
            } else if (TencentLocation.NETWORK_PROVIDER.equals(provider)) {
                // location 是网络定位结果
                UtilTool.Log("位置", tencentLocation.getAddress() + "");
                latLngLocation = new LatLng(tencentLocation.getLatitude(), tencentLocation.getLongitude());
            }
            mLocation = tencentLocation;
            if (isFirstEnter) {
                initMap();
//                tencentMap.animateTo(latLngLocation);
                tencentMap.setCenter(latLngLocation);
                isActiveMove = false;
                Location location = new Location((float) tencentLocation.getLatitude(), (float) tencentLocation.getLongitude());
                searchPoi(location);
                isFirstEnter = false;
            } else {

            }
            // 更新 location Marker
            if (mLocationMarker == null) {
                mLocationMarker =
                        tencentMap.addMarker(new MarkerOptions().
                                position(latLngLocation).
                                icon(BitmapDescriptorFactory.fromResource(R.mipmap.a5z)));
                title= tencentLocation.getAddress().substring(tencentLocation.getAddress().indexOf("省") + 1, tencentLocation.getAddress().indexOf("市"));

            } else {
                mLocationMarker.setPosition(latLngLocation);
            }
        }
    }

    @Override
    public void onStatusUpdate(String s, int i, String s1) {
        LogUtil.e("---onStatusUpdate---" + i + "---" + s + "---" + s1);
    }


    @Override
    public void onCameraChange(CameraPosition cameraPosition) {


    }

    private void stopLocation() {
        if (mLocationManager == null) return;
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        Location location = new Location(((float) cameraPosition.getTarget().getLatitude()), ((float) cameraPosition.getTarget().getLongitude()));
        TranslateAnimation alphaAnimation2 = new TranslateAnimation(0, 0, -100, 0F);  //同一个x轴 (开始结束都是50f,所以x轴保存不变)  y轴开始点50f  y轴结束点80f
        alphaAnimation2.setDuration(500);  //设置时间
        alphaAnimation2.setRepeatCount(Animation.ABSOLUTE);  //为重复执行的次数。如果设置为n，则动画将执行n+1次。INFINITE为无限制播放
        alphaAnimation2.setRepeatMode(Animation.RESTART);  //为动画效果的重复模式，常用的取值如下。RESTART：重新从头开始执行。REVERSE：反方向执行
        alphaAnimation2.setInterpolator(new LinearInterpolator());
        centerIcon.setAnimation(alphaAnimation2);
        alphaAnimation2.start();


        if (mCenterMarker == null) {
            mCenterMarker =
                    tencentMap.addMarker(new MarkerOptions().
                            position(cameraPosition.getTarget()).
                            icon(BitmapDescriptorFactory.fromResource(R.mipmap.aud)));
        } else {
            mCenterMarker.setPosition(cameraPosition.getTarget());
        }

        LogUtil.e("移动后坐标地址为" + cameraPosition.toString());
        if (isActiveMove == false) {

        } else {

            searchPoi(location);

        }
        isActiveMove = true;
    }


    /**
     * 搜索当前坐标点的poilist
     *
     * @param location
     */
    private void searchPoi(Location location) {
        mAdapter.clear();

        Geo2AddressParam param = new Geo2AddressParam().location(location).get_poi(true);
        tencentSearch.geo2address(param, new HttpResponseListener() {
            @Override
            public void onSuccess(int i, BaseObject baseObject) {
                if (baseObject != null) {
                    Geo2AddressResultObject oj = (Geo2AddressResultObject) baseObject;
                    Geo2AddressResultObject.ReverseAddressResult re = oj.result;

                    if (re.pois != null) {
                        oldClick = 0;
                        if (re.pois.size() > 0) {
                            re.pois.get(oldClick).id = "true";
                        }
                        mAdapter.addAll(re.pois);

                        for (Geo2AddressResultObject.ReverseAddressResult.Poi poi : re.pois) {
                            LogUtil.e("移动屏幕后检索当前位置信息--" + poi.title + "----" + poi.address + "  ---" + poi.location);
                        }
                    }


                }
            }

            @Override
            public void onFailure(int i, String s, Throwable throwable) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        if(mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        stopLocation();
    }


    @Override
    protected void onStop() {
        mapView.onStop();
        super.onStop();
    }


    public int dp2px(Context context, float value) {
        final float scale = context.getResources().getDisplayMetrics().densityDpi;
        return (int) (value * (scale / 160) + 0.5f);
    }
}
