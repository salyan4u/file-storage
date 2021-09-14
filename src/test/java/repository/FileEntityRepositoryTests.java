package repository;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import ru.solyanin.filestorage.FileStorageApplication;
import ru.solyanin.filestorage.model.FileEntity;
import ru.solyanin.filestorage.repository.FileEntityRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static util.FilesTestUtil.setUpTestEntityForController;

@ActiveProfiles("test")
@DataJpaTest
@ContextConfiguration(classes = FileStorageApplication.class)
public class FileEntityRepositoryTests {
    @Autowired
    private FileEntityRepository fileEntityRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void initDbTest() {
        List<FileEntity> list = fileEntityRepository.findAll();
        Assertions.assertEquals(5, list.size());
    }

    @Test
    public void findEntityById() {
        Optional<FileEntity> fileEntity = fileEntityRepository.findById(UUID.fromString("00000000-0000-0000-0001-000000000003"));
        if (fileEntity.isEmpty()) throw new EntityNotFoundException();
        Assertions.assertEquals("File.jpeg", fileEntity.get().getName());
        Assertions.assertEquals("image/jpeg", fileEntity.get().getType());
        Assertions.assertEquals("", fileEntity.get().getPath());
        Assertions.assertEquals(413920L, fileEntity.get().getSize());
    }

    @Test
    public void findEntityByIdFalse() {
        Assert.assertThrows(EntityNotFoundException.class, () -> {
            Optional<FileEntity> fileEntity = fileEntityRepository.findById(UUID.fromString("00000000-0000-0000-0001-000002000003"));
            if (fileEntity.isEmpty()) throw new EntityNotFoundException();
        });
    }

    @Test
    public void findEntityByNameAndPath() {
        Optional<FileEntity> fileEntity = fileEntityRepository.findByNameAndPath("File.docx", "");
        if (fileEntity.isEmpty()) throw new EntityNotFoundException();
        Assertions.assertEquals(UUID.fromString("00000000-0000-0000-0001-000000000002"), fileEntity.get().getId());
        Assertions.assertEquals("File.docx", fileEntity.get().getName());
        Assertions.assertEquals("application/vnd.openxmlformats-officedocument.wordprocessingml.document", fileEntity.get().getType());
        Assertions.assertEquals("", fileEntity.get().getPath());
        Assertions.assertEquals(1021049L, fileEntity.get().getSize());
    }

    @Test
    public void findEntityByNameAndPathFalse() {
        Assert.assertThrows(EntityNotFoundException.class, () -> {
            Optional<FileEntity> fileEntity = fileEntityRepository.findByNameAndPath("File.docx", "22");
            if (fileEntity.isEmpty()) throw new EntityNotFoundException();
        });
    }

    @Test
    public void addEntityTest() {
        FileEntity fileEntity = setUpTestEntityForController();
        testEntityManager.persist(fileEntity);
        testEntityManager.flush();

        List<FileEntity> list = fileEntityRepository.findAll();
        Assertions.assertEquals(6, list.size());
        Assertions.assertEquals("name", list.get(5).getName());
        Assertions.assertEquals("test/test", list.get(5).getPath());
        Assertions.assertEquals(11111L, list.get(5).getSize());
        Assertions.assertEquals("test", list.get(5).getType());
    }

    @Test
    public void deleteEntity() {
        FileEntity fileEntity = setUpTestEntityForController();
        testEntityManager.persist(fileEntity);
        testEntityManager.flush();

        testEntityManager.remove(fileEntity);
        testEntityManager.flush();

        List<FileEntity> list = fileEntityRepository.findAll();
        Assertions.assertEquals(5, list.size());
    }
}