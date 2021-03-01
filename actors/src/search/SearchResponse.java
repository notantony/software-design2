package search;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchResponse {
    private final String source;
    private final List<String> entries;

    public SearchResponse(String source) {
        this.source = source;
        this.entries = new ArrayList<>();
    }

    public SearchResponse(String source, List<String> entries) {
        this.source = source;
        this.entries = entries;
    }

    public List<String> getEntries() {
        return entries;
    }

    public String getSource() {
        return source;
    }

    public void append(String response) {
        entries.add(response);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchResponse response = (SearchResponse) o;
        return source.equals(response.source) &&
                entries.equals(response.entries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, entries);
    }

    @Override
    public String toString() {
        return "SearchResponse{" +
                "source='" + source + '\'' +
                ", entries=" + entries +
                '}';
    }
}
