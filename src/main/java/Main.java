import java.util.HashMap;

public class Main {

    static SearchHandler searchHandler = new SearchHandler(new HashMap<>(), new FileReaderImpl());

    public static void main(String[] args) {
        if(args.length == 0)
            throw new IllegalArgumentException("No directory given");
        if(args.length > 1)
            throw new IllegalArgumentException("Only one directory should be passed");
        searchHandler.process(args[0]);
    }
}
