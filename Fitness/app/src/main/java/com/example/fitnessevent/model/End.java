package com.example.fitnessevent.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class End implements Serializable {
    @SerializedName("timezone")
    @Expose
    private String timezone;

    @SerializedName("local")
    @Expose
    private String local;

    @SerializedName("utc")
    @Expose
    private String utc;

    public String getTImezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getUtc() {
        return utc;
    }

    public void setUtc(String utc) {
        this.utc = utc;
    }

    public End() {
    }

    public End(String timezone, String local, String utc) {
        this.timezone = timezone;
        this.local = local;
        this.utc = utc;
    }
}
