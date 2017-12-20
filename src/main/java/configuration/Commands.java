package configuration;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import static java.lang.String.format;

public class Commands {

    public enum Task {
        ALL,
        PASSWORDS,
        GENES;

        public static Task fromString(String code) {
            for(Task task : Task.values()) {
                if(task.toString().equalsIgnoreCase(code)) {
                    return task;
                }
            }
            return null;
        }
    }

    private class TaskConverter implements IStringConverter<Task> {

        @Override
        public Task convert(String value) {
            Task task = Task.fromString(value);
            if (task == null) {
                throw new ParameterException(format("Value %s could not be converted. Allowed values are: %s",
                        value, Arrays.toString(Task.values())));
            }
            return task;
        }
    }

    public static class Parameters {

        @Parameter(names = {"--path"}, required = true, description = "Path of the input csv file.")
        private String path;

        @Parameter(names = {"-w", "--workers"}, description = "Number of workers to start locally.")
        private int numLocalWorkers = 2;

        @Parameter(names = {"-t", "--task"}, converter = TaskConverter.class,
                description = "Which task to work on. Works on all tasks if omitted.")
        private Task task = Task.ALL;

        public String getPath() {
            return path;
        }

        public int getNumLocalWorkers() {
            return numLocalWorkers;
        }

        public Task getTask() {
            return task;
        }
    }
}
