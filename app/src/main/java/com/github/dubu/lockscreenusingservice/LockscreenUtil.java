package com.github.dubu.lockscreenusingservice;

import android.Manifest;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.ViewTreeObserver;

import rx.subjects.PublishSubject;

public class LockscreenUtil {
    private Context mContext = null;
    private static LockscreenUtil mLockscreenUtilInstance;

    private PublishSubject<Boolean> permissionCheckSubject;

    public static LockscreenUtil getInstance(Context context) {
        if (mLockscreenUtilInstance == null) {
            if (null != context) {
                mLockscreenUtilInstance = new LockscreenUtil(context);
            } else {
                mLockscreenUtilInstance = new LockscreenUtil();
            }
        }
        return mLockscreenUtilInstance;
    }

    private LockscreenUtil() {
        mContext = null;
    }

    private LockscreenUtil(Context context) {
        mContext = context;
    }


    public String getDeviceId(Context mContext) {
        String androidId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId;
    }


    public boolean checkInternetConnection(Context mContext) {
        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm != null ? cm.getActiveNetworkInfo() : null;
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public boolean isStandardKeyguardState() {
        boolean isStandardKeyguqrd = false;
        KeyguardManager keyManager = (KeyguardManager) mContext.getSystemService(mContext.KEYGUARD_SERVICE);
        if (null != keyManager) {
            isStandardKeyguqrd = keyManager.isKeyguardSecure();
        }

        return isStandardKeyguqrd;
    }

    public boolean isSoftKeyAvail(Context context) {
        final boolean[] isSoftkey = {false};
        final View activityRootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int rootViewHeight = activityRootView.getRootView().getHeight();
                int viewHeight = activityRootView.getHeight();
                int heightDiff = rootViewHeight - viewHeight;
                if (heightDiff > 100) { // 99% of the time the height diff will be due to a keyboard.
                    isSoftkey[0] = true;
                }
            }
        });
        return isSoftkey[0];
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
            result = mContext.getResources().getDimensionPixelSize(resourceId);

        return result;
    }

    public PublishSubject<Boolean> getPermissionCheckSubject() {
        if (null == permissionCheckSubject) {
            permissionCheckSubject = PublishSubject.create();
        }

        return permissionCheckSubject;
    }
}
