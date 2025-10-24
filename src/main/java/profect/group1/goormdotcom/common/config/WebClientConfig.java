package profect.group1.goormdotcom.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import profect.group1.goormdotcom.payment.config.TossPaymentConfig;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient tossWebClient(TossPaymentConfig tossPaymentConfig) {
        return WebClient.builder()
                .baseUrl(tossPaymentConfig.getConfirmUrl())
                .defaultHeader("Content-Type", "application/json")
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                        .build())
                .build();
    }
}
