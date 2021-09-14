package service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.multipart.MultipartFile;
import ru.solyanin.filestorage.FileStorageApplication;
import ru.solyanin.filestorage.exception.BadRequestException;
import ru.solyanin.filestorage.exception.InternalErrorException;
import ru.solyanin.filestorage.model.FileEntity;
import ru.solyanin.filestorage.repository.FileEntityRepository;
import ru.solyanin.filestorage.service.ifaces.FileEntityService;
import ru.solyanin.filestorage.view.UploadFileResponse;
import util.FilesTestUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.doReturn;
import static util.FilesTestUtil.*;

@ActiveProfiles("test")
@SpringBootTest(classes = FileStorageApplication.class)
@TestPropertySource(locations = "/application-test.yaml")
public class FileEntityServiceTests {
    @Value("${file.service-path}")
    private String servicePath;
    @Value("${file.test-path}")
    private String testPath;

    @Autowired
    private FileEntityService fileEntityService;
    @MockBean
    private FileEntityRepository fileEntityRepository;

    @Test
    public void getFileEntityByNameAndPath() {
        FileEntity fileEntity = setUpTestEntity();

        doReturn(Optional.of(fileEntity))
                .when(fileEntityRepository)
                .findByNameAndPath("File.txt", "");

        FileEntity fileEntityTest = fileEntityService.getFileEntityByNameAndPath("File.txt", "");
        Mockito.verify(fileEntityRepository, Mockito.times(1))
                .findByNameAndPath(ArgumentMatchers.eq("File.txt"), ArgumentMatchers.eq(""));

        entityAssertions(fileEntityTest);
    }

    @Test
    public void getFileEntityById() {
        FileEntity fileEntity = FilesTestUtil.setUpTestEntity();

        doReturn(Optional.of(fileEntity))
                .when(fileEntityRepository)
                .findById(UUID.fromString("786c4aa0-ea57-40fb-83a5-a8ba941e6f14"));

        FileEntity fileEntityTest = fileEntityService.getFileEntityById(UUID.fromString("786c4aa0-ea57-40fb-83a5-a8ba941e6f14"));
        Mockito.verify(fileEntityRepository, Mockito.times(1))
                .findById(ArgumentMatchers.eq(UUID.fromString("786c4aa0-ea57-40fb-83a5-a8ba941e6f14")));

        entityAssertions(fileEntityTest);
    }

    @Test
    public void create() {
        String pathToStore = getPathToStore("File.txt", testPath);
        MultipartFile multipartFile = multipartFile(pathToStore);
        try {
            fileEntityService.create(multipartFile, "/create");
        } catch (BadRequestException | InternalErrorException e) {
            e.printStackTrace();
        }
        Path pathFromStore = Paths.get(servicePath + File.separatorChar + "create\\File.txt");
        File fileFromStore = new File(String.valueOf(pathFromStore));
        Assertions.assertEquals(multipartFile.getSize(), fileFromStore.length());
    }

    @Test
    public void delete() {
        FileEntity fileEntity = setUpTestEntity();
        fileEntity.setPath("/create");
        fileEntity.setId(UUID.fromString("1ee5df56-56a5-4d4f-b97f-e196dfae4f51"));

        doReturn(Optional.of(fileEntity))
                .when(fileEntityRepository)
                .findById(UUID.fromString("1ee5df56-56a5-4d4f-b97f-e196dfae4f51"));

        try {
            fileEntityService.delete(UUID.fromString("1ee5df56-56a5-4d4f-b97f-e196dfae4f51"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createList() {
        List<MultipartFile> files = multipartList(testPath);
        try {
            fileEntityService.createList(files, List.of("", "", "", ""));
        } catch (BadRequestException | InternalErrorException e) {
            e.printStackTrace();
        }

        Assertions.assertEquals(files.size(), 4);
    }

    @Test
    public void createUploadResponse() {
        FileEntity fileEntity = setUpTestEntity();
        UploadFileResponse uploadFileResponse = fileEntityService.createUploadResponse(fileEntity);
        Assertions.assertEquals(uploadFileResponse.getSize(), fileEntity.getSize());
    }

    @Test
    public void createUploadResponseList() {
        List<UploadFileResponse> uploadFileResponseList = fileEntityService.createUploadResponseList(setUpListEntity());
        Assertions.assertEquals(setUpListEntity().size(), uploadFileResponseList.size());
    }
}