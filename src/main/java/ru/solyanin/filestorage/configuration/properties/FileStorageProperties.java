package ru.solyanin.filestorage.configuration.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {
    private final String servicePath;

    public FileStorageProperties(@Value("${file.service-path}") String servicePath) {
        this.servicePath = servicePath;
    }
}