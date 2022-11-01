package com.hvs.flexibility.event.inbound.implementations;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hvs.flexibility.event.inbound.contracts.IFormatReaderEvent;
import com.hvs.flexibility.event.outbound.contracts.IFormatPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class FormatReaderEvent implements IFormatReaderEvent {

    private final WebClient webClient;
    private final Gson gson;
    private final IFormatPublisher formatPublisher;

    public FormatReaderEvent(IFormatPublisher formatPublisher) {
        this.formatPublisher = formatPublisher;
        this.gson = new Gson();
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8082")
                .build();

    }

    @Override
    @RabbitListener(queuesToDeclare = { @Queue("format-reader-topic") }, concurrency = "10")
    public void processFormatReader(String format, @Header(value = "format-id", required = false, defaultValue = "1") String formatId) {
        this.webClient.get()
                .uri("format/".concat(formatId))
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(schema -> {
                    log.info("schema: {}", schema);
                    var json = this.gson.fromJson(schema, JsonObject.class);
                    var output = new JsonObject();
                    json.entrySet().parallelStream().forEach(element -> {
                        var schemaValue = element.getValue().getAsString();
                        var schemaKey = element.getKey();
                        var formatJson = this.gson.fromJson(format, JsonObject.class);
                        var profitValue = formatJson.get(schemaKey);
                        output.add(schemaValue, profitValue);

                    });
                    log.info("output: {}", output);
                    return Mono.just(output);

                })
                .flatMap(profitJson -> {
                    log.info("profitJson: {}", profitJson);
                    String profitJsonStr = this.gson.toJson(profitJson);
                    //this.formatPublisher.publishMappedProfit(this.gson.toJson(profitJson), "processed");
                    return Mono.just(profitJsonStr);
                }).doOnError(throwable -> {
                    log.error("ERROR: {}", throwable.getMessage());
                    this.formatPublisher.publishMappedProfit(format, "unprocessed");

                })
                .doOnSuccess(json -> {
                    this.formatPublisher.publishMappedProfit(json, "processed");
                })
                .subscribe();
    }
}
