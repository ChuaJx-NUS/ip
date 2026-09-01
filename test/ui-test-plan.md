# Console UI Test Plan

This plan covers the user-visible task creation and display behavior of
BigBrother. Inputs are sent one command per line. Expected output entries below
are the important task-response lines from the console transcript; startup and
separator lines are also captured and shown when the tests are run.

## Test 1: Add a generic task

### Aim

Verify that an unrecognized command is stored as a generic task.

### Input

```text
chicken
list
bye
```

### Expected output

```text
     added: chicken
     1.[?][ ] chicken
```

## Test 2: Add a todo task

### Aim

Verify that a `todo` command creates a todo task and displays the `[T]`
marker.

### Input

```text
todo borrow book
list
bye
```

### Expected output

```text
     Understood Creating Task:
       [T][ ] borrow book
     Now you have 1 tasks in the list.
     Here are the tasks in your list:
     1.[T][ ] borrow book
```

## Test 3: Add a deadline task

### Aim

Verify that a deadline stores and displays its description and due date.

### Input

```text
deadline do homework /by Friday
list
bye
```

### Expected output

```text
     Understood Creating Task with Deadline:
       [D][ ] do homework (by: Friday)
     Now you have 1 tasks in the list.
     Here are the tasks in your list:
     1.[D][ ] do homework (by: Friday)
```

## Test 4: Reject an invalid deadline

### Aim

Verify that a deadline without `/by` reports the recommended format and does
not add a task.

### Input

```text
deadline do homework
list
bye
```

### Expected output

```text
     Invalid deadline format. Please use: deadline <description> /by <date or time>
     Here are the tasks in your list:
```

## Test 5: Add an event task

### Aim

Verify that an event stores and displays its description, start time, and end
time.

### Input

```text
event project meeting /from Mon 2pm /to 4pm
list
bye
```

### Expected output

```text
     Understood Created Event task:
       [E][ ] project meeting (from: Mon 2pm to: 4pm)
     Now you have 1 tasks in the list.
     Here are the tasks in your list:
     1.[E][ ] project meeting (from: Mon 2pm to: 4pm)
```

## Test 6: Reject an invalid event

### Aim

Verify that an event without `/from` and `/to` reports the recommended format
and does not add a task.

### Input

```text
event yes /2 /4
list
bye
```

### Expected output

```text
     Invalid event format. Please use: event <description> /from <start> /to <end>
     Here are the tasks in your list:
```

## Test 7: Mark and unmark a typed task

### Aim

Verify that marking and unmarking a typed task preserves its type-specific
display.

### Input

```text
event project meeting /from Mon 2pm /to 4pm
mark 1
unmark 1
bye
```

### Expected output

```text
     Understood Created Event task:
       [E][ ] project meeting (from: Mon 2pm to: 4pm)
     Now you have 1 tasks in the list.
     Nice! I've marked this task as done:
       [E][X] project meeting (from: Mon 2pm to: 4pm)
     I've marked this task as not done:
       [E][ ] project meeting (from: Mon 2pm to: 4pm)
```
