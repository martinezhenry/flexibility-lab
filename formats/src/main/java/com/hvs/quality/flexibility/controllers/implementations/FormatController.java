package com.hvs.quality.flexibility.controllers.implementations;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FormatController {


    private String value = "{\n" +
            "\"a\": \"fecha\",\n" +
            "\"b\": \"clientId\",\n" +
            "\"c\": \"value\",\n" +
            "\"d\": \"period\",\n" +
            "\"e\": \"clientType\"\n" +
            "}";

    @GetMapping(value = "format/{format-id}")
    public String getFormat(@PathVariable("format-id") String formatId){
        return value;
    }

}
