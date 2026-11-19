package com.labflow.labflow_lite;

import org.springframework.boot.SpringApplication;

public class TestLabflowLiteApplication {

	public static void main(String[] args) {
		SpringApplication.from(LabflowLiteApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
