package remote;

import akka.actor.AbstractLoggingActor;
import akka.actor.PoisonPill;
import akka.actor.Props;
import com.google.common.collect.ImmutableList;
import model.Student;
import remote.messages.PasswordFoundMessage;
import remote.messages.ShutdownMessage;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

public class PasswordListener extends AbstractLoggingActor {

    public static final String DEFAULT_NAME = "password-listener";

    public static Props props(ImmutableList<Student> students) {
        return Props.create(PasswordListener.class, () -> new PasswordListener(students));
    }

    private final Map<String, String> hashToPassword = new HashMap<>();
    private final ImmutableList<Student> students;

    public PasswordListener(ImmutableList<Student> students) {
        this.students = students;
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
                .match(PasswordFoundMessage.class, this::handle)
                .match(ShutdownMessage.class, this::handle)
                .matchAny(m -> log().info("{} received unknown message: {}", getClass().getName(), m))
                .build();
    }

    private void handle(PasswordFoundMessage message) {
        hashToPassword.put(message.getPasswordHash(), message.getPassword());
    }

    private void handle(ShutdownMessage message) {
        getSelf().tell(PoisonPill.getInstance(), getSelf());
    }

    private void printResults() {
        StringBuilder stringBuilder = new StringBuilder("\nID, Name, Password");
        students.forEach(student -> {
            if (!hashToPassword.containsKey(student.getPasswordHash())) {
                log().error("===> No password for {}, {}", student.getId(), student.getName());
            }
            String password = hashToPassword.getOrDefault(student.getPasswordHash(), "No Password Found");
            stringBuilder.append(format("\n%d, %s, %s", student.getId(), student.getName(), password));
        });
        log().info("Password Results:" + stringBuilder.toString());
    }
}
