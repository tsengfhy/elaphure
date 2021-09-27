package com.tsengfhy.entry;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
        info = @Info(
                title = "Elaphure RESTful API",
                version = "1.0.0-SNAPSHOT",
                contact = @Contact(
                        name = "Tsengfhy",
                        email = "tsengfhy@gmail.com",
                        url = "https://blog.tsengfhy.com"
                )
        )
)
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

