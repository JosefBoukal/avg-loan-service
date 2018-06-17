package cz.creativedock.interview;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;

import java.util.List;

/**
 * Utilities to work with HTTP headers.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpHeadersUtils {

    public static int intValue(HttpHeaders headers, String headerName, int defaultValue) {
        List<String> values = headers.get(headerName);
        if (values == null || values.isEmpty()) {
            return defaultValue;
        }
        return Integer.valueOf(values.get(0));
    }
}
