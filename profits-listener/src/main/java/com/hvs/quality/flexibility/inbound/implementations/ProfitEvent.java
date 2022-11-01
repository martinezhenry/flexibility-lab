package com.hvs.quality.flexibility.inbound.implementations;

import com.hvs.quality.flexibility.inbound.contracts.IProfitEvent;
import com.hvs.quality.flexibility.services.contracts.IProfitService;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
public class ProfitEvent implements IProfitEvent {

    private IProfitService profitService;

    public ProfitEvent(IProfitService profitService) {
        this.profitService = profitService;
    }


    @Override
    @RabbitListener(queuesToDeclare = @Queue("profit-request-queue"), concurrency = "10")
    public void profitListener(String profit, @Header(value = "format-id", required = false, defaultValue = "1") String formatId) {
        profitService.processProfit(profit, formatId);
    }
}
