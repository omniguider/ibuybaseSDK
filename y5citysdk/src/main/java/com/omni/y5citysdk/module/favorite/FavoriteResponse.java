package com.omni.y5citysdk.module.favorite;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FavoriteResponse implements Serializable {

    @SerializedName("result")
    private String result;
    @SerializedName("error_message")
    private String errorMessage;
    @SerializedName("insert_success")
    private int insert_success;
    @SerializedName("insert_failed")
    private int insert_failed;
    @SerializedName("delete_success")
    private int delete_success;
    @SerializedName("delete_failed")
    private int delete_failed;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getInsert_success() {
        return insert_success;
    }

    public int getDelete_success() {
        return delete_success;
    }

}