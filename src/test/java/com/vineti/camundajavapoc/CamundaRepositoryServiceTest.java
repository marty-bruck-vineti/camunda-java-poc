package com.vineti.camundajavapoc;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Slf4j
class CamundaRepositoryServiceTest {
    @Autowired
    RepositoryService repositoryService;


    @Test
    void canListProcessDefinitions() {

        List<ProcessDefinition> processes = repositoryService.createProcessDefinitionQuery().active().latestVersion().list();
        assertTrue(!processes.isEmpty());
        System.out.println("==> Found Models: ");
        processes.forEach(p->
            log.info("Process Name: {}, Process id: {}. Process Key: {}", p.getName(), p.getId(), p.getKey())
        );
        System.out.println("=====");
    }


}
