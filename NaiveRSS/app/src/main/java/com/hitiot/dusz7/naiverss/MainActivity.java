package com.hitiot.dusz7.naiverss;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import com.hitiot.dusz7.naiverss.activity.FavouriteActivity;
import com.hitiot.dusz7.naiverss.activity.RssShowActivity;
import com.hitiot.dusz7.naiverss.activity.RssSourceActivity;
import com.hitiot.dusz7.naiverss.activity.SettingActivity;
import com.hitiot.dusz7.naiverss.database.FeedDao;
import com.idescout.sql.SqlScoutServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fabAdd;
    CoordinatorLayout container;

    // 与主Activity中的GridView相关
    private static GridView gridView;
    private static SimpleAdapter adapter;
    // 把icons和texts关联起来
    private static List<Map<String, Object>> data_list;
    private static int[] icons = {R.drawable.inspect_icon, R.drawable.source_icon, R.drawable.favourite_icon, R.drawable.setting_icon};
    private static int[] texts = {R.string.inspect_content,R.string.rss_source,R.string.favorite_manager,R.string.app_setting};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        container = (CoordinatorLayout)findViewById(R.id.container);

        SqlScoutServer.create(this, getPackageName());

        FeedDao feedDao = new FeedDao(this);
        feedDao.initFeedTabel();

        // 初始化data_list
        getData();
        gridView = (GridView)findViewById(R.id.grid_main);
        // 加载适配器，绑定cell_item上的image和text
        String[] form = {"image", "texts"};
        int[] to = {R.id.image_button, R.id.text_button};
        adapter = new SimpleAdapter(this, data_list, R.layout.cell_item, form, to);
        // 绑定适配器
        gridView.setAdapter(adapter);
        // item监听事件，通过被点击item的index判断来做不同的事情
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        startActivity(new Intent(MainActivity.this,RssShowActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(MainActivity.this,RssSourceActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(MainActivity.this,FavouriteActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(MainActivity.this, SettingActivity.class));
                        break;
                }
            }
        });

        fabAdd = (FloatingActionButton)findViewById(R.id.fab_add_source);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showAddSourceDialog();
            }
        });
    }

    String sourceCategory;

    private void showAddSourceDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_source, (ViewGroup) findViewById(R.id.dialog_add_source));
        final EditText etSourceUrl = (EditText)layout.findViewById(R.id.edit_source_url);
        final EditText etSourceLabel = (EditText)layout.findViewById(R.id.edit_source_label);

        final Spinner spinnerSourceCategory = (Spinner) layout.findViewById(R.id.spinner_source_category);

        spinnerSourceCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sourceCategory = parent.getItemAtPosition(position).toString();
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("添加订阅源")
                .setView(layout)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String sourceUrl = (etSourceUrl.getText().toString());
                        String sourceLabel = (etSourceLabel.getText().toString());

                        if(!"".equals(sourceUrl) && sourceUrl!=null) {

                            FeedDao feedDao = new FeedDao(MainActivity.this);
                            feedDao.insertOne(sourceUrl,sourceCategory,sourceLabel);
                            Snackbar.make(container, "添加成功", Snackbar.LENGTH_LONG).show();
                        }

                    }
                })
                .setNegativeButton("取消",null)
                .create()
                .show();
    }


    /**
     * 准备GridView的数据源
     */
    public void getData() {

        data_list = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < icons.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", icons[i]);
            map.put("texts", getResources().getString(texts[i]));
            data_list.add(map);
        }
    }
}
