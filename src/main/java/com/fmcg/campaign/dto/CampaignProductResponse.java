package com.fmcg.campaign.dto;

import java.math.BigDecimal;

public record CampaignProductResponse(
        Long productId,
        String productName,
        BigDecimal discount
) {
}
