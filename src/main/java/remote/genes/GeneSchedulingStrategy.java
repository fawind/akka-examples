package remote.genes;

import akka.actor.ActorRef;
import akka.routing.Router;
import akka.routing.SmallestMailboxRoutingLogic;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import remote.genes.messages.GeneTupleMessage;

public class GeneSchedulingStrategy {

    private final ActorRef master;
    private Router workerRouter = new Router(new SmallestMailboxRoutingLogic());
    private int numberOfWorkers = 0;
    private int pendingTasks = 0;

    public GeneSchedulingStrategy(ActorRef master) {
        this.master = master;
    }

    public void schedule(ImmutableSet<String> genes) {
        ImmutableList<String> orderedGenes = ImmutableList.copyOf(genes);
        for (int i = 0; i < orderedGenes.size(); i++) {
            for (int j = i + 1; j < orderedGenes.size(); j++) {
                workerRouter.route(new GeneTupleMessage(orderedGenes.get(i), orderedGenes.get(j)), this.master);
                pendingTasks++;
            }
        }
    }

    public void addWorker(ActorRef worker) {
        numberOfWorkers++;
        workerRouter = workerRouter.addRoutee(worker);
    }

    public void removeWorker(ActorRef worker) {
        numberOfWorkers--;
        workerRouter = workerRouter.removeRoutee(worker);
    }

    public int getNumberOfWorkers() {
        return numberOfWorkers;
    }

    public void finishTask() {
        pendingTasks--;
    }

    public boolean hasPendingTasks() {
        return pendingTasks > 0;
    }
}
