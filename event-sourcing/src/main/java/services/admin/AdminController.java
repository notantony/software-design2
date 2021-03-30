package services.gate;


import database.events.AccountCreated;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import services.QueryProcessingError;
import util.placeholders.StringHolder;


@Controller
public class GateController {
    private final GateClient gateClient;

    public GateController(GateClient gateClient) {
        this.gateClient = gateClient;
    }

    @RequestMapping(value = "/gate", method = RequestMethod.GET)
    public String gate(ModelMap map) {
        clearMsg(map);
        map.addAttribute("user_id", new StringHolder());
        return "gate";
    }

    @RequestMapping(value = "/enter", method = RequestMethod.POST)
    public String enter(@ModelAttribute("user_id") StringHolder userId, ModelMap map) {
        clearMsg(map);
        try {
            Integer id = Integer.parseInt(userId.getValue());
            AccountCreated created = gateClient.getAccountCreated(id);
            boolean ok = gateClient.checkEnter(id);
            if (ok) {
                msg("Welcome, " + created.getName(), map);
                success("OK", map);
            }
        } catch (NumberFormatException e) {
            error("Unexpected id format: " + userId, map);
        } catch (QueryProcessingError e) {
            error(e.getMessage(), map);
        }
        return "gate";
    }

    public void msg(String message, ModelMap map) {
        map.addAttribute("message", message);
    }

    public void success(String message, ModelMap map) {
        map.addAttribute("success", message);
        map.addAttribute("error", "");
    }

    public void error(String message, ModelMap map) {
        map.addAttribute("success", "");
        map.addAttribute("error", message);
    }


    public void clearMsg(ModelMap map) {
        map.addAttribute("message", "");
        map.addAttribute("success", "");
        map.addAttribute("error", "");
    }

//    @RequestMapping(value = "/add-product", method = RequestMethod.POST)
//    public String addProduct(@ModelAttribute("product") ProductHolder product, @ModelAttribute("item_cur") String itemCur, ModelMap map) {
//        String answer;
//        try {
//            Map<String, String> params = new HashMap<>();
//            params.put("id", product.id);
//            params.put("name", product.name);
//            params.put("defaultCurrency", (String) map.get("item_cur"));
//            if (!product.priceRUB.equals("")) {
//                params.put("price_RUB", product.priceRUB);
//            }
//            if (!product.priceUSD.equals("")) {
//                params.put("price_USD", product.priceUSD);
//            }
//            if (!product.priceEUR.equals("")) {
//                params.put("price_EUR", product.priceEUR);
//            }
//            System.err.println(params);
//            answer = shopClient.request("add-product", params);
//        } catch (Exception e) {
//            e.printStackTrace();
//            map.addAttribute("message", "Error occurred during request");
//            return "index";
//        }
//        map.addAttribute("message", answer);
//        map.addAttribute("user", new UserHolder());
//        map.addAttribute("product", new ProductHolder());
//
//        return "index";
//    }
//
//    @RequestMapping(value = "/get-products", method = RequestMethod.GET)
//    public String getProducts(@RequestParam String id, ModelMap map) {
//        if (id.equals("")) {
//            return "redirect:index";
//        }
//        try {
//            JSONObject user = new JSONObject(shopClient.request("get-user", Map.of("id", id)));
//
//            map.addAttribute("name", user.getString("name"));
//
//            String response = shopClient.request("get-products", Map.of("id", id));
//            System.out.print(response);
//            JSONArray responseArray = new JSONArray(response);
//            List<ProductHolder> products = new ArrayList<>();
//            for (int i = 0; i < responseArray.length(); ++i) {
//                products.add(new ProductHolder(responseArray.getJSONObject(i)));
//            }
//            fillProductsList(map, products);
//        } catch (Exception e) {
//            e.printStackTrace();
//            map.addAttribute("error_message", "Error occurred during request");
//            map.addAttribute("user", new UserHolder());
//            return "index";
//        }
//        return "products";
//    }
//
//
//    @RequestMapping(value = "/products", method = RequestMethod.GET)
//    public String products() {
//        return "products";
//    }
//
//
//    private void fillProductsList(ModelMap map, List<ProductHolder> productsList) {
//        map.addAttribute("products_list", productsList);
//    }

}