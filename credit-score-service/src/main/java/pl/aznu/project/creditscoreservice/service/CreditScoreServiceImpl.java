package pl.aznu.project.creditscoreservice.service;

import jakarta.jws.WebService;
import org.springframework.stereotype.Service;
import pl.aznu.project.creditscoreservice.model.CreditScoreRequest;
import pl.aznu.project.creditscoreservice.model.CreditScoreResponse;

import java.util.Date;

@Service
@WebService(endpointInterface = "pl.aznu.project.creditscoreservice.service.CreditScoreService")
public class CreditScoreServiceImpl implements CreditScoreService {
    @Override
    public CreditScoreResponse checkCreditScore(CreditScoreRequest request) {
        CreditScoreResponse response = new CreditScoreResponse();
        int score = 0;
        String pesel = request.getPesel();
        if (pesel == null || pesel.length() != 11) {
            throw new RuntimeException("BŁĄD: Pesel musi mieć 11 znaków!");
        }
        int yearOfBirth = Integer.parseInt(pesel.substring(0, 2));
        int actualYear = new Date().getYear();
        int customerAge = actualYear - yearOfBirth;
        if (customerAge > 40) score = score - 20;

        Double monthlyIncome = request.getMonthlyIncome();
        Double monthlyExpenses = request.getMonthlyExpenses();
        if (monthlyIncome != null && monthlyExpenses != null) {
            if ((monthlyIncome - monthlyExpenses) < 0) {
                response.setScore(0);
                return response;
            }
            else {
                int diff = (int) (monthlyIncome - monthlyExpenses);
                score = score + (diff / 50);
            }
        }

        Integer childrenNumber = request.getChildrenNumber();
        if (childrenNumber != null && childrenNumber > 0) {
            score = score - (childrenNumber * 5);
        }

        Integer pastCreditsNumber = request.getPastCreditsNumber();
        if (pastCreditsNumber != null && pastCreditsNumber > 0) {
            score = score + (pastCreditsNumber * 3);
        }

        if (score < 0) score = 0;
        if (score > 100) score = 100;
        response.setScore(score);
        return response;
    }
}
