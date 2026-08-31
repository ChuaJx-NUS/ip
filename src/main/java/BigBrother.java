import java.util.Scanner;

/**
 * The entry point for the BigBrother chatbot.
 */
public class BigBrother {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);   // To track inputs
        String[] tasks = new String[100];           // Store user inputs
        boolean[] taskDone = new boolean[100];      // Track completed tasks
        int taskCount = 0;
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

            // Bye checker
            if (command.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println("____________________________________________________________");
                break;
            }

            //Check inputs in List
            if (command.equals("list")) {
                System.out.println("     Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    String statusIcon = taskDone[i] ? "X" : " ";
                    System.out.println("     " + (i + 1) + ".[" + statusIcon + "] " + tasks[i]);
                }
            } else if (command.startsWith("mark ")) {
                int taskNumber = Integer.parseInt(command.substring(5));
                int taskIndex = taskNumber - 1;
                taskDone[taskIndex] = true;
                System.out.println("     Nice! I've marked this task as done:");
                System.out.println("       [X] " + tasks[taskIndex]);
            } else {
                tasks[taskCount] = command;
                taskCount++;
                System.out.println("     added: " + command);
            }

            System.out.println("____________________________________________________________");
        }
    }
}
