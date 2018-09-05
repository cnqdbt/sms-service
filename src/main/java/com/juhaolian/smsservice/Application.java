package com.juhaolian.smsservice;

import com.juhaolian.smsservice.dao.SmsTemplateDao;
import com.juhaolian.smsservice.task.SendSmsTask;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import org.springframework.web.client.RestTemplate;

import javax.jms.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan
@EnableAutoConfiguration
public class Application {

	private Logger logger = LoggerFactory.getLogger(Application.class);

	@Value("${spring.activemq.broker-url}")
	String mqURL;

	@Value("${spring.activemq.queue}")
	String queueName;

	@Autowired
	SmsTemplateDao smsTemplateDao;

	@Autowired
	private RestTemplate template;

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

			ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);

			while (true) {
				Message msg = consumer.receive();
				executor.execute(new SendSmsTask(msg, smsTemplateDao, template));
			}
		} catch (JMSException je) {
			logger.error(je.getMessage());
		}
	}


}
