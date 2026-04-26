package com.example.business.handler;

import com.example.business.exception.ServiceUnavailableException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class ClientResponseErrorHandler implements ErrorDecoder {
    @Override
    public Exception decode(String s, Response response) {
        return switch (response.status()) {
            case 503, 504 -> new ServiceUnavailableException("Микросервис рекомендаций в данный момент не доступен!");

            default -> new Exception("Неожиданное исключение!");
        };
    }
}
