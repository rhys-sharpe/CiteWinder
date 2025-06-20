import java.util.Scanner;
import java.util.HashMap;
import java.util.function.Supplier;

public class Main {
    private static final HashMap<Cmd, Supplier<Boolean>> cmdMap = new HashMap<>();
    private static final PropertyLoader config = new PropertyLoader();

    private static void spawnCmds() {
        Runner runner = new Runner(config);
        cmdMap.put(Cmd.CHECK_LIB, runner::checkLib);
        cmdMap.put(Cmd.UPDATE_NOTES, runner::updateNotes);
        cmdMap.put(Cmd.CONFIG, runner::manageConfig);
    }

    private static void runCmd() {
        spawnCmds();

        Cmd userCmd;
        do {
            userCmd = readCmd();
            if (userCmd != Cmd.QUIT) {
                if (cmdMap.get(userCmd).get()) {
                    System.out.printf("Command %s completed successfully.\n", userCmd.toString());
                } else {
                    System.out.printf("Command %s failed. Please try again.\n", userCmd.toString());
                }

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

    public static void main(String[] args) {
//        PropertyLoader config = new PropertyLoader();
        String version = config.getProperty("app.version");

        System.out.println("  ____ _ _     __        ___           _           \n" +
                " / ___(_) |_ __\\ \\      / (_)_ __   __| | ___ _ __ \n" +
                "| |   | | __/ _ \\ \\ /\\ / /| | '_ \\ / _` |/ _ \\ '__|\n" +
                "| |___| | ||  __/\\ V  V / | | | | | (_| |  __/ |   \n" +
                " \\____|_|\\__\\___| \\_/\\_/  |_|_| |_|\\__,_|\\___|_|   ");

        System.out.printf("Welcome to CiteWinder %s\n", version);
        // TODO check for default values

        runCmd();

    }
}
