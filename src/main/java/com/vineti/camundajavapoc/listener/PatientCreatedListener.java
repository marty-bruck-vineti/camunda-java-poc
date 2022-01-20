package com.vineti.camundajavapoc.listener;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;

@Slf4j
public class PatientCreatedListener implements ExecutionListener {

    public void notify(DelegateExecution execution) throws Exception {
        log.info("===== Created Event received for {} =====", execution.getCurrentActivityName());
        execution.getVariables().forEach( (k,v) -> {
            log.info("Key => {}, Value: => {}", k, v);
            if (k.equals("site_name")) {
                execution.setVariable("site_name", "test_site_name");
            }
        });
        log.info("=====");

    }
}