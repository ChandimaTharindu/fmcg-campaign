package com.fmcg.campaign.controller;

import com.fmcg.campaign.dto.CampaignDetailsResponse;
import com.fmcg.campaign.dto.CampaignDiscountRequest;
import com.fmcg.campaign.dto.CampaignProductAssignmentRequest;
import com.fmcg.campaign.dto.CampaignRequest;
import com.fmcg.campaign.dto.CampaignSalesReportResponse;
import com.fmcg.campaign.dto.CampaignUserAssignmentRequest;
import com.fmcg.campaign.dto.ProductRequest;
import com.fmcg.campaign.dto.UserResponse;
import com.fmcg.campaign.entity.Campaign;
import com.fmcg.campaign.entity.CampaignProduct;
import com.fmcg.campaign.entity.CampaignProductDiscount;
import com.fmcg.campaign.entity.Product;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequestMapping
public class AdminController {

    private final CampaignService campaignService;

    public AdminController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @PostMapping("/products")
    @PostMapping("/product")
    @ResponseStatus(HttpStatus.CREATED)
    public Product addProduct(@Valid @RequestBody ProductRequest request) {
        return campaignService.addProduct(request);
    }

  
    @PostMapping("/campaigns")
    @PostMapping("/campain")
    @ResponseStatus(HttpStatus.CREATED)
    public Campaign createCampaign(@Valid @RequestBody CampaignRequest request) {
        return campaignService.createCampaign(request);
    }

  
    @PostMapping("/campaigns/products")
    @PostMapping("/campain/product")
    @ResponseStatus(HttpStatus.CREATED)
    public CampaignProduct addCampaignProduct(@Valid @RequestBody CampaignProductAssignmentRequest request) {
        return campaignService.assignProductToCampaign(request);
    }

  
    @GetMapping("/campaigns")
    @GetMapping("/campain")
    public CampaignDetailsResponse getCampaignProductDetails(@RequestParam Long campainId) {
        return campaignService.getCampaignDetails(campainId);
    }


    @PostMapping("/campaigns/discount")
    @PostMapping("/campain/discount")
    @ResponseStatus(HttpStatus.CREATED)
    public CampaignProductDiscount createCampaignDiscount(@Valid @RequestBody CampaignDiscountRequest request) {
        return campaignService.saveCampaignDiscount(request);
    }

    @GetMapping("/users")
    public List<UserResponse> getUsers() {
        return campaignService.getUsers();
    }


    @PatchMapping("/campaigns/users")
    @PatchMapping("/campain/users")
    public Campaign assignUserToCampaign(@Valid @RequestBody CampaignUserAssignmentRequest request) {
        return campaignService.assignUserToCampaign(request);
    }


    @GetMapping("/campaigns/{campainId}")
    @GetMapping("/campains/{campainId}")
    public CampaignDetailsResponse getCampaignDetails(@PathVariable Long campainId) {
        return campaignService.getCampaignDetails(campainId);
    }


    @GetMapping("/campaigns/list")
    public List<CampaignDetailsResponse> getCampaignList() {
        return campaignService.getCampaignList();
    }

    @GetMapping("/campains/list")
    public List<CampaignDetailsResponse> getCampaignList() {
        return campaignService.getCampaignList();
    }

    @GetMapping("/campains/{campainId}/sales-report")
    public CampaignSalesReportResponse getSalesReport(@PathVariable Long campainId) {
        return campaignService.getCampaignSalesReport(campainId);
    }

}
