package remote;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;
import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import org.jboss.netty.channel.socket.Worker;
import remote.messages.PasswordFoundMessage;
import remote.messages.PasswordValidationMessage;
import utils.PasswordRange;

public class PasswordWorker extends AbstractLoggingActor {

    public static Props props() {
        return Props.create(Worker.class);
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        Reaper.watchWithDefaultReaper(this);
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        this.log().info("Stopped {}.", this.getSelf());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(PasswordValidationMessage.class, this::handle)
                .matchAny(m -> this.log().info("%s received unknown message: %s", this.getClass().getName(), m))
                .build();
    }

    private void handle(PasswordValidationMessage message) {
        this.log().info("Start checking hashes from %d to %d", message.getStartNumber(), message.getStartNumber());
        new PasswordRange(message.getStartNumber(), message.getEndNumber())
                .forEachRemaining(number -> {
                    if (getHash(number).equals(message.getPasswordHash())) {
                        this.getSender().tell(new PasswordFoundMessage(number), this.getSelf());
                    }
                });
    }

    private String getHash(String value) {
        return Hashing.sha256().hashString(value, Charsets.UTF_8).toString();
    }
}
