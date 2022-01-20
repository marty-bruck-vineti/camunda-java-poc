package com.vineti.camundajavapoc.message;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)

@Getter
@Setter
public class JmsWorkflowMessage {
    private String processInstanceId;
    private String processName;
    private String activityId;
    private Map<String, Object> variables;
}
