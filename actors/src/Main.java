import actors.MasterActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import search.SearchResponse;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("Search engines");
        List<SearchResponse> result = new ArrayList<>();

        ActorRef master = system.actorOf(Props.create(MasterActor.class, result, 1000, "search"));
        master.tell("start", ActorRef.noSender());
    }
}
