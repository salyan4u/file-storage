package ru.solyanin.filestorage.util;

import org.springframework.data.domain.Sort;

import javax.validation.constraints.NotNull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SortUtil {

    public static Sort parseSort(
            @NotNull String sort,
            @NotNull String patternRegex,
            @NotNull String fieldSeparator) {
        return parseSort(sort,
                Pattern.compile(patternRegex),
                fieldSeparator);
    }

    public static Sort parseSort(
            @NotNull String sort,
            @NotNull Pattern pattern,
            @NotNull String fieldSeparator) {
        if (fieldSeparator.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Sort fullSort = null;
        Matcher matcher = pattern.matcher(sort + fieldSeparator);
        while (matcher.find()) {
            Sort newCriteria = Sort.by(Sort.Direction.fromString(matcher.group(3)), matcher.group(1));
            fullSort = fullSort == null
                    ? newCriteria
                    : fullSort.and(newCriteria);
        }
        return fullSort;
    }
}