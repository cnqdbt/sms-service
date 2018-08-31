package com.juhaolian.smsservice.domain;

/**
 * Created by bite on 18-8-15.
 */
public class ResponseInfo {
    // 0: success
    private Integer resultCode;

    private String responseCode;

    private String responseMsg;

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }

    public String getResponseMsg() {
        return responseMsg;
    }
}
