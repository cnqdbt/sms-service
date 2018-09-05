package com.juhaolian.smsservice.dao;

import org.apache.ibatis.annotations.Param;


/**
 * Created by bite on 18-8-15.
 */


public interface SmsTemplateDao {
    String getTemplateId(@Param("businessCode") String businessCode);
}
