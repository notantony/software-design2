package stub;

import com.pyruby.stubserver.StubMethod;
import com.pyruby.stubserver.StubServer;
import stub.util.SearchResponseGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class YandexStubServer extends StubServer implements TestingServer {
    private final boolean fail;

    public YandexStubServer(int port, boolean fail) {
        super(port);
        this.fail = fail;
    }

    @Override
    public void start() {
        if (!fail) {
            stub(StubMethod.get("/search"))
                    .thenReturn(200,
                            "text/plain",
                            String.join(" ", getAnswers()));
        }

        stub(StubMethod.get("/search")).thenReturn(404);
        super.start();
    }

    @Override
    public List<String> getAnswers() {
        return new SearchResponseGenerator("Yandex").generateResponses(2);
    }

    @Override
    public String getName() {
        return "Yandex";
    }
}
