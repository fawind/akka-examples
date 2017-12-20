package model;

import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static com.google.common.collect.ImmutableList.toImmutableList;

public class StudentCsvReader {

    private static final String SEPARATOR = ",";

    public static ImmutableList<Student> fromCsv(Path path) throws IOException {
        return Files.lines(path)
                .map(StudentCsvReader::lineToStudent)
                .filter(Objects::nonNull)
                .collect(toImmutableList());
    }

    private static Student lineToStudent(String line) {
        String[] parts = line.trim().split(SEPARATOR);
        if (parts.length != 4) {
            return null;
        }
        return new Student(Integer.valueOf(parts[0]), parts[1], parts[2], parts[3]);
    }
}
