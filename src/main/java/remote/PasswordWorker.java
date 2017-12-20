package remote;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import com.google.common.hash.Hashing;
import remote.messages.PasswordFoundMessage;
import remote.messages.PasswordRangeCompleted;
import remote.messages.PasswordRangeMessage;
import utils.PasswordRange;

public class PasswordWorker extends AbstractLoggingActor {

    public static Props props() {
        return Props.create(PasswordWorker.class);
    }

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
                .match(PasswordRangeMessage.class, this::handle)
                .matchAny(m -> log().info("{} received unknown message: {}", getClass().getName(), m))
                .build();
    }

    private void handle(PasswordRangeMessage message) {
        log().info("Start checking hashes from {} to {}", message.getStartNumber(), message.getEndNumber());
        ImmutableSet<String> passwordHashes = message.getPasswordHashes();
        new PasswordRange(message.getStartNumber(), message.getEndNumber())
                .forEachRemaining(number -> {
                    String hashedNumber = getHash(number);
                    if (passwordHashes.contains(hashedNumber)) {
                        getSender().tell(new PasswordFoundMessage(number, hashedNumber), getSelf());
                    }
                });
        getSender().tell(new PasswordRangeCompleted(), getSelf());
    }

    private String getHash(String value) {
        return Hashing.sha256().hashString(value, Charsets.UTF_8).toString();
    }
}
