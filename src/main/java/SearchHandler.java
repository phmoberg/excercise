import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.toSet;

public class SearchHandler {

    private final Map<String, List<String>> memory;
    private final FileReader fileReader;
    private static final String INVALID_CHARS = "[`~!@#$%^&*()_+\\\\[\\\\]\\\\\\\\;\\',./{}|:\\\"<>?]";

    public SearchHandler(Map<String, List<String>> memory, FileReader fileReader) {
        this.memory = memory;
        this.fileReader = fileReader;
    }

    public void process(String path) {
        saveDataFromPath(path);
        Scanner keyboard = new Scanner(System.in, "utf-8");
        while (true) {
            System.out.print("search>");
            String line = keyboard.nextLine();
            handleInput(line);
        }
    }

    private void handleInput(String line) {
        if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase(":quit"))
            System.exit(0);
        if (StringUtils.isBlank(line))
            System.out.println("Need input to search");
        else
            printResult(line);
    }

    private void printResult(String line) {
        int numberOfSearchArguments = line.split(" ").length;
        Set<String> words = Stream.of(line.split(" "))
                .map(word -> word.replaceAll(INVALID_CHARS, ""))
                .filter(StringUtils::isNoneBlank).collect(toSet());
        if (words.isEmpty())
            System.out.println("no matches found");
        else {
            Map<String, Integer> result = findMatches(words);
            if (result.isEmpty())
                System.out.println("no matches found");
            else {
                result.entrySet().stream().sorted(comparing(Map.Entry::getValue, reverseOrder())).limit(10).forEach(entry -> {
                    System.out.print(entry.getKey() + " : ");
                    System.out.print(Math.round(100 * entry.getValue() / numberOfSearchArguments) + "%");
                    System.out.println();
                });
            }
        }
    }

    private Map<String, Integer> findMatches(Set<String> words) {
        Map<String, Integer> result = new HashMap<>();
        words.stream().forEach(word -> {
            List<String> documents = memory.get(word);
            if (documents != null) {
                documents.forEach(document -> {
                    Integer occurances = result.get(document);
                    if (occurances != null)
                        result.put(document, ++occurances);
                    else
                        result.put(document, 1);
                });
            }
        });
        return result;
    }

    public void saveDataFromPath(String path) {
        List<File> files = fileReader.readFilesInPath(path);
        saveDataToMemory(files);
    }

    private void saveDataToMemory(List<File> files) {
        files.stream().forEach(file -> {
            try (Stream<String> stream = Files.lines(Paths.get(file.getPath()), Charsets.UTF_8)) {
                stream.flatMap(line -> Stream.of(line.split(" ")))
                        .map(word -> word.replaceAll(INVALID_CHARS, ""))
                        .forEach(save(file.getName()));
            } catch (IOException e) {
                System.out.println("Could not read lines from file: " + e.getMessage());
            }
        });
    }

    private Consumer<String> save(String fileName) {
        return word -> {
            List<String> documents = memory.get(word);
            if (documents == null) {
                documents = Lists.newArrayList();
                memory.put(word, documents);
            }
            if (!documents.contains(fileName))
                documents.add(fileName);
        };
    }

}
