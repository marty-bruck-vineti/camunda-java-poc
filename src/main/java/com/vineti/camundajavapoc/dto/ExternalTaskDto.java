package com.vineti.camundajavapoc.dto;

public class ExternalTaskDto extends TaskDto {
    private final String responseQueue;

    public ExternalTaskDto(String id, String name, String responseQueue) {
        super(id, name);
        this.responseQueue = responseQueue;
    }
}
