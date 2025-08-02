package com.pack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ServiceCatalogServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceCatalogServiceApplication.class, args);
	}

}
