import org.jbibtex.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

public class Runner {
    private BibTeXDatabase db = new BibTeXDatabase();
    private final ArrayList<String> fileNames = new ArrayList<>();

    private PropertyLoader config;

    private boolean readBibLib() {
        FileReader reader;
        boolean parseSuccess;
        try {
            reader = new FileReader(config.getProperty("paths.bib"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Could not find a the BibTeX file - bad config!");
            return false;
        }
        try {
            BibTeXParser parser = new BibTeXParser();
            db = parser.parse(reader);
            parseSuccess = true;
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("There was a problem parsing the BibTeX file. Please check the syntax.");
            parseSuccess = false;
        } catch (TokenMgrException e) {
            e.printStackTrace();
            System.out.println("There was a problem with the BibTeX file. Please check the syntax.");
            parseSuccess = false;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An unexpected error occurred while parsing the library.");
            parseSuccess = false;
        }

        return parseSuccess;
    }


    private boolean readFileLib() {
        try (Stream<Path> paths = Files.list(Paths.get(config.getProperty("paths.lib")))) {
            paths.filter(Files::isRegularFile)
                    .forEach(path -> fileNames
                            .add(path.getFileName()
                                    .toString()
                                    .replaceAll(" and .*", "")
                                    .replace("et al", "")
                                    .replaceAll("\\s+", "")
                                    .replaceFirst("\\.[^.]+$", "")
                                    .replace("et al", "")));
            return true;
        } catch (IOException e) {
            System.out.println("Error reading library directory: " + e.getMessage());
            return false;
        }
    }

    public boolean checkLib() {
        System.out.println("Checking library...");
        boolean parseSuccess, readSuccess;

        parseSuccess = readBibLib();
        readSuccess = readFileLib();

        if (!parseSuccess) {
            System.out.println("The system encountered a problem trying to parse your library. Please try again.");
        } else {
            int successCount = 0;
            int failureCount = 0;
            for (String name : fileNames) {
                // System.out.println("File: " + name);
                if (!db.getEntries().containsKey(new Key(name))) {
                    failureCount++;
                    System.out.println("No entry found for file: " + name);
                } else {
                    successCount++;
                }
            }
            System.out.printf("Checked %d files. %d entries found, %d files missing entries.\n",
                    fileNames.size(), successCount, failureCount);
//            for (var key : db.getEntries().keySet()) {
//                System.out.println(key);
//            }
        }
        return parseSuccess && readSuccess;
    }

    public boolean updateNotes() {
        System.out.println("Updating notes...");
        return false;
    }

    public boolean manageConfig() {
        System.out.println("Managing configuration");
        return false;
    }

    public Runner(PropertyLoader config) {
        this.config = config;
    }
}