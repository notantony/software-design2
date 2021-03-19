package mongo.entities;

import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

public class Product {
    public int id;
    public String name;
    public Currency defaultCurrency;
    public Map<Currency, String> prices;

    public Product(Document doc) {
        id = doc.getInteger("id");
        name = doc.getString("name");
        defaultCurrency = Currency.valueOf(doc.getString("defaultCurrency"));
        prices = new HashMap<>();
        for (Currency currency : Currency.values()) {
            String currencyName = currency.name();
            String price = doc.getString("price_" + currencyName);
            if (price != null) {
                prices.put(Currency.valueOf(currencyName), price);
            }
        }
    }

    public Product(int id, String name, Currency defaultCurrency, String price) {
        this.id = id;
        this.name = name;
        this.defaultCurrency = defaultCurrency;
        this.prices = new HashMap<>();
        this.prices.put(defaultCurrency, price);
    }

    public void addPrice(Currency currency, String price) {
        prices.put(currency, price);
    }

    public Document toDocument() {
        Document result = new Document();
        result.put("id", id);
        result.put("name", name);
        result.put("defaultCurrency", defaultCurrency.name());
        for (Map.Entry<Currency, String> entry : prices.entrySet()) {
            result.put("price_" + entry.getKey().name(), entry.getValue());
        }
        return result;
    }

    public String represent(Currency currency) {
        if (!prices.containsKey(currency)) {
            currency = defaultCurrency;
        }
        return "Name: " + name + ". Price: " + prices.get(currency) + " " + currency;
    }

    public JSONObject representJSON(Currency currency) {
        if (!prices.containsKey(currency)) {
            currency = defaultCurrency;
        }
        return new JSONObject(Map.of(
                "name", name,
                "price", prices.get(currency) + " " + currency));
    }

    public enum Currency {
        RUB, USD, EUR
    }

}
