package com.vineti.camundajavapoc.service;

import com.vineti.camundajavapoc.dto.ProcessInstanceDto;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
public class BpmnProcessInstanceServiceTest {
    @Autowired
    RuntimeService runtimeService;

    @Autowired
    BpmnProcessInstanceService processService;
    @Test
    public void testGetAllProcessInstances() {
        // start process instance directly
        ProcessInstance newInstance = runtimeService.startProcessInstanceByKey("poc-flow-1", "My POC Flow 1");
        ProcessInstance newInstance2 = runtimeService.startProcessInstanceByKey("poc-flow-with-gateway", "My POC Flow 2");

        assertNotNull(newInstance);
        assertNotNull(newInstance2);
        List<ProcessInstanceDto> allProcessInstances = processService.findAll();

        assertFalse(allProcessInstances.isEmpty());
        ProcessInstanceDto foundInstance1 = allProcessInstances.stream().filter(p -> p.getBusinessKey().equals("My POC Flow 1")).findFirst().get();
        ProcessInstanceDto foundInstance2 = allProcessInstances.stream().filter(p -> p.getBusinessKey().equals("My POC Flow 2")).findFirst().get();
        assertNotNull(foundInstance1);
        assertNotNull(foundInstance2);
    }
}
