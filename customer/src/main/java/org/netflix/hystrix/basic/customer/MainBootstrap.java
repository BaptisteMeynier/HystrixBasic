package org.netflix.hystrix.basic.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MainBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(MainBootstrap.class, args);
    }
}
