package ru.solyanin.filestorage.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.solyanin.filestorage.configuration.properties.FileStorageProperties;
import ru.solyanin.filestorage.configuration.properties.FilterProperties;
import ru.solyanin.filestorage.configuration.properties.SortProperties;
import ru.solyanin.filestorage.exception.BadRequestException;
import ru.solyanin.filestorage.exception.InternalErrorException;
import ru.solyanin.filestorage.model.FileEntity;
import ru.solyanin.filestorage.repository.FileEntityRepository;
import ru.solyanin.filestorage.service.FileStorageService;
import ru.solyanin.filestorage.service.ifaces.FileEntityService;
import ru.solyanin.filestorage.view.UploadFileResponse;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class FileEntityServiceImpl extends AbstractPageableServiceImpl<FileEntity, FileEntityRepository> implements FileEntityService {
    private final FileStorageService fileStorageService;
    private final FileStorageProperties fileStorageProperties;

    public FileEntityServiceImpl(FileEntityRepository jpaSpecificationExecutor,
                                 FilterProperties filterProperties,
                                 SortProperties sortProperties,
                                 FileStorageService fileStorageService,
                                 FileStorageProperties fileStorageProperties) {
        super(jpaSpecificationExecutor, filterProperties, sortProperties);
        this.fileStorageService = fileStorageService;
        this.fileStorageProperties = fileStorageProperties;
    }

    @Override
    public Page<FileEntity> getPage(int number, int size, String search, String sort) {
        return super.getPage(number, size, search, sort);
    }

    @Override
    public FileEntity getFileEntityById(UUID uuid) throws EntityNotFoundException {
        Optional<FileEntity> fileEntity = jpaSpecificationExecutor.findById(uuid);
        if (fileEntity.isPresent()) {
            return fileEntity.get();
        } else throw new EntityNotFoundException();
    }

    @Override
    public FileEntity getFileEntityByNameAndPath(String name, String path) throws EntityNotFoundException {
        Optional<FileEntity> fileEntity = jpaSpecificationExecutor.findByNameAndPath(name, path);
        if (fileEntity.isPresent()) {
            return fileEntity.get();
        } else throw new EntityNotFoundException();
    }

    @Override
    public FileEntity create(MultipartFile file, String path) throws BadRequestException, InternalErrorException {
        String fileName = file.getOriginalFilename() == null ? null : StringUtils.cleanPath(file.getOriginalFilename());
        if (fileName == null || fileName.isEmpty()) {
            throw new BadRequestException("Filename is empty");
        }
        try {
            fileName = fileStorageService.storeFile(file, path);
        } catch (IOException e) {
            throw new InternalErrorException(e.getMessage());
        }

        String regPath = path.replaceAll("^\\W*|\\D*$", "");
        Optional<FileEntity> duplicate = jpaSpecificationExecutor.findByNameAndPath(fileName, regPath);
        if (duplicate.isPresent()) {
            log.info("Entity updated: " + duplicate.get());
            return duplicate.get();
        } else {
            FileEntity entity = new FileEntity();
            entity.setName(fileName);
            entity.setPath(regPath);
            entity.setSize(file.getSize());
            entity.setType(file.getContentType());
            log.info("Entity created: " + entity);
            return jpaSpecificationExecutor.saveAndFlush(entity);
        }
    }

    @Override
    public List<FileEntity> createList(List<MultipartFile> files, List<String> paths) throws BadRequestException, InternalErrorException {
        List<FileEntity> filesEntity = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            create(files.get(i), paths.get(i));
        }
        return filesEntity;
    }

    @Override
    public void delete(UUID id) throws EntityNotFoundException, IOException {
        Optional<FileEntity> fileEntity = jpaSpecificationExecutor.findById(id);
        if (fileEntity.isPresent()) {
            FileEntity entity = fileEntity.get();
            fileStorageService.deleteFile(entity.getName(), entity.getPath());
            jpaSpecificationExecutor.delete(entity);
            log.info("Entity deleted: " + fileEntity.get());
        } else throw new EntityNotFoundException();
    }

    @Override
    public UploadFileResponse createUploadResponse(FileEntity fileEntity) {
        UploadFileResponse uploadFileResponse = new UploadFileResponse();
        uploadFileResponse.setName(fileEntity.getName());
        uploadFileResponse.setId(fileEntity.getId());
        uploadFileResponse.setSize(fileEntity.getSize());
        uploadFileResponse.setType(fileEntity.getType());
        uploadFileResponse.setFileDownloadUri(ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(fileStorageProperties.getServicePath() + "\\download\\" + fileEntity.getPath())
                .path(uploadFileResponse.getName())
                .toUriString());
        return uploadFileResponse;
    }

    @Override
    public List<UploadFileResponse> createUploadResponseList(List<FileEntity> fileEntities) {
        List<UploadFileResponse> list = new ArrayList<>();
        for (FileEntity entity : fileEntities) {
            list.add(createUploadResponse(entity));
        }
        return list;
    }
}