package com.juhaolian.smsservice.dao;

import org.apache.ibatis.annotations.Param;


/**
 * Created by bite on 18-8-15.
 */


public interface SmsLogDao {
    int logSms(@Param("bizId") String bizId, @Param("responseCode") String responseCode,
               @Param("phoneNumbers") String phoneNumbers, @Param("templateId") String templateId,
               @Param("templateParam") String templateParam, @Param("caller") String caller,
               @Param("signName") String signName);
}
