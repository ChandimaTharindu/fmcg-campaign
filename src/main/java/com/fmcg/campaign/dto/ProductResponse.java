package com.fmcg.campaign.dto;

import java.math.BigDecimal;

public record ProductResponse(Long id, String name, BigDecimal price) {
}
