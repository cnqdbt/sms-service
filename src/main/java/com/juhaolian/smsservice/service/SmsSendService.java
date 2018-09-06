package com.juhaolian.smsservice.service;


import com.juhaolian.smsservice.domain.ResponseInfo;
import org.springframework.http.HttpEntity;
import org.springframework.util.MultiValueMap;

/**
 * Created by bite on 18-8-15.
 */
public interface SmsSendService {
    ResponseInfo sendSms(HttpEntity<MultiValueMap<String, String>> requestEntity);

    String getTemplateId(String businessCode);
}
