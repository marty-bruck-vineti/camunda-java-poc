package com.vineti.camundajavapoc.jms;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class TaskCompleteMessage {
    String processInstanceId;
    String activeTaskId;
    String completionEventName;
    Map<String, Object> variables;
}
