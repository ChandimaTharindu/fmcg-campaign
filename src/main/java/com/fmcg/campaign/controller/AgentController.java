package com.fmcg.campaign.controller;

import com.fmcg.campaign.dto.CampaignDetailsResponse;
import com.fmcg.campaign.entity.Campaign;
import com.fmcg.campaign.service.CampaignService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/agent")
import com.fmcg.campaign.dto.BillingRequest;
import com.fmcg.campaign.dto.BillingResponse;
import com.fmcg.campaign.dto.CampaignDetailsResponse;
import com.fmcg.campaign.entity.Campaign;
import com.fmcg.campaign.service.CampaignService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/agent")
public class AgentController {

    private final CampaignService campaignService;

    public AgentController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @GetMapping("/campaigns")
    @GetMapping("/campain")
    public List<CampaignDetailsResponse> getRelevantCampaigns(@RequestParam Long userId) {
        return campaignService.getCampaignsForAgent(userId);
    }

    @PatchMapping("/campaigns/{campainId}/close")
    @PostMapping("/campain/billing")
    @ResponseStatus(HttpStatus.CREATED)
    public BillingResponse createBilling(@Valid @RequestBody BillingRequest request) {
        return campaignService.createBilling(request);
    }

    @GetMapping("/campain/billing/{campainId}")
    public List<BillingResponse> getBillingItems(@PathVariable Long campainId) {
        return campaignService.getBillingItems(campainId);
    }

    @PatchMapping("/campain/{campainId}/close")
    public Campaign closeCampaign(@PathVariable Long campainId) {
        return campaignService.closeCampaign(campainId);
    }
}
