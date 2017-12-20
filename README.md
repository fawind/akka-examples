# Akka Examples

Akka hands-on exercise for the lecture [Distributed Data Analytics](https://hpi.de/naumann/teaching/teaching/ws-1718/distributed-data-analytics-vl-master.html).

## Tasks

1. **Password Cracking:** What are the students’ clear-text passwords?
2. **Gene Analysis:** Which student pairs share the longest gene sub-strings?

## Input

[`students.csv`](https://github.com/fawind/akka-example/blob/master/data/students.csv) with the following columns:

1. Id: Unique id of the student.
2. Name: Name of the student.
3. Password Hash: SHA-256 hash of the student's password. Passwords are numeric and length seven.
4. Gene: Gene sequence of the student.

## Usage

1. Build the jar or run from inside your IDE.
2. Run the [main method](https://github.com/fawind/akka-example/blob/master/src/main/java/Main.java) with the following program arguments:
    * `--path <path to students.csv>` - Path to the input csv file. Required.
    * `--task [passwords|genes|all]` - Which task to run. Optional, defaults to `all`.
