import java.io.File;
import java.util.List;


public interface FileReader {

    List<File> readFilesInPath(String path);
}
