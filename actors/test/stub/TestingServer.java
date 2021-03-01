package stub;

import java.util.List;

public interface TestingServer {
    void start();

    void stop();

    List<String> getAnswers();

    String getName();
}
