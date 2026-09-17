package bigbrother;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

import bigbrother.exception.BigBrotherException;
import bigbrother.storage.Storage;
import bigbrother.task.Deadline;
import bigbrother.task.Event;
import bigbrother.task.Task;
import bigbrother.task.ToDo;

/**
 * Runs the BigBrother chatbot.
 */
public class BigBrother {
    private static final String SEPARATOR = "____________________________________________________________";
    private static final String COMMAND_BYE = "bye";
    private static final String COMMAND_DELETE = "delete";
    private static final String COMMAND_LIST = "list";
    private static final String COMMAND_MARK = "mark";
    private static final String COMMAND_UNMARK = "unmark";
    private static final String COMMAND_TODO = "todo";
    private static final String COMMAND_DEADLINE = "deadline";
    private static final String COMMAND_EVENT = "event";
    private static final Path DATA_FILE_PATH = Path.of("data", "bigbrother.txt");
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
        ArrayList<Task> tasks = new ArrayList<>();
        Storage storage = new Storage(DATA_FILE_PATH);

        printWelcomeMessage();

        try {
            storage.loadTasks(tasks);
        } catch (BigBrotherException exception) {
            System.out.println("     ERROR!!! " + exception.getMessage());
            System.out.println(SEPARATOR);
        }

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(SEPARATOR);

            if (command.equals(COMMAND_BYE)) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(SEPARATOR);
                break;
            }

            try {
                processCommand(command, tasks);
                if (changesTaskList(command)) {
                    storage.saveTasks(tasks);
                }
            } catch (BigBrotherException exception) {
                System.out.println("     ERROR!!! " + exception.getMessage());
            }

            System.out.println(SEPARATOR);
        }
    }

    /**
     * Processes one non-exit command.
     *
     * @param command the command entered by the user
     * @param tasks the current task list
     * @throws BigBrotherException if the command is invalid
     */
    private static void processCommand(String command, ArrayList<Task> tasks) throws BigBrotherException {
        if (command.equals(COMMAND_LIST)) {
            handleListCommand(tasks);
            return;
        }

        if (matchesCommand(command, COMMAND_MARK)) {
            handleMarkCommand(command, tasks);
            return;
        }

        if (matchesCommand(command, COMMAND_UNMARK)) {
            handleUnmarkCommand(command, tasks);
            return;
        }

        if (matchesCommand(command, COMMAND_DELETE)) {
            handleDeleteCommand(command, tasks);
            return;
        }

        if (matchesCommand(command, COMMAND_TODO)) {
            handleTodoCommand(command, tasks);
            return;
        }

        if (matchesCommand(command, COMMAND_DEADLINE)) {
            handleDeadlineCommand(command, tasks);
            return;
        }

        if (matchesCommand(command, COMMAND_EVENT)) {
            handleEventCommand(command, tasks);
            return;
        }

        throw new BigBrotherException("Invalid command. Try todo, deadline, event, list, mark,"
                + " unmark, delete, or bye.");
    }

    /**
     * Displays all tasks currently stored in the task list.
     *
     * @param tasks the current task list
     */
    private static void handleListCommand(ArrayList<Task> tasks) {
        System.out.println("     Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            String taskOutput = "     " + (i + 1) + "." + tasks.get(i);
            System.out.println(taskOutput);
        }
    }

    /**
     * Marks the task identified by a mark command as done.
     *
     * @param command the mark command entered by the user
     * @param tasks the current task list
     * @throws BigBrotherException if the task number is missing or invalid
     */
    private static void handleMarkCommand(String command, ArrayList<Task> tasks) throws BigBrotherException {
        int taskIndex = getTaskIndex(command, COMMAND_MARK, tasks.size());
        tasks.get(taskIndex).markAsDone();
        System.out.println("     Nice! I've marked this task as done:");
        System.out.println("       " + tasks.get(taskIndex));
    }

    /**
     * Marks the task identified by an unmark command as not done.
     *
     * @param command the unmark command entered by the user
     * @param tasks the current task list
     * @throws BigBrotherException if the task number is missing or invalid
     */
    private static void handleUnmarkCommand(String command, ArrayList<Task> tasks) throws BigBrotherException {
        int taskIndex = getTaskIndex(command, COMMAND_UNMARK, tasks.size());
        tasks.get(taskIndex).markAsUndone();
        System.out.println("     I've marked this task as not done:");
        System.out.println("       " + tasks.get(taskIndex));
    }

    /**
     * Deletes the task identified by a delete command.
     *
     * @param command the delete command entered by the user
     * @param tasks the current task list
     * @throws BigBrotherException if the task number is missing or invalid
     */
    private static void handleDeleteCommand(String command, ArrayList<Task> tasks) throws BigBrotherException {
        int taskIndex = getTaskIndex(command, COMMAND_DELETE, tasks.size());
        Task removedTask = tasks.remove(taskIndex);
        System.out.println("     Noted. I've removed this task:");
        System.out.println("       " + removedTask);
        System.out.println("     Now you have " + tasks.size() + " tasks in the list.");
    }

    /**
     * Adds a todo task and displays the resulting task count.
     *
     * @param command the todo command entered by the user
     * @param tasks the current task list
     * @throws BigBrotherException if the description is empty
     */
    private static void handleTodoCommand(String command, ArrayList<Task> tasks) throws BigBrotherException {
        String description = command.substring(COMMAND_TODO.length()).trim();
        if (description.isEmpty()) {
            throw new BigBrotherException("     ERROR - Empty Todo task.");
        }

        Task task = new ToDo(description);
        tasks.add(task);

        System.out.println("     Understood Creating Task:");
        System.out.println("       " + task);
        System.out.println("     Now you have " + tasks.size() + " tasks in the list.");
    }

    /**
     * Adds a deadline task when its command has the required format.
     *
     * @param command the deadline command entered by the user
     * @param tasks the current task list
     * @throws BigBrotherException if any required deadline detail is missing
     */
    private static void handleDeadlineCommand(String command, ArrayList<Task> tasks) throws BigBrotherException {
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

        Task task = new Deadline(description, by);
        tasks.add(task);

        System.out.println("     Understood Creating Task with Deadline:");
        System.out.println("       " + task);
        System.out.println("     Now you have " + tasks.size() + " tasks in the list.");
    }

    /**
     * Adds an event task when its command has the required format.
     *
     * @param command the event command entered by the user
     * @param tasks the current task list
     * @throws BigBrotherException if any required event detail is missing
     */
    private static void handleEventCommand(String command, ArrayList<Task> tasks) throws BigBrotherException {
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

        Task task = new Event(description, from, to);
        tasks.add(task);

        System.out.println("     Understood Created Event task:");
        System.out.println("       " + task);
        System.out.println("     Now you have " + tasks.size() + " tasks in the list.");
    }

    /**
     * Converts the task number in a task-selection command into a list index.
     *
     * @param command the task-selection command entered by the user
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
     * Checks whether the input is a command name or starts with that command name followed by a space.
     *
     * @param command the complete user input
     * @param commandName the command name to match
     * @return true if the input matches the command name
     */
    private static boolean matchesCommand(String command, String commandName) {
        return command.equals(commandName) || command.startsWith(commandName + " ");
    }

    /**
     * Checks whether a successful command changes task data that must be saved.
     *
     * @param command the complete user input
     * @return true if the command adds or updates a task
     */
    private static boolean changesTaskList(String command) {
        return matchesCommand(command, COMMAND_MARK)
                || matchesCommand(command, COMMAND_UNMARK)
                || matchesCommand(command, COMMAND_DELETE)
                || matchesCommand(command, COMMAND_TODO)
                || matchesCommand(command, COMMAND_DEADLINE)
                || matchesCommand(command, COMMAND_EVENT);
    }

    private static void printWelcomeMessage() {
        System.out.println(SEPARATOR);
        System.out.println(BANNER);
        System.out.println("Hello! I'm BigBrother.");
        System.out.println("What can I do for you?");
        System.out.println(SEPARATOR);
    }
}
