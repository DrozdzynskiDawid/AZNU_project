package pl.aznu.project.creditscoreservice.service;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import pl.aznu.project.creditscoreservice.model.CreditScoreRequest;
import pl.aznu.project.creditscoreservice.model.CreditScoreResponse;

@WebService
public interface CreditScoreService {
    @WebMethod
    CreditScoreResponse checkCreditScore(CreditScoreRequest request);
}
