package com.nr_yper.lockscreen;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;

import com.hungvv.lib.lockscreenusingservice.Lockscreen;
import com.hungvv.lib.lockscreenusingservice.SharedPreferencesUtil;
import com.nr_yper.lockscreen.data.api.LockApiService;

public class MainActivity extends AppCompatActivity {
    private Context mContext = null;
    private SwitchCompat switchCompat;
    private LockApiService lockApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        SharedPreferencesUtil.init(mContext);
        switchCompat = (SwitchCompat) findViewById(R.id.switchCompat);

        boolean lockState = SharedPreferencesUtil.get(Lockscreen.ISLOCK);
        if (lockState) {
            switchCompat.setChecked(true);

        } else {
            switchCompat.setChecked(false);

        }

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
                    Lockscreen.getInstance(mContext).startLockscreenService();
                    finish();
                } else {
                    SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, false);
                    Lockscreen.getInstance(mContext).stopLockscreenService();
                }
            }
        });


    }
}
