package remote;

import akka.actor.AbstractLoggingActor;
import akka.actor.PoisonPill;
import akka.actor.Props;
import remote.messages.PasswordFoundMessage;
import remote.messages.ShutdownMessage;

import java.util.HashMap;
import java.util.Map;

public class PasswordListener extends AbstractLoggingActor {

    public static final String DEFAULT_NAME = "password-listener";

    public static Props props() {
        return Props.create(PasswordListener.class);
    }

    private final Map<String, String> hashToPassword = new HashMap<>();


    @Override
    public void preStart() throws Exception {
        super.preStart();
        Reaper.watchWithDefaultReaper(this);
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        log().info("Stopped {}", getSelf());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(PasswordFoundMessage.class, this::handle)
                .match(ShutdownMessage.class, this::handle)
                .matchAny(m -> log().info("{} received unknown message: {}", getClass().getName(), m))
                .build();
    }

    private void handle(PasswordFoundMessage message) {
        hashToPassword.put(message.getPasswordHash(), message.getPassword());
        log().info("Found password {}", message.getPassword());
    }

    private void handle(ShutdownMessage message) {
        getSelf().tell(PoisonPill.getInstance(), getSelf());
    }
}
