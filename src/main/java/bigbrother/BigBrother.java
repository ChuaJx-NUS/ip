package bigbrother;

import java.util.Scanner;

/**
 * Runs the BigBrother chatbot.
 */
public class BigBrother {
    private static final int MAX_TASKS = 100;
    private static final String SEPARATOR = "____________________________________________________________";
    private static final String COMMAND_BYE = "bye";
    private static final String COMMAND_LIST = "list";
    private static final String COMMAND_MARK = "mark ";
    private static final String COMMAND_UNMARK = "unmark ";
    private static final String COMMAND_TODO = "todo ";
    private static final String COMMAND_DEADLINE = "deadline ";
    private static final String COMMAND_EVENT = "event ";
    private static final String BANNER = "______ _      ______           _   _\n"
            + "| ___ (_)     | ___ \\         | | | |\n"
            + "| |_/ /_  __ _| |_/ /_ __ ___ | |_| |__   ___ _ __\n"
            + "| ___ \\ |/ _` | ___ \\ '__/ _ \\| __| '_ \\ / _ \\ '__|\n"
            + "| |_/ / | (_| | |_/ / | | (_) | |_| | | |  __/ |\n"
            + "\\____/|_|\\__, \\____/|_|  \\___/ \\__|_| |_|\\___|_|\n"
            + "          __/ |\n"
            + "         |___/\n";

    /**
     * Starts BigBrother and processes commands until the user exits.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;

        printWelcomeMessage();

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(SEPARATOR);

            if (command.equals(COMMAND_BYE)) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(SEPARATOR);
                break;
            }

            if (command.equals(COMMAND_LIST)) {
                System.out.println("     Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    String taskOutput = "     " + (i + 1) + "." + tasks[i]; // String Overrides according to Type
                    System.out.println(taskOutput);
                }
            } else if (command.startsWith(COMMAND_MARK)) {
                int taskNumber = Integer.parseInt(command.substring(COMMAND_MARK.length()));
                int taskIndex = taskNumber - 1;
                tasks[taskIndex].markAsDone();
                System.out.println("     Nice! I've marked this task as done:");
                System.out.println("       " + tasks[taskIndex]);
            } else if (command.startsWith(COMMAND_UNMARK)) {
                int taskNumber = Integer.parseInt(command.substring(COMMAND_UNMARK.length()));
                int taskIndex = taskNumber - 1;
                tasks[taskIndex].markAsUndone();
                System.out.println("     I've marked this task as not done:");
                System.out.println("       " + tasks[taskIndex]);
            } else if (command.startsWith(COMMAND_TODO)) {
                String description = command.substring(COMMAND_TODO.length());
                tasks[taskCount] = new ToDo(description);
                taskCount++;

                System.out.println("     Understood Creating Task:");
                System.out.println("       " + tasks[taskCount - 1]);
                System.out.println("     Now you have " + taskCount + " tasks in the list.");
            } else if (command.startsWith(COMMAND_DEADLINE)) {
                String input = command.substring(COMMAND_DEADLINE.length());
                String[] parts = input.split(" /by ", 2);

                if (parts.length < 2) {
                    System.out.println("     Invalid deadline format. Please use:"
                            + " deadline <description> /by <date or time>");
                } else {
                    tasks[taskCount] = new Deadline(parts[0], parts[1]);
                    taskCount++;

                    System.out.println("     Understood Creating Task with Deadline:");
                    System.out.println("       " + tasks[taskCount - 1]);
                    System.out.println("     Now you have " + taskCount + " tasks in the list.");
                }
            } else if (command.startsWith(COMMAND_EVENT)) {
                String input = command.substring(COMMAND_EVENT.length());
                String[] descriptionAndTime = input.split(" /from ", 2);

                if (descriptionAndTime.length < 2) {
                    System.out.println("     Invalid event format. Please use:"
                            + " event <description> /from <start> /to <end>");
                } else {
                    String[] timeRange = descriptionAndTime[1].split(" /to ", 2);

                    if (timeRange.length < 2) {
                        System.out.println("     Invalid event format. Please use:"
                                + " event <description> /from <start> /to <end>");
                    } else {
                        tasks[taskCount] = new Event(descriptionAndTime[0], timeRange[0], timeRange[1]);
                        taskCount++;

                        System.out.println("     Understood Created Event task:");
                        System.out.println("       " + tasks[taskCount - 1]);
                        System.out.println("     Now you have " + taskCount + " tasks in the list.");
                    }
                }
            } else {
                tasks[taskCount] = new Task(command);
                taskCount++;
                System.out.println("     added: " + command);
            }

            System.out.println(SEPARATOR);
        }
    }

    private static void printWelcomeMessage() {
        System.out.println(SEPARATOR);
        System.out.println(BANNER);
        System.out.println("Hello! I'm BigBrother.");
        System.out.println("What can I do for you?");
        System.out.println(SEPARATOR);
    }
}
