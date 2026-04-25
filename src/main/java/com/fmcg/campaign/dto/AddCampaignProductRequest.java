package com.fmcg.campaign.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record AddCampaignProductRequest(
        @NotNull Long productId,
        @NotNull @DecimalMin("0.0") BigDecimal discount,
        @NotNull Long userId
) {
}
