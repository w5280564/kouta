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
import androidx.fragment.app.Fragment;
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

    public static void actionStartForResult(Fragment fragment, int requestCode) {
        Intent intent = new Intent(fragment.getContext(), GroupDetail_Edit_Address.class);
        fragment.startActivityForResult(intent, requestCode);
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
        //????????????????????????
        mMapView = (MapView) findViewById(R.id.bmapView);
        //???activity??????onCreate?????????mMapView.onCreate(savedInstanceState)???????????????
        mMapView.onCreate(savedInstanceState);
        //??????????????????????????????
        if (aMap == null) {
            aMap = mMapView.getMap();
        }

        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//??????????????????????????????
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);//??????????????????????????????????????????????????????
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????1???1???????????????????????????myLocationType????????????????????????????????????
        myLocationStyle.interval(2000); //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        aMap.setMyLocationStyle(myLocationStyle);//?????????????????????Style
        //aMap.getUiSettings().setMyLocationButtonEnabled(true);?????????????????????????????????????????????????????????
        aMap.setMyLocationEnabled(true);// ?????????true?????????????????????????????????false??????????????????????????????????????????????????????false???
        aMap.setOnMyLocationChangeListener(this);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(17));//????????????????????????????????? 17 ????????? 3 ??? 19???????????????????????????????????????????????????

        EaseTitleBar titleBarMap = findViewById(R.id.title_bar_map);
        titleBarMap.setLeftLayoutVisibility(View.VISIBLE);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) titleBarMap.getLayoutParams();
        params.topMargin = (int) EaseCommonUtils.dip2px(this, 24);
        titleBarMap.setOnBackPressListener(this);

        //???????????????
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //????????????????????????
        mLocationClient.setLocationListener(mLocationListener);
        //?????????AMapLocationClientOption??????
        mLocationOption = new AMapLocationClientOption();
        //?????????????????????AMapLocationMode.Hight_Accuracy?????????????????????
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //???????????????????????????
        //??????????????????false???
        mLocationOption.setOnceLocation(true);
        //????????????3s???????????????????????????????????????
        //??????setOnceLocationLatest(boolean b)?????????true??????????????????SDK???????????????3s?????????????????????????????????????????????????????????true???setOnceLocation(boolean b)????????????????????????true???????????????????????????false???
        mLocationOption.setOnceLocationLatest(true);
        //??????????????????????????????????????????
        mLocationClient.setLocationOption(mLocationOption);
        //????????????
        mLocationClient.startLocation();
    }

    //??????AMapLocationClientOption??????
    public AMapLocationClientOption mLocationOption = null;
    //??????AMapLocationClient?????????
    public AMapLocationClient mLocationClient = null;
    private AMapLocation MapLocation;
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        //???????????????????????????
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //??????????????????amapLocation?????????????????????
                    MapLocation = aMapLocation;

                } else {
                    //???????????????????????????ErrCode????????????????????????????????????????????????errInfo???????????????????????????????????????
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
        //???activity??????onDestroy?????????mMapView.onDestroy()???????????????
        mMapView.onDestroy();
//        if(null != mlocationClient){
//            mlocationClient.onDestroy();
//        }
        mLocationClient.onDestroy();//?????????????????????????????????????????????????????????
    }

    @Override
    protected void onResume() {
        super.onResume();
        //???activity??????onResume?????????mMapView.onResume ()???????????????????????????
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //???activity??????onPause?????????mMapView.onPause ()????????????????????????
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //???activity??????onSaveInstanceState?????????mMapView.onSaveInstanceState (outState)??????????????????????????????
        mMapView.onSaveInstanceState(outState);
    }


    @Override
    public void onMyLocationChange(Location location) {
        //???location????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        lp = new LatLonPoint(location.getLatitude(), location.getLongitude());// 116.472995,39.993743
        doSearchQuery();
//        Log.e("location", location.toString());

    }

    private class SeekOnEditorListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {//?????????????????????
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
     * ????????????poi??????
     */
    private PoiSearch.Query query;// Poi???????????????

    protected void doSearchQuery() {

        keyWord = seek_edit.getText().toString().trim();
//        currentPage = 0;
//        query = new PoiSearch.Query(keyWord, "", "");// ????????????????????????????????????????????????????????????poi????????????????????????????????????poi??????????????????????????????????????????
        query = new PoiSearch.Query(keyWord, "", "");// ????????????????????????????????????????????????????????????poi????????????????????????????????????poi??????????????????????????????????????????
        query.setPageSize(20);// ?????????????????????????????????poiitem
//        query.setPageNum(currentPage);// ??????????????????

        if (lp != null) {
            poiSearch = new PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.setBound(new PoiSearch.SearchBound(lp, 5000, true));//
            // ????????????????????????lp????????????????????????5000?????????
            poiSearch.searchPOIAsyn();// ????????????
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
        //????????????????????????item????????????????????????????????????????????????????????????
        adress_Recycler.setHasFixedSize(true);
        mAdapter = new GroupDetail_Edit_Adress_Adapter(context, poiResult);
        adress_Recycler.setAdapter(mAdapter);

        mAdapter.setOnSendClickLitener(new GroupDetail_Edit_Adress_Adapter.OnSendClickLitener() {
            @Override
            public void onClick(View v, int pos) {
                sendLocation(poiResult, pos);
            }
        });
    }

    @Override
    public void onBackPress(View view) {
        finish();
    }

    private void sendLocation(PoiResult poiResult, int pos) {
        if (poiResult != null) {
            PoiItem poiItem = poiResult.getPois().get(pos);
            String locName = poiItem.toString();
            String location = poiItem.getCityName() + poiItem.getAdName() + poiItem.getSnippet();
            double latitude = poiItem.getLatLonPoint().getLatitude();
            double longitude = poiItem.getLatLonPoint().getLongitude();
            String Address = locName + "," + location;
            Intent intent = getIntent();
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            intent.putExtra("address", Address);
//            intent.putExtra("address", MapLocation.getAddress());

            this.setResult(RESULT_OK, intent);
            finish();
        }
    }
}