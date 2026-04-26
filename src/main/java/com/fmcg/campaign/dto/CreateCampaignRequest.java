package com.fmcg.campaign.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fmcg.campaign.entity.CampaignStatus;
import com.fmcg.campaign.utile.IsoLocalDateTimeDeserializer;
import com.fmcg.campaign.utile.IsoLocalDateTimeSerializer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CreateCampaignRequest(
        @NotNull Long id,
        @NotBlank String name,
        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @JsonSerialize(using = IsoLocalDateTimeSerializer.class)
        @JsonDeserialize(using = IsoLocalDateTimeDeserializer.class)
        LocalDateTime fromDateTime,
        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @JsonSerialize(using = IsoLocalDateTimeSerializer.class)
        @JsonDeserialize(using = IsoLocalDateTimeDeserializer.class)
        LocalDateTime toDateTime,
        @NotBlank String campaignLocation,
        @NotBlank String location,
        @NotNull CampaignStatus status,
        @NotNull Long userId
) {
}
