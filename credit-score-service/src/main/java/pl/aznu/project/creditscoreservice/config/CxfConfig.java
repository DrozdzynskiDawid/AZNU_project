package pl.aznu.project.creditscoreservice.config;

import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import jakarta.xml.ws.Endpoint;
import pl.aznu.project.creditscoreservice.service.CreditScoreService;

@Configuration
public class CxfConfig {

    @Autowired
    private Bus bus;

    @Autowired
    private CreditScoreService creditScoreService;

    @Bean
    public Endpoint endpoint() {
        EndpointImpl endpoint = new EndpointImpl(bus, creditScoreService);
        endpoint.publish("/soap");
        return endpoint;
    }
}