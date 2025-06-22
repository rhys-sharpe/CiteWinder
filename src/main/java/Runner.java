import org.jbibtex.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Stream;

import java.io.StringWriter;

public class Runner {
    private BibTeXDatabase db = new BibTeXDatabase();
    private BibTeXDatabase mem = new BibTeXDatabase();
    private final ArrayList<String> fileNames = new ArrayList<>();

    private PropertyLoader config;

    private boolean readBibLib(boolean readMem) {
        FileReader reader;
        boolean parseSuccess;
        try {
            reader = new FileReader(config.getProperty(readMem ? "paths.mem" : "paths.bib"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Could not find a the BibTeX file - bad config!");
            return false;
        }
        try {
            BibTeXParser parser = new BibTeXParser();
            if (readMem) {
                mem = parser.parse(reader);
            } else {
                db = parser.parse(reader);
            }
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

    private boolean updateMemory() {
        try {
            Path source = Paths.get(config.getProperty("paths.bib"));
            Path target = Paths.get(config.getProperty("paths.mem"));
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            System.out.println("Error copying memory file: " + e.getMessage());
            return false;
        }
    }

    private boolean createMarkdownNote(BibTeXEntry entry) {
        BibTeXFormatter formatter = new BibTeXFormatter();
        boolean writeSuccess;
        StringBuilder newFileName = new StringBuilder();
//        System.out.println("");
        newFileName.append(((StringValue) entry.getField(BibTeXEntry.KEY_TITLE)).getString().replaceAll("[\\\\/:\\*\\?\"<>|]", "_"))
                    .append(" (").append(entry.getKey().toString()).append(").md");
        StringBuilder markdown = new StringBuilder();
        String newPath = config.getProperty("paths.new_notes") + "\\" + newFileName.toString();

        StringWriter stringWriter = new StringWriter();
        BibTeXDatabase tempDb = new BibTeXDatabase();
        tempDb.addObject(entry);
        try {
            formatter.format(tempDb, stringWriter);
        } catch (IOException e) {
            System.out.println("Error formatting BibTeX entry: " + e.getMessage());
            return false;
        }

        try (FileWriter writer = new FileWriter(newPath)) {
            markdown.append("---\n")
                    .append("added: ").append(java.time.LocalDate.now()).append(" ").append(java.time.LocalTime.now()).append("\n")
                    .append("status: not_read\n")
                    .append("---\n");
            markdown.append("# ").append(newFileName.toString()).append("\n\n");
            markdown.append("```bibtex\n").append(stringWriter.toString()).append("\n```\n");
            writer.write(markdown.toString());

            writeSuccess = true;
        } catch (IOException e) {
            writeSuccess = false;
            System.out.println("Error creating new note file: " + e.getMessage());
        }
        return writeSuccess;
    }

    public boolean checkLib() {
        System.out.println("Checking library...");
        boolean parseSuccess, readSuccess;

        parseSuccess = readBibLib(false);
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
        Scanner scanner = new Scanner(System.in);
        System.out.println("Do you want to update the memory? (y/n) ");
        String input = scanner.nextLine().trim().toLowerCase();
        boolean updateMemory = input.equals("y");

        boolean readMemSuccess = readBibLib(true);
        boolean readLibSuccess = readBibLib(false);
        boolean writeSuccess = false;
        boolean copySuccess = true;
        int successCount = 0;
        int failureCount = 0;

        if (readMemSuccess && readLibSuccess) {
            for (var key : db.getEntries().keySet()) {
                if (!(mem.getEntries().containsKey((key)))) {
                    // Found a new library entry; not in memory - time to add a literature note
                    BibTeXEntry entry = db.getEntries().get(key);
                    writeSuccess = createMarkdownNote(entry);
                    if (!writeSuccess) {
                        failureCount++;
                        return false;
                    } else {
                        successCount++;
                    }
                }
            }
            // No new entries found
            writeSuccess = true;
        }
        System.out.printf("updated %d notes\n", successCount);
        System.out.printf("failed to update %d notes\n", failureCount);

        if (updateMemory) {
            copySuccess = false;
            System.out.println("Updating memory...");
            copySuccess = updateMemory();
            if (copySuccess) {
                System.out.println("Memory updated successfully.");
            } else {
                System.out.println("Failed to update memory.");
            }
        }
        return copySuccess && readMemSuccess && readLibSuccess && writeSuccess;
    }

    public boolean manageConfig() {
        System.out.println("Managing configuration");
        return false;
    }

    public Runner(PropertyLoader config) {
        this.config = config;
    }
}