package com.vineti.camundajavapoc.configuration;

import com.vineti.camundajavapoc.service.bpmn.BpmnDelegateExecutor;
import com.vineti.camundajavapoc.service.bpmn.JavaTaskBpmnStarter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JavaTaskStarterConfiguration {
    @Bean
    public JavaTaskBpmnStarter starter() {
        return new JavaTaskBpmnStarter();
    }

    @Bean
    public BpmnDelegateExecutor bpmnDelegateExecutor() {
        return new BpmnDelegateExecutor();
    }
}
