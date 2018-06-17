package cz.creativedock.interview.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * The REST response model with average amount of all Loans of particular rating.
 */
@Getter
@ToString
@Builder
public class AverageLoanAmountResponse {
    private long averageAmount;
}
