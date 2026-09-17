# Console UI Test Plan

This plan covers the user-visible task creation, modification, deletion,
display, and persistence behavior of BigBrother. Inputs are sent one command
per line. Expected output entries below are the important task-response lines
from the console transcript; startup and separator lines are also captured and
shown when the tests are run.

Unless a test states otherwise, run it from a fresh temporary working directory
that does not contain a `data` folder. This keeps saved tasks from one test from
affecting another test.

## Test 1: Reject an unknown command

### Aim

Verify that an unrecognized command reports a helpful error and is not stored.

### Input

```text
blah
list
bye
```

### Expected output

```text
     ERROR!!! Invalid command. Try todo, deadline, event, list, mark, unmark, delete, or bye.
     Here are the tasks in your list:
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
     ERROR!!! A deadline must use: deadline <description> /by <date or time>.
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
     ERROR!!! An event must use: event <description> /from <start> /to <end>.
     Here are the tasks in your list:
```

## Test 7: Reject empty task descriptions

### Aim

Verify that todo, deadline, and event commands require descriptions and do not
add tasks when their descriptions are empty.

### Input

```text
todo
deadline /by Friday
event /from 2pm /to 4pm
list
bye
```

### Expected output

```text
     ERROR!!!      ERROR - Empty Todo task.
     ERROR!!! ERROR - Empty Deadline Task.
     ERROR!!! The description of an event cannot be empty.
     Here are the tasks in your list:
```

## Test 8: Reject missing deadline and event times

### Aim

Verify that deadline and event commands explain which required time is missing.

### Input

```text
deadline submit report /by
event meeting /from
event meeting /from 2pm /to
list
bye
```

### Expected output

```text
     ERROR!!! A deadline must include a date or time after /by.
     ERROR!!! An event must include a start time after /from.
     ERROR!!! An event must include an end time after /to.
     Here are the tasks in your list:
```

## Test 9: Reject invalid task numbers

### Aim

Verify that mark and unmark commands handle missing, non-numeric, and
out-of-range task numbers without stopping the chatbot.

### Input

```text
todo borrow book
mark
mark one
mark 0
unmark 2
list
bye
```

### Expected output

```text
     Understood Creating Task:
       [T][ ] borrow book
     Now you have 1 tasks in the list.
     ERROR!!! Please provide a task number after mark.
     ERROR!!! The task number must be a whole number.
     ERROR!!! Task 0 does not exist. Choose a number from the list.
     ERROR!!! Task 2 does not exist. Choose a number from the list.
     Here are the tasks in your list:
     1.[T][ ] borrow book
```

## Test 10: Mark and unmark a typed task

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

## Test 11: Delete a task

### Aim

Verify that deleting a task displays the removed task, updates the task count,
and renumbers the remaining tasks.

### Input

```text
todo read book
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
todo join sports club
todo borrow book
delete 3
list
bye
```

### Expected output

```text
     Understood Creating Task:
       [T][ ] read book
     Now you have 1 tasks in the list.
     Understood Creating Task with Deadline:
       [D][ ] return book (by: June 6th)
     Now you have 2 tasks in the list.
     Understood Created Event task:
       [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
     Now you have 3 tasks in the list.
     Understood Creating Task:
       [T][ ] join sports club
     Now you have 4 tasks in the list.
     Understood Creating Task:
       [T][ ] borrow book
     Now you have 5 tasks in the list.
     Noted. I've removed this task:
       [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
     Now you have 4 tasks in the list.
     Here are the tasks in your list:
     1.[T][ ] read book
     2.[D][ ] return book (by: June 6th)
     3.[T][ ] join sports club
     4.[T][ ] borrow book
```

## Test 12: Reject invalid delete task numbers

### Aim

Verify that delete commands handle missing, non-numeric, and out-of-range task
numbers without removing a valid task or stopping the chatbot.

### Input

```text
todo borrow book
delete
delete one
delete 0
delete 2
list
bye
```

### Expected output

```text
     Understood Creating Task:
       [T][ ] borrow book
     Now you have 1 tasks in the list.
     ERROR!!! Please provide a task number after delete.
     ERROR!!! The task number must be a whole number.
     ERROR!!! Task 0 does not exist. Choose a number from the list.
     ERROR!!! Task 2 does not exist. Choose a number from the list.
     Here are the tasks in your list:
     1.[T][ ] borrow book
```

## Test 13: Save, load, and delete tasks across restarts

### Aim

Verify that adding, marking, and deleting tasks saves all changes, and that new
BigBrother processes load them from `data/bigbrother.txt`. Run all sessions from
the same fresh temporary working directory.

### First session input

```text
todo borrow book
deadline return book /by Friday
event project meeting /from Mon 2pm /to 4pm
mark 2
bye
```

### First session expected output

```text
     Understood Creating Task:
       [T][ ] borrow book
     Now you have 1 tasks in the list.
     Understood Creating Task with Deadline:
       [D][ ] return book (by: Friday)
     Now you have 2 tasks in the list.
     Understood Created Event task:
       [E][ ] project meeting (from: Mon 2pm to: 4pm)
     Now you have 3 tasks in the list.
     Nice! I've marked this task as done:
       [D][X] return book (by: Friday)
```

### Expected saved file

```text
T | 0 | borrow book
D | 1 | return book | Friday
E | 0 | project meeting | Mon 2pm | 4pm
```

### Second session input

```text
list
delete 1
bye
```

### Second session expected output

```text
     Here are the tasks in your list:
     1.[T][ ] borrow book
     2.[D][X] return book (by: Friday)
     3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
     Noted. I've removed this task:
       [T][ ] borrow book
     Now you have 2 tasks in the list.
```

### Third session input

```text
list
bye
```

### Third session expected output

```text
     Here are the tasks in your list:
     1.[D][X] return book (by: Friday)
     2.[E][ ] project meeting (from: Mon 2pm to: 4pm)
```

## Test 14: Handle a corrupted data file

### Aim

Verify that BigBrother reports a clear error instead of crashing when the saved
file contains an invalid task. Before starting the program, create
`data/bigbrother.txt` with the following content:

```text
T | invalid-status | damaged task
```

### Input

```text
list
bye
```

### Expected output

```text
     ERROR!!! The data file is corrupted at line 1. Starting with no tasks.
     Here are the tasks in your list:
```
