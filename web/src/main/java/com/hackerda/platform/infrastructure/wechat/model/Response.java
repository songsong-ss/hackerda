package com.hackerda.platform.infrastructure.wechat.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Response {
    @SerializedName("errcode")
    private int errcode;
    @SerializedName("errmsg")
    private String errMsg;

}
