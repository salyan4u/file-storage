package util;

import org.junit.jupiter.api.Assertions;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.solyanin.filestorage.model.FileEntity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FilesTestUtil {
    public static FileEntity setUpTestEntityForController() {
        FileEntity fileEntity = new FileEntity();
        fileEntity.setType("test");
        fileEntity.setSize(11111L);
        fileEntity.setPath("test/test");
        fileEntity.setName("name");
        return fileEntity;
    }

    public static FileEntity setUpTestEntity() {
        FileEntity fileEntity = new FileEntity();
        fileEntity.setId(UUID.fromString("786c4aa0-ea57-40fb-83a5-a8ba941e6f14"));
        fileEntity.setName("File.txt");
        fileEntity.setPath("");
        fileEntity.setSize(1542L);
        fileEntity.setType("text/plain");
        return fileEntity;
    }

    public static List<FileEntity> setUpListEntity() {
        List<FileEntity> list = new ArrayList<>();
        list.add(setUpTestEntity());
        list.add(new FileEntity(
                UUID.fromString("93cf6872-7c3b-49b2-948e-c8fa0d631f64"),
                "File.pdf",
                "",
                285548L,
                "application/pdf"));
        list.add(new FileEntity(
                UUID.fromString("cff0ad56-4647-4882-931f-58b73771b170"),
                "File.docx",
                "",
                1021049L,
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
        list.add(new FileEntity(
                UUID.fromString("38ddd9b6-9673-4400-8339-f7704ed0b348"),
                "File.jpeg",
                "",
                413920L,
                "image/jpeg"));
        return list;
    }

    public static void entityAssertions(FileEntity fileEntityTest) {
        Assertions.assertEquals(UUID.fromString("786c4aa0-ea57-40fb-83a5-a8ba941e6f14"), fileEntityTest.getId());
        Assertions.assertEquals("File.txt", fileEntityTest.getName());
        Assertions.assertEquals("", fileEntityTest.getPath());
        Assertions.assertEquals("text/plain", fileEntityTest.getType());
        Assertions.assertEquals(1542L, fileEntityTest.getSize());
    }

    public static MockMultipartFile multipartFile(String pathToStore) {
        File fileToStore = new File(pathToStore);
        String name = fileToStore.getName();
        String contentType = null;
        try {
            contentType = Files.probeContentType(Path.of(pathToStore));
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] content = null;
        try {
            content = Files.readAllBytes(Path.of(pathToStore));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new MockMultipartFile(name, name, contentType, content);
    }

    public static List<MultipartFile> multipartList(String testPath) {
        List<String> paths = new ArrayList<>(List.of("File.txt", "File.docx", "File.pdf", "File.jpeg"));
        paths.replaceAll(filename -> getPathToStore(filename, testPath));

        List<MultipartFile> files = new ArrayList<>();
        for (String name : paths) {
            files.add(multipartFile(name));
        }
        return files;
    }

    public static String getPathToStore(String filename, String testPath) {
        return testPath + filename;
    }
}
