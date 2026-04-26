package com.fmcg.campaign.dto;

import jakarta.validation.constraints.NotNull;

public record UserRequest(
        @NotNull Long id,
        @NotNull String userName,
        @NotNull String contactNumber)  {
}
