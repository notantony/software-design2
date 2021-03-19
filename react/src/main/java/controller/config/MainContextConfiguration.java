package controller.config;

import controller.ShopClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class MainContextConfiguration {
    int SHOP_SERVER_PORT = 8081;

    @Bean
    public ShopClient shopClient() {
        return new ShopClient(SHOP_SERVER_PORT);
    }
}
