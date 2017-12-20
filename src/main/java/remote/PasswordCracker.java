package remote;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.typesafe.config.Config;
import model.Student;
import remote.messages.PasswordHashListMessage;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static utils.AkkaUtils.createRemoteAkkaConfig;

public class PasswordCracker {

    private static final int MAX_PASSWORD_LENGTH = 7;
    private static final String DEFAULT_MASTER_SYSTEM_NAME = "PasswordMasterActorSystem";

    public static void runMaster(ImmutableList<Student> students, int numLocalWorkers) {
        ActorSystem actorSystem = ActorSystem.create(DEFAULT_MASTER_SYSTEM_NAME);

        actorSystem.actorOf(Reaper.props(), Reaper.DEFAULT_NAME);
        ActorRef listener = actorSystem.actorOf(PasswordListener.props(students), PasswordListener.DEFAULT_NAME);
        ActorRef master = actorSystem.actorOf(
                PasswordMaster.props(listener, numLocalWorkers), PasswordMaster.DEFAULT_NAME);

        ImmutableSet<String> passwordHashes = students.stream()
                .map(Student::getPasswordHash)
                .collect(toImmutableSet());
        master.tell(new PasswordHashListMessage(passwordHashes, MAX_PASSWORD_LENGTH), ActorRef.noSender());
    }
}
