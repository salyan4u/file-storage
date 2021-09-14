package ru.solyanin.filestorage.configuration.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "pagination.sort")
public class SortProperties {
    private String pattern;
    private String fieldSeparator;

    public SortProperties(@Value("${pagination.sort.pattern}") String pattern,
                          @Value("${pagination.sort.field-separator}") String fieldSeparator) {
        this.pattern = pattern;
        this.fieldSeparator = fieldSeparator;
    }
}