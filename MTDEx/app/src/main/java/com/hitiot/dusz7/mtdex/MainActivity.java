package com.hitiot.dusz7.mtdex;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.hitiot.dusz7.mtdex.ex1.CalculatorActivity;
import com.hitiot.dusz7.mtdex.ex1.PinballActivity;
import com.hitiot.dusz7.mtdex.ex2.LocationActivity;
import com.hitiot.dusz7.mtdex.ex3.ChatRoomActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-calculate");
    }

    private final int INTERNET_REQUEST_CODE = 0;
    private final int ACCESS_NETWORK_STATE_REQUEST_CODE = 1;
    private final int ACCESS_WIFI_STATE_REQUEST_CODE = 2;
    private final int CHANGE_WIFI_STATE_REQUEST_CODE = 3;
    private final int WRITE_COARSE_LOCATION_REQUEST_CODE = 4;
    private final int WRITE_FINE_LOCATION_REQUEST_CODE = 5;


    private GridView gridView;
    private List<Map<String, Object>> data_list;
    private SimpleAdapter adapter;

    private int[] icons = {R.drawable.calculator_icon,R.drawable.pinball_icon,
    R.drawable.map_icon,R.drawable.chat_icon};

    private int[] text = {R.string.ex1_1,R.string.ex1_2,R.string.ex2,R.string.ex3};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET},
                    INTERNET_REQUEST_CODE);//自定义的code
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                    ACCESS_NETWORK_STATE_REQUEST_CODE);//自定义的code
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_WIFI_STATE},
                    ACCESS_WIFI_STATE_REQUEST_CODE);//自定义的code
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CHANGE_WIFI_STATE},
                    CHANGE_WIFI_STATE_REQUEST_CODE);//自定义的code
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    WRITE_COARSE_LOCATION_REQUEST_CODE);//自定义的code
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    WRITE_FINE_LOCATION_REQUEST_CODE);//自定义的code
        }


        gridView = (GridView)findViewById(R.id.grid_main);
        data_list = new ArrayList<Map<String, Object>>();
        getData();
        //加载适配器
        String[] form = {"image", "text"};
        int[] to = {R.id.image_button, R.id.text_button};
        adapter = new SimpleAdapter(this, data_list, R.layout.cell_item, form, to);
        gridView.setAdapter(adapter);
        //监听item每一项
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        startActivity(new Intent(MainActivity.this,CalculatorActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(MainActivity.this,PinballActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(MainActivity.this,LocationActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(MainActivity.this, ChatRoomActivity.class));
                        break;
                }
            }
        });



    }

    //准备数据源
    public void getData() {

        for (int i = 0; i < icons.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", icons[i]);
            map.put("text", getResources().getString(text[i]));
            data_list.add(map);
        }
    }


    /**
     * 实现只有应用第一次启动的时候有启动页面
     */
    @Override
    public void onBackPressed() {
        // super.onBackPressed(); 	不要调用父类的方法
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }


    /**
     * A native method that is implemented by the 'native-calculate' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();



}
