package com.vineti.camundajavapoc.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * send a message to the topic indicated by the workflow. NOTE: This bean is
 * generated dynamically by the workflow and cannot be a spring bean.
 */
public class JmsWorkflowMessageAdapter implements JavaDelegate {
    // NOTE: This could be injected from the workflow or looked up by activity name. For now, it's just hardcoded
    private String topicName = "WorkflowPoc";

    @Override
    /**
     * This method handles any incoming message from the bpmn model that needs to output a jms message.
     * Eventually this should use some sort of schema lookup service to create the jms message that is specific
     * for that event. For now, it just sends a map of variables to the
     */
    public void execute(DelegateExecution execution) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JmsWorkflowMessage message = new JmsWorkflowMessage();
        message.setProcessName(execution.getProcessBusinessKey());
        message.setProcessInstanceId(execution.getProcessInstanceId());
        message.setActivityId(execution.getCurrentActivityId());
        message.setVariables(execution.getVariables());
        new JmsMessageSender().sendMessage(this.topicName, mapper.writeValueAsString(message));
    }
}