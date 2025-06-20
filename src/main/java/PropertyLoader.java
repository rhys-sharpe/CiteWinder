// Gemini 2.5 Pro

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyLoader {

    private final Properties properties = new Properties();

    public PropertyLoader() {
        String fileName = "application.properties";

        try (InputStream inputStream = PropertyLoader.class.getClassLoader().getResourceAsStream(fileName)) {

            // Check if the file is found
            if (inputStream == null) {
                System.out.println("Sorry, unable to find " + fileName);
            } else {
                // Load properties from file
                properties.load(inputStream);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}