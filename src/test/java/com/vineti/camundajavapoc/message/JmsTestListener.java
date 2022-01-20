package com.vineti.camundajavapoc.message;

import camundajar.impl.com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@Component
@Slf4j
    public class JmsTestListener {
    private CountDownLatch latch = new CountDownLatch(1);

    public CountDownLatch getLatch() {
        return latch;
    }
    @JmsListener(destination = "WorkflowPoc")
    public void receive(String message) {
        log.info("'subscriber1' received message='{}'", message);
        latch.countDown();
    }
}

