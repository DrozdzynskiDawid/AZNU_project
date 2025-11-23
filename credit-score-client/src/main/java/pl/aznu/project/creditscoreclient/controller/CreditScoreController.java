package pl.aznu.project.creditscoreclient.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.aznu.project.creditscoreclient.soap.generated.CreditScoreRequest;
import pl.aznu.project.creditscoreclient.soap.generated.CreditScoreResponse;
import pl.aznu.project.creditscoreclient.soap.generated.CreditScoreService;

@RestController
public class CreditScoreController {
    @Autowired
    private CreditScoreService soapClient;

    @GetMapping("/getCreditScore")
    public ResponseEntity<?> getCreditScore(
            @RequestParam("pesel") String pesel,
            @RequestParam(value = "childrenNumber", required = false) Integer childrenNumber,
            @RequestParam(value = "monthlyIncome", required = false) Double monthlyIncome,
            @RequestParam(value = "monthlyExpenses", required = false) Double monthlyExpenses,
            @RequestParam(value = "pastCreditsNumber", required = false) Integer pastCreditsNumber
    ) {
        CreditScoreRequest request = new CreditScoreRequest();
        request.setPesel(pesel);
        if (childrenNumber != null) {
            request.setChildrenNumber(childrenNumber);
        }
        if (monthlyIncome != null) {
            request.setMonthlyIncome(monthlyIncome);
        }
        if (monthlyExpenses != null) {
            request.setMonthlyExpenses(monthlyExpenses);
        }
        if (pastCreditsNumber != null) {
            request.setPastCreditsNumber(pastCreditsNumber);
        }
        try {
            CreditScoreResponse response = soapClient.checkCreditScore(request);
            return ResponseEntity.ok(response);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
