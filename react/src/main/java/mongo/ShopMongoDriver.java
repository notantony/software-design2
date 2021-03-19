package mongo;

import com.mongodb.client.model.InsertOneOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.rx.client.MongoClient;
import com.mongodb.rx.client.MongoClients;
import com.mongodb.rx.client.MongoCollection;
import com.mongodb.rx.client.Success;
import mongo.entities.Product;
import mongo.entities.User;
import org.bson.Document;
import rx.Observable;

import static com.mongodb.client.model.Filters.eq;

public class ShopMongoDriver {
    private final MongoClient client;

    public ShopMongoDriver(int port) {
        client = createMongoClient(port);
    }

    public Observable<User> getUser(Integer id) {
        return client.getDatabase("main")
                .getCollection("users")
                .find(eq("id", id))
                .toObservable()
                .first()
                .map(User::new);
    }

    public Observable<Product> getProduct(Integer id) {
        return client.getDatabase("main")
                .getCollection("product")
                .find(eq("id", id))
                .toObservable()
                .first()
                .map(Product::new);
    }

    public Observable<User> getAllUsers() {
        return client.getDatabase("main")
                .getCollection("users")
                .find()
                .toObservable()
                .map(User::new);
    }

    public Observable<Product> getAllProducts() {
        return client.getDatabase("main")
                .getCollection("products")
                .find()
                .toObservable()
                .map(Product::new);
    }

    public Observable<UpdateResult> addUser(User user) {
        return client.getDatabase("main")
                .getCollection("users")
                .replaceOne(eq("id", user.id), user.toDocument(), new UpdateOptions().upsert(true));
    }

    public Observable<UpdateResult> addProduct(Product product) {
        return client.getDatabase("main")
                .getCollection("products")
                .replaceOne(eq("id", product.id), product.toDocument(), new UpdateOptions().upsert(true));
//                .updateOne(eq("id", user.id), user.toDocument(), new UpdateOptions().upsert(true));
//                .insertOne(user.toDocument(), new InsertOneOptions());
    }

    private static MongoClient createMongoClient(int port) {
        return MongoClients.create("mongodb://localhost:" + port);
    }
}
