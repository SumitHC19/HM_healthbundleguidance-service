package com.alight;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ServletComponentScan
@ComponentScan({ "com.alight" })
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        logger.info("Starting Health Bundle Guidance Service...");
        try {
            SpringApplication.run(Application.class, args);
            logger.info("Health Bundle Guidance Service started successfully");
        } catch (Exception e) {
            logger.error("Failed to start Health Bundle Guidance Service", e);
            throw e;
        }
    }
}
