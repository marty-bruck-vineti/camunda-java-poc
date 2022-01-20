package com.vineti.camundajavapoc.jms;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.vineti.camundajavapoc.message.JmsWorkflowMessage;
import lombok.Getter;
import lombok.Setter;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)

@Getter
@Setter
public class JmsTaskStartMessage extends JmsWorkflowMessage {
    String activityName;
    String responseListenerQueue;
    String completionEventName;

}
