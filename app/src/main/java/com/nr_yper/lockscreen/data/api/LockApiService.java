package com.nr_yper.lockscreen.data.api;

import com.nr_yper.lockscreen.data.model.Mansion;
import com.nr_yper.lockscreen.data.model.Transporter;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface LockApiService {

    @POST("/api/lock/device_status")
    Call<Mansion> validateLockscreen(@Query("tracking_number") String tracking_number,
                                     @Query("device_id") String device_id,
                                     @Query("transport_corp_id") int transport_corp_id);
    @GET("/api/get_lists_transport_corp")
    Call<List<Transporter>> getTransporter();

}


