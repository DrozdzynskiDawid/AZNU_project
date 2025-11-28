package pl.aznu.project.creditscoreclient.route;


import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.aznu.project.creditscoreclient.soap.generated.CreditScoreRequest;
import pl.aznu.project.creditscoreclient.soap.generated.CreditScoreResponse;
import pl.aznu.project.creditscoreclient.soap.generated.CreditScoreService;

import java.util.HashMap;
import java.util.Map;

@Component
public class CreditScoreRoute extends RouteBuilder {

    @Autowired
    private CreditScoreService soapClient;

    @Override
    public void configure() throws Exception {
        String kafkaBrokers = "kafka:29092";

        from("kafka:loan-requests?brokers=" + kafkaBrokers + "&groupId=credit-service-group")
                .routeId("CreditScoreSagaRoute")
                .log("Otrzymano wniosek z Kafki: ${body}")
                .unmarshal().json(JsonLibrary.Jackson, Map.class)
                .process(exchange -> {
                    Map<String, Object> input = exchange.getIn().getBody(Map.class);
                    String sagaId = (String) input.get("id");
                    exchange.setProperty("sagaId", sagaId);

                    CreditScoreRequest request = new CreditScoreRequest();
                    request.setPesel((String) input.get("pesel"));

                    if (input.get("childrenNumber") != null)
                        request.setChildrenNumber((Integer) input.get("childrenNumber"));

                    if (input.get("monthlyIncome") != null)
                        request.setMonthlyIncome(((Number) input.get("monthlyIncome")).doubleValue());

                    if (input.get("monthlyExpenses") != null)
                        request.setMonthlyExpenses(((Number) input.get("monthlyExpenses")).doubleValue());

                    if (input.get("pastCreditsNumber") != null)
                        request.setPastCreditsNumber((Integer) input.get("pastCreditsNumber"));

                    exchange.getIn().setBody(request);
                })
                .bean(soapClient, "checkCreditScore")
                .process(exchange -> {
                    CreditScoreResponse response = exchange.getIn().getBody(CreditScoreResponse.class);
                    String sagaId = exchange.getProperty("sagaId", String.class);

                    Map<String, Object> result = new HashMap<>();
                    result.put("loanId", sagaId);
                    result.put("score", response.getScore());

                    exchange.getIn().setBody(result);
                })
                .marshal().json(JsonLibrary.Jackson)
                .log("Wysyłanie wyniku do Kafki: ${body}")
                .to("kafka:credit-score-results?brokers=" + kafkaBrokers);
    }
}