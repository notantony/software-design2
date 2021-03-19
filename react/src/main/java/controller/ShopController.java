package controller;

import controller.holder.ProductHolder;
import controller.holder.UserHolder;
import mongo.entities.Product;
import mongo.entities.User;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ShopController {
    private final ShopClient shopClient;

    public ShopController(ShopClient shopClient) {
        this.shopClient = shopClient;
    }

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(ModelMap map) {
        map.addAttribute("user", new UserHolder());
        map.addAttribute("product", new ProductHolder());
        map.addAttribute("message", "");
        return "index";
    }

    @RequestMapping(value = "/add-user", method = RequestMethod.POST)
    public String addUser(@ModelAttribute("user") UserHolder user, @ModelAttribute("user_cur") String userCur, ModelMap map) {
        String answer;
        try {
            Map<String, String> params = Map.of(
                    "id", user.id,
                    "name", user.name,
                    "defaultCurrency", (String) map.get("user_cur"));
            System.err.println(params);
            answer = shopClient.request("add-user", params);
        } catch (Exception e) {
            e.printStackTrace();
            map.addAttribute("message", "Error occurred during request");
            return "index";
        }
        map.addAttribute("message", answer);
        map.addAttribute("user", new UserHolder());
        map.addAttribute("product", new ProductHolder());
        return "index";
    }

    @RequestMapping(value = "/add-product", method = RequestMethod.POST)
    public String addProduct(@ModelAttribute("product") ProductHolder product, @ModelAttribute("item_cur") String itemCur, ModelMap map) {
        String answer;
        try {
            Map<String, String> params = new HashMap<>();
            params.put("id", product.id);
            params.put("name", product.name);
            params.put("defaultCurrency", (String) map.get("item_cur"));
            if (!product.priceRUB.equals("")) {
                params.put("price_RUB", product.priceRUB);
            }
            if (!product.priceUSD.equals("")) {
                params.put("price_USD", product.priceUSD);
            }
            if (!product.priceEUR.equals("")) {
                params.put("price_EUR", product.priceEUR);
            }
            System.err.println(params);
            answer = shopClient.request("add-product", params);
        } catch (Exception e) {
            e.printStackTrace();
            map.addAttribute("message", "Error occurred during request");
            return "index";
        }
        map.addAttribute("message", answer);
        map.addAttribute("user", new UserHolder());
        map.addAttribute("product", new ProductHolder());

        return "index";
    }

    @RequestMapping(value = "/get-products", method = RequestMethod.GET)
    public String getProducts(@RequestParam String id, ModelMap map) {
        if (id.equals("")) {
            return "redirect:index";
        }
        try {
            JSONObject user = new JSONObject(shopClient.request("get-user", Map.of("id", id)));

            map.addAttribute("name", user.getString("name"));

            String response = shopClient.request("get-products", Map.of("id", id));
            System.out.print(response);
            JSONArray responseArray = new JSONArray(response);
            List<ProductHolder> products = new ArrayList<>();
            for (int i = 0; i < responseArray.length(); ++i) {
                products.add(new ProductHolder(responseArray.getJSONObject(i)));
            }
            fillProductsList(map, products);
        } catch (Exception e) {
            e.printStackTrace();
            map.addAttribute("error_message", "Error occurred during request");
            map.addAttribute("user", new UserHolder());
            return "index";
        }
        return "products";
    }


    @RequestMapping(value = "/products", method = RequestMethod.GET)
    public String products() {
        return "products";
    }


    private void fillProductsList(ModelMap map, List<ProductHolder> productsList) {
        map.addAttribute("products_list", productsList);
    }

}
