package cz.creativedock.interview.service.zonky;

import cz.creativedock.interview.HttpHeadersUtils;
import cz.creativedock.interview.model.AverageLoanAmountResponse;
import cz.creativedock.interview.service.zonky.model.ZonkyMarketplaceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.springframework.http.HttpMethod.GET;

@RequiredArgsConstructor
@Service
public class ZonkyLoanService {

    private final RestTemplate restTemplate;

    @Value("${zonky.api.baseUrl:https://api.zonky.cz/}")
    private URI baseUri;

    @Value("${zonky.api.marketplace.pageSize:10000}")
    private int pageSize;

    @Retryable(
            value = HttpServerErrorException.class,
            maxAttemptsExpression = "${zonky.api.retry.maxAttempts:3}",
            backoff = @Backoff(
                    delayExpression = "${zonky.api.retry.delay:3000}",
                    multiplierExpression = "${zonky.api.retry.multiplier:2}"
            )
    )
    public AverageLoanAmountResponse getLoanAverage(String rating) {
        Assert.notNull(rating, "Rating is required to calculate average price for Zonky Marketplace!");
        long averageAmount = calculateAverageAmountOfAllMarketplaceLoans(rating);
        return AverageLoanAmountResponse.builder()
                .averageAmount(averageAmount)
                .build();
    }

    private long calculateAverageAmountOfAllMarketplaceLoans(String rating) {
        int page = 0;
        int count = 0;
        long sum = 0;

        URI uri = buildMarketplaceUri(rating);

        for (; ; ) {
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.set("X-Page", String.valueOf(page++));
            requestHeaders.set("X-Size", String.valueOf(pageSize));
            RequestEntity<?> requestEntity = new RequestEntity<>(null, requestHeaders, GET, uri);
            ResponseEntity<ZonkyMarketplaceResponse[]> responseEntity = restTemplate.exchange(
                    requestEntity, ZonkyMarketplaceResponse[].class
            );
            ZonkyMarketplaceResponse[] responses = responseEntity.getBody();
            for (ZonkyMarketplaceResponse response : responses) {
                sum += response.getAmount();
            }
            count += responses.length;
            int total = HttpHeadersUtils.intValue(responseEntity.getHeaders(), "X-Total", 0);
            if (count >= total) {
                break;
            }
        }
        return count == 0 ? 0 : sum / count;
    }

    private URI buildMarketplaceUri(String rating) {
        return UriComponentsBuilder.fromUri(baseUri)
                .path("/loans/marketplace")
                .queryParam("rating__eq", rating)
                .queryParam("fields", "amount")
                .build()
                .toUri();
    }

}
