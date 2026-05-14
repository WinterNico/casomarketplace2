package com.example.busqueda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class BusquedaApplication {

	public static void main(String[] args) {
		SpringApplication.run(BusquedaApplication.class, args);
	}

}
