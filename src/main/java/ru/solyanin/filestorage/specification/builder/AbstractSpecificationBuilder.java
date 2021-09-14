package ru.solyanin.filestorage.specification.builder;

import org.springframework.data.jpa.domain.Specification;
import ru.solyanin.filestorage.specification.AbstractSpecification;
import ru.solyanin.filestorage.view.SearchCriteria;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class AbstractSpecificationBuilder<T> {
    private final List<SearchCriteria> filter;
    private String levelSeparator;

    public AbstractSpecificationBuilder() {
        levelSeparator = "_";
        filter = new LinkedList<>();
    }

    public AbstractSpecificationBuilder<T> levelSeparator(String levelSeparator) {
        this.levelSeparator = levelSeparator;
        return this;
    }

    public AbstractSpecificationBuilder<T> with(String key, String operation, String value) {
        filter.add(new SearchCriteria(key, operation, value));
        return this;
    }

    public AbstractSpecificationBuilder<T> with(SearchCriteria criteria) {
        filter.add(criteria);
        return this;
    }

    public Specification<T> build() {
        if (filter.size() == 0) {
            return null;
        }

        List<Specification<T>> specs = filter.stream()
                .map(c -> new AbstractSpecification<T>(c, levelSeparator) {
                })
                .collect(Collectors.toList());

        Specification<T> result = specs.get(0);

        for (int i = 1; i < specs.size(); i++) {
            result = Specification.where(result)
                    .and(specs.get(i));
        }
        return result;
    }
}