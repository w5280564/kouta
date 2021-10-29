/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xunda.mo.hx.section.chat.activicy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.MyLocationStyle;
import com.hyphenate.easeui.ui.base.EaseBaseActivity;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.xunda.mo.R;

//LocationSource, AMapLocationListener
public class MyEaseGaodeMapActivity extends EaseBaseActivity implements EaseTitleBar.OnBackPressListener,
        EaseTitleBar.OnRightClickListener, AMap.OnMyLocationChangeListener {
    private EaseTitleBar titleBarMap;
    protected double latitude;
    protected double longtitude;
    protected String address;
    private MapView mMapView;
    AMap aMap;

    public static void actionStartForResult(Fragment fragment, int requestCode) {
        Intent intent = new Intent(fragment.getContext(), MyEaseGaodeMapActivity.class);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void actionStartForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, MyEaseGaodeMapActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void actionStart(Context context, double latitude, double longtitude, String address) {
        Intent intent = new Intent(context, MyEaseGaodeMapActivity.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longtitude", longtitude);
        intent.putExtra("address", address);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ease_activity_gaodemap);
        setFitSystemForTheme(false, R.color.transparent, true);
        initIntent();
        initView(savedInstanceState);
        initListener();
        initData();
    }

    private void initIntent() {
        latitude = getIntent().getDoubleExtra("latitude", 0);
        longtitude = getIntent().getDoubleExtra("longtitude", 0);
        address = getIntent().getStringExtra("address");
    }

    private void initView(Bundle savedInstanceState) {
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        //初始化地图控制器对象
        if (aMap == null) {
            aMap = mMapView.getMap();
        }

        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);//定位一次，且将视角移动到地图中心点。
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        //aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.setOnMyLocationChangeListener(this);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(17));//地图的缩放级别一共分为 17 级，从 3 到 19。数字越大，展示的图面信息越精细。

        titleBarMap = findViewById(R.id.title_bar_map);
//		mapView = findViewById(R.id.bmapView);
        titleBarMap.setRightTitleResource(R.string.button_send);
        double latitude = getIntent().getDoubleExtra("latitude", 0);
        if (latitude != 0) {
            titleBarMap.getRightLayout().setVisibility(View.GONE);
        } else {
            titleBarMap.getRightLayout().setVisibility(View.VISIBLE);
//            titleBarMap.getRightLayout().setClickable(false);
        }
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) titleBarMap.getLayoutParams();
        params.topMargin = (int) EaseCommonUtils.dip2px(this, 24);
        titleBarMap.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent));
        titleBarMap.getRightText().setTextColor(ContextCompat.getColor(this, R.color.white));
        titleBarMap.getRightText().setBackgroundResource(R.drawable.ease_title_bar_right_selector);
        int left = (int) EaseCommonUtils.dip2px(this, 10);
        int top = (int) EaseCommonUtils.dip2px(this, 5);
        titleBarMap.getRightText().setPadding(left, top, left, top);
        ViewGroup.LayoutParams layoutParams = titleBarMap.getRightLayout().getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ((ViewGroup.MarginLayoutParams) layoutParams).setMargins(0, 0, left, 0);
        }

        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);
        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    private AMapLocation MapLocation;
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
    //声明定位回调监听器

        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //可在其中解析amapLocation获取相应内容。
                    MapLocation = aMapLocation;

                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }
        }
    };


    private void initListener() {
        titleBarMap.setOnBackPressListener(this);
        titleBarMap.setOnRightClickListener(this);
    }

    private void initData() {
//		if(latitude == 0) {
//			mapView = new MapView(this, new BaiduMapOptions());
//			baiduMap.setMyLocationConfigeration(
//					new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null));
//			showMapWithLocationClient();
//		}else {
//			LatLng lng = new LatLng(latitude, longtitude);
//			mapView = new MapView(this,
//					new BaiduMapOptions().mapStatus(new MapStatus.Builder().target(lng).build()));
//			showMap(latitude, longtitude);
//		}
//		IntentFilter iFilter = new IntentFilter();
//		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
//		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
//		mBaiduReceiver = new BaiduSDKReceiver();
//		registerReceiver(mBaiduReceiver, iFilter);
    }

    protected void showMapWithLocationClient() {
//		mLocClient = new LocationClient(this);
//		mLocClient.registerLocationListener(new EaseBDLocationListener());
//		LocationClientOption option = new LocationClientOption();
//		// open gps
//		option.setOpenGps(true);
//		// option.setCoorType("bd09ll");
//		// Johnson change to use gcj02 coordination. chinese national standard
//		// so need to conver to bd09 everytime when draw on baidu map
//		option.setCoorType("gcj02");
//		option.setScanSpan(30000);
//		option.setAddrType("all");
//		option.setIgnoreKillProcess(true);
//		mLocClient.setLocOption(option);
//		if(!mLocClient.isStarted()) {
//			mLocClient.start();
//		}
    }

    protected void showMap(double latitude, double longtitude) {
//		LatLng lng = new LatLng(latitude, longtitude);
//		CoordinateConverter converter = new CoordinateConverter();
//		converter.coord(lng);
//		converter.from(CoordinateConverter.CoordType.COMMON);
//		LatLng convertLatLng = converter.convert();
//		OverlayOptions ooA = new MarkerOptions().position(convertLatLng).icon(BitmapDescriptorFactory
//				.fromResource(R.drawable.ease_icon_marka))
//				.zIndex(4).draggable(true);
//		baiduMap.addOverlay(ooA);
//		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(convertLatLng, 17.0f);
//		baiduMap.animateMapStatus(u);
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }

    @Override
    public void onRightClick(View view) {
        sendLocation();
    }

//	public void onReceiveBDLocation(BDLocation bdLocation) {
//		if(bdLocation == null) {
//			return;
//		}
//		if (lastLocation != null) {
//			if (lastLocation.getLatitude() == bdLocation.getLatitude() && lastLocation.getLongitude() == bdLocation.getLongitude()) {
//				Log.d("map", "same location, skip refresh");
//				// mMapView.refresh(); //need this refresh?
//				return;
//			}
//		}
//		titleBarMap.getRightLayout().setClickable(true);
//		lastLocation = bdLocation;
//		baiduMap.clear();
//		showMap(lastLocation.getLatitude(), lastLocation.getLongitude());
//	}

    private void sendLocation() {
        if (MapLocation != null) {
            Intent intent = getIntent();
            intent.putExtra("latitude", MapLocation.getLatitude());
            intent.putExtra("longitude", MapLocation.getLongitude());
            intent.putExtra("address", MapLocation.getAddress());
            this.setResult(RESULT_OK, intent);
            finish();
        }
    }



//	public class BaiduSDKReceiver extends BroadcastReceiver {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			String action = intent.getAction();
//			if(TextUtils.equals(action, SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
//				showErrorToast(getResources().getString(R.string.please_check));
//			}else if(TextUtils.equals(action, SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
//				showErrorToast(getResources().getString(R.string.Network_error));
//			}
//		}
//	}
//
//	public class EaseBDLocationListener implements BDLocationListener {
//
//		@Override
//		public void onReceiveLocation(BDLocation bdLocation) {
//			onReceiveBDLocation(bdLocation);
//		}
//	}

    /**
     * show error message
     *
     * @param message
     */
    protected void showErrorToast(String message) {
        Toast.makeText(MyEaseGaodeMapActivity.this, message, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
//        if(null != mlocationClient){
//            mlocationClient.onDestroy();
//        }
        mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onMyLocationChange(Location location) {
        //从location对象中获取经纬度信息，地址描述信息，建议拿到位置之后调用逆地理编码接口获取（获取地址描述数据章节有介绍）

//        Log.e("location", location.toString());
    }



}
