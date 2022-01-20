package com.vineti.camundajavapoc.jms;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JmsTaskEndListenerEvent implements ExecutionListener {

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        log.info("External task {}:{} ended ", execution.getCurrentActivityName(), execution.getId());

    }}
