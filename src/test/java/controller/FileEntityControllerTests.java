package controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import ru.solyanin.filestorage.FileStorageApplication;

import javax.servlet.http.Part;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static util.FilesTestUtil.multipartFile;
import static util.FilesTestUtil.multipartList;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(classes = FileStorageApplication.class)
@TestPropertySource(locations = "/application-test.yaml")
public class FileEntityControllerTests {
    @Value("${file.test-path}")
    private String testPath;

    @Autowired
    private MockMvc mvc;

    @Test
    public void testPageSizeAndNumber() {
        try {
            mvc.perform(get("/api/v1/storage/page")
                    .queryParam("size", "5")
                    .queryParam("number", "2")
                    .contentType(MediaType.APPLICATION_JSON)
            )
                    .andExpect(jsonPath(("$.pageable.pageSize"), equalTo(5)))
                    .andExpect(jsonPath(("$.pageable.pageNumber"), equalTo(2)))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPageSpec() {
        try {
            mvc.perform(get("/api/v1/storage/page")
                    .queryParam("search", "name::File.txt;")
                    .contentType(MediaType.APPLICATION_JSON)
            )
                    .andExpect(jsonPath(("$.pageable.pageSize"), equalTo(10)))
                    .andExpect(jsonPath(("$.pageable.pageNumber"), equalTo(0)))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPageSort() {
        try {
            mvc.perform(get("/api/v1/storage/page")
                    .queryParam("sort", "id:asc;")
                    .contentType(MediaType.APPLICATION_JSON)
            )
                    .andExpect(jsonPath(("$.pageable.pageSize"), equalTo(10)))
                    .andExpect(jsonPath(("$.pageable.pageNumber"), equalTo(0)))
                    .andExpect(jsonPath(("$.content[0].id"), equalTo("00000000-0000-0000-0001-000000000000")))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPageSortSpec() {
        try {
            mvc.perform(get("/api/v1/storage/page")
                    .queryParam("sort", "id:asc;")
                    .queryParam("search", "name::File.txt;")
                    .contentType(MediaType.APPLICATION_JSON)
            )
                    .andExpect(jsonPath(("$.pageable.pageSize"), equalTo(10)))
                    .andExpect(jsonPath(("$.pageable.pageNumber"), equalTo(0)))
                    .andExpect(jsonPath(("$.content[0].id"), equalTo("00000000-0000-0000-0001-000000000000")))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void uploadFile() {
        MockMultipartFile file = multipartFile(testPath + "File.txt");
        Part part = null;
        try {
            part = new MockPart("file", file.getName(), file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mvc.perform(multipart("/api/v1/storage/upload")
                    .part(part)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .queryParam("path", "/create")
            )
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteFile() {
        try {
            mvc.perform(delete("/api/v1/storage")
                    .queryParam("id", "00000000-0000-0000-0001-000000000004")
            )
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void uploadList() {
        List<MultipartFile> files = multipartList(testPath);
        List<Part> parts = new ArrayList<>();
        for (MultipartFile file : files) {
            Part part = null;
            try {
                part = new MockPart("files", file.getName(), file.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            parts.add(part);
        }
        try {
            mvc.perform(multipart("/api/v1/storage/upload/list")
                    .part(parts.get(0), parts.get(1), parts.get(2), parts.get(3))
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .queryParam("paths", "", "", "", "")
            )
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void downloadFileById() {
        try {
            mvc.perform(get("/api/v1/storage/download-by-id")
                    .queryParam("id", "00000000-0000-0000-0001-000000000003")
            )
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void downloadFileByIdFalse() {
        try {
            mvc.perform(get("/api/v1/storage/download-by-id")
                    .queryParam("id", "00000000-0000-0000-0002-000000000003")
            )
                    .andExpect(status().isNotFound());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void downloadFileByPathAndFilename() {
        try {
            mvc.perform(get("/api/v1/storage/download-by-path-and-filename")
                    .queryParam("name", "File.docx")
                    .queryParam("path", "")
            )
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void downloadFileByPathAndFilenameFalse() {
        try {
            mvc.perform(get("/api/v1/storage/download-by-path-and-filename")
                    .queryParam("name", "File.docx")
                    .queryParam("path", "/create")
            )
                    .andExpect(status().isNotFound());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}