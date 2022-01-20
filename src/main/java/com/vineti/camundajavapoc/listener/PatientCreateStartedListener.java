package com.vineti.camundajavapoc.listener;

import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

import java.util.Collection;

@Slf4j
@Accessors
public class PatientCreateStartedListener implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {
        log.info("===== Create Started Event received for {} =====", delegateTask.getName());
        delegateTask.getVariables().forEach( (k,v) -> {
            log.info("Key => {}, Value: => {}", k, v);
        });
        Collection<ModelElementInstance> form = delegateTask.getBpmnModelElementInstance().getExtensionElements().getElements();
        log.info("=====");

    }
}