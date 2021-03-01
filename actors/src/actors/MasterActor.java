package actors;

import actors.child.BingActor;
import actors.child.GoogleActor;
import actors.child.YandexActor;
import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import scala.concurrent.duration.Duration;
import search.SearchResponse;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class MasterActor extends UntypedActor {
    private final String query;
    private int received;
    private final List<SearchResponse> storage;
    private final List<ActorRef> childrenActors;
    private final long timeout;

    public static class StopException extends RuntimeException {
        public StopException() {
            super();
        }
    }

    private static List<Props> getAllChildrenProps() {
        return List.of(Props.create(GoogleActor.class),
                Props.create(YandexActor.class),
                Props.create(BingActor.class)
        );
    }

    public MasterActor(String query, long timeout, List<SearchResponse> storage) {
        this.query = query;
        this.storage = storage;
        this.received = 0;
        this.timeout = timeout;
        UntypedActorContext context = getContext();

        this.childrenActors = getAllChildrenProps().stream()
                .map(context::actorOf)
                .collect(Collectors.toList());
    }

    private void incReceived() {
        received++;
        if (received == childrenActors.size()) {
            shutdown();
        }
    }

    private void shutdown() {
        getContext().parent().tell(this.storage, getSelf());
        getContext().become((Object o) -> {
        });
    }

    @Override
    public void onReceive(Object message) {
        if (message.equals("start")) {
            Logger.getGlobal().info("Timer started at: " + System.currentTimeMillis());
            getContext().setReceiveTimeout(Duration.create(timeout, TimeUnit.MILLISECONDS));
            for (ActorRef child : this.childrenActors) {
                child.tell(query, getSelf());
            }
        } else if (message instanceof ReceiveTimeout) {
            shutdown();
        } else if (message instanceof SearchResponse) {
            System.out.println("Received response from: " + ((SearchResponse) message).getSource());
            storage.add((SearchResponse) message);
            incReceived();
        } else if (message instanceof Exception) {
            ((Exception) message).printStackTrace();
            incReceived();
        }
    }


    @Override
    public void postStop() {
        System.out.println("Stopped");
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return new OneForOneStrategy(true, DeciderBuilder
                .match(StopException.class, e -> OneForOneStrategy.stop())
                .build());
    }
}
