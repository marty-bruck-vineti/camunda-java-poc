package com.vineti.camundajavapoc.jms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vineti.camundajavapoc.message.JmsMessageSender;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.engine.ExternalTaskService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.externaltask.LockedExternalTask;
import org.camunda.bpm.engine.impl.el.FixedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * send a message to the topic indicated by the workflow. NOTE: This bean is
 * generated dynamically by the workflow and cannot be a spring bean.
 */

@Slf4j
@Component("jmsTaskStarter")

//public class JmsServiceTaskStartAdapter implements JavaDelegate {
public class JmsServiceTaskStartAdapter implements JavaDelegate {
    // TODO: Retrieve from model
    private  String sourceQueue = "OrderServicePoc";
    private String responseListenerQueue = "CamundaServiceTaskComplete";

    @Autowired
    JmsMessageSender messageSender;

    @Autowired
    ExternalTaskService externalTaskService;

    @Autowired
    TaskService taskService;

    private FixedValue completionEventName;


    @SneakyThrows
    @Override
    public void execute(DelegateExecution externalTask) {
        ObjectMapper mapper = new ObjectMapper();
        JmsTaskStartMessage message = new JmsTaskStartMessage();
        message.setProcessName(externalTask.getBusinessKey()); // TODO : Shouldn't be task key
        message.setProcessInstanceId(externalTask.getProcessInstanceId());
        message.setActivityId(externalTask.getId());
        message.setActivityName(externalTask.getBusinessKey());
        message.setVariables(externalTask.getVariables());
        message.setResponseListenerQueue(this.responseListenerQueue);
        message.setCompletionEventName(this.completionEventName.getExpressionText());

        String messageStr = mapper.writeValueAsString(message);
        //String queue = this.sourceQueue.getExpressionText();
        String queue = this.sourceQueue;
        messageSender.sendMessage(queue, messageStr);
        log.info("Successfully sent message {} to queue {}", message, queue );
    }
}