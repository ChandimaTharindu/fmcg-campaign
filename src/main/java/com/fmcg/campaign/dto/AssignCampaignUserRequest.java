package com.fmcg.campaign.dto;

import jakarta.validation.constraints.NotNull;

public record AssignCampaignUserRequest(@NotNull Long userId) {
}
