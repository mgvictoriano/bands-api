package com.mgvictoriano.bandsapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BandsApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BandsApiApplication.class, args);
    }
}
