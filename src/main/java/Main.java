import org.jbibtex.ParseException;

public class Main {
    public static void main(String[] args) throws ParseException {
        PropertyLoader config = new PropertyLoader();
        String version = config.getProperty("app.version");

        System.out.println("  ____ _ _     __        ___           _           \n" +
                " / ___(_) |_ __\\ \\      / (_)_ __   __| | ___ _ __ \n" +
                "| |   | | __/ _ \\ \\ /\\ / /| | '_ \\ / _` |/ _ \\ '__|\n" +
                "| |___| | ||  __/\\ V  V / | | | | | (_| |  __/ |   \n" +
                " \\____|_|\\__\\___| \\_/\\_/  |_|_| |_|\\__,_|\\___|_|   ");

        System.out.printf("Welcome to CiteWinder %s\n", version);
        // TODO check for default values


        org.jbibtex.BibTeXParser parser = new org.jbibtex.BibTeXParser();
    }
}
