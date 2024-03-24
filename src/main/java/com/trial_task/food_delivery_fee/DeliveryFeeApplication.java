package com.trial_task.food_delivery_fee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * The main application class for the Delivery Fee service.
 */
@SpringBootApplication
@EnableScheduling
public class DeliveryFeeApplication {

	/**
	 * The main method that starts the Spring Boot application.
	 *
	 * @param args Command line arguments passed to the application.
	 */
	public static void main(String[] args) {
		SpringApplication.run(DeliveryFeeApplication.class, args);
	}

}
