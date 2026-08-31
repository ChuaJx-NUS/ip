import java.util.Scanner;

/**
 * The entry point for the BigBrother chatbot.
 */
public class BigBrother {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String banner = "______ _      ______           _   _\n"
                        + "| ___ (_)     | ___ \\         | | | |\n"
                        + "| |_/ /_  __ _| |_/ /_ __ ___ | |_| |__   ___ _ __\n"
                        + "| ___ \\ |/ _` | ___ \\ '__/ _ \\| __| '_ \\ / _ \\ '__|\n"
                        + "| |_/ / | (_| | |_/ / | | (_) | |_| | | |  __/ |\n"
                        + "\\____/|_|\\__, \\____/|_|  \\___/ \\__|_| |_|\\___|_|\n"
                        + "          __/ |\n"
                        + "         |___/\n";

        System.out.println("____________________________________________________________");
        System.out.println(banner);
        System.out.println("Hello! I'm BigBrother.");
        System.out.println("What can I do for you?");
        System.out.println("____________________________________________________________");

        //Waiting for user response
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println("____________________________________________________________");

            if (command.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println("____________________________________________________________");
                break;
            }

            System.out.println("     " + command);
            System.out.println("____________________________________________________________");
        }
    }
}
