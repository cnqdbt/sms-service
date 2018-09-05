package com.juhaolian.smsservice.task;

import com.juhaolian.smsservice.dao.SmsTemplateDao;
import com.juhaolian.smsservice.domain.ResponseInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import java.util.UUID;

/**
 * Created by bite on 18-9-5.
 */

public class SendSmsTask implements Runnable {
    private Message msg;

    private SmsTemplateDao smsTemplateDao;

    private RestTemplate template;

    private Logger logger = LoggerFactory.getLogger(SendSmsTask.class);

    public SendSmsTask(Message msg, SmsTemplateDao dao, RestTemplate template) {
        this.msg = msg;
        this.smsTemplateDao = dao;
        this.template = template;
    }

    public void run () {
        if (msg instanceof MapMessage) {
            try {
                MapMessage mapMessage = (MapMessage) msg;
                HttpHeaders headers = new HttpHeaders();
                headers.add("X-Auth-Token", UUID.randomUUID().toString());

                MultiValueMap<String, String> postParameters = new LinkedMultiValueMap<>();
                postParameters.add("phoneNumbers", mapMessage.getString("phoneNumbers"));
                postParameters.add("templateId", smsTemplateDao.getTemplateId(mapMessage.getString("businessCode")));
                postParameters.add("templateParam", mapMessage.getString("templateParam"));
                HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(postParameters, headers);

                String urlSend = "http://sms-adapter/sms/send";
                ResponseInfo ri = template.postForObject(urlSend, requestEntity, ResponseInfo.class);

                if (ri != null && ri.getResultCode() == 1) {
                    logger.warn("sending message to " + mapMessage.getString("phoneNumbers") + " failed.");
                    Thread.sleep(1000);
                    // send again
                    ResponseInfo ri2 = template.postForObject(urlSend, requestEntity, ResponseInfo.class);
                    if (ri2 != null && ri2.getResultCode() == 1) {
                        logger.warn("sending message to " + mapMessage.getString("phoneNumbers") + " failed again.");
                    }
                }
            } catch (JMSException je) {
                logger.error(je.getMessage());
            } catch (InterruptedException ie) {
                logger.error(ie.getMessage());
            } catch (HttpServerErrorException he) {
                logger.error(he.toString());
            }
        } else {
            logger.info("Message is not MapMessage.");
        }
    }
}
