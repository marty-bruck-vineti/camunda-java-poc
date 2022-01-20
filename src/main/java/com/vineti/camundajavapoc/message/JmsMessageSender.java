package com.vineti.camundajavapoc.message;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.stereotype.Component;

import javax.jms.*;

/**
 * send a message to the indicated topic. NOTE: This also implements a singleton pattern because the message adapter
 * is instantiated from camunda and cannot be accessed via dependency injection
 */
@Component
public class JmsMessageSender  {
    private final ActiveMQConnectionFactory connectionFactory;

    public JmsMessageSender() throws JMSException {
        connectionFactory = new ActiveMQConnectionFactory("tcp://0.0.0.0:61616");
    }

    public void sendMessage(final String queueName, final String messageText) throws JMSException {
        try(Connection connection = connectionFactory.createConnection()) {
            connection.start();
            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue(queueName);
            MessageProducer producer = session.createProducer(queue);
            Message message = session.createTextMessage(messageText);
            producer.send(message);
        }
    }
}