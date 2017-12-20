package remote.scheduling;

import akka.actor.ActorRef;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Router;
import com.google.common.collect.ImmutableSet;
import remote.messages.PasswordRangeMessage;

public class PasswordSchedulingStrategy {

    private static final int PASSWORD_LENGTH = 7;

	private final ActorRef master;
	private Router workerRouter = new Router(new RoundRobinRoutingLogic());
	private int numberOfWorkers = 0;

	public PasswordSchedulingStrategy(ActorRef master) {
		this.master = master;
	}

	public void schedule(ImmutableSet<String> passwordHashes) {
	    double maxPassword = Math.pow(10, PASSWORD_LENGTH) - 1;
	    int segmentLength = (int) Math.ceil(maxPassword / numberOfWorkers);

	    for (int i = 0; i < numberOfWorkers; i++) {
	        int startNumber = i * segmentLength;
	        int endNumber = startNumber + segmentLength - 1;
	        if (i == this.numberOfWorkers - 1) {
	            endNumber = (int) maxPassword;
            }
            this.workerRouter.route(new PasswordRangeMessage(startNumber, endNumber, passwordHashes), this.master);
        }
	}

	public void addWorker(final ActorRef worker) {
	    numberOfWorkers++;
	    workerRouter= workerRouter.addRoutee(worker);
	}

	public void removeWorker(final ActorRef worker) {
	    numberOfWorkers--;
	    workerRouter = workerRouter.removeRoutee(worker);
	}

	public int getNumberOfWorkers() {
        return numberOfWorkers;
	}
}
