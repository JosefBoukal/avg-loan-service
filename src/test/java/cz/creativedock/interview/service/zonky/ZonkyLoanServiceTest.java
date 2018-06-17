package cz.creativedock.interview.service.zonky;

import cz.creativedock.interview.model.AverageLoanAmountResponse;
import cz.creativedock.interview.service.zonky.model.ZonkyMarketplaceResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ZonkyLoanServiceTest {

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private ZonkyLoanService zonkyLoanService;

    @Test
    public void givenOnePage_whenGetLoanAverage_thenReturnAverage() {
        mockRestTemplate(4);

        AverageLoanAmountResponse response = zonkyLoanService.getLoanAverage("AAA");

        verify(restTemplate).exchange(any(RequestEntity.class), (Class<?>) any());
        assertEquals(3500L, response.getAverageAmount());
    }

    @Test
    public void givenTwoPages_whenGetLoanAverage_thenReturnAverage() {
        mockRestTemplate(8);

        AverageLoanAmountResponse response = zonkyLoanService.getLoanAverage("AAA");

        verify(restTemplate, times(2)).exchange(any(RequestEntity.class), (Class<?>) any());
        assertEquals(3500L, response.getAverageAmount());
    }

    @Test
    public void givenServiceUnavailable_whenGetLoanAverage_thenRetry() {
        mockInvalidRestTemplate();

        try {
            zonkyLoanService.getLoanAverage("AAA");
        } catch (HttpServerErrorException e) {
            // ok
        }
        // verify that there have been 5 attempts to call the service (see application.properties in the test scope)
        verify(restTemplate, times(5)).exchange(any(RequestEntity.class), (Class<?>) any());
    }

    private void mockRestTemplate(int totalCount) {
        ZonkyMarketplaceResponse[] responses = new ZonkyMarketplaceResponse[]{
                new ZonkyMarketplaceResponse(1L, 2000),
                new ZonkyMarketplaceResponse(2L, 3000),
                new ZonkyMarketplaceResponse(3L, 4000),
                new ZonkyMarketplaceResponse(4L, 5000)
        };
        HttpHeaders mockHeaders = new HttpHeaders();
        mockHeaders.set("X-Total", String.valueOf(totalCount));
        ResponseEntity<?> responseEntity = new ResponseEntity<>(responses, mockHeaders, OK);
        doReturn(responseEntity)
                .when(restTemplate).exchange(any(RequestEntity.class), (Class<?>) any());
    }

    private void mockInvalidRestTemplate() {
        doThrow(new HttpServerErrorException(SERVICE_UNAVAILABLE))
                .when(restTemplate).exchange(any(RequestEntity.class), (Class<?>) any());
    }
}