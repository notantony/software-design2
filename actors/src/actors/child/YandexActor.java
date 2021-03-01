package actors.child;

import actors.MasterActor;
import akka.actor.UntypedActor;
import search.HTTPRequester;
import search.SearchResponse;

import java.io.IOException;

public class YandexActor extends AbstractChildActor {
    static public int PORT = 5051;
    static private final String API_ADDRESS = "http://localhost:" + PORT;

    @Override
    SearchResponse processQuery(String query) throws IOException {
        HTTPRequester requester = new HTTPRequester(API_ADDRESS);
        String responseString = requester.rawRequest(query);

        SearchResponse searchResponse = new SearchResponse("Yandex");
        String[] entries = responseString.split(" ");
        for (String entry : entries) {
            searchResponse.append(entry);
        }

        return searchResponse;
    }
}
