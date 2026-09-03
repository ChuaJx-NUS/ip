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

            taskCount = processCommand(command, tasks, taskCount);

            System.out.println(SEPARATOR);
        }
    }

    /**
     * Processes one non-exit command and returns the updated number of tasks.
     *
     * @param command the command entered by the user
     * @param tasks the current task list
     * @param taskCount the current number of tasks
     * @return the number of tasks after processing the command
     */
    private static int processCommand(String command, Task[] tasks, int taskCount) {
        if (command.equals(COMMAND_LIST)) {
            handleListCommand(tasks, taskCount);
            return taskCount;
        }

        if (command.startsWith(COMMAND_MARK)) {
            handleMarkCommand(command, tasks);
            return taskCount;
        }

        if (command.startsWith(COMMAND_UNMARK)) {
            handleUnmarkCommand(command, tasks);
            return taskCount;
        }

        if (command.startsWith(COMMAND_TODO)) {
            return handleTodoCommand(command, tasks, taskCount);
        }

        if (command.startsWith(COMMAND_DEADLINE)) {
            return handleDeadlineCommand(command, tasks, taskCount);
        }

        if (command.startsWith(COMMAND_EVENT)) {
            return handleEventCommand(command, tasks, taskCount);
        }

        return addGenericTask(command, tasks, taskCount);
    }

    /**
     * Displays all tasks currently stored in the task list.
     *
     * @param tasks the current task list
     * @param taskCount the number of tasks in the list
     */
    private static void handleListCommand(Task[] tasks, int taskCount) {
        System.out.println("     Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            String taskOutput = "     " + (i + 1) + "." + tasks[i];
            System.out.println(taskOutput);
        }
    }

    /**
     * Marks the task identified by a mark command as done.
     *
     * @param command the mark command entered by the user
     * @param tasks the current task list
     */
    private static void handleMarkCommand(String command, Task[] tasks) {
        int taskIndex = getTaskIndex(command, COMMAND_MARK);
        tasks[taskIndex].markAsDone();
        System.out.println("     Nice! I've marked this task as done:");
        System.out.println("       " + tasks[taskIndex]);
    }

    /**
     * Marks the task identified by an unmark command as not done.
     *
     * @param command the unmark command entered by the user
     * @param tasks the current task list
     */
    private static void handleUnmarkCommand(String command, Task[] tasks) {
        int taskIndex = getTaskIndex(command, COMMAND_UNMARK);
        tasks[taskIndex].markAsUndone();
        System.out.println("     I've marked this task as not done:");
        System.out.println("       " + tasks[taskIndex]);
    }

    /**
     * Adds a todo task and displays the resulting task count.
     *
     * @param command the todo command entered by the user
     * @param tasks the current task list
     * @param taskCount the current number of tasks
     * @return the updated number of tasks
     */
    private static int handleTodoCommand(String command, Task[] tasks, int taskCount) {
        String description = command.substring(COMMAND_TODO.length());
        int taskIndex = taskCount;
        tasks[taskIndex] = new ToDo(description);
        int updatedTaskCount = taskCount + 1;

        System.out.println("     Understood Creating Task:");
        System.out.println("       " + tasks[taskIndex]);
        System.out.println("     Now you have " + updatedTaskCount + " tasks in the list.");
        return updatedTaskCount;
    }

    /**
     * Adds a deadline task when its command has the required format.
     *
     * @param command the deadline command entered by the user
     * @param tasks the current task list
     * @param taskCount the current number of tasks
     * @return the updated number of tasks
     */
    private static int handleDeadlineCommand(String command, Task[] tasks, int taskCount) {
        String input = command.substring(COMMAND_DEADLINE.length());
        String[] parts = input.split(" /by ", 2);

        if (parts.length < 2) {
            System.out.println("     Invalid deadline format. Please use:"
                    + " deadline <description> /by <date or time>");
            return taskCount;
        }

        int taskIndex = taskCount;
        tasks[taskIndex] = new Deadline(parts[0], parts[1]);
        int updatedTaskCount = taskCount + 1;

        System.out.println("     Understood Creating Task with Deadline:");
        System.out.println("       " + tasks[taskIndex]);
        System.out.println("     Now you have " + updatedTaskCount + " tasks in the list.");
        return updatedTaskCount;
    }

    /**
     * Adds an event task when its command has the required format.
     *
     * @param command the event command entered by the user
     * @param tasks the current task list
     * @param taskCount the current number of tasks
     * @return the updated number of tasks
     */
    private static int handleEventCommand(String command, Task[] tasks, int taskCount) {
        String input = command.substring(COMMAND_EVENT.length());
        String[] descriptionAndTime = input.split(" /from ", 2);

        if (descriptionAndTime.length < 2) {
            System.out.println("     Invalid event format. Please use:"
                    + " event <description> /from <start> /to <end>");
            return taskCount;
        }

        String[] timeRange = descriptionAndTime[1].split(" /to ", 2);
        if (timeRange.length < 2) {
            System.out.println("     Invalid event format. Please use:"
                    + " event <description> /from <start> /to <end>");
            return taskCount;
        }

        int taskIndex = taskCount;
        tasks[taskIndex] = new Event(descriptionAndTime[0], timeRange[0], timeRange[1]);
        int updatedTaskCount = taskCount + 1;

        System.out.println("     Understood Created Event task:");
        System.out.println("       " + tasks[taskIndex]);
        System.out.println("     Now you have " + updatedTaskCount + " tasks in the list.");
        return updatedTaskCount;
    }

    /**
     * Adds an unrecognized command as a generic task.
     *
     * @param command the command entered by the user
     * @param tasks the current task list
     * @param taskCount the current number of tasks
     * @return the updated number of tasks
     */
    private static int addGenericTask(String command, Task[] tasks, int taskCount) {
        tasks[taskCount] = new Task(command);
        System.out.println("     added: " + command);
        return taskCount + 1;
    }

    /**
     * Converts the task number in a mark or unmark command into an array index.
     *
     * @param command the mark or unmark command entered by the user
     * @param commandPrefix the prefix to remove from the command
     * @return the zero-based index of the selected task
     */
    private static int getTaskIndex(String command, String commandPrefix) {
        int taskNumber = Integer.parseInt(command.substring(commandPrefix.length()));
        return taskNumber - 1;
    }

    private static void printWelcomeMessage() {
        System.out.println(SEPARATOR);
        System.out.println(BANNER);
        System.out.println("Hello! I'm BigBrother.");
        System.out.println("What can I do for you?");
        System.out.println(SEPARATOR);
    }
}
