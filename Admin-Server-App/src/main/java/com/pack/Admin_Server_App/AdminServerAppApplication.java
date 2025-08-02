package com.pack.Admin_Server_App;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@EnableAdminServer
public class AdminServerAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdminServerAppApplication.class, args);
	}

}
