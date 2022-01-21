package com.vineti.camundajavapoc.controller;

import com.vineti.camundajavapoc.dto.ProcessDefinitionDto;
import com.vineti.camundajavapoc.service.BpmnProcessDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = {"/api/v1/processdefinition"}, produces = APPLICATION_JSON_VALUE)

public class ProcessDefinitionController {

    private final BpmnProcessDefinitionService bpmnProcessDefinitionService;


    public ProcessDefinitionController(@Autowired BpmnProcessDefinitionService bpmnProcessDefinitionService) {
        this.bpmnProcessDefinitionService = bpmnProcessDefinitionService;
    }

    /**
     * Get a list of all process definitions
     * @return - List of process Definitions @see - ProcessDefinitionDto
     */
    @GetMapping(path="/")
    public ResponseEntity<List<ProcessDefinitionDto>> getAll(
//            @RequestParam(required = false, name = "page", defaultValue = "0") int page,
//            @RequestParam(required = false, name = "size", defaultValue = "1000") int size,
//            @RequestParam(required = false, name = "sortField", defaultValue = "createdAt") String sortField,
//            @RequestParam(required = false, name = "direction", defaultValue = "DESC") String direction
    ) {
        // TODO: Add pagination
        final List<ProcessDefinitionDto> processDefinitionDtos = bpmnProcessDefinitionService.findAll();
        return ResponseEntity.ok(processDefinitionDtos);
    }
}
