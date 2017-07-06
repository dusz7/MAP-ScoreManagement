package com.hitiot.dusz7.naiverss.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.hitiot.dusz7.naiverss.R;
import com.hitiot.dusz7.naiverss.cache.CacheUtils;

public class SettingActivity extends AppCompatActivity {

    Button btClearCache;

    final String[] categorys = {"tech","news","life","amuse","others","all"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        btClearCache = (Button)findViewById(R.id.clear_cache_button);

        btClearCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (String cate : categorys) {
                    CacheUtils.clearFile(cate+"_cache.dat");
                }
            }
        });
    }
}
