package com.omni.y5citysdk.module.login;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LogoutResponseData implements Serializable {

    @SerializedName("u_id")
    private String u_id;
    @SerializedName("user_name")
    private String user_name;

    public String getU_id() {
        return u_id;
    }

    public String getUser_name() {
        return user_name;
    }
}

