package com.juhaolian.smsservice.test;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.ConnectionFactory;
import javax.jms.MapMessage;


public class TestSendMsg {

	
	public static void main(String[] args) {

		ConnectionFactory cf = new ActiveMQConnectionFactory("tcp://127.0.0.1:61616");
		
		JmsTemplate jmsTemplate = new JmsTemplate(cf);

		jmsTemplate.setDefaultDestination(new ActiveMQQueue("sms_queue"));
		
		MessageCreator mapMC = session -> {
            MapMessage mapMsg = session.createMapMessage();
            mapMsg.setString("phoneNumbers", "15864299365");
            mapMsg.setString("businessCode", "verification_code");
            mapMsg.setString("templateParam", "{\"code\":\"123\"}");

            return mapMsg;
        };
		
		jmsTemplate.send(mapMC);

	}
}
