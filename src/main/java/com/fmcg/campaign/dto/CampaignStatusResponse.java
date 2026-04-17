package com.fmcg.campaign.dto;

import com.fmcg.campaign.entity.CampaignStatus;

public record CampaignStatusResponse(Long campaignId, CampaignStatus status) {
}
