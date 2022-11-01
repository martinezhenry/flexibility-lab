package com.hvs.flexibility.event.outbound.contracts;

public interface IFormatPublisher {

    void publishMappedProfit(String profit, String status);
    void requestMappedProfit(String profit, String status);

}
