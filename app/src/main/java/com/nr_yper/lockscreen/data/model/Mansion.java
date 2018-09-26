package com.nr_yper.lockscreen.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Mansion {
    @SerializedName("is_correct")
    @Expose
    private boolean is_correct;

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("lock_key")
    @Expose
    private String lock_key;

    public String getLock_key() {
        return lock_key;
    }

    public void setLock_key(String lock_key) {
        this.lock_key = lock_key;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getIs_correct() {
        return is_correct;
    }

    public void setIs_correct(boolean is_correct) {
        this.is_correct = is_correct;
    }
}
