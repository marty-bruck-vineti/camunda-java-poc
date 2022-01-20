package com.vineti.camundajavapoc.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.impl.pvm.runtime.ExecutionImpl;
import org.camunda.bpm.engine.variable.impl.VariableMapImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Slf4j
public class JmsWorkflowMessageAdapterTest {

    @Autowired
    JmsTestListener jmsTestListener;

    @Test
    public void testExecute() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "yo");
        String json  = new ObjectMapper().writeValueAsString(values);
        JmsWorkflowMessageAdapter adapter = new JmsWorkflowMessageAdapter();
        DelegateExecution execution = new TestDelegateExecution("29", "my-process-instance", "an_activity", values);


        adapter.execute(execution);

        jmsTestListener.getLatch().await(3000, TimeUnit.MILLISECONDS);
        assertEquals(0, jmsTestListener.getLatch().getCount());
    }

    class TestDelegateExecution extends ExecutionImpl {
        private String myProcessInstanceId;
        private String myProcessBusinessKey;
        private String myCurrentActivityId;
        private Map<String, Object> myVariables;
        public TestDelegateExecution(String myProcessInstanceId, String myProcessBusinessKey,
                                         String myCurrentActivityId, Map<String, Object> myVariables) {
            this.myProcessInstanceId = myProcessInstanceId;
            this.myProcessBusinessKey = myProcessBusinessKey;
            this.myCurrentActivityId = myCurrentActivityId;
            this.myVariables = myVariables;
        }

        @Override
        public String getProcessInstanceId() {
            return myProcessInstanceId;
        }

        @Override
        public String getProcessBusinessKey() {
            return myProcessBusinessKey;
        }

        @Override
        public String getCurrentActivityId() {
            return myCurrentActivityId;
        }

        @Override
        public VariableMapImpl getVariables() {
            return new VariableMapImpl(myVariables);
        }
    }
}

