package com.ciberaccion.paypro.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ciberaccion.paypro.controller.exception.PaymentNotFoundException;
import com.ciberaccion.paypro.dto.PaymentRequest;
import com.ciberaccion.paypro.dto.PaymentResponse;
import com.ciberaccion.paypro.model.Payment;
import com.ciberaccion.paypro.model.PaymentStatus;
import com.ciberaccion.paypro.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    // Crear un nuevo pago
    public Payment create(PaymentRequest request) {
        Payment payment = new Payment();
        payment.setMerchant(request.getMerchant());
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency());
        payment.setStatus(PaymentStatus.PENDING); // estado inicial
        payment.setCreatedAt(LocalDateTime.now());
        return paymentRepository.save(payment);
    }

    // Buscar pago por ID
    public PaymentResponse findById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
        return toResponse(payment);
    }

    // Obtener todos los pagos
    public List<PaymentResponse> findAll() {
        return paymentRepository.findAll()
            .stream()
            .map(this::toResponse)
            .toList();
    }

    // método privado de conversión
    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getMerchant(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getCreatedAt());
    }

}
