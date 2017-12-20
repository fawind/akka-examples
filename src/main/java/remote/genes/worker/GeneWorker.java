package remote.genes.worker;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;
import remote.Reaper;
import remote.genes.messages.GeneTupleMessage;
import remote.genes.messages.GeneTupleSubstringMessage;
import utils.CommonSubstring;

public class GeneWorker extends AbstractLoggingActor {

    public static Props props() {
        return Props.create(GeneWorker.class);
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
                .match(GeneTupleMessage.class, this::handle)
                .matchAny(m -> log().info("{} received unknown message: {}", getClass().getName(), m))
                .build();
    }

    private void handle(GeneTupleMessage message) {
        String longestSubstring = CommonSubstring.getLongestCommonSubstring(message.getGeneA(), message.getGeneB());
        getSender().tell(
                new GeneTupleSubstringMessage(message.getGeneA(), message.getGeneB(), longestSubstring),
                getSelf());
    }
}
