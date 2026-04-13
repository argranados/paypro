package com.ciberaccion.paypro.service;

import com.ciberaccion.paypro.dto.DebitRequest;
import com.ciberaccion.paypro.dto.PaymentRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class AccountServiceClient {

    private final WebClient accountWebClient;

    public AccountServiceClient(WebClient accountWebClient) {
        this.accountWebClient = accountWebClient;
    }

    @CircuitBreaker(name = "accountService", fallbackMethod = "debitFallback")
    @Retry(name = "accountService")
    public void debit(PaymentRequest request) {
        accountWebClient.post()
                .uri("/accounts/{merchantId}/debit", request.getMerchant())
                .bodyValue(new DebitRequest(request.getAmount()))
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public void debitFallback(PaymentRequest request, Exception e) {
        if (e instanceof io.github.resilience4j.circuitbreaker.CallNotPermittedException) {
            log.warn("⚡ Circuit breaker OPEN — llamada bloqueada sin intentar: {}", e.getMessage());
        } else {
            log.warn("❌ Account service falló, circuito contando fallo: {}", e.getMessage());
        }
        throw new RuntimeException("Account service unavailable");
    }
}