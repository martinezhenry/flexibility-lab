package com.hvs.quality.flexibility.services.implementations;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.devtools.filewatch.ChangedFile;
import org.springframework.boot.devtools.filewatch.ChangedFiles;
import org.springframework.boot.devtools.filewatch.FileChangeListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Set;

@Slf4j
public class SchemaChangeListener implements FileChangeListener {

    @Getter
    private final HashMap<String, String> schemas;

    public SchemaChangeListener() throws IOException {
        this.schemas = new HashMap<>();
        loadInitialSchema();
    }



    public void loadInitialSchema() throws IOException {
        var paths = Files.list(Paths.get("./schemas"));
        paths.forEach(path -> {
            try {
                if (!Files.isDirectory(path)) {
                    var schema = Files.readString(path);
                    this.schemas.put(path.getFileName().toString(), schema);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


        log.info("schemas loaded: {}", this.schemas);
    }


    @Override
    public void onChange(Set<ChangedFiles> changeSet) {
        log.info("change detected");
        for (ChangedFiles files:changeSet) {
            for (ChangedFile file:files.getFiles()) {
                log.info("change type: {}, filename: {}", file.getType(), file.getFile().getName());
                try {
                    if (file.getType().equals(ChangedFile.Type.ADD) || file.getType().equals(ChangedFile.Type.MODIFY)) {
                        var schemaContent = Files.readString(Paths.get(file.getFile().getPath()));
                        this.schemas.put(file.getFile().getName(), schemaContent);

                    } else {
                        this.schemas.remove(file.getFile().getName());
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }

        log.info("schemas changed: {}", this.schemas);
    }
}
