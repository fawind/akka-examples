package remote;

import akka.actor.AbstractActor;
import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.actor.Terminated;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Reaper extends AbstractLoggingActor {

    public static final String DEFAULT_NAME = "reaper";

    public static Props props() {
        return Props.create(Reaper.class);
    }

    public static class WatchMeMessage implements Serializable {}

    public static void watchWithDefaultReaper(AbstractActor actor) {
        ActorSelection defaultReaper = actor.getContext().getSystem().actorSelection("/user/" + DEFAULT_NAME);
        defaultReaper.tell(new WatchMeMessage(), actor.getSelf());
    }

    private final Set<ActorRef> watchees = new HashSet<>();

    @Override
    public void preStart() throws Exception {
        super.preStart();
        log().info("Started {}", getSelf());
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        log().info("Stopped {}", getSelf());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(WatchMeMessage.class, this::handle)
                .match(Terminated.class, this::handle)
                .matchAny(m -> log().error("{} received unknown message: {}", getClass().getName(), m))
                .build();
    }

    private void handle(WatchMeMessage message) {
        final ActorRef sender = getSender();
        if (watchees.add(sender)) {
            getContext().watch(sender);
            log().info("Started watching {}", sender);
        }
    }

    private void handle(Terminated message) {
        final ActorRef sender = getSender();
        if (watchees.remove(sender)) {
            log().info("Reaping {}.", sender);
            if (watchees.isEmpty()) {
                log().info("Every local actor has been reaped. Terminating the actor system...");
                getContext().getSystem().terminate();
            }
        } else {
            log().error("Got termination message from unwatched {}", sender);
        }
    }
}
