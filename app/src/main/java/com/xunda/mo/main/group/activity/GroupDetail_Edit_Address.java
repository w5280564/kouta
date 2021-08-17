package com.xunda.mo.main.group.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.hyphenate.easeui.ui.base.EaseBaseActivity;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.xunda.mo.R;
import com.xunda.mo.main.group.adapter.GroupDetail_Edit_Adress_Adapter;

public class GroupDetail_Edit_Address extends EaseBaseActivity implements AMap.OnMyLocationChangeListener, PoiSearch.OnPoiSearchListener, EaseTitleBar.OnBackPressListener {

    protected double latitude;
    protected double longtitude;
    protected String address;
    private MapView mMapView;
    AMap aMap;
    private PoiSearch poiSearch;
    private RecyclerView adress_Recycler;
    private EditText seek_edit;
    private String keyWord = "";
    private LatLonPoint lp;

    public static void actionStartForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, GroupDetail_Edit_Address.class);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, GroupDetail_Edit_Address.class);
        context.startActivity(intent);
    }

    public static void actionStart(Context context, double latitude, double longtitude, String address) {
        Intent intent = new Intent(context, GroupDetail_Edit_Address.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longtitude", longtitude);
        intent.putExtra("address", address);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupdetail_edit_adress);

        setFitSystemForTheme(false, R.color.transparent, true);
        initIntent();
        initView(savedInstanceState);

    }

//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_groupdetail_edit_adress;
//    }

    private void initIntent() {
        latitude = getIntent().getDoubleExtra("latitude", 0);
        longtitude = getIntent().getDoubleExtra("longtitude", 0);
        address = getIntent().getStringExtra("address");
    }

    private void initView(Bundle savedInstanceState) {
        seek_edit = findViewById(R.id.seek_edit);
        seek_edit.setOnEditorActionListener(new SeekOnEditorListener());
        adress_Recycler = findViewById(R.id.adress_Recycler);
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

        EaseTitleBar titleBarMap = findViewById(R.id.title_bar_map);
        titleBarMap.setLeftLayoutVisibility(View.VISIBLE);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) titleBarMap.getLayoutParams();
        params.topMargin = (int) EaseCommonUtils.dip2px(this, 24);
        titleBarMap.setOnBackPressListener(this);

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
        lp = new LatLonPoint(location.getLatitude(), location.getLongitude());// 116.472995,39.993743
        doSearchQuery();
//        Log.e("location", location.toString());

    }

    private class SeekOnEditorListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {//打开关闭软键盘
                imm.hideSoftInputFromWindow(seek_edit.getWindowToken(), 0);
            }
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                searchData();
                keyWord = seek_edit.getText().toString().trim();
                doSearchQuery();
            }
            return false;
        }
    }

    /**
     * 开始进行poi搜索
     */
    private PoiSearch.Query query;// Poi查询条件类

    protected void doSearchQuery() {

        keyWord = seek_edit.getText().toString().trim();
//        currentPage = 0;
//        query = new PoiSearch.Query(keyWord, "", "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query = new PoiSearch.Query(keyWord, "", "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
//        query.setPageNum(currentPage);// 设置查第一页

        if (lp != null) {
            poiSearch = new PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.setBound(new PoiSearch.SearchBound(lp, 5000, true));//
            // 设置搜索区域为以lp点为圆心，其周围5000米范围
            poiSearch.searchPOIAsyn();// 异步搜索
        }
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
//        poiResult.getPois().get(0).getSnippet();

        initlist(GroupDetail_Edit_Address.this, poiResult);
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    GroupDetail_Edit_Adress_Adapter mAdapter;

    public void initlist(final Context context, PoiResult poiResult) {
        LinearLayoutManager mMangaer = new LinearLayoutManager(context);
        adress_Recycler.setLayoutManager(mMangaer);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        adress_Recycler.setHasFixedSize(true);
        mAdapter = new GroupDetail_Edit_Adress_Adapter(context, poiResult);
        adress_Recycler.setAdapter(mAdapter);

        mAdapter.setOnSendClickLitener(new GroupDetail_Edit_Adress_Adapter.OnSendClickLitener() {
            @Override
            public void onClick(View v, int pos) {
                sendLocation(poiResult,pos);
            }
        });
    }

    @Override
    public void onBackPress(View view) {
        finish();
    }

    private void sendLocation(PoiResult poiResult,int pos) {
        if (poiResult != null) {
            double latitude = poiResult.getPois().get(pos).getLatLonPoint().getLatitude();
            double longitude = poiResult.getPois().get(pos).getLatLonPoint().getLongitude();
            String Address = poiResult.getPois().get(pos).toString();
            Intent intent = getIntent();
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            intent.putExtra("address", Address);
            this.setResult(RESULT_OK, intent);
            finish();
        }
    }
}