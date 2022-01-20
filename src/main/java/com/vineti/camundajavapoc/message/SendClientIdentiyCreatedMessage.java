package com.vineti.camundajavapoc.message;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SendClientIdentiyCreatedMessage implements JavaDelegate{


    @Override
    public void execute(DelegateExecution execution) throws Exception {
        RuntimeService runtimeService = execution.getProcessEngineServices().getRuntimeService();
        runtimeService.startProcessInstanceByMessage("SendMessage");

        log.info("\n\n  ... my process=" + execution.getProcessDefinitionId() + ", activtyId=" + execution.getCurrentActivityId() + ", activtyName='" + execution.getCurrentActivityName() + "', processInstanceId=" + execution.getProcessInstanceId() + ", businessKey=" + execution.getProcessBusinessKey() + ", executionId=" + execution.getId() + " \n\n");

    }
}

