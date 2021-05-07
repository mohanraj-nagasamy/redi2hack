package com.mohan.redi2hack.init;

import com.mohan.redi2hack.service.RedisDBService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class CustomerInitializer implements CommandLineRunner {

    private final RedisDBService redisDBService;

    @Override
    public void run(String... args) {
        redisDBService.resetDatabase();
    }

}
