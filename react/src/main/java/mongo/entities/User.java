package mongo.entities;

import org.bson.Document;
import org.json.JSONObject;

import java.util.Map;


public class User {
    public int id;
    public String name;
    public Product.Currency defaultCurrency;


    public User(Document doc) {
        this(doc.getInteger("id"),
                doc.getString("name"),
                Product.Currency.valueOf(doc.getString("defaultCurrency"))
        );
    }

    public User(int id, String name, Product.Currency defaultCurrency) {
        this.id = id;
        this.name = name;
        this.defaultCurrency = defaultCurrency;
    }

    public JSONObject representJSON() {
        return new JSONObject(Map.of(
                "id", id,
                "name", name));
    }

    public Document toDocument() {
        Document result = new Document();
        result.put("id", id);
        result.put("name", name);
        result.put("defaultCurrency", defaultCurrency.name());
        return result;
    }
}