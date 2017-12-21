package remote.genes;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import model.Student;
import remote.Reaper;
import remote.genes.messages.GeneListMessage;
import remote.genes.worker.GeneListener;
import remote.genes.worker.GeneMaster;

import static com.google.common.collect.ImmutableSet.toImmutableSet;

public class GeneAnalyzer {

    private static final String DEFAULT_MASTER_SYSTEM_NAME = "GeneMasterActorSystem";

    public static void runMaster(ImmutableList<Student> students, int numLocalWorkers) {
        ActorSystem actorSystem = ActorSystem.create(DEFAULT_MASTER_SYSTEM_NAME);
        actorSystem.actorOf(Reaper.props(), Reaper.DEFAULT_NAME);
        ActorRef listener = actorSystem.actorOf(GeneListener.props(students), GeneListener.DEFAULT_NAME);
        ActorRef master = actorSystem.actorOf(
                GeneMaster.props(listener, numLocalWorkers), GeneMaster.DEFAULT_NAME);

        ImmutableSet<String> genes = students.stream()
                .map(Student::getGene)
                .collect(toImmutableSet());
        master.tell(new GeneListMessage(genes), ActorRef.noSender());
    }
}
