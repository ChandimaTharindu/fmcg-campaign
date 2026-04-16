package com.fmcg.campaign.dto;

public record BillingResponse(Long campaignId, Long userId, Long productId, Integer qty) {
}
