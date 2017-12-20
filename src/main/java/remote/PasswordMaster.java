package remote;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.Terminated;
import akka.japi.pf.DeciderBuilder;
import remote.messages.PasswordFoundMessage;
import remote.messages.PasswordListMessage;
import remote.messages.PasswordRangeMessage;
import remote.messages.ShutdownMessage;
import remote.scheduling.PasswordSchedulingStrategy;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

public class PasswordMaster extends AbstractLoggingActor {

    public static final String DEFAULT_NAME = "master";

    public static Props props(final ActorRef listener, final int numLocalWorkers) {
        return Props.create(PasswordMaster.class, () -> new PasswordMaster(listener, numLocalWorkers));
    }

    private static SupervisorStrategy strategy =
            new OneForOneStrategy(0, Duration.create(1, TimeUnit.SECONDS), DeciderBuilder
                    .match(Exception.class, e -> SupervisorStrategy.stop())
                    .matchAny(o -> SupervisorStrategy.escalate())
                    .build());

    private final ActorRef listener;
    private final PasswordSchedulingStrategy schedulingStrategy;
    private boolean isAcceptingRequests = true;

    public PasswordMaster(ActorRef listener, int numLocalWorkers) {
        this.listener = listener;
        schedulingStrategy = new PasswordSchedulingStrategy(getSelf());
        for (int i = 0; i < numLocalWorkers; i++) {
            ActorRef worker = getContext().actorOf(PasswordWorker.props());
            schedulingStrategy.addWorker(worker);
            getContext().watch(worker);
        }
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        Reaper.watchWithDefaultReaper(this);
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        this.listener.tell(PoisonPill.getInstance(), this.getSelf());
        this.log().info("Stopped {}.", this.getSelf());
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return PasswordMaster.strategy;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(PasswordListMessage.class, this::handle)
                .match(PasswordFoundMessage.class, this::handle)
                .match(ShutdownMessage.class, this::handle)
                .match(Terminated.class, this::handle)
                .matchAny(m -> this.log().info("%s received unknown message: %s", this.getClass().getName(), m))
                .build();
    }

    private void handle(PasswordListMessage message) {
        if (!this.isAcceptingRequests) {
            this.log().warning("Discarding request {}", message);
            return;
        }
       schedulingStrategy.schedule(message.getPasswordHashes());
    }

    private void handle(PasswordFoundMessage message) {
        listener.tell(message, getSelf());
        schedulingStrategy.finished();
        if (hasFinished()) {
            stopSelfAndListener();
        }
    }

    private void handle(ShutdownMessage message) {
        isAcceptingRequests = false;
        if (hasFinished()) {
            stopSelfAndListener();
        }
    }

    private void handle(Terminated message) {
        ActorRef sender = getSender();
        schedulingStrategy.removeWorker(sender);
        this.log().warning("{} has terminated.", sender);
        if (hasFinished()) {
            stopSelfAndListener();
        }
    }

    private boolean hasFinished() {
        return !isAcceptingRequests &&
                (!schedulingStrategy.hasTasksInProgress() || schedulingStrategy.getNumberOfWorkers() <= 0);
    }

    private void stopSelfAndListener() {
        listener.tell(new ShutdownMessage(), getSelf());
        getSelf().tell(PoisonPill.getInstance(), getSelf());
    }
}
