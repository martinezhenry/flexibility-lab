package com.hvs.quality.flexibility;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class FormatApplication {


    public static void main(String[] args) {
        SpringApplication.run(FormatApplication.class, args);
    }


}
