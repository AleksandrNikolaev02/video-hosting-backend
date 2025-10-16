package com.example.camunda;

import lombok.AllArgsConstructor;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.camunda.bpm.spring.boot.starter.event.PostDeployEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@AllArgsConstructor
@EnableProcessApplication
public class CamundaServiceApplication {
	private RuntimeService service;

	public static void main(String[] args) {
		SpringApplication.run(CamundaServiceApplication.class, args);
	}

	@EventListener
	public void processPostDeploy(PostDeployEvent event) {
		service.startProcessInstanceByKey("test_bpmn");
	}
}
