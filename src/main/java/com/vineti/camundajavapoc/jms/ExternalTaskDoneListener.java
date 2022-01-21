package com.vineti.camundajavapoc.jms;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.MessageCorrelationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.BytesMessage;

@Component
@Slf4j
public class ExternalTaskDoneListener {

@Autowired
RuntimeService runtimeService;

    /**
     * Send requested signal to process instance
     * @param bytesMessage
     * @throws Exception
     */
    @JmsListener(destination = "CamundaServiceTaskComplete")
    public void receive(BytesMessage bytesMessage) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        try {
            byte[] byteData = new byte[(int) bytesMessage.getBodyLength()];
            bytesMessage.readBytes(byteData);
            String message = new String(byteData);
            TaskCompleteMessage completeMessage = mapper.readValue(message, TaskCompleteMessage.class);
            log.info("camunda task complete listener received message='{}'", message);
            MessageCorrelationResult result = runtimeService.
                    createMessageCorrelation(completeMessage.getCompletionEventName())
                    .processInstanceId(completeMessage.getProcessInstanceId())
                    .setVariables(completeMessage.getVariables())
                    .correlateWithResult();
            log.info(result.toString());

        }catch (Exception e) {
            log.error("Received exception parsing message. Dropping message from queue", e);
        }
    }
}