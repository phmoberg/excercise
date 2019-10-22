import com.google.common.collect.Lists;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class FileReaderImpl implements FileReader {

    @Override
    public List<File> readFilesInPath(String path) {
        try (Stream<Path> paths = Files.walk(Paths.get(path))) {
            return paths.filter(Files::isRegularFile)
                    .filter(f -> f.getFileName().toString().endsWith(".txt"))
                    .map(Path::toFile)
                    .collect(toList());
        } catch (IOException e) {
            System.out.println("Could not read files: " + e.getMessage());
        }
        return Lists.newArrayList();
    }
}
