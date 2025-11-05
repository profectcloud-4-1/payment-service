package profect.group1.goormdotcom.payment.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class TossPaymentConfig {
    @Value("${secret.toss.client_api_key}")
    private String clientApiKey;
    @Value("${secret.toss.secret_api_key}")
    private String secretKey;
    @Value("${secret.toss.success_url}")
    private String successUrl;
    @Value("${secret.toss.fail_url}")
    private String failUrl;

    public static final String URL = "https://api.tosspayments.com/v1/payments/";

    public String getConfirmUrl() {
        return URL;
    }
}
