package com.august.gulimall.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GulimaillMemberApplication {

	public static void main(String[] args) {
		SpringApplication.run(GulimaillMemberApplication.class, args);
	}

}
