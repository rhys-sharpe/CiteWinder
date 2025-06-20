import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyReader {

    public static void main(String[] args) {
        Properties properties = new Properties();
        String fileName = "application.properties";

        try (InputStream inputStream = PropertyReader.class.getClassLoader().getResourceAsStream(fileName)) {

            // Check if the file is found
            if (inputStream == null) {
                System.out.println("Sorry, unable to find " + fileName);
                return;
            }

            // Load properties from the file
            properties.load(inputStream);

            // Access the properties
            String appVersion = properties.getProperty("app.version");
            String libPath = properties.getProperty("paths.lib");
            String bibPath = properties.getProperty("paths.bib");

            // Print the values
            System.out.println("Application Version: " + appVersion);
            System.out.println("Library Path: " + libPath);
            System.out.println("BibTeX Path: " + bibPath);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}