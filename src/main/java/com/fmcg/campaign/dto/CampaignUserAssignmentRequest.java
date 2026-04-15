package com.fmcg.campaign.dto;

import jakarta.validation.constraints.NotNull;

public record CampaignUserAssignmentRequest(
        @NotNull Long userId,
        @NotNull Long campaignId
) {
}
