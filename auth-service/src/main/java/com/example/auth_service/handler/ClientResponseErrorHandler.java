package com.example.auth_service.handler;

import com.example.auth_service.exceptions.MicroserviceUnavailableException;
import com.example.auth_service.exceptions.RestSendMessageClientException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;

import java.util.Objects;

public class ClientResponseErrorHandler implements ErrorDecoder {
    @Override
    public Exception decode(String s, Response response) {
        HttpStatus status = HttpStatus.resolve(response.status());

        Objects.requireNonNull(status, "Статуса с кодом %s не существует в библиотеке!".formatted(response.status()));

        if (status.is5xxServerError()) {
            return new MicroserviceUnavailableException("Микросервис в данный момент не доступен!");
        } else if (status.is4xxClientError()) {
            return new RestSendMessageClientException("Клиентская ошибка при отправке запроса в микросервис!");
        }

        return new Exception("Неожиданный статус при отправке сообщения через REST!");
    }
}
