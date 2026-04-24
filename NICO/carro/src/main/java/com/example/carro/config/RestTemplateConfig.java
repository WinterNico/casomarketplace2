package com.example.carro.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration // 1. Le dice a Spring: "Lee este archivo cuando arranques"
public class RestTemplateConfig {

    @Bean // 2. Le dice a Spring: "Crea este objeto y guárdalo para cuando alguien use @Autowired"
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}