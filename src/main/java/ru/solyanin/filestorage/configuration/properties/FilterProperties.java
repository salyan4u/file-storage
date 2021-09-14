package ru.solyanin.filestorage.configuration.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "pagination.filter")
public class FilterProperties {
    private String arraySeparator;
    private String fieldSeparator;
    private String levelSeparator;
    private String pattern;

    public FilterProperties(@Value("${pagination.filter.array-separator}") String arraySeparator,
                            @Value("${pagination.filter.field-separator}") String fieldSeparator,
                            @Value("${pagination.filter.level-separator}") String levelSeparator,
                            @Value("${pagination.filter.pattern}") String pattern) {
        this.arraySeparator = arraySeparator;
        this.fieldSeparator = fieldSeparator;
        this.levelSeparator = levelSeparator;
        this.pattern = pattern;
    }
}