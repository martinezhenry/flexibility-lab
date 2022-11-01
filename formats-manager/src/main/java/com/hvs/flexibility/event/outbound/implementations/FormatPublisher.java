package com.hvs.flexibility.event.outbound.implementations;

import com.hvs.flexibility.event.outbound.contracts.IFormatPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class FormatPublisher implements IFormatPublisher {

    private RabbitTemplate rabbitTemplate;
    private WebClient webClient;

    public FormatPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8083")
                .build();
    }

    @Override
    public void publishMappedProfit(String profit, String status) {
        log.info("publishing profit: {}", profit);
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeader("status", status);
        MessageConverter messageConverter = new SimpleMessageConverter();
        Message message = messageConverter.toMessage(profit, messageProperties);

        this.rabbitTemplate.convertAndSend("processed-formats-queue", message);
    }

    public void requestMappedProfit(String profit, String status) {
        log.info("request profit: {}", profit);
        this.webClient.post()
                .uri("profit/process")
                .header("status", status)
                .header("content-type", "application/json")
                .bodyValue(profit)
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    log.info("profit unprocessed sent");
                    return  Mono.empty();
                })
                .bodyToMono(String.class)
                .subscribe();

    }
}
