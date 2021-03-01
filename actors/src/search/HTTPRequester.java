package search;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HTTPRequester {
    private String host;

    public HTTPRequester(String host) {
        this.host = host;
    }

    public String rawRequest(String query) throws IOException {
        URL target = new URL(host + "/" + query);
        HttpURLConnection connection = (HttpURLConnection) target.openConnection();

        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000);
        connection.connect();

        int code = connection.getResponseCode();
        if (code != 200) {
            throw new IOException("Received response code: " + code);
        }

        return new String(connection.getInputStream().readAllBytes());
    }

    public JSONObject requestJson(String query) throws IOException {
        return new JSONObject(rawRequest(query));
    }
}
