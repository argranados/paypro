package com.ciberaccion.paypro.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class DebitRequest {
    private BigDecimal amount;
}