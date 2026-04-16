package com.fmcg.campaign.dto;

import java.math.BigDecimal;

public record CampaignSalesReportResponse(Long campaignId, BigDecimal grossSales, Long billedItems) {
}
