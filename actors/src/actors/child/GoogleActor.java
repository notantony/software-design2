package actors.child;

import akka.actor.UntypedActor;
import org.json.JSONObject;
import search.HTTPRequester;
import search.SearchResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GoogleActor extends AbstractChildActor {
    static public int PORT = 5050;
    static private final String API_ADDRESS = "http://localhost:" + PORT;

    public GoogleActor() {

    }

    @Override
    SearchResponse processQuery(String query) throws Exception {
        HTTPRequester requester = new HTTPRequester(API_ADDRESS);

        JSONObject response = requester.requestJson(query);
        List<String> entries = new ArrayList<>();
        for (Object entry : response.getJSONArray("entries")) {
            entries.add((String) entry);
        }

        return new SearchResponse("Google", entries);
    }
}
