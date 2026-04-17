package com.fmcg.campaign.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateProductRequest(
        @NotNull Long id,
        @NotBlank String name,
        @NotNull @DecimalMin("0.0") BigDecimal price
) {
}
