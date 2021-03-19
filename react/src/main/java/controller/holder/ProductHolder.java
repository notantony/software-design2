package controller.holder;

import org.json.JSONObject;

public class ProductHolder {
    public String name, price;
    public String priceUSD = "", priceRUB = "", priceEUR = "", id = "";

    public ProductHolder() {

    }

    public ProductHolder(JSONObject jsonObject) {
        name = jsonObject.getString("name");
        price = jsonObject.getString("price");
    }

    public ProductHolder(String json) {
        JSONObject jsonObject = new JSONObject(json);
        name = jsonObject.getString("name");
        price = jsonObject.getString("price");
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getPriceEUR() {
        return priceEUR;
    }

    public String getPriceRUB() {
        return priceRUB;
    }

    public String getPriceUSD() {
        return priceUSD;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setPriceEUR(String priceEUR) {
        this.priceEUR = priceEUR;
    }

    public void setPriceRUB(String priceRUB) {
        this.priceRUB = priceRUB;
    }

    public void setPriceUSD(String priceUSD) {
        this.priceUSD = priceUSD;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
