package ru.solyanin.filestorage.util;

import org.springframework.data.jpa.domain.Specification;
import ru.solyanin.filestorage.specification.builder.AbstractSpecificationBuilder;

import javax.validation.constraints.NotNull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpecificationUtil {
    public static <T> Specification<T> parseSpecification(
            @NotNull String search,
            @NotNull String patternRegex,
            @NotNull String fieldSeparator,
            @NotNull String levelSeparator) {
        if (fieldSeparator == null || fieldSeparator.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Pattern pattern = Pattern.compile(patternRegex);
        return parseSpecification(search, pattern, fieldSeparator, levelSeparator);
    }

    public static <T> Specification<T> parseSpecification(
            @NotNull String search,
            @NotNull Pattern pattern,
            @NotNull String fieldSeparator,
            @NotNull String levelSeparator) {
        if (fieldSeparator == null || fieldSeparator.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Specification<T> spec;
        AbstractSpecificationBuilder<T> builder = new AbstractSpecificationBuilder<T>();
        Matcher matcher = pattern.matcher(search + fieldSeparator);
        while (matcher.find()) {
            builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
        }

        spec = builder.levelSeparator(levelSeparator).build();
        return spec;
    }
}