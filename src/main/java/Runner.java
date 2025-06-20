import org.jbibtex.ParseException;
import org.jbibtex.BibTeXParser;
import org.jbibtex.BibTeXDatabase;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class Runner {
    private BibTeXDatabase db = new BibTeXDatabase();
    private PropertyLoader config;

    private boolean loadLibs(boolean loadLibrary, boolean loadNotes) throws ParseException {
        BibTeXParser parser = new BibTeXParser();
        FileReader reader;
        try {
            reader = new FileReader(config.getProperty("paths.bib"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Could not find a the BibTeX file - bad config!");
            return false;
        }
        db = parser.parse(reader);
        return true;
    }

    public boolean checkLib() {
        System.out.println("Checking library...");
        boolean parseSuccess;
        try {
            parseSuccess = loadLibs(true, false);
        } catch (ParseException e) {
            e.printStackTrace();
            parseSuccess = false;
        }
        if (!parseSuccess) {
            System.out.println("The system encountered a problem trying to parse your library. Please try again.");
        }
        return parseSuccess;
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