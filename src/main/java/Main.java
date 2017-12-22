import com.beust.jcommander.JCommander;
import com.google.common.collect.ImmutableList;
import configuration.Commands.Parameters;
import model.Student;
import model.StudentCsvReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import remote.PasswordCracker;
import remote.genes.GeneAnalyzer;

import java.io.IOException;
import java.nio.file.Paths;

import static java.lang.String.format;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        Parameters parameters = new Parameters();
        JCommander commander = JCommander.newBuilder()
                .addObject(parameters)
                .build();

        commander.parse(args);
        ImmutableList<Student> students = getStudents(parameters.getPath());

        switch (parameters.getTask()) {
            case PASSWORDS:
                startPasswordCracker(parameters, students);
                break;
            case GENES:
                startGeneAnalysis(parameters, students);
                break;
            case ALL:
                startPasswordCracker(parameters, students);
                startGeneAnalysis(parameters, students);
                break;
            default:
                throw new AssertionError(format("Invalid task parameter: %s", parameters.getTask()));
        }
    }

    private static ImmutableList<Student> getStudents(String path) {
        try {
            return StudentCsvReader.fromCsv(Paths.get(path));
        } catch (IOException e) {
            log.error("Error parsing csv file for path {}", path, e);
            throw new RuntimeException(e);
        }
    }

    private static void startPasswordCracker(Parameters parameters, ImmutableList<Student> students) {
        log.info("Starting password cracker with {} local worker", parameters.getNumLocalWorkers());
        PasswordCracker.runMaster(students, parameters.getNumLocalWorkers());
    }

    private static void startGeneAnalysis(Parameters parameters, ImmutableList<Student> students) {
        log.info("Starting gene analyzer with {} local worker", parameters.getNumLocalWorkers());
        GeneAnalyzer.runMaster(students, parameters.getNumLocalWorkers());
    }
}
