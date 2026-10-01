package bigbrother;

import java.nio.file.Path;
import java.util.ArrayList;

import bigbrother.exception.BigBrotherException;
import bigbrother.storage.Storage;
import bigbrother.task.Deadline;
import bigbrother.task.Event;
import bigbrother.task.Task;
import bigbrother.task.ToDo;
import bigbrother.ui.Ui;

/**
 * Runs the BigBrother chatbot.
 */
public class BigBrother {
    private static final String COMMAND_BYE = "bye";
    private static final String COMMAND_DELETE = "delete";
    private static final String COMMAND_LIST = "list";
    private static final String COMMAND_MARK = "mark";
    private static final String COMMAND_UNMARK = "unmark";
    private static final String COMMAND_TODO = "todo";
    private static final String COMMAND_DEADLINE = "deadline";
    private static final String COMMAND_EVENT = "event";
    private static final Path DATA_FILE_PATH = Path.of("data", "bigbrother.txt");

    /**
     * Starts BigBrother and processes commands until the user exits.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ArrayList<Task> tasks = new ArrayList<>();
        Storage storage = new Storage(DATA_FILE_PATH);

        ui.showWelcome();

        try {
            storage.loadTasks(tasks);
        } catch (BigBrotherException exception) {
            ui.showError(exception.getMessage());
            ui.showLine();
        }

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showLine();

            if (command.equals(COMMAND_BYE)) {
                ui.showGoodbye();
                ui.showLine();
                break;
            }

            try {
                processCommand(command, tasks, ui);
                if (changesTaskList(command)) {
                    storage.saveTasks(tasks);
                }
            } catch (BigBrotherException exception) {
                ui.showError(exception.getMessage());
            }

            ui.showLine();
        }
    }

    /**
     * Processes one non-exit command.
     *
     * @param command the command entered by the user
     * @param tasks the current task list
     * @param ui the user interface used to display command results
     * @throws BigBrotherException if the command is invalid
     */
    private static void processCommand(String command, ArrayList<Task> tasks, Ui ui) throws BigBrotherException {
        if (command.equals(COMMAND_LIST)) {
            handleListCommand(tasks, ui);
            return;
        }

        if (matchesCommand(command, COMMAND_MARK)) {
            handleMarkCommand(command, tasks, ui);
            return;
        }

        if (matchesCommand(command, COMMAND_UNMARK)) {
            handleUnmarkCommand(command, tasks, ui);
            return;
        }

        if (matchesCommand(command, COMMAND_DELETE)) {
            handleDeleteCommand(command, tasks, ui);
            return;
        }

        if (matchesCommand(command, COMMAND_TODO)) {
            handleTodoCommand(command, tasks, ui);
            return;
        }

        if (matchesCommand(command, COMMAND_DEADLINE)) {
            handleDeadlineCommand(command, tasks, ui);
            return;
        }

        if (matchesCommand(command, COMMAND_EVENT)) {
            handleEventCommand(command, tasks, ui);
            return;
        }

        throw new BigBrotherException("Invalid command. Try todo, deadline, event, list, mark,"
                + " unmark, delete, or bye.");
    }

    /**
     * Displays all tasks currently stored in the task list.
     *
     * @param tasks the current task list
     * @param ui the user interface used to display the task list
     */
    private static void handleListCommand(ArrayList<Task> tasks, Ui ui) {
        ui.showTaskList(tasks);
    }

    /**
     * Marks the task identified by a mark command as done.
     *
     * @param command the mark command entered by the user
     * @param tasks the current task list
     * @param ui the user interface used to display the result
     * @throws BigBrotherException if the task number is missing or invalid
     */
    private static void handleMarkCommand(String command, ArrayList<Task> tasks, Ui ui)
            throws BigBrotherException {
        int taskIndex = getTaskIndex(command, COMMAND_MARK, tasks.size());
        tasks.get(taskIndex).markAsDone();
        ui.showTaskMarked(tasks.get(taskIndex));
    }

    /**
     * Marks the task identified by an unmark command as not done.
     *
     * @param command the unmark command entered by the user
     * @param tasks the current task list
     * @param ui the user interface used to display the result
     * @throws BigBrotherException if the task number is missing or invalid
     */
    private static void handleUnmarkCommand(String command, ArrayList<Task> tasks, Ui ui)
            throws BigBrotherException {
        int taskIndex = getTaskIndex(command, COMMAND_UNMARK, tasks.size());
        tasks.get(taskIndex).markAsUndone();
        ui.showTaskUnmarked(tasks.get(taskIndex));
    }

    /**
     * Deletes the task identified by a delete command.
     *
     * @param command the delete command entered by the user
     * @param tasks the current task list
     * @param ui the user interface used to display the result
     * @throws BigBrotherException if the task number is missing or invalid
     */
    private static void handleDeleteCommand(String command, ArrayList<Task> tasks, Ui ui)
            throws BigBrotherException {
        int taskIndex = getTaskIndex(command, COMMAND_DELETE, tasks.size());
        Task removedTask = tasks.remove(taskIndex);
        ui.showTaskDeleted(removedTask, tasks.size());
    }

    /**
     * Adds a todo task and displays the resulting task count.
     *
     * @param command the todo command entered by the user
     * @param tasks the current task list
     * @param ui the user interface used to display the result
     * @throws BigBrotherException if the description is empty
     */
    private static void handleTodoCommand(String command, ArrayList<Task> tasks, Ui ui)
            throws BigBrotherException {
        String description = command.substring(COMMAND_TODO.length()).trim();
        if (description.isEmpty()) {
            throw new BigBrotherException("     ERROR - Empty Todo task.");
        }

        Task task = new ToDo(description);
        tasks.add(task);

        ui.showTodoAdded(task, tasks.size());
    }

    /**
     * Adds a deadline task when its command has the required format.
     *
     * @param command the deadline command entered by the user
     * @param tasks the current task list
     * @param ui the user interface used to display the result
     * @throws BigBrotherException if any required deadline detail is missing
     */
    private static void handleDeadlineCommand(String command, ArrayList<Task> tasks, Ui ui)
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

        Task task = new Deadline(description, by);
        tasks.add(task);

        ui.showDeadlineAdded(task, tasks.size());
    }

    /**
     * Adds an event task when its command has the required format.
     *
     * @param command the event command entered by the user
     * @param tasks the current task list
     * @param ui the user interface used to display the result
     * @throws BigBrotherException if any required event detail is missing
     */
    private static void handleEventCommand(String command, ArrayList<Task> tasks, Ui ui)
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

        Task task = new Event(description, from, to);
        tasks.add(task);

        ui.showEventAdded(task, tasks.size());
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

}
