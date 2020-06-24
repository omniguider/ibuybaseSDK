package com.omni.y5citysdk.module.login;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CheckUserLoginData implements Serializable {

    @SerializedName("u_id")
    private String uId;
    @SerializedName("user_email")
    private String email;
    @SerializedName("login_token")
    private String loginToken;
    @SerializedName("user_name")
    private String userName;
    @SerializedName("last_login")
    /** "2018-01-23 22:57:24" */
    private String lastLoginTime;
    @SerializedName("last_logout")
    private String lastLogoutTime;
    @SerializedName("fb_ts")
    private String fb_ts;
    @SerializedName("fb_id")
    private String fb_id;

    public String getuId() {
        return uId;
    }

    public String getEmail() {
        return email;
    }

    public String getLoginToken() {
        return loginToken;
    }

    public String getUserName() {
        return userName;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public String getLastLogoutTime() {
        return lastLogoutTime;
    }

    @Override
    public String toString() {
        return "CheckUserLoginData{" +
                "uId='" + uId + '\'' +
                ", email='" + email + '\'' +
                ", loginToken='" + loginToken + '\'' +
                ", userName='" + userName + '\'' +
                ", lastLoginTime='" + lastLoginTime + '\'' +
                ", lastLogoutTime='" + lastLogoutTime + '\'' +
                '}';
    }
}
