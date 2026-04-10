package com.ciberaccion.paypro.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.ciberaccion.paypro.dto.DebitRequest;
import com.ciberaccion.paypro.dto.PaymentRequest;
import com.ciberaccion.paypro.dto.PaymentResponse;
import com.ciberaccion.paypro.exception.PaymentNotFoundException;
import com.ciberaccion.paypro.model.Payment;
import com.ciberaccion.paypro.model.PaymentStatus;
import com.ciberaccion.paypro.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final WebClient accountWebClient;

    public PaymentService(PaymentRepository paymentRepository, WebClient accountWebClient) {
        this.paymentRepository = paymentRepository;
        this.accountWebClient = accountWebClient;
    }

    public PaymentResponse create(PaymentRequest request) {
        Payment payment = new Payment();
        payment.setMerchant(request.getMerchant());
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency());
        payment.setCreatedAt(LocalDateTime.now());

        try {
            accountWebClient.post()
                    .uri("/accounts/{merchantId}/debit", request.getMerchant())
                    .bodyValue(new DebitRequest(request.getAmount()))
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            payment.setStatus(PaymentStatus.APPROVED);

        } catch (WebClientResponseException e) {
            payment.setStatus(PaymentStatus.REJECTED);
        }

        return toResponse(paymentRepository.save(payment));
    }

    public PaymentResponse findById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
        return toResponse(payment);
    }

    public List<PaymentResponse> findAll() {
        return paymentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getMerchant(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getCreatedAt()
        );
    }
}