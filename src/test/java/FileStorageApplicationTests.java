import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.solyanin.filestorage.FileStorageApplication;

@SpringBootTest(classes = FileStorageApplication.class)
@ActiveProfiles("test")
public class FileStorageApplicationTests {
    @Test
    public void contextLoads() {
    }
}