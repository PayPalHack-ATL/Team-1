package io.synople.scanmoney;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        final ImageView logo = (ImageView)findViewById(R.id.logo);

        //Fade
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(logo, View.ALPHA, 0.3f, .9f);
        //Move back and forth
        ObjectAnimator alphaAnimator1 = ObjectAnimator.ofFloat(logo, View.TRANSLATION_X, -80, 80);

        alphaAnimator.setDuration(1300);
        alphaAnimator.setRepeatMode(ValueAnimator.REVERSE);
        alphaAnimator.setRepeatCount(ValueAnimator.INFINITE);
        alphaAnimator1.setDuration(1300);
        alphaAnimator1.setRepeatMode(ValueAnimator.REVERSE);
        alphaAnimator1.setRepeatCount(ValueAnimator.INFINITE);
        alphaAnimator.start();
        alphaAnimator1.start();


        Thread wait = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(2700);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        wait.start();
    }

}
