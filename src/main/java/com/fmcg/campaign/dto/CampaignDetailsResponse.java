package com.fmcg.campaign.dto;

import com.fmcg.campaign.entity.CampaignStatus;
import java.time.LocalDateTime;
import java.util.List;

public record CampaignDetailsResponse(
        Long id,
        String name,
        LocalDateTime fromDateTime,
        LocalDateTime toDateTime,
        String campaignLocation,
        String location,
        CampaignStatus status,
        Long userId,
        List<CampaignProductResponse> products
) {
}
