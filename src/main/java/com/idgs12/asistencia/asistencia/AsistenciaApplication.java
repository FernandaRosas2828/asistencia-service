package com.idgs12.asistencia.asistencia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class AsistenciaApplication {

	public static void main(String[] args) {
		SpringApplication.run(AsistenciaApplication.class, args);
	}

}
