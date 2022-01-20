package com.vineti.camundajavapoc.service.bpmn;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class BpmnDelegateExecutor implements JavaDelegate {
    public void execute(DelegateExecution delegate) {
        System.out.println("Invoked java delegate from BPMN process!");
    }
}
