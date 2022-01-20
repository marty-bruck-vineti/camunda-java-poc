package com.vineti.camundajavapoc;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.runtime.*;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.impl.instance.FlowNodeImpl;
import org.camunda.bpm.model.bpmn.impl.instance.TaskImpl;
import org.camunda.bpm.model.bpmn.instance.Event;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j

public class CamundaRuntimeServiceTest {
    @Autowired
    RepositoryService repositoryService;

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    FormService formService;

    @Autowired
    TaskService taskService;

    @Autowired
    HistoryService historyService;

    @Test
    public void testStartProcess() {
        ProcessInstance newInstance = runtimeService.startProcessInstanceByKey("poc-flow-1");
        assertNotNull(newInstance);
    }

    @Test
    public void testGetForm() throws IOException {
        InputStream fs = formService.getDeployedTaskForm("patient_demo_form");
        String content =  fs.readAllBytes().toString();
        log.info(content);
    }
    @Test
    public void testGetActiveTasksForProcess() {
        ProcessInstance newInstance = runtimeService.startProcessInstanceByKey("poc-flow-1");
        assertNotNull(newInstance);
        FlowNodeImpl activeTask = getActiveTask(newInstance);
        listNodeTree(activeTask);
    }

    /**
     * Run through the happy path of the job completing successfully
     */
    @Test
    public void testCompleteWorkflow() {
        // start process instance directly
        ProcessInstance newInstance = runtimeService.startProcessInstanceByKey("poc-flow-1");
        assertNotNull(newInstance);
        assertTrue(processInstanceActive(newInstance));

        // get and verify currently active task
        FlowNodeImpl activeTask = getActiveTask(newInstance);
        verifyActiveTask(activeTask, "Add Patient Demographics", newInstance);

        // complete the first task and move to the second
        Map<String, Object> patientDemographics = new HashMap<>();
        patientDemographics.put("last_name", "Alastname");
        completeUserTask(activeTask.getId(), newInstance.getId(), patientDemographics);

        // verify that this is the second task and move to the third
        activeTask = getActiveTask(newInstance);
        verifyActiveTask(activeTask, "Create Order", newInstance);
        completeUserTask(activeTask.getId(), newInstance.getId(), patientDemographics);

        // verify this is the third task and complete the workflow
        activeTask = getActiveTask(newInstance);
        verifyActiveTask(activeTask, "Submit Order", newInstance);
        completeUserTask(activeTask.getId(), newInstance.getId(), patientDemographics);

        // verify processInstance complete
        assertFalse(processInstanceActive(newInstance));
    }

    @Test
    public void testWorkflowWithSignal() {
        // start process instance directly
        ProcessInstance newInstance = runtimeService.startProcessInstanceByKey("poc-flow-1");
        assertNotNull(newInstance);
        assertTrue(processInstanceActive(newInstance));

        // get and verify currently active task
        FlowNodeImpl activeTask = getActiveTask(newInstance);
        verifyActiveTask(activeTask, "Add Patient Demographics", newInstance);

        // complete the first task and move to the second
        Map<String, Object> patientDemographics = new HashMap<>();
        patientDemographics.put("last_name", "Alastname");
        completeUserTask(activeTask.getId(), newInstance.getId(), patientDemographics);

        // verify that this is the second task
        activeTask = getActiveTask(newInstance);
        verifyActiveTask(activeTask, "Create Order", newInstance);
        assertTrue(processInstanceActive(newInstance));
        // generate the order cancelled event to end the workflow
        sendMessageToProcessInstance(newInstance, "user_cancelled_order");

        // verify processInstance complete
        assertFalse(processInstanceActive(newInstance));
    }
    @Test
    public void testWorkflowWithXorGateFlow1() {
        // start process instance directly
        ProcessInstance newInstance = runtimeService.startProcessInstanceByKey("poc-flow-with-gateway");
        assertNotNull(newInstance);
        assertTrue(processInstanceActive(newInstance));

        // get and verify currently active task
        FlowNodeImpl activeTask = getActiveTask(newInstance);
        verifyActiveTask(activeTask, "Add Patient Demographics", newInstance);

        // complete the first task and move to the second
        Map<String, Object> patientDemographics = new HashMap<>();
        patientDemographics.put("last_name", "Alastname");
        completeUserTask(activeTask.getId(), newInstance.getId(), patientDemographics);

        // verify that this is the second task
        activeTask = getActiveTask(newInstance);
        verifyActiveTask(activeTask, "Create Order", newInstance);
        assertTrue(processInstanceActive(newInstance));

        // complete the user task with the order location set to US, and verify it submits to US.
        Map<String, Object> orderDetails = new HashMap<>();
        orderDetails.put("order_location", "US");
        completeUserTask(activeTask.getId(), newInstance.getId(), orderDetails);

        // verify that this is the second task
        activeTask = getActiveTask(newInstance);
        verifyActiveTask(activeTask, "Send Order to US", newInstance);
        assertTrue(processInstanceActive(newInstance));

        // finally complete the user task and verify that the process instance is complete
        completeUserTask(activeTask.getId(), newInstance.getId(), new HashMap<>());
        assertFalse(processInstanceActive(newInstance));
    }
    @Test
    public void testWorkflowWithXorGateFlow2() {
        // start process instance directly
        ProcessInstance newInstance = runtimeService.startProcessInstanceByKey("poc-flow-with-gateway");
        assertNotNull(newInstance);
        assertTrue(processInstanceActive(newInstance));

        // get and verify currently active task
        FlowNodeImpl activeTask = getActiveTask(newInstance);
        verifyActiveTask(activeTask, "Add Patient Demographics", newInstance);

        // complete the first task and move to the second
        Map<String, Object> patientDemographics = new HashMap<>();
        patientDemographics.put("last_name", "Alastname");
        completeUserTask(activeTask.getId(), newInstance.getId(), patientDemographics);

        // verify that this is the second task
        activeTask = getActiveTask(newInstance);
        verifyActiveTask(activeTask, "Create Order", newInstance);
        assertTrue(processInstanceActive(newInstance));

        // complete the user task with the order location set to US, and verify it submits to US.
        Map<String, Object> orderDetails = new HashMap<>();
        orderDetails.put("order_location", "EUROPE");
        completeUserTask(activeTask.getId(), newInstance.getId(), orderDetails);

        // verify that this is the second task
        activeTask = getActiveTask(newInstance);
        verifyActiveTask(activeTask, "Send Order to Europe", newInstance);
        assertTrue(processInstanceActive(newInstance));

        // finally complete the user task and verify that the process instance is complete
        completeUserTask(activeTask.getId(), newInstance.getId(), new HashMap<>());
        assertFalse(processInstanceActive(newInstance));
    }

    @Test
    public void testGetProcessVariables() {
        // start process instance directly
        ProcessInstance newInstance = runtimeService.startProcessInstanceByKey("poc-flow-1");
        assertNotNull(newInstance);
        assertTrue(processInstanceActive(newInstance));

        // get and verify currently active task
        FlowNodeImpl activeTask = getActiveTask(newInstance);
        verifyActiveTask(activeTask, "Add Patient Demographics", newInstance);

        // complete the first task and move to the second
        Map<String, Object> patientDemographics = new HashMap<>();
        patientDemographics.put("last_name", "Alastname");
        patientDemographics.put("first_name", "Alastname");
        completeUserTask(activeTask.getId(), newInstance.getId(), patientDemographics);

        // verify that this is the second task and move to the third
        activeTask = getActiveTask(newInstance);
        Map<String, Object> orderDemographics = new HashMap<>();
        orderDemographics.put("order_location", "US");
        orderDemographics.put("order_date", "2021-30-Dec");

        verifyActiveTask(activeTask, "Create Order", newInstance);
        completeUserTask(activeTask.getId(), newInstance.getId(), orderDemographics);


        List<VariableInstance> variables = runtimeService.createVariableInstanceQuery()
                .processInstanceIdIn(newInstance.getProcessInstanceId())
                .orderByVariableName()
                .asc()
                .list();

        // verify processInstance complete
        assertFalse(variables.isEmpty());
    }

    private void sendMessageToProcessInstance(ProcessInstance processInstance, String messageName) {
        MessageCorrelationResult result = runtimeService.createMessageCorrelation(messageName)
                .processInstanceId(processInstance.getProcessInstanceId())
                .correlateWithResult();
        log.info(result.toString());
    }

    private FlowNodeImpl getActiveTask(ProcessInstance processInstance) {
        BpmnModelInstance bpmnModelInstance =
                repositoryService.getBpmnModelInstance(processInstance.getProcessDefinitionId());

        List<String> activeActivityIds = runtimeService.getActiveActivityIds(processInstance.getId());
        FlowNodeImpl task = activeActivityIds.stream()
                .map(id ->  (FlowNodeImpl) bpmnModelInstance.getModelElementById(id))
                .findFirst()
                .orElse(null);
        return task;
    }

    private void listNodeTree (FlowNodeImpl node) {
        log.info("=== On task: {} ", node.getName());
        log.info("==== Previous steps: ");
        if (node instanceof TaskImpl) {
            TaskImpl task = (TaskImpl) node;
            task.getPreviousNodes()
                    .list()
                    .forEach(u -> log.info("Step => {}", u.getName()));
        }

        else if (node instanceof Event) {
            Event event = (Event) node;
            event.getPreviousNodes()
                    .list()
                    .forEach(n -> log.info("Step => {}", n.getName()));
        }
    }

    private void verifyActiveTask(FlowNodeImpl activeTask, String expectedTask, ProcessInstance processInstance) {
        // verify instance is on the second task, then complete it
        activeTask = getActiveTask(processInstance);
        assertEquals(expectedTask, activeTask.getName());
        listNodeTree(activeTask);
    }

    private void completeUserTask (String taskName, String processInstanceId, Map<String, Object> variables) {

        // taskService is instance of org.camunda.bpm.engine.TaskService
        String taskId = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .list()
                .stream()
                .filter(task -> task.getTaskDefinitionKey().equals(taskName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        String.format("Could not find task with name %s on processInstanceId %s",
                                taskName,
                                processInstanceId))).getId();
        taskService.complete(taskId, variables);
    }

    private boolean processInstanceActive(ProcessInstance processInstance) {

        Execution found = runtimeService.createExecutionQuery()
                .executionId(processInstance.getProcessInstanceId())
                .active()
                .singleResult();
        return (found != null && !found.isEnded());
    }


}
