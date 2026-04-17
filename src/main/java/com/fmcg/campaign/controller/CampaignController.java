package com.fmcg.campaign.controller;

import com.fmcg.campaign.dto.AddCampaignProductRequest;
import com.fmcg.campaign.dto.AssignCampaignUserRequest;
import com.fmcg.campaign.dto.BillingRequest;
import com.fmcg.campaign.dto.BillingResponse;
import com.fmcg.campaign.dto.CampaignProductResponse;
import com.fmcg.campaign.dto.CampaignResponse;
import com.fmcg.campaign.dto.CampaignStatusResponse;
import com.fmcg.campaign.dto.CampaignSummaryResponse;
import com.fmcg.campaign.dto.CampaignUserAssignResponse;
import com.fmcg.campaign.dto.CreateCampaignRequest;
import com.fmcg.campaign.dto.UpdateCampaignStatusRequest;
import com.fmcg.campaign.service.BillingService;
import com.fmcg.campaign.service.CampaignService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/campaigns")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;
    private final BillingService billingService;

    @PostMapping
    public ResponseEntity<CampaignResponse> create(@Valid @RequestBody CreateCampaignRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(campaignService.createCampaign(request));
    }

    @GetMapping("/{campaignId}")
    public ResponseEntity<CampaignResponse> getById(@PathVariable Long campaignId) {
        return ResponseEntity.ok(campaignService.getCampaign(campaignId));
    }

    @GetMapping
    public ResponseEntity<List<CampaignSummaryResponse>> getAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String location) {
        return ResponseEntity.ok(campaignService.getCampaigns(status, location));
    }

    @PostMapping("/{campaignId}/products")
    public ResponseEntity<CampaignProductResponse> addProduct(
            @PathVariable Long campaignId,
            @Valid @RequestBody AddCampaignProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(campaignService.addProduct(campaignId, request));
    }

    @PatchMapping("/{campaignId}/users")
    public ResponseEntity<CampaignUserAssignResponse> assignUser(
            @PathVariable Long campaignId,
            @Valid @RequestBody AssignCampaignUserRequest request) {
        return ResponseEntity.ok(campaignService.assignUser(campaignId, request));
    }

    @PatchMapping("/{campaignId}/status")
    public ResponseEntity<CampaignStatusResponse> updateStatus(
            @PathVariable Long campaignId,
            @Valid @RequestBody UpdateCampaignStatusRequest request) {
        return ResponseEntity.ok(campaignService.updateStatus(campaignId, request));
    }

    @PostMapping("/billing")
    public ResponseEntity<BillingResponse> billing(@Valid @RequestBody BillingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(billingService.createBilling(request));
    }

    @GetMapping("/{campaignId}/billing")
    public ResponseEntity<List<BillingResponse>> getBilling(
            @PathVariable Long campaignId,
            @RequestParam(required = false) Long userId) {
        return ResponseEntity.ok(billingService.getBillingByCampaign(campaignId, userId));
    }
}
