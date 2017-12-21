package remote.genes.worker;

import akka.actor.AbstractLoggingActor;
import akka.actor.PoisonPill;
import akka.actor.Props;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import model.Student;
import remote.Reaper;
import remote.genes.messages.GeneTupleSubstringMessage;
import remote.shared.ShutdownMessage;
import utils.GenePartner;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.lang.String.format;

public class GeneListener extends AbstractLoggingActor {

    public static final String DEFAULT_NAME = "gene-listener";

    public static Props props(ImmutableList<Student> students) {
        return Props.create(GeneListener.class, () -> new GeneListener(students));
    }

    private final Map<Student, GenePartner> genePartners = new HashMap<>();
    private final ImmutableList<Student> students;
    private final ImmutableMap<String, Student> geneToStudent;
    private long count = 0;

    public GeneListener(ImmutableList<Student> students) {
        this.students = students;
        geneToStudent = students.stream()
                .collect(toImmutableMap(Student::getGene, student -> student));
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        Reaper.watchWithDefaultReaper(this);
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        log().info("Stopped {}", getSelf());
        printResults();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(GeneTupleSubstringMessage.class, this::handle)
                .match(ShutdownMessage.class, this::handle)
                .matchAny(m -> log().info("{} received unknown message: {}", getClass().getName(), m))
                .build();
    }

    private void handle(GeneTupleSubstringMessage message) {
        Student studentA = geneToStudent.get(message.getGeneA());
        Student studentB = geneToStudent.get(message.getGeneB());
        if (hasLargerGeneSubstring(studentA, studentB, message.getLongestSubstring()) ) {
            GenePartner newGenePartner = new GenePartner(studentA, studentB, message.getLongestSubstring());
            genePartners.put(studentA, newGenePartner);
            genePartners.put(studentB, newGenePartner);
        }
        count++;
        if (count % 10 == 0) {
            log().info("Tuple Count: {}", count);
        }

    }

    private void handle(ShutdownMessage message) {
        getSelf().tell(PoisonPill.getInstance(), getSelf());
    }

    private boolean hasLargerGeneSubstring(Student studentA, Student studentB, String substring) {
        return genePartners.containsKey(studentA) &&
                genePartners.get(studentA).getGeneMatchLength() < substring.length();
    }

    private void printResults() {
        StringBuilder stringBuilder = new StringBuilder("\nID, Name, Gene Partner, Longest Gene Match");
        students.forEach(student -> {
            if (!genePartners.containsKey(student)) {
                log().error("No gene pair found for student {}", student);
                return;
            }
            GenePartner genePartner = genePartners.get(student);
            stringBuilder.append(format("\n%d, %s, %s, %s",student.getId(), student.getName(),
                    genePartner.getOtherStudent(student), genePartner.getGeneMatch()));
        });
        log().info("Gene Match Results:" + stringBuilder.toString());
    }
}
