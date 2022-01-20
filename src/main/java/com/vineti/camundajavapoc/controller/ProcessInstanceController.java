package com.vineti.camundajavapoc.controller;

import com.vineti.camundajavapoc.dto.ProcessInstanceDto;
import com.vineti.camundajavapoc.dto.TaskDto;
import com.vineti.camundajavapoc.service.BpmnProcessInstanceService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.ws.rs.Path;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = {"/api/v1/processinstance"}, produces = APPLICATION_JSON_VALUE)
@Slf4j
public class ProcessInstanceController {

    private final BpmnProcessInstanceService bpmnProcessInstanceService;

    public ProcessInstanceController(@Autowired BpmnProcessInstanceService bpmnProcessInstanceService) {
        this.bpmnProcessInstanceService = bpmnProcessInstanceService;
    }

    // TODO: Temp for initial testing - REMOVE


    @GetMapping
    public ResponseEntity<List<ProcessInstanceDto>> getAll(
            //@RequestParam(required = false, name = "page", defaultValue = "0") int page,
            //@RequestParam(required = false, name = "size", defaultValue = "1000") int size,
            //@RequestParam(required = false, name = "sortField", defaultValue = "createdAt") String sortField,
            //@RequestParam(required = false, name = "direction", defaultValue = "DESC") String direction
    ) {
        // TODO: Add pagination
        final List<ProcessInstanceDto> processInstanceDtos = bpmnProcessInstanceService.findAll();
        return ResponseEntity.ok(processInstanceDtos);
    }

    @GetMapping(path="{id}/activeTask")
    public ResponseEntity<TaskDto> getActiveTask (@PathVariable("id") String processInstanceId) {
        TaskDto activeTask = null;
        try {
            activeTask = bpmnProcessInstanceService.getActiveTaskDto(processInstanceId);
        } catch (ResourceNotFoundException e) {
            log.info("No active task found for processId {}", processInstanceId);
        }
        return ResponseEntity.ok(activeTask);

    }

    @GetMapping(path="/{id}/steps")
    public ResponseEntity<List<TaskDto>> getStepList(@PathVariable("id") String processInstancedId) {
        return ResponseEntity.ok( bpmnProcessInstanceService.getStepList(processInstancedId));
    }

    @PostMapping(path="/create/{processName}/{businessKey}")
    public ResponseEntity<ProcessInstanceDto> startNewWorkflow (@PathVariable("processName") String processName,
                                                       @PathVariable("businessKey") String businessKey) {
        return ResponseEntity.ok( bpmnProcessInstanceService.startNewWorkflow(processName, businessKey));
    }

    @PatchMapping(path="/{id}/completeTask/{taskId}")
    public ResponseEntity completeTask(@PathVariable("id") String processInstanceId,
                                       @PathVariable("taskId") String taskToComplete,
                                       @RequestBody Map<String, Object> variables) {
        bpmnProcessInstanceService.completeUserTask(processInstanceId, taskToComplete, variables);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path="/{id}")
    public ResponseEntity cancelProcessInstance(@PathVariable("id") String processInstanceId) {
        bpmnProcessInstanceService.cancelProcessInstance(processInstanceId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(path="/{id}/sendEvent/{eventName}")
    public ResponseEntity sendEvent(@PathVariable("id") String processInstanceId,
                                    @PathVariable("eventName") String eventName,
                                    @RequestBody(required = false) Map<String, Object> variables) {
        bpmnProcessInstanceService.sendMessageToProcessInstance(processInstanceId, eventName, variables);
        return ResponseEntity.noContent().build();

    }
    @GetMapping(path="{id}/variables")
    public ResponseEntity<Map<String, Object>> getVariables(@PathVariable("id") String processInstanceId) {
        return ResponseEntity.ok(bpmnProcessInstanceService.getInstanceVariables(processInstanceId));
    }


}
