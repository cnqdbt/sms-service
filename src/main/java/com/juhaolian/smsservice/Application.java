package com.juhaolian.smsservice;

import com.juhaolian.smsservice.dao.SmsTemplateDao;
import com.juhaolian.smsservice.domain.ResponseInfo;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.jms.*;
import java.util.UUID;

@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan
public class Application {
	@Autowired
	private RestTemplate template;

	private Logger logger = LoggerFactory.getLogger(Application.class);

	@Value("${spring.activemq.broker-url}")
	String mqURL;

	@Value("${spring.activemq.queue}")
	String queueName;

	@Autowired
	SmsTemplateDao smsTemplateDao;

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	public static void main(String[] args) {
		 new SpringApplicationBuilder(Application.class).web(true).run(args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void startMQListener() {
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(mqURL);
		try {
			Connection connection = factory.createConnection();
			connection.start();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination dest = new ActiveMQQueue(queueName);
			MessageConsumer consumer = session.createConsumer(dest);

			while (true) {
				Message msg = consumer.receive();
				if (msg instanceof MapMessage) {
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
				} else {
					logger.info("Message is not MapMessage.");
				}
			}

		} catch (JMSException je) {
			logger.error(je.getMessage());
		} catch (InterruptedException ie) {
			logger.error(ie.getMessage());
		}
	}


}
