package com.hungvv.lib.lockscreenusingservice.service;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hungvv.lib.lockscreenusingservice.Lockscreen;
import com.hungvv.lib.lockscreenusingservice.LockscreenUtil;
import com.hungvv.lib.lockscreenusingservice.PermissionActivity;

import com.hungvv.lib.lockscreenusingservice.SharedPreferencesUtil;
import com.nr_yper.lockscreen.R;
import com.nr_yper.lockscreen.adapter.TranspoterAdapter;
import com.nr_yper.lockscreen.data.api.LockApiService;
import com.nr_yper.lockscreen.data.model.Mansion;
import com.nr_yper.lockscreen.data.model.Transporter;
import com.nr_yper.lockscreen.data.service.LockAppUtils;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class LockscreenViewService extends Service {
    private final int LOCK_OPEN_OFFSET_VALUE = 50;
    private Context mContext = null;
    private LayoutInflater mInflater = null;
    private View mLockscreenView = null;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;
    private RelativeLayout mBackgroundLayout = null;
    private RelativeLayout mBackgroundInLayout = null;
    //    private ImageView mBackgroundLockImageView = null;
    private RelativeLayout mForgroundLayout = null;
    private RelativeLayout mStatusBackgruondDummyView = null;
    private RelativeLayout mStatusForgruondDummyView = null;
    private ShimmerTextView mShimmerTextView = null;
    private boolean mIsLockEnable = false;
    private boolean mIsSoftkeyEnable = false;
    private int mDeviceWidth = 0;
    private int mDevideDeviceWidth = 0;
    private float mLastLayoutX = 0;
    private int mServiceStartId = 0;
    private SendMassgeHandler mMainHandler = null;
    private TextView tvTextClick;
    //    private boolean sIsSoftKeyEnable = false;
    private ImageView imgClick;
    private RelativeLayout layoutRoot;
    private EditText edPassword;
    private LockApiService lockApiService;
    private ProgressDialog progressDialog = null;
    private String device_id = "1";
    private LinearLayout layoutValidate;
    private TextView tvErrorMess;
    private String ninjalockLink = "market://details?id=com.linough.android.ninjalock";
    private RecyclerView recyclerTransporter;
    private List<Transporter> transporters = new ArrayList<>();

    private class SendMassgeHandler extends android.os.Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            changeBackGroundLockView(mLastLayoutX);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        SharedPreferencesUtil.init(mContext);
//        sIsSoftKeyEnable = SharedPreferencesUtil.get(Lockscreen.ISSOFTKEY);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mMainHandler = new SendMassgeHandler();
        if (isLockScreenAble()) {
            if (null != mWindowManager) {
                if (null != mLockscreenView) {
                    mWindowManager.removeView(mLockscreenView);
                }
                mWindowManager = null;
                mParams = null;
                mInflater = null;
                mLockscreenView = null;
            }
            initState();
            initView();
            attachLockScreenView();

        }
        return LockscreenViewService.START_STICKY;
    }

    @Override
    public void onDestroy() {
        dettachLockScreenView();
    }

    private void loadListTransport() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);

        recyclerTransporter.setLayoutManager(layoutManager);

        lockApiService.getTransporter().enqueue(new Callback<List<Transporter>>() {
            @Override
            public void onResponse(Call<List<Transporter>> call, Response<List<Transporter>> response) {
                if (response.isSuccessful()) {
                    LockscreenViewService.this.transporters = response.body();
                    final TranspoterAdapter transpoterAdapter = new TranspoterAdapter(transporters, mContext);
                    transpoterAdapter.notifyDataSetChanged();
                    recyclerTransporter.setAdapter(transpoterAdapter);
                }


            }

            @Override
            public void onFailure(Call<List<Transporter>> call, Throwable t) {
                showErrorMess("Error Fetching data");
            }
        });



    }

    private void initState() {
        device_id = LockscreenUtil.getInstance(mContext).getDeviceId(mContext);
        lockApiService = LockAppUtils.getLockApiService();
        mIsLockEnable = LockscreenUtil.getInstance(mContext).isStandardKeyguardState();
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

        if (null == mLockscreenView) {
            mLockscreenView = mInflater.inflate(R.layout.view_lookscreen, null);

        }
    }

    private boolean isLockScreenAble() {
        boolean isLock = SharedPreferencesUtil.get(Lockscreen.ISLOCK);
        if (isLock) {
            isLock = true;
        } else {
            isLock = false;
        }
        return isLock;
    }


    private void attachLockScreenView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(mContext)) {
                Intent permissionActivityIntent = new Intent(mContext, PermissionActivity.class);
                permissionActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(permissionActivityIntent);

                LockscreenUtil.getInstance(mContext).getPermissionCheckSubject()
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                new Action1<Boolean>() {
                                    @Override
                                    public void call(Boolean aBoolean) {
                                        addLockScreenView();
                                    }
                                }
                        );
            } else {
                addLockScreenView();
            }
        } else {
            addLockScreenView();
        }


    }

    public void showErrorMess(String errorText) {
        tvErrorMess.setVisibility(View.VISIBLE);
        tvErrorMess.setText(errorText);
    }

    public void hideErrorMess() {
        tvErrorMess.setVisibility(View.GONE);
    }

    public void showProgress() {
        edPassword.setClickable(false);
        layoutValidate.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        layoutValidate.setVisibility(View.GONE);
        edPassword.setClickable(true);
    }

    private void addLockScreenView() {
        if (null != mWindowManager && null != mLockscreenView && null != mParams) {
            mLockscreenView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });
            mWindowManager.addView(mLockscreenView, mParams);
            settingLockView();
        }
    }


    private boolean dettachLockScreenView() {
        if (null != mWindowManager && null != mLockscreenView && isAttachedToWindow()) {
            mWindowManager.removeView(mLockscreenView);
            mLockscreenView = null;
            mWindowManager = null;
            stopSelf(mServiceStartId);
            return true;
        } else {
            return false;
        }
    }

    private void openUrl(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean isAttachedToWindow() {
        return mLockscreenView.isAttachedToWindow();
    }

    private void settingLockView() {
        recyclerTransporter = (RecyclerView) mLockscreenView.findViewById(R.id.listTransporter);
        layoutValidate = (LinearLayout) mLockscreenView.findViewById(R.id.layoutProgress);
        tvErrorMess = (TextView) mLockscreenView.findViewById(R.id.tvErrorText);
        edPassword = (EditText) mLockscreenView.findViewById(R.id.edPass);
        layoutRoot = (RelativeLayout) mLockscreenView.findViewById(R.id.layoutRoot);
        imgClick = (ImageView) mLockscreenView.findViewById(R.id.imgClick);
        mBackgroundLayout = (RelativeLayout) mLockscreenView.findViewById(R.id.lockscreen_background_layout);
        mBackgroundInLayout = (RelativeLayout) mLockscreenView.findViewById(R.id.lockscreen_background_in_layout);
//        mBackgroundLockImageView = (ImageView) mLockscreenView.findViewById(R.id.lockscreen_background_image);
        mForgroundLayout = (RelativeLayout) mLockscreenView.findViewById(R.id.lockscreen_forground_layout);
        mShimmerTextView = (ShimmerTextView) mLockscreenView.findViewById(R.id.shimmer_tv);
        (new Shimmer()).start(mShimmerTextView);
        mForgroundLayout.setOnTouchListener(mViewTouchListener);

        mStatusBackgruondDummyView = (RelativeLayout) mLockscreenView.findViewById(R.id.lockscreen_background_status_dummy);
        mStatusForgruondDummyView = (RelativeLayout) mLockscreenView.findViewById(R.id.lockscreen_forground_status_dummy);
//        setBackGroundLockView();

        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        mDeviceWidth = displayMetrics.widthPixels;
        mDevideDeviceWidth = (mDeviceWidth / 2);
//        mBackgroundLockImageView.setX((int) (((mDevideDeviceWidth) * -1)));
        imgClick.setOnClickListener(mOnclick);
        layoutRoot.setOnClickListener(mOnclick);
        //TODO change when in product
        loadListTransport();
        edPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //Call api
                    String inputString = edPassword.getText().toString();
                    if (!inputString.equals("")) {
                        showProgress();
                        hideErrorMess();
                        if (LockscreenUtil.getInstance(mContext).checkInternetConnection(mContext)) {
                            //device_id for product, 1 for testing
                            lockApiService.validateLockscreen(edPassword.getText().toString(), device_id).enqueue(new Callback<Mansion>() {
                                @Override
                                public void onResponse(Call<Mansion> call, Response<Mansion> response) {
                                    hideProgress();

                                    if (response.isSuccessful()) {
                                        //Check is_correct
                                        Mansion responseMansion = response.body();
                                        boolean isCorrect = (responseMansion != null && responseMansion.getIs_correct());
                                        if (isCorrect) {
                                            try {
                                                Intent i = mContext.getPackageManager().getLaunchIntentForPackage("com.linough.android.ninjalock");
                                                mContext.startActivity(i);
                                            } catch (Exception e) {
                                                // TODO Auto-generated catch block
                                                //If current device don't have ninja lock
                                                openUrl(ninjalockLink);

                                            }
                                            mForgroundLayout.setX(mDevideDeviceWidth);
                                            mForgroundLayout.setY(0);
                                            dettachLockScreenView();
                                        } else {

                                            showErrorMess("入力された追跡番号は無効です。別の追跡番号をお持ちの場合はそちらを入力してください。");
                                        }


                                    } else {
                                        //Failed, re-enter the track_number
                                        showErrorMess("Somethings went wrong, please try again! ");

                                    }
                                }

                                @Override
                                public void onFailure(Call<Mansion> call, Throwable t) {
                                    hideProgress();
                                    showErrorMess("Somethings went wrong, please try again! ");
                                }
                            });

                        } else {
                            hideProgress();
                            showErrorMess("Please check your internet connection!");

                        }
                        return false;

                    } else {
                        return true;
                    }

                }
                return false;
            }
        });
        //kitkat
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int val = LockscreenUtil.getInstance(mContext).getStatusBarHeight();
            RelativeLayout.LayoutParams forgroundParam = (RelativeLayout.LayoutParams) mStatusForgruondDummyView.getLayoutParams();
            forgroundParam.height = val;
            mStatusForgruondDummyView.setLayoutParams(forgroundParam);
            AlphaAnimation alpha = new AlphaAnimation(0.5F, 0.5F);
            alpha.setDuration(0); // Make animation instant
            alpha.setFillAfter(true); // Tell it to persist after the animation ends
            mStatusForgruondDummyView.startAnimation(alpha);
            RelativeLayout.LayoutParams backgroundParam = (RelativeLayout.LayoutParams) mStatusBackgruondDummyView.getLayoutParams();
            backgroundParam.height = val;
            mStatusBackgruondDummyView.setLayoutParams(backgroundParam);
        }
    }

//    private void setBackGroundLockView() {
//        if (mIsLockEnable) {
//            mBackgroundInLayout.setBackgroundColor(getResources().getColor(R.color.lock_background_color));
//            mBackgroundLockImageView.setVisibility(View.VISIBLE);
//
//        } else {
//            mBackgroundInLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
//            mBackgroundLockImageView.setVisibility(View.GONE);
//        }
//    }


//    private void changeBackGroundLockView(float forgroundX) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            if (forgroundX < mDeviceWidth) {
//                mBackgroundLockImageView.setBackground(getResources().getDrawable(R.drawable.lock));
//            } else {
//                mBackgroundLockImageView.setBackground(getResources().getDrawable(R.drawable.unlock));
//            }
//        } else {
//            if (forgroundX < mDeviceWidth) {
//                mBackgroundLockImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.lock));
//            } else {
//                mBackgroundLockImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.unlock));
//            }
//        }
//    }

    private View.OnClickListener mOnclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            Toast.makeText(mContext, "OnClickID" + view.getId(), Toast.LENGTH_SHORT).show();

        }
    };

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
                    layoutPrevX = mForgroundLayout.getX();
//                    layoutInPrevX = mBackgroundLockImageView.getX();
                    if (firstTouchX <= LOCK_OPEN_OFFSET_VALUE) {
                        isLockOpen = true;
                    }
                }
                break;
                case MotionEvent.ACTION_MOVE: { // 2
                    if (isLockOpen) {
                        touchMoveX = (int) (event.getRawX() - firstTouchX);
                        if (mForgroundLayout.getX() >= 0) {
                            mForgroundLayout.setX((int) (layoutPrevX + touchMoveX));
//                            mBackgroundLockImageView.setX((int) (layoutInPrevX + (touchMoveX / 1.8)));
                            mLastLayoutX = lastLayoutX;
                            mMainHandler.sendEmptyMessage(0);
                            if (mForgroundLayout.getX() < 0) {
                                mForgroundLayout.setX(0);
                            }
                            lastLayoutX = mForgroundLayout.getX();
                        }
                    } else {
                        return false;
                    }
                }
                break;
                case MotionEvent.ACTION_UP: { // 1
                    if (isLockOpen) {
                        mForgroundLayout.setX(lastLayoutX);
                        mForgroundLayout.setY(0);
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
        if (forgroundX < mDevideDeviceWidth) {
            int startPostion = 0;
            for (startPostion = mDevideDeviceWidth; startPostion >= 0; startPostion--) {
                mForgroundLayout.setX(startPostion);
            }
        } else {
            TranslateAnimation animation = new TranslateAnimation(0, mDevideDeviceWidth, 0, 0);
            animation.setDuration(300);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mForgroundLayout.setX(mDevideDeviceWidth);
                    mForgroundLayout.setY(0);
                    dettachLockScreenView();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            mForgroundLayout.startAnimation(animation);
        }
    }
}
