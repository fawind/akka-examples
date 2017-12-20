import com.beust.jcommander.JCommander;
import com.google.common.collect.ImmutableList;
import configuration.Commands.MasterCommand;
import configuration.Commands.SlaveCommand;
import model.Student;
import model.StudentCsvReader;
import remote.PasswordCracker;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.InvalidParameterException;

import static java.lang.String.format;

public class Main {

    public static void main(String[] args) {
        MasterCommand masterCommand = new MasterCommand();
        SlaveCommand slaveCommand = new SlaveCommand();
        JCommander commander = JCommander.newBuilder()
                .addObject(masterCommand)
                .addCommand("master", masterCommand)
                .addCommand("slave", slaveCommand)
                .build();

        commander.parse(args);
        if (commander.getParsedCommand() == null) {
            startMaster(masterCommand);
        } else {
            switch (commander.getParsedCommand()) {
                case "master":
                    startMaster(masterCommand);
                    break;
                case "slave":
                    startSlave(slaveCommand);
                    break;
                default:
                    throw new AssertionError(format("Invalid command: %s", commander.getParsedCommand()));

            }
        }
    }

    private static void startMaster(MasterCommand masterCommand) {
        if (masterCommand.getPath() == null) {
            throw new InvalidParameterException("--path is required");
        }
        try {
            ImmutableList<Student> students = StudentCsvReader.fromCsv(Paths.get(masterCommand.getPath()));
            PasswordCracker.runMaster(
                    masterCommand.getHost(), masterCommand.getPort(), masterCommand.getNumLocalWorkers(), students);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startSlave(SlaveCommand slaveCommand) {
        System.out.println("Starting slave");
    }
}
