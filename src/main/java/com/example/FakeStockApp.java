package com.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.AsyncRestTemplate;

import java.util.Random;

@Component
@EnableScheduling
@EnableAutoConfiguration
public class FakeStockApp {

    private AsyncRestTemplate restTemplate = new AsyncRestTemplate();

    private final Random random = new Random();

    private Double stockPrice = random.nextDouble() * 800;

    @Value("${app.url}")
    private String url;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(FakeStockApp.class);
        app.setWebEnvironment(false);
        app.run(args);
    }

    @Scheduled(fixedRate = 1000)
    @Async
    public void tick() {
        stockPrice = newStockPrice(stockPrice);
        HttpEntity<Double> requestEntity = new HttpEntity<>(stockPrice);
        System.out.println("Sending stock price " + stockPrice);
        restTemplate.put(url, requestEntity);
    }

    private Double newStockPrice(Double lastPrice) {
        return lastPrice * (0.95  + (0.1 * random.nextDouble()));
    }

}
