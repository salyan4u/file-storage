package ru.solyanin.filestorage.service.ifaces;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import ru.solyanin.filestorage.exception.BadRequestException;
import ru.solyanin.filestorage.exception.InternalErrorException;
import ru.solyanin.filestorage.model.FileEntity;
import ru.solyanin.filestorage.view.UploadFileResponse;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface FileEntityService {
    FileEntity getFileEntityById(UUID uuid) throws EntityNotFoundException;

    FileEntity getFileEntityByNameAndPath(String name, String path) throws EntityNotFoundException;

    Page<FileEntity> getPage(int number, int size, String search, String sort);

    @Transactional
    FileEntity create(MultipartFile file, String path) throws BadRequestException, InternalErrorException;

    @Transactional
    List<FileEntity> createList(List<MultipartFile> files, List<String> paths) throws BadRequestException, InternalErrorException;

    @Transactional
    void delete(UUID id) throws EntityNotFoundException, IOException;

    UploadFileResponse createUploadResponse(FileEntity fileEntity);

    List<UploadFileResponse> createUploadResponseList(List<FileEntity> fileEntities);
}