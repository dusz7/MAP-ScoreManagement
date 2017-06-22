package com.hitiot.dusz7.mtdex.ex2;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.renderscript.Double2;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.hitiot.dusz7.mtdex.R;

import java.util.ArrayList;

public class LocationActivity extends AppCompatActivity implements PoiSearch.OnPoiSearchListener{

    MapView mapView = null;
    AMap aMap = null;

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();

    MyLocationStyle myLocationStyle; // 蓝点

    private String myLocation;

    private final int ZOOM_SIZE = 16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);


        //获取地图控件引用
        mapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mapView.onCreate(savedInstanceState);


        if (aMap == null) {
            aMap = mapView.getMap();
        }

        // 缩放地图展示视图
        aMap.moveCamera(CameraUpdateFactory.zoomTo(ZOOM_SIZE));

        initLocation();
        startLocation();

        // 初始定位，以及展示定位蓝点，移动视角等
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE) ;//定位一次，且将视角移动到地图中心点。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_location, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_loc_present:
                aMap.setMyLocationStyle(myLocationStyle);
                return true;
            case R.id.action_loc_coordinate:
                showCoordinateDialog();
                return true;
            case R.id.action_loc_search:
                showSearchDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showCoordinateDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_coordinate, (ViewGroup) findViewById(R.id.dialog_coordinate));
        final EditText etlo = (EditText)layout.findViewById(R.id.edit_longitude);
        etlo.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
        final EditText etla = (EditText)layout.findViewById(R.id.edit_latitude);
        etla.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);

        AlertDialog.Builder builder = new AlertDialog.Builder(LocationActivity.this);
        builder.setTitle("输入查询地点经纬坐标")
                .setView(layout)
                .setPositiveButton("定位", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        double mlongitude = 0.0;
                        double mlatitude = 0.0;

                        mlongitude = Double.valueOf(etlo.getText().toString());
                        mlatitude = Double.valueOf(etla.getText().toString());
                        if(mlatitude != 0.0 && mlongitude != 0.0) {
                            locationWithCoordinate(mlatitude,mlongitude);
                        }
                        Log.d("lola",""+mlongitude+","+mlatitude);
                    }
                })
                .setNegativeButton("取消",null)
                .create()
                .show();
    }

    private void showSearchDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_search, (ViewGroup) findViewById(R.id.dialog_search));
        final EditText etsn = (EditText)layout.findViewById(R.id.edit_search_name);

        AlertDialog.Builder builder = new AlertDialog.Builder(LocationActivity.this);
        builder.setTitle("输入查询地点")
                .setView(layout)
                .setPositiveButton("定位", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String address = "";

                        address = (etsn.getText().toString());

                        if(address != "") {
                            locationWithAddress(address);
                        }
                    }
                })
                .setNegativeButton("取消",null)
                .create()
                .show();
    }

    private void locationWithCoordinate(double latitude, double longitude) {
        LatLng latLng = new LatLng(latitude,longitude);
        final Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).title("搜索点").snippet(latLng.toString()));

        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,ZOOM_SIZE));

    }

    private void locationWithAddress(String address) {
        PoiSearch.Query query = new PoiSearch.Query(address, "", null);
        //keyWord表示搜索字符串，
        //第二个参数表示POI搜索类型，二者选填其一，选用POI搜索类型时建议填写类型代码
        //cityCode表示POI搜索区域，可以是城市编码也可以是城市名称，也可以传空字符串，空字符串代表全国在全国范围内进行搜索
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
//        query.setPageNum(currentPage);//设置查询页码

        PoiSearch poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        //解析result获取POI信息
        Log.d("poi",result.getQuery().toString());

        ArrayList<PoiItem> items = result.getPois();
        PoiItem item = items.get(0);
//        Log.d("poi",item.getLatLonPoint().getLongitude()+","+item.getLatLonPoint().getLatitude());
        locationWithCoordinate(item.getLatLonPoint().getLatitude(),item.getLatLonPoint().getLongitude());

    }
    public void onPoiItemSearched(PoiItem item, int code) {
        Log.d("poi","poiItem");
    }


    private AMapLocationClientOption getDefaultOption(){
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是ture
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        return mOption;
    }

    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation loc) {
            if (null != loc) {
                //解析逆地理编码，等到当前定位点的经纬度坐标
                String result = Utils.getMyLocation(loc);
                Log.i("location_icon",result);
                myLocation = result;
            } else {
            }
        }
    };

    /**
     * 初始化定位相关
     */
    private void initLocation(){
        //初始化client
        locationClient = new AMapLocationClient(this.getApplicationContext());
        //设置定位参数
        locationClient.setLocationOption(getDefaultOption());
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
    }

    /**
     * 开始定位
     */
    private void startLocation(){
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState);
    }

}
