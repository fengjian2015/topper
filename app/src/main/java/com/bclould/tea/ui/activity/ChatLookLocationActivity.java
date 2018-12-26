package com.bclould.tea.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bclould.tea.R;
import com.bclould.tea.base.MyApp;
import com.bclould.tea.ui.widget.CenterIcon;
import com.bclould.tea.ui.widget.OpenMapFromBottomPopup;
import com.bclould.tea.utils.AppLanguageUtils;
import com.bclould.tea.utils.MySharedPreferences;
import com.bclould.tea.utils.ToastShow;
import com.bclould.tea.utils.UtilTool;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ChatLookLocationActivity extends AppCompatActivity implements
        TencentLocationListener, TencentMap.OnMapCameraChangeListener {

    @Bind(R.id.bark)
    ImageView bark;
    @Bind(R.id.mapview)
    MapView mapview;

    @Bind(R.id.iv_location)
    ImageView ivLocation;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_address)
    TextView tvAddress;
    @Bind(R.id.iv_open_map)
    ImageView ivOpenMap;

    private Marker mLocationMarker;
    private Marker mOtherMarker;
    private TencentMap tencentMap;
    private TencentLocation mLocation;
    private TencentLocationManager mLocationManager;
    private CenterIcon centerIcon = null;
    private LatLng latLngLocation;

    private float lat;
    private float lng;
    private String title;
    private String address;
    private LatLng myLatlng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_look_location);
        ButterKnife.bind(this);
        setTitle(getString(R.string.location));
        MyApp.getInstance().addActivity(this);
        initIntent();
        checkSelf();
        initMap();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, MySharedPreferences.getInstance().getString(newBase.getString(R.string.language_pref_key))));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        startLocation();
    }

    private void checkSelf(){
        if (Build.VERSION.SDK_INT >= 23) {
            String[] permissions = {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };

            if (checkSelfPermission(permissions[0]) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(permissions, 0);
            }else{
                startLocation();
            }
        }else{
            startLocation();
        }
    }

    private void initIntent() {
        lng = getIntent().getFloatExtra("lng", 0);
        lat = getIntent().getFloatExtra("lat", 0);
        title = getIntent().getStringExtra("title");
        address = getIntent().getStringExtra("address");
        tvTitle.setText(title);
        tvAddress.setText(address);
    }

    private void startLocation() {
        LogUtil.e("开始定位" + mLocationManager);
        TencentLocationListener listener = this;
        TencentLocationRequest request = TencentLocationRequest.create();
        request.setInterval(10000);
        request.setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_NAME);
        request.setAllowGPS(true);
        request.setAllowDirection(true);
        TencentLocationManager locationManager = TencentLocationManager.getInstance(this);
        int error = locationManager.requestLocationUpdates(request, listener);
        LogUtil.e("定位error:" + error);

    }

    private void initMap() {
        // 初始化屏幕中心
        centerIcon = new CenterIcon(this, mapview);
        getWindow().addContentView(
                centerIcon,
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
        tencentMap = mapview.getMap();
        tencentMap.setZoom(16);
        tencentMap.setCenter(new LatLng(lat, lng));
        UiSettings uiSettings = mapview.getUiSettings();
        uiSettings.setAnimationEnabled(true);
        mLocationManager = TencentLocationManager.getInstance(this);
        tencentMap.setOnMapCameraChangeListener(this);

        mOtherMarker = tencentMap.addMarker(new MarkerOptions().
                position(new LatLng(lat, lng)).
                icon(BitmapDescriptorFactory.fromResource(R.mipmap.other_location)));
    }

    @OnClick({R.id.bark, R.id.iv_location,R.id.iv_open_map})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bark:
                finish();
                break;
            case R.id.iv_location:
                if (tencentMap != null && myLatlng != null) {
                    tencentMap.animateTo(myLatlng);
                }else{
                    ToastShow.showToast2(ChatLookLocationActivity.this,getString(R.string.no_location_obtained));
                }
                break;
            case R.id.iv_open_map:
                if(latLngLocation==null){
                    ToastShow.showToast2(ChatLookLocationActivity.this,getString(R.string.no_location_obtained));
                    return;
                }
                new OpenMapFromBottomPopup(ChatLookLocationActivity.this,latLngLocation.getLatitude(),latLngLocation.getLongitude(),lat,lng).executeShowPopWindow(ivOpenMap);
                break;
        }
    }

    @Override
    public void onLocationChanged(TencentLocation tencentLocation, int error, String s) {
        if (error == TencentLocation.ERROR_OK) {
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
            myLatlng = latLngLocation;
//            if (isFirstEnter) {
//                isFirstEnter = false;
//            } else {
//
//            }
            // 更新 location Marker
            if (mLocationMarker == null) {
                mLocationMarker =
                        tencentMap.addMarker(new MarkerOptions().
                                position(latLngLocation).
                                icon(BitmapDescriptorFactory.fromResource(R.mipmap.a5z)));
            } else {
                mLocationMarker.setPosition(latLngLocation);
            }
        }
    }

    @Override
    public void onStatusUpdate(String s, int i, String s1) {

    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {

    }

    @Override
    protected void onDestroy() {
        mapview.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapview.onPause();
        stopLocation();
    }

    private void stopLocation() {
        if(mLocationManager==null)return;
        mLocationManager.removeUpdates(this);
    }

    @Override
    protected void onStop() {
        mapview.onStop();
        super.onStop();
    }
}
