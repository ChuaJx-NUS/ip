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
    private static final String COMMAND_MARK = "mark";
    private static final String COMMAND_UNMARK = "unmark";
    private static final String COMMAND_TODO = "todo";
    private static final String COMMAND_DEADLINE = "deadline";
    private static final String COMMAND_EVENT = "event";
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

            try {
                taskCount = processCommand(command, tasks, taskCount);
            } catch (BigBrotherException exception) {
                System.out.println("     ERROR!!! " + exception.getMessage());
            }

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
     * @throws BigBrotherException if the command is invalid
     */
    private static int processCommand(String command, Task[] tasks, int taskCount)
            throws BigBrotherException {
        if (command.equals(COMMAND_LIST)) {
            handleListCommand(tasks, taskCount);
            return taskCount;
        }

        if (matchesCommand(command, COMMAND_MARK)) {
            handleMarkCommand(command, tasks, taskCount);
            return taskCount;
        }

        if (matchesCommand(command, COMMAND_UNMARK)) {
            handleUnmarkCommand(command, tasks, taskCount);
            return taskCount;
        }

        if (matchesCommand(command, COMMAND_TODO)) {
            return handleTodoCommand(command, tasks, taskCount);
        }

        if (matchesCommand(command, COMMAND_DEADLINE)) {
            return handleDeadlineCommand(command, tasks, taskCount);
        }

        if (matchesCommand(command, COMMAND_EVENT)) {
            return handleEventCommand(command, tasks, taskCount);
        }

        throw new BigBrotherException("Invalid command. Try todo, deadline, event, list, mark,"
                + " unmark, or bye.");
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
     * @param taskCount the number of tasks in the list
     * @throws BigBrotherException if the task number is missing or invalid
     */
    private static void handleMarkCommand(String command, Task[] tasks, int taskCount)
            throws BigBrotherException {
        int taskIndex = getTaskIndex(command, COMMAND_MARK, taskCount);
        tasks[taskIndex].markAsDone();
        System.out.println("     Nice! I've marked this task as done:");
        System.out.println("       " + tasks[taskIndex]);
    }

    /**
     * Marks the task identified by an unmark command as not done.
     *
     * @param command the unmark command entered by the user
     * @param tasks the current task list
     * @param taskCount the number of tasks in the list
     * @throws BigBrotherException if the task number is missing or invalid
     */
    private static void handleUnmarkCommand(String command, Task[] tasks, int taskCount)
            throws BigBrotherException {
        int taskIndex = getTaskIndex(command, COMMAND_UNMARK, taskCount);
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
     * @throws BigBrotherException if the description is empty or the task list is full
     */
    private static int handleTodoCommand(String command, Task[] tasks, int taskCount)
            throws BigBrotherException {
        String description = command.substring(COMMAND_TODO.length()).trim();
        if (description.isEmpty()) {
            throw new BigBrotherException("     ERROR - Empty Todo task.");
        }

        ensureTaskListHasSpace(tasks, taskCount);
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
     * @throws BigBrotherException if any required deadline detail is missing or the task list is full
     */
    private static int handleDeadlineCommand(String command, Task[] tasks, int taskCount)
            throws BigBrotherException {
        String input = command.substring(COMMAND_DEADLINE.length()).trim();
        if (input.isEmpty() || input.equals("/by") || input.startsWith("/by ")) {
            throw new BigBrotherException("ERROR - Empty Deadline Task.");
        }

        if (input.endsWith(" /by")) {
            throw new BigBrotherException("A deadline must include a date or time after /by.");
        }

        String[] parts = input.split(" /by ", 2);
        if (parts.length < 2) {
            throw new BigBrotherException("A deadline must use: deadline <description> /by <date or time>.");
        }

        String description = parts[0].trim();
        String by = parts[1].trim();
        if (description.isEmpty()) {
            throw new BigBrotherException("The description of a deadline cannot be empty.");
        }
        if (by.isEmpty()) {
            throw new BigBrotherException("A deadline must include a date or time after /by.");
        }

        ensureTaskListHasSpace(tasks, taskCount);
        int taskIndex = taskCount;
        tasks[taskIndex] = new Deadline(description, by);
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
     * @throws BigBrotherException if any required event detail is missing or the task list is full
     */
    private static int handleEventCommand(String command, Task[] tasks, int taskCount)
            throws BigBrotherException {
        String input = command.substring(COMMAND_EVENT.length()).trim();
        if (input.isEmpty() || input.equals("/from") || input.startsWith("/from ")) {
            throw new BigBrotherException("The description of an event cannot be empty.");
        }

        if (input.endsWith(" /from")) {
            throw new BigBrotherException("An event must include a start time after /from.");
        }

        String[] descriptionAndTime = input.split(" /from ", 2);
        if (descriptionAndTime.length < 2) {
            throw new BigBrotherException("An event must use: event <description> /from <start> /to <end>.");
        }

        String description = descriptionAndTime[0].trim();
        String timeInput = descriptionAndTime[1].trim();
        if (description.isEmpty()) {
            throw new BigBrotherException("The description of an event cannot be empty.");
        }
        if (timeInput.isEmpty() || timeInput.equals("/to") || timeInput.startsWith("/to ")) {
            throw new BigBrotherException("An event must include a start time after /from.");
        }
        if (timeInput.endsWith(" /to")) {
            throw new BigBrotherException("An event must include an end time after /to.");
        }

        String[] timeRange = timeInput.split(" /to ", 2);
        if (timeRange.length < 2) {
            throw new BigBrotherException("An event must use: event <description> /from <start> /to <end>.");
        }

        String from = timeRange[0].trim();
        String to = timeRange[1].trim();
        if (from.isEmpty()) {
            throw new BigBrotherException("An event must include a start time after /from.");
        }
        if (to.isEmpty()) {
            throw new BigBrotherException("An event must include an end time after /to.");
        }

        ensureTaskListHasSpace(tasks, taskCount);
        int taskIndex = taskCount;
        tasks[taskIndex] = new Event(description, from, to);
        int updatedTaskCount = taskCount + 1;

        System.out.println("     Understood Created Event task:");
        System.out.println("       " + tasks[taskIndex]);
        System.out.println("     Now you have " + updatedTaskCount + " tasks in the list.");
        return updatedTaskCount;
    }

    /**
     * Converts the task number in a mark or unmark command into an array index.
     *
     * @param command the mark or unmark command entered by the user
     * @param commandPrefix the prefix to remove from the command
     * @param taskCount the number of tasks in the list
     * @return the zero-based index of the selected task
     * @throws BigBrotherException if the task number is missing, non-numeric, or outside the task list
     */
    private static int getTaskIndex(String command, String commandPrefix, int taskCount)
            throws BigBrotherException {
        String taskNumberInput = command.substring(commandPrefix.length()).trim();
        if (taskNumberInput.isEmpty()) {
            throw new BigBrotherException("Please provide a task number after " + commandPrefix + ".");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(taskNumberInput);
        } catch (NumberFormatException exception) {
            throw new BigBrotherException("The task number must be a whole number.");
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new BigBrotherException("Task " + taskNumber + " does not exist. Choose a number from the list.");
        }
        return taskNumber - 1;
    }

    /**
     * Checks whether the task list has room for another task.
     *
     * @param tasks the current task list
     * @param taskCount the number of tasks in the list
     * @throws BigBrotherException if the task list has reached its capacity
     */
    private static void ensureTaskListHasSpace(Task[] tasks, int taskCount) throws BigBrotherException {
        if (taskCount >= tasks.length) {
            throw new BigBrotherException("The task list is full.");
        }
    }

    /**
     * Checks whether the input is a command name or starts with that command name followed by a space.
     *
     * @param command the complete user input
     * @param commandName the command name to match
     * @return true if the input matches the command name
     */
    private static boolean matchesCommand(String command, String commandName) {
        return command.equals(commandName) || command.startsWith(commandName + " ");
    }


    // Initialisation - Welcome Message Method
    private static void printWelcomeMessage() {
        System.out.println(SEPARATOR);
        System.out.println(BANNER);
        System.out.println("Hello! I'm BigBrother.");
        System.out.println("What can I do for you?");
        System.out.println(SEPARATOR);
    }
}
