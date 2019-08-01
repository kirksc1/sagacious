package com.github.kirksc1.sagacious.sample;

import com.github.kirksc1.sagacious.Saga;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan( basePackageClasses = {Saga.class} )
@EnableJpaRepositories({"com.github.kirksc1.sagacious"})
public class ServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

}
