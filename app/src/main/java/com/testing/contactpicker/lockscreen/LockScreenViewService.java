package com.testing.contactpicker.lockscreen;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.testing.contactpicker.Modules.GPSTracker;
import com.testing.contactpicker.Preferences;
import com.testing.contactpicker.R;
import com.testing.contactpicker.SOSActivity;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class LockScreenViewService extends Service  implements LocationListener{
    private final int LOCK_OPEN_OFFSET_VALUE = 50;
    private Context mContext = null;
    private LayoutInflater mInflater = null;
    private View mLockScreenView = null;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;
    private RelativeLayout mBackgroundLayout = null;
    private RelativeLayout mBackgroundInLayout = null;
    private ImageView mBackgroundLockImageView = null;
    private RelativeLayout mForegroundLayout = null;
    private RelativeLayout mStatusBackgroundDummyView = null;
    private RelativeLayout mStatusForgroundDummyView = null;
    private boolean mIsLockEnable = false;
    private boolean mIsSoftkeyEnable = false;
    private int mDeviceWidth = 0;
    private int mDeviceDeviceWidth = 0;
    private float mLastLayoutX = 0;
    private int mServiceStartId = 0;
    private SendMessageHandler mMainHandler = null;
//    private boolean sIsSoftKeyEnable = false;
    GPSTracker gps;
    StringBuilder strAddress = new StringBuilder();
    List<Address> addresses;
    private class SendMessageHandler extends android.os.Handler{

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            changeBackGroundLockView(mLastLayoutX);
        }
    }
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate - LockScreenViewService");
        super.onCreate();
        mContext = this;
        SharedPreferencesUtil.init(mContext);
//        sIsSoftKeyEnable = SharedPreferencesUtil.get(LockScreen.ISSOFTKEY);

        // listen the events get fired during the call
        StateListener phoneStateListener = new StateListener();
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);
    }

    // Handle events of calls and unlock screen if necessary
    private class StateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            Log.d(TAG, "callState: " + state);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    dettachLockScreenView();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mMainHandler = new SendMessageHandler();
        if (isLockScreenAble()) {
            if (null != mWindowManager) {
                if (null != mLockScreenView) {
                    mWindowManager.removeView(mLockScreenView);
                }
                mWindowManager = null;
                mParams = null;
                mInflater = null;
                mLockScreenView = null;
            }
            initState();
            initView();
            attachLockScreenView();
        }
        return LockScreenViewService.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        dettachLockScreenView();
    }

    private void initState() {

        mIsLockEnable = LockScreenUtil.getInstance(mContext).isStandardKeyguardState();
        if (mIsLockEnable) {
            mParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        } else {
            mParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                            | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD,
                    PixelFormat.TRANSLUCENT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mIsLockEnable && mIsSoftkeyEnable) {
                mParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            } else {
                mParams.flags = WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
            }
        } else {
            mParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        }

        if (null == mWindowManager) {
            mWindowManager = ((WindowManager) mContext.getSystemService(WINDOW_SERVICE));
        }
    }

    private void initView() {
        if (null == mInflater) {
            mInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (null == mLockScreenView) {
            mLockScreenView = mInflater.inflate(R.layout.content_lock_screen, null);
        }

    }

    private boolean isLockScreenAble() {
        boolean isLock = SharedPreferencesUtil.get(LockScreen.ISLOCK);
        if (isLock) {
            isLock = true;
        } else {
            isLock = false;
        }
        return isLock;
    }

    private void attachLockScreenView() {

        if (null != mWindowManager && null != mLockScreenView && null != mParams) {
            mLockScreenView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });
            mWindowManager.addView(mLockScreenView, mParams);
            settingLockView();
        }
    }

    private boolean dettachLockScreenView() {
        if (null != mWindowManager && null != mLockScreenView) {
            mWindowManager.removeView(mLockScreenView);
            mLockScreenView = null;
            mWindowManager = null;
            stopSelf(mServiceStartId);
            return true;
        } else {
            return false;
        }
    }

    private void settingLockView() {
        mBackgroundLayout = (RelativeLayout) mLockScreenView.findViewById(R.id.lockscreen_background_layout);
        mBackgroundInLayout = (RelativeLayout) mLockScreenView.findViewById(R.id.lockscreen_background_in_layout);
        mBackgroundLockImageView = (ImageView) mLockScreenView.findViewById(R.id.lockscreen_background_image);
        mForegroundLayout = (RelativeLayout) mLockScreenView.findViewById(R.id.lockscreen_forground_layout);
        mForegroundLayout.setOnTouchListener(mViewTouchListener);

        mStatusBackgroundDummyView = (RelativeLayout) mLockScreenView.findViewById(R.id.lockscreen_background_status_dummy);
        mStatusForgroundDummyView = (RelativeLayout) mLockScreenView.findViewById(R.id.lockscreen_forground_status_dummy);
        setBackGroundLockView();

        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        mDeviceWidth = displayMetrics.widthPixels;
        mDeviceDeviceWidth = (mDeviceWidth / 2);
        mBackgroundLockImageView.setX((int) (((mDeviceDeviceWidth) * -1)));

        //kitkat
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int val = LockScreenUtil.getInstance(mContext).getStatusBarHeight();
            RelativeLayout.LayoutParams forgroundParam = (RelativeLayout.LayoutParams) mStatusForgroundDummyView.getLayoutParams();
            forgroundParam.height = val;
            mStatusForgroundDummyView.setLayoutParams(forgroundParam);
            AlphaAnimation alpha = new AlphaAnimation(0.5F, 0.5F);
            alpha.setDuration(0); // Make animation instant
            alpha.setFillAfter(true); // Tell it to persist after the animation ends
            mStatusForgroundDummyView.startAnimation(alpha);
            RelativeLayout.LayoutParams backgroundParam = (RelativeLayout.LayoutParams) mStatusBackgroundDummyView.getLayoutParams();
            backgroundParam.height = val;
            mStatusBackgroundDummyView.setLayoutParams(backgroundParam);
        }



        TextView Date = (TextView) mLockScreenView.findViewById(R.id.date);
        final Calendar cal = Calendar.getInstance();
        int dd = cal.get(Calendar.DAY_OF_MONTH);
        int mm = cal.get(Calendar.MONTH);
        int yy = cal.get(Calendar.YEAR);
        String month_name= theMonth(mm);
        Date.setText(new StringBuilder()
                .append(month_name).append(" ").append(dd).append(", ").append(yy));
        Button sos = (Button)mLockScreenView.findViewById(R.id.sos);
        sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBackgroundLayout.setVisibility(View.INVISIBLE);
                mForegroundLayout.setVisibility(View.INVISIBLE);
                Intent sosIntent = new Intent(mContext,SOSActivity.class);
                sosIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(sosIntent);
            }
        });
        Button safe = (Button)mLockScreenView.findViewById(R.id.safe);
        Button danger = (Button)mLockScreenView.findViewById(R.id.danger);

        safe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SmsManager smsManager = SmsManager.getDefault();
                gps = new GPSTracker(mContext);

                final double latitude = gps.getLatitude();
                final double longitude = gps.getLongitude();

                Geocoder geocoder= new Geocoder(mContext, Locale.ENGLISH);
                try {  //Place your latitude and longitude
                    addresses = geocoder.getFromLocation(latitude,longitude, 1);

                    if(addresses != null) {
                        Address fetchedAddress = addresses.get(0);

                        for(int i=0; i<fetchedAddress.getMaxAddressLineIndex(); i++) {
                            strAddress.append(fetchedAddress.getAddressLine(i)).append("\n");
                        }
                    }  else Toast.makeText(mContext,"gggg", Toast.LENGTH_SHORT).show();
                }
                catch (IOException e) { //
                    e.printStackTrace();
                }
                if(!Preferences.getPrimaryContactNumber(mContext).isEmpty()){
                    smsManager.sendTextMessage(Preferences.getPrimaryContactNumber(mContext), null, "I am in danger at " + strAddress.toString(), null, null);
                }
                if(!Preferences.getSecondaryContactNumber(mContext).isEmpty()){
                    smsManager.sendTextMessage(Preferences.getSecondaryContactNumber(mContext), null, "I am in danger at " + strAddress.toString(), null, null);
                }
                Toast.makeText(mContext,"Successfully Sent",Toast.LENGTH_SHORT).show();
            }
        });
        danger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SmsManager smsManager = SmsManager.getDefault();
                gps = new GPSTracker(mContext);

                final double latitude = gps.getLatitude();
                final double longitude = gps.getLongitude();

                Geocoder geocoder= new Geocoder(mContext, Locale.ENGLISH);
                try {  //Place your latitude and longitude
                    addresses = geocoder.getFromLocation(latitude,longitude, 1);

                    if(addresses != null) {
                        Address fetchedAddress = addresses.get(0);

                        for(int i=0; i<fetchedAddress.getMaxAddressLineIndex(); i++) {
                            strAddress.append(fetchedAddress.getAddressLine(i)).append("\n");
                        }
                    }  else Toast.makeText(mContext,"gggg", Toast.LENGTH_SHORT).show();

                }
                catch (IOException e) { //
                    e.printStackTrace();
                }
                if(!Preferences.getPrimaryContactNumber(mContext).isEmpty()){
                    smsManager.sendTextMessage(Preferences.getPrimaryContactNumber(mContext), null, "I am safe. Do not worry about me. I am at " + strAddress.toString(), null, null);
                }
                if(!Preferences.getSecondaryContactNumber(mContext).isEmpty()){
                    smsManager.sendTextMessage(Preferences.getSecondaryContactNumber(mContext), null, "I am safe. Do not worry about me. I am at " + strAddress.toString(), null, null);
                }
                Toast.makeText(mContext,"Successfully Sent",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static String theMonth(int month){
        String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        return monthNames[month];
    }

    private static final String TAG = "LockScreenViewService";

    private class EndCallListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (TelephonyManager.CALL_STATE_RINGING == state) {
                Log.i(TAG, "RINGING, number: " + incomingNumber);
            }
            if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
                //wait for phone to go offhook (probably set a boolean flag) so you know your app initiated the call.
                Log.i(TAG, "OFFHOOK");
            }
            if (TelephonyManager.CALL_STATE_IDLE == state) {
                //when this state occurs, and your flag is set, restart your app
                Log.i(TAG, "IDLE");
            }
        }
    }

    private void setBackGroundLockView() {
        if (mIsLockEnable) {
            mBackgroundInLayout.setBackgroundColor(getResources().getColor(R.color.lock_background_color));
            mBackgroundLockImageView.setVisibility(View.VISIBLE);

        } else {
            mBackgroundInLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            mBackgroundLockImageView.setVisibility(View.GONE);
        }
    }

    private void changeBackGroundLockView(float forgroundX) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (forgroundX < mDeviceWidth) {
                mBackgroundLockImageView.setBackground(getResources().getDrawable(R.drawable.lock));
            } else {
                mBackgroundLockImageView.setBackground(getResources().getDrawable(R.drawable.unlock));
            }
        } else {
            if (forgroundX < mDeviceWidth) {
                mBackgroundLockImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.lock));
            } else {
                mBackgroundLockImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.unlock));
            }
        }
    }

    private View.OnTouchListener mViewTouchListener = new View.OnTouchListener() {
        private float firstTouchX = 0;
        private float layoutPrevX = 0;
        private float lastLayoutX = 0;
        private float layoutInPrevX = 0;
        private boolean isLockOpen = false;
        private int touchMoveX = 0;
        private int touchInMoveX = 0;

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: {// 0
                    firstTouchX = event.getX();
                    layoutPrevX = mForegroundLayout.getX();
                    layoutInPrevX = mBackgroundLockImageView.getX();
                    if (firstTouchX <= LOCK_OPEN_OFFSET_VALUE) {
                        isLockOpen = true;
                    }
                }
                break;
                case MotionEvent.ACTION_MOVE: { // 2
                    if (isLockOpen) {
                        touchMoveX = (int) (event.getRawX() - firstTouchX);
                        if (mForegroundLayout.getX() >= 0) {
                            mForegroundLayout.setX((int) (layoutPrevX + touchMoveX));
                            mBackgroundLockImageView.setX((int) (layoutInPrevX + (touchMoveX / 1.8)));
                            mLastLayoutX = lastLayoutX;
                            mMainHandler.sendEmptyMessage(0);
                            if (mForegroundLayout.getX() < 0) {
                                mForegroundLayout.setX(0);
                            }
                            lastLayoutX = mForegroundLayout.getX();
                        }
                    } else {
                        return false;
                    }
                }
                break;
                case MotionEvent.ACTION_UP: { // 1
                    if (isLockOpen) {
                        mForegroundLayout.setX(lastLayoutX);
                        mForegroundLayout.setY(0);
                        optimizeForground(lastLayoutX);
                    }
                    isLockOpen = false;
                    firstTouchX = 0;
                    layoutPrevX = 0;
                    layoutInPrevX = 0;
                    touchMoveX = 0;
                    lastLayoutX = 0;
                }
                break;
                default:
                    break;
            }

            return true;
        }
    };

    private void optimizeForground(float forgroundX) {
//        final int devideDeviceWidth = (mDeviceWidth / 2);
        if (forgroundX < mDeviceDeviceWidth) {
            int startPostion = 0;
            for (startPostion = mDeviceDeviceWidth; startPostion >= 0; startPostion--) {
                mForegroundLayout.setX(startPostion);
            }
        } else {
            TranslateAnimation animation = new TranslateAnimation(0, mDeviceDeviceWidth, 0, 0);
            animation.setDuration(300);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mForegroundLayout.setX(mDeviceDeviceWidth);
                    mForegroundLayout.setY(0);
                    dettachLockScreenView();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            mForegroundLayout.startAnimation(animation);
        }
    }

    /*---------- Listener class to get coordinates ------------- */
//    private class MyLocationListener implements LocationListener {
//        @Override
//        public void onLocationChanged(Location loc) {
//            String longitude = "Longitude: " + loc.getLongitude();
//            Log.v(TAG, "long: " + longitude);
//            String latitude = "Latitude: " + loc.getLatitude();
//            Log.v(TAG, "lat: " + latitude);
//
//
//        /*------- To get city name from coordinates -------- */
//            String cityName = null;
//            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
//            List<Address> addresses;
//            try {
//                addresses = gcd.getFromLocation(loc.getLatitude(),
//                        loc.getLongitude(), 1);
//                if (addresses.size() > 0) {
//                    System.out.println(addresses.get(0).getLocality());
//                    cityName = addresses.get(0).getLocality();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            String s = longitude + "\n" + latitude + "\n\nMy Current City is: "
//                    + cityName;
//            Log.d(TAG, "location cityName: " + s);
//        }
}
