package com.vineti.camundajavapoc.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)

@Getter
@Setter
@AllArgsConstructor
// TODO: Add active flag?
public class ProcessInstanceDto {
    private String processDefinitionName;
    private String businessKey;
    private String id;
    private TaskDto currentTaskDto;
}
