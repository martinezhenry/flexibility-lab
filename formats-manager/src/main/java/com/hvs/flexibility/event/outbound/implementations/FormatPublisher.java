package com.hvs.flexibility.event.outbound.implementations;

import com.hvs.flexibility.event.outbound.contracts.IFormatPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FormatPublisher implements IFormatPublisher {

    private RabbitTemplate rabbitTemplate;

    public FormatPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
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
}
