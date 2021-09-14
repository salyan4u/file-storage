package ru.solyanin.filestorage.service.ifaces;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface PageableService<T, R extends JpaRepository<T, UUID> & JpaSpecificationExecutor<T>> {
    Page<T> getPage(int number, int size, String search, String sort);
}