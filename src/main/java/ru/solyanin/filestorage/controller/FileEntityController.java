package ru.solyanin.filestorage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.solyanin.filestorage.exception.BadRequestException;
import ru.solyanin.filestorage.exception.InternalErrorException;
import ru.solyanin.filestorage.model.FileEntity;
import ru.solyanin.filestorage.service.FileStorageService;
import ru.solyanin.filestorage.service.ifaces.FileEntityService;
import ru.solyanin.filestorage.view.FileEntityDto;
import ru.solyanin.filestorage.view.UploadFileResponse;
import ru.solyanin.filestorage.view.mapper.FileEntityMapper;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequestMapping("api/v1/storage")
@Tag(name = "FileStorageService", description = "File storage service")
@RestController
public class FileEntityController {
    private final FileStorageService fileStorageService;
    private final FileEntityService fileEntityService;
    private final FileEntityMapper mapper;

    public FileEntityController(FileStorageService fileStorageService,
                                FileEntityService fileEntityService,
                                FileEntityMapper mapper) {
        this.fileStorageService = fileStorageService;
        this.fileEntityService = fileEntityService;
        this.mapper = mapper;
    }

    @Operation(summary = "Get page of files")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(produces = "application/json", value = "/page")
    @ResponseBody
    public ResponseEntity<Page<FileEntityDto>> getPage(@RequestParam(required = false, defaultValue = "0") int number,
                                                       @RequestParam(required = false, defaultValue = "10") int size,
                                                       @Parameter(description = "operators: '::' '!:' '>>' '<<' '>:' '<:'", example = "name::First_file.txt;")
                                                       @RequestParam(required = false, defaultValue = "") String search,
                                                       @Parameter(description = "operators: ':'", example = "id:asc;")
                                                       @RequestParam(required = false, defaultValue = "") String sort) {
        Page<FileEntity> page = fileEntityService.getPage(number, size, search, sort);
        return ResponseEntity.ok(page.map(mapper::toDto));
    }

    @Operation(summary = "Delete file by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<?> deleteFile(@Parameter(description = "UUID of file") @RequestParam("id") UUID id) {
        try {
            fileEntityService.delete(id);
            return ResponseEntity.ok("success");
        } catch (EntityNotFoundException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Upload file to storage")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(value = "/upload")
    @ResponseBody
    public ResponseEntity<UploadFileResponse> uploadFile(@RequestParam(value = "file") MultipartFile file,
                                                         @RequestParam(required = false, defaultValue = "") String path) {
        FileEntity fileEntity = null;
        try {
            fileEntity = fileEntityService.create(file, path);
        } catch (BadRequestException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (InternalErrorException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.ok(fileEntityService.createUploadResponse(fileEntity));
    }

    @Operation(summary = "Upload list of files to storage")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(value = "/upload/list")
    @ResponseBody
    public ResponseEntity<List<UploadFileResponse>> uploadList(@RequestParam(value = "files") List<MultipartFile> files,
                                                               @RequestParam(required = false, defaultValue = "") List<String> paths) {
        List<FileEntity> fileEntities = null;
        try {
            fileEntities = fileEntityService.createList(files, paths);
        } catch (BadRequestException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (InternalErrorException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.ok(fileEntityService.createUploadResponseList(fileEntities));
    }

    @Operation(summary = "Get file by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/download-by-id")
    @ResponseBody
    public ResponseEntity<Resource> downloadFileById(@Parameter(description = "UUID of file") @RequestParam(value = "id") UUID id) {
        try {
            return getResourceResponseEntity(fileEntityService.getFileEntityById(id));
        } catch (EntityNotFoundException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get file by path and name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/download-by-path-and-filename")
    @ResponseBody
    public ResponseEntity<Resource> downloadFileByPathAndFilename(@Parameter(description = "Filename") @RequestParam(value = "name") String name,
                                                                  @Parameter(description = "Path") @RequestParam(value = "path") String path) {
        try {
            return getResourceResponseEntity(fileEntityService.getFileEntityByNameAndPath(name, path));
        } catch (EntityNotFoundException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private ResponseEntity<Resource> getResourceResponseEntity(FileEntity file) {
        Resource resource = null;
        try {
            resource = fileStorageService.loadFileAsResource(file.getName(), file.getPath());
        } catch (MalformedURLException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        String name;
        try {
            if (file.getName() != null) {
                name = URLEncoder.encode(file.getName(), StandardCharsets.UTF_8.toString());
            } else {
                log.info("Filename is null");
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (UnsupportedEncodingException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''\"" + name + "\"")
                .body(resource);
    }
}