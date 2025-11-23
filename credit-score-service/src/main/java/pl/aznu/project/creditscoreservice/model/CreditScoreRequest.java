package pl.aznu.project.creditscoreservice.model;

import lombok.Data;

@Data
public class CreditScoreRequest {
    private String pesel;
    private Integer childrenNumber;
    private Double monthlyIncome;
    private Double monthlyExpenses;
    private Integer pastCreditsNumber;
}