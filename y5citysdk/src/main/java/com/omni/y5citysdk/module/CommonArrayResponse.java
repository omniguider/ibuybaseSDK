package com.omni.y5citysdk.module;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CommonArrayResponse implements Serializable {

    @SerializedName("result")
    private String result;
    @SerializedName("error_message")
    private String errorMessage;
    @SerializedName("data")
    private Object[] data;

    public String getResult() {
        return result;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Object[] getData() {
        return data;
    }
}
