package services.gate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    @Bean
    public GateClient gateClient() {
        return new GateClient();
    }
}