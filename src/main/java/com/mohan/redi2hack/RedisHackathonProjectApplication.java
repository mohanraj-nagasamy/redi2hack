package com.mohan.redi2hack;

import com.mohan.redi2hack.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RedisHackathonProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisHackathonProjectApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(CustomerRepository customerRepository) {
        return args -> {

        };
    }
}
