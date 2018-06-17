package cz.creativedock.interview.controller;

import cz.creativedock.interview.model.AverageLoanAmountResponse;
import cz.creativedock.interview.service.zonky.ZonkyLoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AverageLoanAmountController {

    private final ZonkyLoanService zonkyLoanService;

    @GetMapping("/api/loan/average-amount")
    public AverageLoanAmountResponse getAverageLoanAmount(@RequestParam String rating) {
        return zonkyLoanService.getLoanAverage(rating);
    }


}
