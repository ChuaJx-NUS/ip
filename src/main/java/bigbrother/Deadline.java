package bigbrother;

/**
 * Represents a task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    private final String by;

    /**
     * Creates an incomplete deadline with the given description and due date.
     *
     * @param description the text that describes the deadline
     * @param by the date or time by which the deadline should be completed
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the formatted representation of this deadline.
     *
     * @return the deadline type, status, description, and due date
     */
    @Override
    public String toString() {
        return "[D][" + getStatusIcon() + "] " + getDescription() + " (by: " + by + ")";
    }
}
