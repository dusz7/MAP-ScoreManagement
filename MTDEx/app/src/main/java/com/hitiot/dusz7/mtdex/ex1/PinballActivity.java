package com.hitiot.dusz7.mtdex.ex1;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.hitiot.dusz7.mtdex.R;

public class PinballActivity extends AppCompatActivity {

    TextView tx;

    private static int duration = 3000;
    private static final int step = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinball);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        tx = (TextView)findViewById(R.id.pinball);

        final Animation animationFall = AnimationUtils.loadAnimation(this, R.anim.anim_pinball_fall);
        animationFall.setFillAfter(true);
        final Animation animationUp = AnimationUtils.loadAnimation(this, R.anim.anim_pinball_up);
        animationUp.setFillAfter(true);

        animationFall.setDuration(this.duration);
        animationFall.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                PinballActivity.duration -= PinballActivity.step;
                if(PinballActivity.duration <= PinballActivity.step) {
                    PinballActivity.duration = 3000;
                }
                animationUp.setDuration(PinballActivity.duration);
                animationUp.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        PinballActivity.duration -= PinballActivity.step;
                        if(PinballActivity.duration <= PinballActivity.step) {
                            PinballActivity.duration = 3000;
                        }
                        animationFall.setDuration(PinballActivity.duration);
                        tx.startAnimation(animationFall);
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                tx.startAnimation(animationUp);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        tx.startAnimation(animationFall);




    }
}
