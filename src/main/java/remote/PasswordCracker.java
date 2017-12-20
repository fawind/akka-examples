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

    private static final String DEFAULT_MASTER_SYSTEM_NAME = "PasswordMasterActorSystem";

    public static void runMaster(String host, int port, int numLocalWorkers, ImmutableList<Student> students) {
        Config config = createRemoteAkkaConfig(host, port);
        ActorSystem actorSystem = ActorSystem.create(DEFAULT_MASTER_SYSTEM_NAME, config);

        actorSystem.actorOf(Reaper.props(), Reaper.DEFAULT_NAME);
        ActorRef listener = actorSystem.actorOf(PasswordListener.props(), PasswordListener.DEFAULT_NAME);
        ActorRef master = actorSystem.actorOf(
                PasswordMaster.props(listener, numLocalWorkers), PasswordMaster.DEFAULT_NAME);

        ImmutableSet<String> passwordHashes = students.stream()
                .map(Student::getPasswordHash)
                .collect(toImmutableSet());
        master.tell(new PasswordHashListMessage(passwordHashes), ActorRef.noSender());
    }
}
