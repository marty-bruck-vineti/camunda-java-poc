package com.vineti.camundajavapoc.service;

import com.vineti.camundajavapoc.dto.ProcessDefinitionDto;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BpmnProcessDefinitionService {

    private final RuntimeService runtimeService; // camunda runtime service
    private final RepositoryService repositoryService; // camunda repository service

    BpmnProcessDefinitionService(@Autowired RuntimeService runtimeService, @Autowired RepositoryService repositoryService) {
        this.runtimeService = runtimeService;
        this.repositoryService = repositoryService;
    }

    public List<ProcessDefinitionDto> findAll() {
        List<ProcessDefinitionDto> processDefinitions = new ArrayList<>();
        List<ProcessDefinition> processes = repositoryService.createProcessDefinitionQuery().active().latestVersion().list();
        processes.forEach( p -> {
            ProcessDefinitionDto dto = new ProcessDefinitionDto();
            dto.setId(p.getId());
            dto.setKey(p.getKey());
            dto.setName(p.getName());
            processDefinitions.add(dto);
        });
        return processDefinitions;

    }


}
