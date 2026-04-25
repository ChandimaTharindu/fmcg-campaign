package com.fmcg.campaign.dto;

import com.fmcg.campaign.entity.CampaignStatus;

public record CampaignSummaryResponse(Long id, String name, CampaignStatus status, String location) {
}
