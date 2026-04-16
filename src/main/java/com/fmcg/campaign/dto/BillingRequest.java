package com.fmcg.campaign.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record BillingRequest(
        @NotNull Long campaignId,
        @NotNull Long userId,
        @NotNull Long productId,
        @NotNull @Min(1) Integer qty
) {
}
