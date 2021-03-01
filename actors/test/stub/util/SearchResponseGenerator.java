package stub.util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SearchResponseGenerator {
    private final String label;

    public SearchResponseGenerator(String label) {
        this.label = label;
    }

    public List<String> generateResponses() {
        return generateResponses(5);
    }

    public List<String> generateResponses(int size) {
        return IntStream.range(0, size).mapToObj(i -> label + "_response_" + i).collect(Collectors.toList());
    }

    public static String toJsonArray(List<String> responses) {
        return "[" +
                responses.stream()
                        .map(response -> "'" + response + "'")
                        .collect(Collectors.joining(", ")) +
                "]";
    }
}
