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
                .baseUrl("http://localhost:8080")
                .build();
    }


    @Override
    public void processProfit(String profit, String formatId) {
        this.webClient.post()
                .uri("scan/process")
                .body(profit, String.class)
                .header("format-id", formatId)
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    log.error("profit ERROR, status: {}", response.rawStatusCode());
                    return Mono.error(new Throwable("Error"));
                })
                .bodyToMono(Profit.class);

    }
}
