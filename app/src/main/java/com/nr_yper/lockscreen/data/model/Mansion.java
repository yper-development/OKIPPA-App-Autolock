package com.nr_yper.lockscreen.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Mansion {
    @SerializedName("is_correct")
    @Expose
    private boolean is_correct;

    public boolean getIs_correct() {
        return is_correct;
    }

    public void setIs_correct(boolean is_correct) {
        this.is_correct = is_correct;
    }
}
