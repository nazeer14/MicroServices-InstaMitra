package com.pack.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

public class FeignConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }

    public static class CustomErrorDecoder implements ErrorDecoder {
        private final ErrorDecoder defaultDecoder = new Default();

        @Override
        public Exception decode(String methodKey, Response response) {
            // Customize based on status
            switch (response.status()) {
                case 404:
                    return new RuntimeException("Order not found");
                case 500:
                    return new RuntimeException("Internal server error in order service");
                default:
                    return defaultDecoder.decode(methodKey, response);
            }
        }
    }
}
