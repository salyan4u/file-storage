package ru.solyanin.filestorage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.solyanin.filestorage.model.FileEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FileEntityRepository extends JpaRepository<FileEntity, UUID>, JpaSpecificationExecutor<FileEntity> {
    Optional<FileEntity> findByNameAndPath(String fileName, String path);
}