package ru.solyanin.filestorage.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.solyanin.filestorage.configuration.properties.FilterProperties;
import ru.solyanin.filestorage.configuration.properties.SortProperties;
import ru.solyanin.filestorage.service.ifaces.PageableService;
import ru.solyanin.filestorage.util.SortUtil;
import ru.solyanin.filestorage.util.SpecificationUtil;

import java.util.UUID;
import java.util.regex.Pattern;

public abstract class AbstractPageableServiceImpl<T, R extends JpaRepository<T, UUID> & JpaSpecificationExecutor<T>> implements PageableService<T, R> {
    protected final R jpaSpecificationExecutor;
    private final FilterProperties filterProperties;
    private final SortProperties sortProperties;

    private final Pattern filterPattern;
    private final Pattern sortPattern;

    protected AbstractPageableServiceImpl(R jpaSpecificationExecutor,
                                          FilterProperties filterProperties,
                                          SortProperties sortProperties) {
        this.jpaSpecificationExecutor = jpaSpecificationExecutor;
        this.filterProperties = filterProperties;
        this.sortProperties = sortProperties;
        this.filterPattern = Pattern.compile(filterProperties.getPattern());
        this.sortPattern = Pattern.compile(sortProperties.getPattern());
    }

    public Page<T> getPage(
            int number,
            int size,
            String search,
            String sort) {
        if (search.isEmpty() && sort.isEmpty()) {
            return jpaSpecificationExecutor.findAll(PageRequest.of(number, size));
        }
        Specification<T> spec = null;
        Sort fullSort = null;
        Pageable pageable;

        if (!search.isEmpty()) {
            spec = SpecificationUtil.parseSpecification(search,
                    filterPattern,
                    filterProperties.getFieldSeparator(),
                    filterProperties.getLevelSeparator());
        }

        if (!sort.isEmpty()) {
            fullSort = SortUtil.parseSort(sort, sortPattern, sortProperties.getFieldSeparator());
        }
        pageable = fullSort == null
                ? PageRequest.of(number, size)
                : PageRequest.of(number, size, fullSort);
        return jpaSpecificationExecutor.findAll(spec, pageable);
    }
}