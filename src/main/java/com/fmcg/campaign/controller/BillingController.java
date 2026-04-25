package com.fmcg.campaign.controller;

import com.fmcg.campaign.dto.BillingRequest;
import com.fmcg.campaign.dto.BillingResponse;
import com.fmcg.campaign.service.CampaignService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/campaigns/billing")
public class BillingController {

    private final CampaignService campaignService;

    public BillingController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BillingResponse createBilling(@Valid @RequestBody BillingRequest request) {
        return campaignService.createBilling(request);
    }

    @GetMapping("/{campainId}")
    public List<BillingResponse> getBillingItems(@PathVariable Long campainId) {
        return campaignService.getBillingItems(campainId);
    }
}
