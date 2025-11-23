package pl.aznu.project.creditscoreclient.config;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.aznu.project.creditscoreclient.soap.generated.CreditScoreService;

@Configuration
public class SoapClientConfig {
    @Value("${soap.service.url}")
    private String soapUrl;

    @Bean(name = "soapClient")
    public CreditScoreService creditScoreClient() {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(CreditScoreService.class);
        factory.setAddress(soapUrl);
        return (CreditScoreService) factory.create();
    }
}
