package remote.genes;

import akka.actor.ActorRef;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Router;
import com.google.common.collect.ImmutableSet;
import remote.genes.messages.GeneTupleMessage;

public class GeneSchedulingStrategy {

    private final ActorRef master;
    private Router workerRouter = new Router(new RoundRobinRoutingLogic());
    private int numberOfWorkers = 0;

    public GeneSchedulingStrategy(ActorRef master) {
        this.master = master;
    }

    public void schedule(ImmutableSet<String> genes) {
        for (String geneA : genes) {
            for (String geneB: genes) {
                if (!geneA.equals(geneB)) {
                    workerRouter.route(new GeneTupleMessage(geneA, geneB), this.master);
                }
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
}
