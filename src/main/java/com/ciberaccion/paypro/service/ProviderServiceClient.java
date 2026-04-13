package com.ciberaccion.paypro.service;

import com.ciberaccion.paypro.dto.CardValidationRequest;
import com.ciberaccion.paypro.dto.CardValidationResponse;
import com.ciberaccion.paypro.dto.PaymentRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class ProviderServiceClient {

    private final WebClient providerWebClient;

    public ProviderServiceClient(WebClient providerWebClient) {
        this.providerWebClient = providerWebClient;
    }

    @CircuitBreaker(name = "providerService", fallbackMethod = "validateFallback")
    @Retry(name = "providerService")
    public boolean validate(PaymentRequest request) {
        CardValidationRequest cardRequest = new CardValidationRequest(
                request.getCardNumber(),
                request.getAmount(),
                request.getCurrency());
        CardValidationResponse response = providerWebClient.post()
                .uri("/provider/validate")
                .bodyValue(cardRequest)
                .retrieve()
                .bodyToMono(CardValidationResponse.class)
                .block();
        return response != null && response.isApproved();
    }

    public boolean validateFallback(PaymentRequest request, Exception e) {
        if (e instanceof io.github.resilience4j.circuitbreaker.CallNotPermittedException) {
            log.warn("⚡ Circuit breaker OPEN — llamada bloqueada sin intentar: {}", e.getMessage());
        } else {
            log.warn("❌ Provider service falló, circuito contando fallo: {}", e.getMessage());
        }
        return false;
    }
}