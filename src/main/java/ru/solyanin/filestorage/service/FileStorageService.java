package ru.solyanin.filestorage.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.solyanin.filestorage.configuration.properties.FileStorageProperties;
import ru.solyanin.filestorage.exception.BadRequestException;

import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@Slf4j
public class FileStorageService {
    private final Path serviceFileStorage;

    public FileStorageService(FileStorageProperties fileStorageProperties) throws IOException {
        this.serviceFileStorage = Paths.get(fileStorageProperties.getServicePath())
                .toAbsolutePath().normalize();
        Files.createDirectories(this.serviceFileStorage);
    }

    public String storeFile(MultipartFile file, String path) throws IOException, BadRequestException {
        String originalFileName = file.getOriginalFilename();
        String fileName;
        if (originalFileName != null) {
            fileName = StringUtils.cleanPath(file.getOriginalFilename());
            if (fileName.contains("..")) {
                throw new BadRequestException("Filename contains invalid path sequence " + fileName);
            }
        } else throw new BadRequestException("Filename zero");

        String resolveString = this.serviceFileStorage.toString() + File.separatorChar + path;
        Path resolved = this.serviceFileStorage.resolve(resolveString);
        if (!Files.exists(resolved)) {
            Files.createDirectories(resolved);
        }
        Path targetLocation;
        targetLocation = resolved.resolve(fileName);
        System.out.println(targetLocation.toString());
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        log.info("File stored: " + file);
        return fileName;
    }

    public Resource loadFileAsResource(String name, String path) throws MalformedURLException, EntityNotFoundException {
        Path filePath = null;
        if (path.equals("")) {
            filePath = this.serviceFileStorage.resolve(name).normalize();
        } else {
            String filePathString = this.serviceFileStorage.toString() + File.separatorChar + path + File.separatorChar + name;
            filePath = this.serviceFileStorage.resolve(filePathString).normalize();
        }
        Resource resource = new UrlResource(filePath.toUri());
        if (resource.exists()) {
            return resource;
        } else {
            throw new EntityNotFoundException("File not found " + name);
        }
    }

    public void deleteFile(String name, String path) throws IOException {
        Path filePath = null;
        if (path.equals("")) {
            filePath = this.serviceFileStorage.resolve(name).normalize();
        } else {
            String fullPath = this.serviceFileStorage.toString() + File.separatorChar + path + File.separatorChar + name;
            filePath = Path.of(fullPath).normalize();
        }
        if (Files.deleteIfExists(filePath)) {
            log.info("Deleted file " + filePath.toString());
        } else {
            log.info("File not found" + filePath.toString() + name);
        }
    }
}