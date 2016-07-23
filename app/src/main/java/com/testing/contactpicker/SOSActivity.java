package com.testing.contactpicker;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;

public class SOSActivity extends AppCompatActivity {

    LinearLayout red, blue;
    MediaPlayer mp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);
        mp = MediaPlayer.create(this,R.raw.siren);
        mp.setLooping(true);
        mp.start();
        red = (LinearLayout)findViewById(R.id.redLayout);
        blue = (LinearLayout)findViewById(R.id.blueLayout);

        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.screenBrightness = 1F;
        getWindow().setAttributes(layout);

        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(50);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        red.startAnimation(anim);

        Animation pito = new AlphaAnimation(0.0f, 1.0f);
        pito.setDuration(50);
        pito.setStartOffset(40);
        pito.setRepeatMode(Animation.REVERSE);
        pito.setRepeatCount(Animation.INFINITE);
        blue.startAnimation(pito);

    }
}
