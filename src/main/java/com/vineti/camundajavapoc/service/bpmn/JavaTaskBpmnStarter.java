package com.vineti.camundajavapoc.service.bpmn;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public class JavaTaskBpmnStarter implements InitializingBean {
    @Autowired
    private RuntimeService runtimeService;

    public void afterPropertiesSet() throws Exception {
        runtimeService.startProcessInstanceByKey("invokeJavaTask");
    }

    public void setRuntimeService(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }
}
