package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Random;

@Component
public class FakeStockApp {

    private final Random random = new Random();

    private Double stockPrice = random.nextDouble() * 800;

    @Autowired
    private WebApp webApp;

    @Scheduled(fixedRate = 1000)
    public void tick() throws IOException {
        stockPrice = newStockPrice(stockPrice);
        System.out.println("Generating new stock price: " + stockPrice);
        webApp.notifyWebSockets(stockPrice);
    }

    private Double newStockPrice(Double lastPrice) {
        return lastPrice * (0.95  + (0.1 * random.nextDouble()));
    }

}
