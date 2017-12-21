package remote.genes.worker;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.Terminated;
import akka.japi.pf.DeciderBuilder;
import remote.Reaper;
import remote.genes.GeneSchedulingStrategy;
import remote.genes.messages.GeneListMessage;
import remote.genes.messages.GeneTupleSubstringMessage;
import remote.shared.ShutdownMessage;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

public class GeneMaster extends AbstractLoggingActor {

    public static final String DEFAULT_NAME = "gene-master";

    public static Props props(ActorRef listener, int numLocalWorkers) {
        return Props.create(GeneMaster.class, () -> new GeneMaster(listener, numLocalWorkers));
    }

    private static SupervisorStrategy strategy =
            new OneForOneStrategy(0, Duration.create(1, TimeUnit.SECONDS), DeciderBuilder
                    .match(Exception.class, e -> SupervisorStrategy.stop())
                    .matchAny(o -> SupervisorStrategy.escalate())
                    .build());

    private final ActorRef listener;
    private final GeneSchedulingStrategy schedulingStrategy;

    public GeneMaster(ActorRef listener, int numLocalWorkers) {
        this.listener = listener;
        schedulingStrategy = new GeneSchedulingStrategy(getSelf());
        for (int i = 0; i < numLocalWorkers; i++) {
            ActorRef worker = getContext().actorOf(GeneWorker.props());
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
        this.listener.tell(PoisonPill.getInstance(), getSelf());
        log().info("Stopped {}.", getSelf());
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return GeneMaster.strategy;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(GeneListMessage.class, this::handle)
                .match(GeneTupleSubstringMessage.class, this::handle)
                .match(Terminated.class, this::handle)
                .matchAny(m -> log().info("{} received unknown message: {}", getClass().getName(), m))
                .build();
    }

    private void handle(GeneListMessage message) {
        schedulingStrategy.schedule(message.getGenes());
    }

    private void handle(GeneTupleSubstringMessage message) {
        schedulingStrategy.finishTask();
        listener.tell(message, getSelf());
        if (hasFinished()) {
            stopSelfAndListener();
        }
    }

    private void handle(Terminated message) {
        ActorRef sender = getSender();
        schedulingStrategy.removeWorker(sender);
        log().warning("{} has terminated", sender);
        if (hasFinished()) {
            stopSelfAndListener();
        }
    }

    private boolean hasFinished() {
        return schedulingStrategy.getNumberOfWorkers() <= 0 ||
                !schedulingStrategy.hasPendingTasks();
    }

    private void stopSelfAndListener() {
        listener.tell(new ShutdownMessage(), getSelf());
        getSelf().tell(PoisonPill.getInstance(), getSelf());
    }
}
