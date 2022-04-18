package com.elo.elo_calculation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.elo.elo_calculation.impl.entity")
public class EloCalculationApplication {

    public static void main(String[] args) {
        SpringApplication.run(EloCalculationApplication.class, args);
    }

}
