import org.jbibtex.ParseException;
import java.util.Scanner;
import java.util.HashMap;
import java.util.function.Supplier;

public class Main {
    private static final HashMap<Cmd, Supplier<Void>> cmdMap = new HashMap<>();

    private static void spawnCmds() {
        cmdMap.put(Cmd.CHECK_LIB, () -> {
            System.out.println("Checking library...");
            return null;
        });
        cmdMap.put(Cmd.UPDATE_NOTES, () -> {
            System.out.println("Updating notes...");
            return null;
        });
        cmdMap.put(Cmd.CONFIG, () -> {
            System.out.println("Configuring...");
            return null;
        });
    }

    private static void runCmd() {
        spawnCmds();

        Cmd userCmd;
        do {
            userCmd = readCmd();
            if (userCmd != Cmd.QUIT) {
                cmdMap.get(userCmd).get();
            }
        } while (userCmd != Cmd.QUIT);
    }

    private static Cmd readCmd() {
        // JBAI
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter command (help/h for options): ");
            String input = scanner.nextLine().trim().toUpperCase();
            try {
                return Cmd.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid command. Please try again.");
            }
        }
    }

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

        runCmd();

        org.jbibtex.BibTeXParser parser = new org.jbibtex.BibTeXParser();
    }
}
