package com.fmcg.campaign.dto;

import com.fmcg.campaign.entity.CampaignStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateCampaignStatusRequest(@NotNull CampaignStatus status) {
}
