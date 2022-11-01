package com.hvs.quality.flexibility.controllers.implementations;

import com.hvs.quality.flexibility.services.implementations.SchemaChangeListener;
import com.hvs.quality.flexibility.services.implementations.WatcherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class FormatController {


    private WatcherService watcherService;

    public FormatController(WatcherService watcherService) {
        this.watcherService = watcherService;
    }


    @GetMapping(value = "format/{format-id}")
    public String getFormat(@PathVariable("format-id") String formatId) {
        log.info("searching schema with id: {}", formatId);
        var schema = "";
        try {
            schema = this.watcherService.getSchemaChangeListener().getSchemas().get(formatId.concat(".json"));
            if (schema == null) {
                log.info("schema with id {} not found", formatId);
                throw new SchemaNotFoundException();
            }
            log.info("schema found");
        } catch (NullPointerException e) {
            throw new SchemaNotFoundException();
        }

        return schema;

    }

}
