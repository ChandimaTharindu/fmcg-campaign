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
public class AgentController {

    private final CampaignService campaignService;

    public AgentController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @GetMapping("/campaigns")
    public List<CampaignDetailsResponse> getRelevantCampaigns(@RequestParam Long userId) {
        return campaignService.getCampaignsForAgent(userId);
    }

    @PatchMapping("/campaigns/{campainId}/close")
    public Campaign closeCampaign(@PathVariable Long campainId) {
        return campaignService.closeCampaign(campainId);
    }
}
