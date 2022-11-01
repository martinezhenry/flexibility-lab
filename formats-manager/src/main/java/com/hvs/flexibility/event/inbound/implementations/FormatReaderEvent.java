package com.hvs.flexibility.event.inbound.implementations;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hvs.flexibility.event.inbound.contracts.IFormatReaderEvent;
import com.hvs.flexibility.event.outbound.contracts.IFormatPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicInteger;

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
    @RabbitListener(queuesToDeclare = { @Queue(value = "formats-to-process-queue", durable = "false") }, concurrency = "10")
    public void processFormatReader(String format, @Header(value = "format-id", required = true, defaultValue = "1") String formatId) {
        log.info("event received, format: {}, formatId: {}", format, formatId);
        log.info("searching schema for format id {}", formatId);
        this.webClient.get()
                .uri("format/".concat(formatId))
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    log.error("schema not found");
                    this.formatPublisher.requestMappedProfit(format, "unprocessed");

                    return Mono.empty();
                })

                .bodyToMono(String.class)

                /*.flatMap(schema -> {
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
                })*/
                .doOnError(throwable -> {

                    //log.error("ERROR: {}", throwable.getMessage());
                    log.error("unprocessed schema");
                    this.formatPublisher.requestMappedProfit(format, "unprocessed");

                })
                .doOnSuccess(schema -> {
                    log.info("schema: {}", schema);
                    if (!schema.equals("{ \"msg\": \"schema not found\" }")) {
                    var json = this.gson.fromJson(schema, JsonObject.class);
                    var output = new JsonObject();
                    AtomicInteger countNull = new AtomicInteger();
                    AtomicInteger count = new AtomicInteger();
                    json.entrySet().parallelStream().forEach(element -> {
                        var schemaValue = element.getValue().getAsString();
                        var schemaKey = element.getKey();
                        var formatJson = this.gson.fromJson(format, JsonObject.class);
                        var profitValue = formatJson.get(schemaKey);
                        if (profitValue == null) {
                            countNull.getAndIncrement();
                        }
                        output.add(schemaValue, profitValue);
                        count.getAndIncrement();

                    });

                    if (output.size() == 0 || count.get() == countNull.get()) {
                        log.error("schema is present, but format received invalid");
                        this.formatPublisher.requestMappedProfit(format, "unprocessed");
                    } else {
                        log.info("output: {}", output);

                        log.info("profitJson: {}", output);
                        String profitJsonStr = this.gson.toJson(output);
                        //this.formatPublisher.publishMappedProfit(this.gson.toJson(profitJson), "processed");
                        //return Mono.just(profitJsonStr);


                        this.formatPublisher.requestMappedProfit(profitJsonStr, "processed");
                    }


                    }
                })
                .subscribe();
    }
}
