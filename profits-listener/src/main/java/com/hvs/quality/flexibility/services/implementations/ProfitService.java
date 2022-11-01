package com.hvs.quality.flexibility.services.implementations;

import com.hvs.quality.flexibility.models.Profit;
import com.hvs.quality.flexibility.services.contracts.IProfitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ProfitService implements IProfitService {


    private WebClient webClient;

    public ProfitService() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8083")
                .build();
    }


    @Override
    public void processProfit(String profit, String formatId) {
        log.info("send profit to scan, formatId: {}, profit: {}", formatId, profit);
        this.webClient.post()
                .uri("scan/process")
                .bodyValue(profit)
                .header("format-id", formatId)
                .header("content-type", "application/json")
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    ///log.error("profit ERROR, status: {}, ", response.rawStatusCode());
                    return Mono.empty();
                })
                .bodyToMono(Profit.class)
                .doOnSuccess(p -> log.info("profit event sent"))
                .doOnError(p -> log.info("profit event sent"))
                .subscribe();

    }
}
