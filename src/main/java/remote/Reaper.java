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
        log().info("Started {}", this.getSelf());
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        this.log().info("Stopped {}", this.getSelf());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(WatchMeMessage.class, this::handle)
                .match(Terminated.class, this::handle)
                .matchAny(m -> this.log().error("%s received unknown message: %s", this.getClass().getName(), m))
                .build();
    }

    private void handle(WatchMeMessage message) {
        final ActorRef sender = this.getSender();
        if (this.watchees.add(sender)) {
            this.getContext().watch(sender);
            this.log().info("Started watching {}.", sender);
        }
    }

    private void handle(Terminated message) {
        final ActorRef sender = this.getSender();
        if (this.watchees.remove(sender)) {
            this.log().info("Reaping {}.", sender);
            if (this.watchees.isEmpty()) {
                this.log().info("Every local actor has been reaped. Terminating the actor system...");
                this.getContext().getSystem().terminate();
            }
        } else {
            this.log().error("Got termination message from unwatched {}.", sender);
        }
    }
}
