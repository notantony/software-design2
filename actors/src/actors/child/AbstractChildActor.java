package actors.child;

import akka.actor.UntypedActor;
import search.SearchResponse;

public abstract class AbstractChildActor extends UntypedActor {
    public AbstractChildActor() {
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof String) {
            try {
                SearchResponse response = processQuery((String) o);
                getContext().parent().tell(response, self());
            } catch (Exception e) {
                e.printStackTrace();
                getContext().parent().tell(e, self());
            }
        } else {
            throw new Exception("Unexpected query object received: " + o);
        }
    }

    abstract SearchResponse processQuery(String query) throws Exception;
}
