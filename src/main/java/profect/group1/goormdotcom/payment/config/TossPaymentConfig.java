package profect.group1.goormdotcom.payment.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class TossPaymentConfig {
    @Value("${TOSS_CLIENT_API_KEY}")
    private String clientApiKey;
    @Value("${TOSS_SECRET_API_KEY}")
    private String secretKey;
    @Value("${TOSS_SUCCESS_URL}")
    private String successUrl;
    @Value("${TOSS_FAIL_URL}")
    private String failUrl;

    public static final String URL = "https://api.tosspayments.com/v1/payments/";

    public String getConfirmUrl() {
        return URL;
    }
}
