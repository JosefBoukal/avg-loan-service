package cz.creativedock.interview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class AvgLoanServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AvgLoanServiceApplication.class, args);
    }
}
