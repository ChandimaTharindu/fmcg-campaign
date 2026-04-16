package com.fmcg.campaign.controller;

import com.fmcg.campaign.dto.CampaignSalesReportResponse;
import com.fmcg.campaign.service.CampaignService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final CampaignService campaignService;

    public ReportController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @GetMapping("/campaigns/{campainId}/sales")
    public CampaignSalesReportResponse getSalesReport(@PathVariable Long campainId) {
        return campaignService.getCampaignSalesReport(campainId);
    }
}
