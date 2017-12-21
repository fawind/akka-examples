package remote.passwords.worker;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.Terminated;
import akka.japi.pf.DeciderBuilder;
import remote.Reaper;
import remote.passwords.messages.PasswordFoundMessage;
import remote.passwords.messages.PasswordHashListMessage;
import remote.passwords.messages.PasswordRangeCompleted;
import remote.shared.ShutdownMessage;
import remote.passwords.PasswordSchedulingStrategy;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

public class PasswordMaster extends AbstractLoggingActor {

    public static final String DEFAULT_NAME = "password-master";

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
        this.listener.tell(PoisonPill.getInstance(), getSelf());
        log().info("Stopped {}.", getSelf());
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return PasswordMaster.strategy;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(PasswordHashListMessage.class, this::handle)
                .match(PasswordFoundMessage.class, this::handle)
                .match(PasswordRangeCompleted.class, this::handle)
                .match(Terminated.class, this::handle)
                .matchAny(m -> log().info("{} received unknown message: {}", getClass().getName(), m))
                .build();
    }

    private void handle(PasswordHashListMessage message) {
       schedulingStrategy.schedule(message.getPasswordHashes(), message.getMaxPasswordLength());
    }

    private void handle(PasswordFoundMessage message) {
        listener.tell(message, getSelf());
    }

    private void handle(PasswordRangeCompleted message) {
        schedulingStrategy.removeWorker(sender());
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
        return schedulingStrategy.getNumberOfWorkers() <= 0;
    }

    private void stopSelfAndListener() {
        listener.tell(new ShutdownMessage(), getSelf());
        getSelf().tell(PoisonPill.getInstance(), getSelf());
    }
}
