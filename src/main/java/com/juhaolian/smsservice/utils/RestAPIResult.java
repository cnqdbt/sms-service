
package com.juhaolian.smsservice.utils;


import java.io.Serializable;

/**
 * ClassName:RestAPIResulut <br/>
 * Function: REST API接口统一响应接口实体. <br/>
 */
//@ApiModel(value = "REST API接口统一响应接口实体")
public class RestAPIResult<T> implements Serializable {

	private static final long serialVersionUID = 1L;

    private T response = (T) new Object();

    private String signatureServer;

    public T getResponse() {
        return response;
    }

    public void setResponse(T respData) {
        this.response = respData;
    }

    public String getSignatureServer() {
		return signatureServer;
	}

	public void setSignatureServer(String signatureServer) {
		this.signatureServer = signatureServer;
	}
	
	public RestAPIResult(){
		this.response = (T) new Object();
		this.signatureServer = "K+3I10vme5hNUO0m6RcgJRljXmDSoFxgJFoqzOec5dbAjG4ZSfOgCTKvGAf0hOR/xzBlG7CuWmoeeX3xglC6vw==";
	}

	
}
