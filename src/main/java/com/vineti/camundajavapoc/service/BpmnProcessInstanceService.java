package com.vineti.camundajavapoc.service;

import com.vineti.camundajavapoc.dto.ProcessInstanceDto;
import com.vineti.camundajavapoc.dto.TaskDto;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.exception.NullValueException;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.runtime.MessageCorrelationResult;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.VariableInstance;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.impl.instance.FlowNodeImpl;
import org.camunda.bpm.model.bpmn.impl.instance.TaskImpl;
import org.camunda.bpm.model.bpmn.instance.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.ws.rs.BadRequestException;
import java.util.*;
import java.util.concurrent.Flow;

@Service
@Slf4j
public class BpmnProcessInstanceService {

    private final RuntimeService runtimeService; // camunda runtime service
    private final RepositoryService repositoryService; // camunda repository service
    private final TaskService taskService;
    private final HistoryService historyService;

    BpmnProcessInstanceService(@Autowired RuntimeService runtimeService,
                               @Autowired RepositoryService repositoryService,
                               @Autowired TaskService taskService,
                               @Autowired HistoryService historyService) {
        this.runtimeService = runtimeService;
        this.repositoryService = repositoryService;
        this.taskService = taskService;
        this.historyService = historyService;
    }

    public List<ProcessInstanceDto> findAll() {
        List<ProcessInstanceDto> processInstances = new ArrayList<>();
        List<ProcessInstance> allInstances = getAllProcessInstances();
        allInstances.forEach(pi -> {
            processInstances.add (new ProcessInstanceDto(
                    pi.getProcessDefinitionId(),
                    pi.getBusinessKey(),
                    pi.getProcessInstanceId(),
                    getActiveTaskDto(pi.getProcessInstanceId())));

        });
        return processInstances;
    }

    private List<ProcessInstance> getAllProcessInstances() {
        return runtimeService.createProcessInstanceQuery()
                .rootProcessInstances()
                .list();
    }

    public TaskDto getActiveTaskDto(String processInstanceId) {
        FlowNodeImpl activeTask = getActiveTask(processInstanceId);
        return activeTask == null ? null : new TaskDto(activeTask.getId(), activeTask.getName());
    }

    /**
     * First get the activeActivity ids, then look at the model in the repository to get the activity (task) info
     * @param processInstanceId - process instance of interest
     * @return - taskDto or null
     */
    public FlowNodeImpl getActiveTask(String processInstanceId) {
        try {
            // if there are no active tasks, just return a null. This is not an error condition
            List<String> activeActivityIds = runtimeService.getActiveActivityIds(processInstanceId);
            if (activeActivityIds.isEmpty()) {
                log.info("No active task found for process instance with id: {}.", processInstanceId);
                return null;
            }

            // first get the process instance - It should exist. If not, throw exception
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();
            if (processInstance == null) {
                throw new ResourceNotFoundException("no process instance found for process instance: " + processInstanceId);
            }

            // get the process model
            BpmnModelInstance bpmnModelInstance = repositoryService.getBpmnModelInstance(processInstance.getProcessDefinitionId());

            // use the process model to get the task definition. The task definition should exist. If not, throw exception
            FlowNodeImpl task = activeActivityIds.stream()
                    .map(id -> (FlowNodeImpl) bpmnModelInstance.getModelElementById(id))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("No active task was found for process instance: " + processInstanceId));
            return task;

        } catch( ProcessEngineException re) {
            log.error("Error trying to retrieve active task: {}", re.getMessage());
                return null;
        } catch (Exception e) {
            // convert any other exceptions to RNF
            log.error("Unable to retrieve active task for process instance: " + processInstanceId, e);
            throw new ResourceNotFoundException(e.getMessage() + " Process Instance: " + processInstanceId);
        }
    }

    // TODO: should this be in a separate service/controller?
    public void completeUserTask (String processInstanceId, String taskDefinitionKey, Map<String, Object> variables) {
        // taskService is instance of org.camunda.bpm.engine.TaskService
        String taskInternalId = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .list()
                .stream()
                .filter(task -> task.getTaskDefinitionKey().equals(taskDefinitionKey))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Could not find task with name %s on processInstanceId %s",
                                taskDefinitionKey,
                                processInstanceId))).getId();
        taskService.complete(taskInternalId, variables);
    }

    public void sendMessageToProcessInstance(String processInstanceId, String messageName, Map<String, Object> variables) {
        try {
            MessageCorrelationResult result = runtimeService.createMessageCorrelation(messageName)
                    .processInstanceId(processInstanceId)
                    .setVariables(variables)
                    .correlateWithResult();
            log.info(result.toString());
        } catch( MismatchingMessageCorrelationException e) {
            throw new BadRequestException("There is no task currently listening for this event. ", e);
        }
    }

    public List<TaskDto> getStepList(String processInstancedId) {
        List<TaskDto> steps = new ArrayList<>();
        FlowNodeImpl node = getActiveTask(processInstancedId);
        // first push current node
        steps.add(new TaskDto(node.getId(), node.getName()));

        // get all previous nodes
        node.getPreviousNodes()
            .list()
            .forEach(n -> steps.add(0, new TaskDto(n.getId(), n.getName())));
        return steps;

    }

    public ProcessInstanceDto startNewWorkflow(String processName, String businessKey) {
            ProcessInstance ni = runtimeService.startProcessInstanceByKey(processName, businessKey);
            String processInstanceId = ni.getProcessInstanceId();
            return new ProcessInstanceDto(processName, businessKey, ni.getProcessInstanceId(), getActiveTaskDto(processInstanceId));
    }

    public Map<String, Object> getInstanceVariables(String processInstanceId) {
        Map<String, Object> results = new HashMap<>();
        List<VariableInstance> variables = runtimeService.createVariableInstanceQuery()
                .processInstanceIdIn(processInstanceId)
                .orderByVariableName()
                .asc()
                .list();

        variables.forEach(h -> results.put(h.getName(), h.getValue()));
        return results;

    }

    public void cancelProcessInstance(String processInstanceId) {
        runtimeService.deleteProcessInstance(processInstanceId, "Process cancelled by user");
    }
}
