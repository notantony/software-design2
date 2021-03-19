import mongo.ShopMongoDriver;
import org.json.JSONArray;
import org.json.JSONObject;
import server.ShopHttpServer;
import controller.ShopClient;

import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        int serverPort = 8081;
        ShopMongoDriver driver = new ShopMongoDriver(27017);
        ShopHttpServer server = new ShopHttpServer(driver, serverPort);
        server.start();

        ShopClient client = new ShopClient(serverPort);
        client.request("add-user",
                Map.of("id", "1",
                        "name", "Ivan_RU",
                        "defaultCurrency", "RUB"));
        client.request("add-user",
                Map.of("id", "2",
                        "name", "John",
                        "defaultCurrency", "USD"));
        client.request("add-user",
                Map.of("id", "3",
                        "name", "Charlie",
                        "defaultCurrency", "EUR"));
        client.request("add-product",
                Map.of("id", "1",
                        "name", "Milk",
                        "defaultCurrency", "RUB",
                        "price_RUB", "10",
                        "price_USD", "0.33"));
        client.request("add-product",
                Map.of("id", "2",
                        "name", "Peanut",
                        "defaultCurrency", "USD",
                        "price_USD", "0.5"));
        client.request("get-products",
                Map.of("id", "1"));
        client.request("get-products",
                Map.of("id", "2"));
        String str = client.request("get-products",
                Map.of("id", "2"));
        new JSONArray(str).getJSONObject(0).get("name");

        server.awaitShutdown();
    }
}
