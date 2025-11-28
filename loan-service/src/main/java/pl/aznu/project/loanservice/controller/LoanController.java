package pl.aznu.project.loanservice.controller;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/loans")
public class LoanController {

    @Autowired
    private ProducerTemplate producerTemplate;

    @PostMapping
    public String createLoan(@RequestBody Map<String, Object> loanData) {
        producerTemplate.sendBody("direct:startLoanSaga", loanData);
        return "Wniosek przyjęty do przetwarzania.";
    }
}