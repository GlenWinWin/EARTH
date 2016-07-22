package com.testing.contactpicker.lockscreen;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.testing.contactpicker.R;

public class LockScreenActivity extends AppCompatActivity {

    private final String TAG = "LockScreenActivity";
    private static Context sLockScreenActivityContext = null;;
    private RelativeLayout mLockScreenMainLayout = null;

    public static SendMessageHandler mMainHandler = null;

    public PhoneStateListener phoneStateListener = new PhoneStateListener() {
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    break;
                default:
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        sLockScreenActivityContext = this;
        mMainHandler = new SendMessageHandler();
//        getWindow().setType(2004);
//        getWindow().addFlags(524288);
//        getWindow().addFlags(4194304);
        ///
        getWindow().setType(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        manager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        initLockScreenUi();

        setLockGuard();

    }

    private class SendMessageHandler extends android.os.Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            finishLockscreenAct();
        }
    }

    private void finishLockscreenAct() {
        finish();
    }

    private void initLockScreenUi() {
        setContentView(R.layout.activity_lock_screen);
        mLockScreenMainLayout = (RelativeLayout) findViewById(R.id.lockscreen_main_layout);
        mLockScreenMainLayout.getBackground().setAlpha(15);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    private void setLockGuard() {
        boolean isLockEnable = false;
        if (!LockScreenUtil.getInstance(sLockScreenActivityContext).isStandardKeyguardState()) {
            isLockEnable = false;
        } else {
            isLockEnable = true;
        }

        Intent startLockScreenIntent = new Intent(this, LockScreenViewService.class);
        startService(startLockScreenIntent);

        boolean isSoftKeyEnable = LockScreenUtil.getInstance(sLockScreenActivityContext).isSoftKeyAvail(this);
        SharedPreferencesUtil.setBoolean(LockScreen.ISSOFTKEY, isSoftKeyEnable);
        if (!isSoftKeyEnable) {
            mMainHandler.sendEmptyMessage(0);
        } else if (isSoftKeyEnable) {
            if (isLockEnable) {
                mMainHandler.sendEmptyMessage(0);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

}
