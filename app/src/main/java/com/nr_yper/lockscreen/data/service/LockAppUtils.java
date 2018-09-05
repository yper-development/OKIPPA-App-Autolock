package com.nr_yper.lockscreen.data.service;

import com.nr_yper.lockscreen.data.api.LockApiService;

public class LockAppUtils {
    public static LockApiService getLockApiService() {
        return RetrofitClient.getLocckService().create(LockApiService.class);
    }

}
