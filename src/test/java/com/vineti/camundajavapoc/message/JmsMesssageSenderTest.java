package com.vineti.camundajavapoc.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.jms.JMSException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Slf4j
public class JmsMesssageSenderTest {
    @Autowired
    JmsTestListener jmsTestListener;

    @Test
    public void testSendJmsMessage() throws InterruptedException, JsonProcessingException, JMSException {
        JmsMessageSender sender = new JmsMessageSender();
        Map<String, String> values = new HashMap<>();
        values.put("name", "yo");
        String json  = new ObjectMapper().writeValueAsString(values);
        sender.sendMessage("WorkflowPoc",json );

        jmsTestListener.getLatch().await(3000, TimeUnit.MILLISECONDS);
        assertEquals(0, jmsTestListener.getLatch().getCount());
    }
}

