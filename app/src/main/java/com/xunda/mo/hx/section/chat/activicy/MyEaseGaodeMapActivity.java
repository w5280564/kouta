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
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
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
import com.xunda.mo.main.baseView.BasePopupWindow;
import com.xunda.mo.staticdata.gpslocation.GPS;
import com.xunda.mo.staticdata.gpslocation.GPSConverterUtils;
import com.xunda.mo.staticdata.viewTouchDelegate;

//LocationSource, AMapLocationListener
public class MyEaseGaodeMapActivity extends EaseBaseActivity implements EaseTitleBar.OnBackPressListener,
        EaseTitleBar.OnRightClickListener, AMap.OnMyLocationChangeListener {
    private EaseTitleBar titleBarMap;
    protected double latitude;
    protected double longtitude;
    protected String address;
    private MapView mMapView;
    AMap aMap;
    private TextView tv_location_name;

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
        tv_location_name = findViewById(R.id.tv_location_name);
        TextView tv_location = findViewById(R.id.tv_location);
        ImageView map_nav = findViewById(R.id.map_nav);
        map_nav.setOnClickListener(new map_navClick());
        viewTouchDelegate.expandViewTouchDelegate(map_nav, 50, 50, 50, 50);

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
        aMap.getUiSettings().setZoomControlsEnabled(false);//+ — 缩放按钮是否显示

        titleBarMap = findViewById(R.id.title_bar_map);
//		mapView = findViewById(R.id.bmapView);
        titleBarMap.setRightTitleResource(R.string.button_send);
        double latitude = getIntent().getDoubleExtra("latitude", 0);
        if (latitude != 0) {
            titleBarMap.getRightLayout().setVisibility(View.GONE);
            String Address = address;
            String locName = Address.substring(0, Address.indexOf(","));
            String location = Address.substring(Address.indexOf(",") + 1);
            tv_location_name.setText(locName);
            tv_location.setText(location);
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

    private class map_navClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            showMore(MyEaseGaodeMapActivity.this, mMapView);
        }
    }

    private void showMore(final Context mContext, final View view) {
        View contentView = View.inflate(mContext, R.layout.map_nav_popup, null);
        PopupWindow MorePopup = new BasePopupWindow(mContext);
        MorePopup.setWidth(RadioGroup.LayoutParams.MATCH_PARENT);
        MorePopup.setHeight(RadioGroup.LayoutParams.WRAP_CONTENT);
        MorePopup.setTouchable(true);
        MorePopup.setContentView(contentView);
        MorePopup.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        TextView gaode_txt = contentView.findViewById(R.id.gaode_txt);
        TextView baidu_txt = contentView.findViewById(R.id.baidu_txt);
        TextView cancel_txt = contentView.findViewById(R.id.cancel_txt);
        gaode_txt.setOnClickListener(v -> {
            gaodeMap();
            MorePopup.dismiss();
        });
        baidu_txt.setOnClickListener(v -> {
            baiduMap();
            MorePopup.dismiss();
        });
        cancel_txt.setOnClickListener(v -> {
            MorePopup.dismiss();
        });
    }


    //pixel3包名无法判断是否安装了高德
    private void gaodeMap() {
        Intent gdNav = new Intent();
        String pkg = "com.autonavi.minimap";
        String actView = Intent.ACTION_VIEW;
        String cat = Intent.CATEGORY_DEFAULT;
        String gbNavName = tv_location_name.getText().toString();// 终点的显示名称 必要参数
        gdNav.setAction(actView);
        gdNav.addCategory(cat);
        try {
            Uri gdUri = Uri.parse("amapuri://route/plan/?dlat=" + latitude + "&dlon=" + longtitude + "&dname=" + gbNavName + "&dev=0&t=0");
//            Uri gdUri = Uri.parse("dat=amapuri://route/plan/?sid=&slat=39.92848272&slon=116.39560823&sname=A&did=&dlat=" + latitude + "&dlon=" + longtitude + "&dname=" + gbNavName + "&dev=0&t=0");
            gdNav.setData(gdUri);
            startActivity(gdNav);
        } catch (Exception e) {
            e.printStackTrace();
            //网页版不支持路径规划
            Toast.makeText(this, "请安装高德地图", Toast.LENGTH_SHORT).show();
            Uri uri = Uri.parse("market://details?id=" + pkg);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

    }


    private void baiduMap() {
        Intent bdNav = new Intent();
        try {
           GPS gps =  GPSConverterUtils.gcj02_To_Bd09(latitude,longtitude);
            bdNav.setData(Uri.parse("baidumap://map/direction?destination=" + "latlng:"+ gps.getLat() + "," + gps.getLon() +"|addr:"+tv_location_name.getContext().toString() + "&coord_type=bd09ll&mode=driving&src=andr.baidu.openAPIdemo"));
            startActivity(bdNav);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "请安装百度地图", Toast.LENGTH_SHORT).show();
            Uri uri = Uri.parse("market://details?id=com.baidu.BaiduMap");
            bdNav = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(bdNav);
        }

    }


}
