package ru.solyanin.filestorage.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.solyanin.filestorage.view.SearchCriteria;

import javax.persistence.criteria.*;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class AbstractSpecification<T> implements Specification<T> {

    private final SearchCriteria criteria;
    private final String levelSeparator;

    public AbstractSpecification(SearchCriteria searchCriteria) {
        criteria = searchCriteria;
        levelSeparator = "_";
    }

    public AbstractSpecification(SearchCriteria searchCriteria, String levelSeparator) {
        criteria = searchCriteria;
        this.levelSeparator = levelSeparator;
    }

    @Override
    public Predicate toPredicate
            (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Path path = null;
        query.distinct(true);
        String[] levels = criteria.getKey().split(levelSeparator);
        if (levels.length > 1) {
            Join chain = null;
            for (int i = 0; i < levels.length - 1; i++) {
                if (chain == null) chain = root.join(levels[i], JoinType.LEFT);
                else chain = chain.join(levels[i], JoinType.LEFT);
            }
            path = chain.get(levels[levels.length - 1]);
        } else {
            path = root.get(criteria.getKey());
        }
        return getPredicate(builder, path, criteria.getOperation().toUpperCase(Locale.ROOT), criteria.getValue());
    }

    private Predicate getPredicate(CriteriaBuilder builder,
                                   Path path,
                                   String operation,
                                   String value) {
        Predicate result = null;
        switch (operation) {
            case "::": {
                result = getEqualPredicate(builder, path, value);
                break;
            }
            case ">>": {
                result = builder.greaterThan(
                        path, value);
                break;
            }
            case ">:": {
                result = builder.greaterThanOrEqualTo(
                        path, value);
                break;
            }
            case "<<": {
                result = builder.lessThan(
                        path, value);
                break;
            }
            case "<:": {
                result = builder.lessThanOrEqualTo(
                        path, value);
                break;
            }
            case "!:": {
                result = builder.not(getEqualPredicate(builder, path, value));
                break;
            }
        }
        return result;
    }

    private Predicate getEqualPredicate(CriteriaBuilder builder, Path path, String value) {
        Predicate result;
        List<String> values = Arrays.asList(value.split("\\|"));
        Class javaType = path.getJavaType();
        if (values.size() == 1) {
            if ("null".equalsIgnoreCase(values.get(0))) {
                result = builder.isNull(path);
            } else {
                if (String.class == javaType) {
                    if (values.get(0).startsWith("%") || values.get(0).endsWith("%")) {
                        result = builder.like(builder.lower(path), values.get(0).toLowerCase());
                    } else {
                        result = builder.equal(builder.lower(path), values.get(0).toLowerCase());
                    }
                } else {
                    result = builder.equal(path, parseByFieldType(javaType, value));
                }
            }
        } else {
            if (String.class == javaType) {
                Path finalPath = path;
                result = builder.or((Predicate[]) values.stream()
                        .map(val -> builder.like(builder.lower(finalPath),
                                "%" + val.toLowerCase() + "%"))
                        .toArray(Predicate[]::new));
            } else {
                List<Object> parsedValues = values.stream().map(val -> parseByFieldType(javaType, val)).collect(Collectors.toList());
                result = builder.in(path).value(parsedValues);
            }
        }
        return result;
    }

    private Object parseByFieldType(Class fieldType, String value) {
        Object result;
        if (Boolean.class == fieldType) {
            result = Boolean.parseBoolean(value);
        } else if (UUID.class == fieldType) {
            result = UUID.fromString(value);
        } else if (Long.class == fieldType) {
            result = Long.parseLong(value);
        } else if (Integer.class == fieldType) {
            result = Integer.parseInt(value);
        } else if (Float.class == fieldType) {
            result = Float.parseFloat(value);
        } else {
            result = value;
        }
        return result;
    }
}