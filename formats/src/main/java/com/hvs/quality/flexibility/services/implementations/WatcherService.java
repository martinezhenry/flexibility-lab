package com.hvs.quality.flexibility.services.implementations;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.devtools.filewatch.FileSystemWatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.Duration;
import java.util.HashMap;

@Service
@Slf4j
public class WatcherService {

    private final String folderPath = "./schemas";
    @Getter
    private final SchemaChangeListener schemaChangeListener;

    public WatcherService() throws IOException {
        this.schemaChangeListener = new SchemaChangeListener();
    }


    @Bean
    public FileSystemWatcher fileSystemWatcher() throws IOException {
        FileSystemWatcher fileSystemWatcher = new FileSystemWatcher(true, Duration.ofMillis(5000L), Duration.ofMillis(3000L));
        fileSystemWatcher.addSourceDirectory(new File(folderPath));
        fileSystemWatcher.addListener(this.schemaChangeListener);
        fileSystemWatcher.start();
        log.info("started fileSystemWatcher");
        return fileSystemWatcher;
    }

    @PreDestroy
    public void onDestroy() throws Exception {
        fileSystemWatcher().stop();
    }

}
