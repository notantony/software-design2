package stub;

import com.pyruby.stubserver.StubMethod;
import com.pyruby.stubserver.StubServer;
import stub.util.SearchResponseGenerator;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class JsonStubServer extends StubServer implements TestingServer {
    private final int delay;
    private final String name;

    public JsonStubServer(int port, String name, int delay) {
        super(port);
        this.delay = delay;
        this.name = name;
    }

    @Override
    public void start() {
        String responseString = "{entries: " + SearchResponseGenerator.toJsonArray(getAnswers()) + "}";

        if (delay >= 0) {
            stub(StubMethod.get("/search"))
                    .delay(delay, TimeUnit.MILLISECONDS)
                    .thenReturn(200, "application/json", responseString);

            Logger.getGlobal().info("Stubbed at: " + (System.currentTimeMillis() + delay));
        }

        stub(StubMethod.get("/search")).thenReturn(404);
        super.start();
    }

    public List<String> getAnswers() {
        return new SearchResponseGenerator(this.name + "_" + (delay >= 0 ? delay : "_DEAD"))
                .generateResponses(3);
    }

    @Override
    public String getName() {
        return name;
    }
}
