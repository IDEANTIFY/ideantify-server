package com.github.ideantifyserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class IdeantifyServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(IdeantifyServerApplication.class, args);
    }

}
