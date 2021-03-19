package controller;

import javafx.application.Application;
import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.stream.Collectors;

public class ShopClient {
    int serverPort;

    public ShopClient(int serverPort) {
        this.serverPort = serverPort;
    }

    public String request(String service, Map<String, String> params) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String paramsStr = params.entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
        HttpRequest httpRequest = HttpRequest
                .newBuilder(new URI("http://localhost:" + serverPort + "/" + service + "?" + paramsStr))
                .POST(HttpRequest.BodyProcessor.fromString(""))
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandler.asString());
        if (response.statusCode() / 100 != 2) {
            throw new IOException("Bad status code: " + response.statusCode() + "\n" + response.body());
        }
        System.out.println(response.statusCode() + ":\n" + response.body());
        return response.body();
    }
}
