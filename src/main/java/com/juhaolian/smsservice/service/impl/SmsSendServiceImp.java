package com.juhaolian.smsservice.service.impl;

import com.juhaolian.smsservice.dao.SmsTemplateDao;
import com.juhaolian.smsservice.domain.ResponseInfo;
import com.juhaolian.smsservice.service.SmsSendService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Created by bite on 18-8-15.
 */

@Service
public class SmsSendServiceImp implements SmsSendService {

    @Autowired
    SmsTemplateDao smsTemplateDao;

    @Autowired
    private RestTemplate template;


    private Logger logger = LoggerFactory.getLogger(SmsSendServiceImp.class);

    public ResponseInfo sendSms(HttpEntity<MultiValueMap<String, String>> requestEntity) {
        String urlSend = "http://sms-adapter/sms/send";
        ResponseInfo ri = template.postForObject(urlSend, requestEntity, ResponseInfo.class);
        return ri;
    }

    public String getTemplateId(String businessCode) {
        return  smsTemplateDao.getTemplateId(businessCode);
    }
}
