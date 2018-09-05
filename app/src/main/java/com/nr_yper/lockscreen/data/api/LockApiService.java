package com.nr_yper.lockscreen.data.api;

import com.nr_yper.lockscreen.data.model.Mansion;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface LockApiService {

    @POST("/api/lock/device_status")
    Call<Mansion> validateLockscreen(@Query("tracking_number") String tracking_number,
                                     @Query("device_id") String device_id);

}


