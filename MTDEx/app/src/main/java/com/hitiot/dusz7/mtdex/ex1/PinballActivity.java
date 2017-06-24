package com.hitiot.dusz7.mtdex.ex1;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.hitiot.dusz7.mtdex.R;

public class PinballActivity extends AppCompatActivity {

    // 小球
    TextView pinBallAsATextView;

    private static final int INIT_DURATION = 3000;
    private static int duration = INIT_DURATION;
    private static final int STEP = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinball);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        // 这个TextView的样式是自定义的，一个红色实心小球
        pinBallAsATextView = (TextView)findViewById(R.id.pinball);

        // 添加动画
        final Animation animationFall = AnimationUtils.loadAnimation(this, R.anim.anim_pinball_fall);
        animationFall.setFillAfter(true);
        final Animation animationUp = AnimationUtils.loadAnimation(this, R.anim.anim_pinball_up);
        animationUp.setFillAfter(true);

        // 总的思想就是在下降动画结束的监听函数里启动上升动画的，在上升动画的回调函数里启动下降的，循环
        animationFall.setDuration(this.duration);
        animationFall.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                PinballActivity.duration -= PinballActivity.STEP;
                if(PinballActivity.duration <= PinballActivity.STEP) {
                    PinballActivity.duration = PinballActivity.INIT_DURATION;
                }
                animationUp.setDuration(PinballActivity.duration);
                animationUp.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        PinballActivity.duration -= PinballActivity.STEP;
                        if(PinballActivity.duration <= PinballActivity.STEP) {
                            PinballActivity.duration = PinballActivity.INIT_DURATION;
                        }
                        animationFall.setDuration(PinballActivity.duration);
                        pinBallAsATextView.startAnimation(animationFall);
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                pinBallAsATextView.startAnimation(animationUp);
            }

        });

        pinBallAsATextView.startAnimation(animationFall);


    }
}
