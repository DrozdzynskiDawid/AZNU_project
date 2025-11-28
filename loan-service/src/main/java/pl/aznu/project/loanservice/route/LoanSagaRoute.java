package pl.aznu.project.loanservice.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class LoanSagaRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        String kafkaBrokers = "kafka:29092";

        from("direct:startLoanSaga")
                .routeId("StartSaga")
                .process(exchange -> {
                    Map<String, Object> body = exchange.getIn().getBody(Map.class);
                    if (!body.containsKey("id")) {
                        body.put("id", UUID.randomUUID().toString());
                    }
                })
                .log("Wysyłanie wniosku do Kafki: ${body}")
                .marshal().json(JsonLibrary.Jackson)
                .to("kafka:loan-requests?brokers=" + kafkaBrokers);

        from("kafka:credit-score-results?brokers=" + kafkaBrokers + "&groupId=loan-group")
                .routeId("CreditScoreSaga")
                .unmarshal().json(JsonLibrary.Jackson, Map.class)
                .log("Odebrano wynik punktowy analizy: ${body}")
                .process(exchange -> {
                    Map<String, Object> body = exchange.getIn().getBody(Map.class);
                    if (body.containsKey("score")) {
                        int score = Integer.parseInt(body.get("score").toString());
                        if (score > 50) {
                            body.put("decision", "POSITIVE");
                        }
                        else {
                            body.put("decision", "NEGATIVE");
                        }
                    }
                })
                .marshal().json(JsonLibrary.Jackson)
                .to("kafka:loan-results?brokers=" + kafkaBrokers);

        from("kafka:loan-results?brokers=" + kafkaBrokers + "&groupId=loan-group")
                .routeId("EndSaga")
                .unmarshal().json(JsonLibrary.Jackson, Map.class)
                .log("Odebrano wynik analizy: ${body}")
                .end();
    }
}
