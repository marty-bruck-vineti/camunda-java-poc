package com.vineti.camundajavapoc;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableProcessApplication
@EnableJms
public class CamundaJavaPocApplication {

    public static void main(String[] args) {
        SpringApplication.run(CamundaJavaPocApplication.class, args);
    }

}
