package server;

import com.mongodb.client.result.UpdateResult;
import com.mongodb.rx.client.Success;
import io.netty.buffer.ByteBuf;
import mongo.entities.Product;
import mongo.entities.User;
import io.reactivex.netty.protocol.http.server.HttpServer;
import org.json.JSONArray;
import org.json.JSONObject;
import rx.Observable;
import mongo.ShopMongoDriver;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ShopHttpServer {
    private final ShopMongoDriver driver;
    private final HttpServer<ByteBuf, ByteBuf> server;

    public ShopHttpServer(ShopMongoDriver driver, int port) {
        this.driver = driver;
        server = HttpServer.newServer(port);
    }

    public void start() {
        server.start((request, response) -> {
            String service = request.getDecodedPath().substring(1);
            Map<String, List<String>> params = request.getQueryParameters();
            Observable<String> result;
            switch (service) {
                case "add-user":
                    result = addUser(params);
                    break;
                case "get-user":
                    result = getUser(params);
                    break;
                case "add-product":
                    result = addProduct(params);
                    break;
                case "get-products":
                    result = getProducts(params);
                    break;
                default:
                    result = Observable.just("Unexpected service: `" + service + "`");
            }

            result.onErrorReturn(Throwable::getMessage);
            result.timeout(2000, TimeUnit.MILLISECONDS);

            return response.writeString(result);
        });
    }

    public void awaitShutdown() {
        server.awaitShutdown();
    }

    public Observable<String> addProduct(Map<String, List<String>> params) {
        try {
            int id = Integer.parseInt(params.get("id").get(0));
            String name = params.get("name").get(0);
            Product.Currency defaultCurrency = Product.Currency.valueOf(params.get("defaultCurrency").get(0));
            String defaultPrice = params.get("price_" + defaultCurrency.name()).get(0);
            Product product = new Product(id, name, defaultCurrency, defaultPrice);
            for (Product.Currency currency : Product.Currency.values()) {
                String priceStr = "price_" + currency.name();
                if (currency != defaultCurrency && params.containsKey(priceStr)) {
                    String price = params.get(priceStr).get(0);
                    if (price != null) {
                        product.addPrice(currency, price);
                    }
                }
            }
            return driver.addProduct(product)
                    .map(result -> result.wasAcknowledged() ? "Success" : "Error while adding product");
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            e.printStackTrace();
            return Observable.just("Bad request");
        }
    }

    public Observable<String> addUser(Map<String, List<String>> params) {
        try {
            int id = Integer.parseInt(params.get("id").get(0));
            String name = params.get("name").get(0);
            Product.Currency defaultCurrency = Product.Currency.valueOf(params.get("defaultCurrency").get(0));
            User user = new User(id, name, defaultCurrency);
            return driver.addUser(user)
                    .map(result -> result.wasAcknowledged() ? "Success" : "Error while adding user");
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            e.printStackTrace();
            return Observable.just("Bad request");
        }
    }

    public Observable<String> getUser(Map<String, List<String>> params) {
        try {
            int id = Integer.parseInt(params.get("id").get(0));
            return driver.getUser(id)
                    .map(User::representJSON)
                    .map(JSONObject::toString);
        } catch (NullPointerException e) {
            e.printStackTrace();
            return Observable.just("Bad request");
        }
    }

    public Observable<String> getProducts(Map<String, List<String>> params) {
        try {
            int userId = Integer.parseInt(params.get("id").get(0));
            Observable<User> userObs = driver.getUser(userId);
            Product.Currency currency = userObs.toBlocking().first().defaultCurrency;
            return driver.getAllProducts()
                    .map(product -> product.representJSON(currency))
                    .reduce(new JSONArray(), JSONArray::put)
                    .map(JSONArray::toString);
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            e.printStackTrace();
            return Observable.just("Bad request");
        }
    }
}
