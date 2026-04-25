package com.fmcg.campaign.dto;

import com.fmcg.campaign.entity.CampaignStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CreateCampaignRequest(
        @NotNull Long id,
        @NotBlank String name,
        @NotNull LocalDateTime fromDateTime,
        @NotNull LocalDateTime toDateTime,
        @NotBlank String campaignLocation,
        @NotBlank String location,
        @NotNull CampaignStatus status,
        @NotNull Long userId
) {
}
