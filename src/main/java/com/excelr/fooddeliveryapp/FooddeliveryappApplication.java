package com.excelr.fooddeliveryapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;

@Slf4j

@EnableCaching
@SpringBootApplication
public class FooddeliveryappApplication {

	private static final Logger LOGGER= LoggerFactory.getLogger(FooddeliveryappApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(FooddeliveryappApplication.class, args);
		LOGGER.info("FOOD DELIVERY APPLICATION IS RUNNING NOW !!!");
	}

	
     //Configure ObjectMapper bean for JSON serialization/deserialization
	@Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // <--- ENSURE THIS LINE IS PRESENT
        // You might also want to configure other serialization features
        // objectMapper.findAndRegisterModules(); // This can register all found modules
        return objectMapper;
    }
}
