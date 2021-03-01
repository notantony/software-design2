import actors.child.BingActor;
import actors.child.GoogleActor;
import actors.MasterActor;
import actors.child.YandexActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.pyruby.stubserver.StubServer;
import org.junit.Test;
import search.SearchResponse;
import stub.JsonStubServer;
import stub.TestingServer;
import stub.YandexStubServer;

import java.util.*;

import static org.junit.Assert.*;

public class StubTests {
    private void testWith(List<TestingServer> servers, long masterTimeLimit, List<Boolean> expected) {
        List<SearchResponse> masterStorage = new ArrayList<>();
        try {

            ActorSystem system = ActorSystem.create("TestSystem");
            ActorRef master = system.actorOf(Props.create(MasterActor.class, "search", masterTimeLimit, masterStorage));

            master.tell("start", ActorRef.noSender());
            servers.forEach(TestingServer::start);

            Thread.sleep(masterTimeLimit * 5);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        } finally {
            servers.forEach(TestingServer::stop);
        }

        masterStorage.forEach(System.out::println);


        List<SearchResponse> expectedResult = new ArrayList<>();
        for (int i = 0; i < expected.size(); i++) {
            if (expected.get(i)) {
                expectedResult.add(new SearchResponse(servers.get(i).getName(), servers.get(i).getAnswers()));
            }
        }

        assertTrue(masterStorage.containsAll(expectedResult));
        assertEquals(expectedResult.size(), masterStorage.size());
    }


    @Test
    public void TestAllSuccess() {
        List<TestingServer> servers = List.of(
                new JsonStubServer(GoogleActor.PORT, "Google", 0),
                new JsonStubServer(BingActor.PORT, "Bing", 100),
                new YandexStubServer(YandexActor.PORT, false)
        );
        List<Boolean> expected = List.of(
                true,
                true,
                true
        );
        testWith(servers, 1000, expected);
    }

    @Test
    public void TestAllFail() {
        List<TestingServer> servers = List.of(
                new JsonStubServer(GoogleActor.PORT, "Google", -1),
                new JsonStubServer(BingActor.PORT, "Bing", -1),
                new YandexStubServer(YandexActor.PORT, true)
        );
        List<Boolean> expected = List.of(
                false,
                false,
                false
        );
        testWith(servers, 1000, expected);
    }

    @Test
    public void TestGoogleFail() {
        List<TestingServer> servers = List.of(
                new JsonStubServer(GoogleActor.PORT, "Google", 2700),
                new JsonStubServer(BingActor.PORT, "Bing", 300),
                new YandexStubServer(YandexActor.PORT, false)
        );
        List<Boolean> expected = List.of(
                false,
                true,
                true
        );
        testWith(servers, 1000, expected);
    }

    @Test
    public void TestBingYandexFail() {
        List<TestingServer> servers = List.of(
                new JsonStubServer(GoogleActor.PORT, "Google", 700),
                new JsonStubServer(BingActor.PORT, "Bing", -1),
                new YandexStubServer(YandexActor.PORT, true)
        );
        List<Boolean> expected = List.of(
                true,
                false,
                false
        );
        testWith(servers, 1000, expected);
    }

    @Test
    public void TestBingSlow() {
        List<TestingServer> servers = List.of(
                new JsonStubServer(GoogleActor.PORT, "Google", 800),
                new JsonStubServer(BingActor.PORT, "Bing", 2600),
                new YandexStubServer(YandexActor.PORT, true)
        );
        List<Boolean> expected = List.of(
                true,
                false,
                false
        );
        testWith(servers, 1000, expected);
    }

//    @Test
//    public void TestGoogleSlow() {
//        JsonStubServer googleServer = new JsonStubServer(GoogleActor.PORT);
//        googleServer.start();
//        YandexStubServer yandexServer = new YandexStubServer(YandexActor.PORT);
//        yandexServer.start();
//
//        List<SearchResponse> result = new ArrayList<>();
//
//        ActorSystem system = ActorSystem.create("TestSystem");
//        ActorRef master = system.actorOf(Props.create(MasterActor.class, "slow", result));
//        master.tell("start", ActorRef.noSender());
//
//        try {
//            Thread.sleep(5000);
//            googleServer.stop();
//            yandexServer.stop();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//
//        List<SearchResponse> expectedResult = new ArrayList<>();
//        expectedResult.add(new SearchResponse("Yandex", YandexStubServer.getAnswers("slow_")));
//
//        expectedResult.sort(Comparator.comparing(SearchResponse::getSource));
//        result.sort(Comparator.comparing(SearchResponse::getSource));
//        assertEquals(expectedResult.toString(), result.toString());
//    }
//
//    @Test
//    public void TestNoResponse() {
//        JsonStubServer googleServer = new JsonStubServer(GoogleActor.PORT + 10);
//        googleServer.start();
//        YandexStubServer yandexServer = new YandexStubServer(YandexActor.PORT + 10);
//        yandexServer.start();
//
//        List<SearchResponse> result = new ArrayList<>();
//
//        ActorSystem system = ActorSystem.create("TestSystem");
//        ActorRef master = system.actorOf(Props.create(MasterActor.class, "test", result));
//        master.tell("start", ActorRef.noSender());
//
//        try {
//            Thread.sleep(5000);
//            googleServer.stop();
//            yandexServer.stop();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        assertTrue(result.isEmpty());
//    }
}
