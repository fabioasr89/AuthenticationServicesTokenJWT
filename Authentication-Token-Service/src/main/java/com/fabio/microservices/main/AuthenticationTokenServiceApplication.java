package com.fabio.microservices.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages= {"com.fabio.microservices.controller","com.fabio.microservices.security","com.fabio.microservices.configuration","com.fabio.microservices.exception","com.fabio.microservices.jwt"})
@EntityScan(basePackages= {"com.fabio.microservices.model"})
@EnableJpaRepositories(basePackages= {"com.fabio.microservices.repository"})
@EnableConfigurationProperties
public class AuthenticationTokenServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthenticationTokenServiceApplication.class, args);

	}

}
