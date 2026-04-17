package com.fmcg.campaign.service;

import com.fmcg.campaign.dto.AddCampaignProductRequest;
import com.fmcg.campaign.dto.CampaignProductResponse;
import com.fmcg.campaign.dto.CampaignResponse;
import com.fmcg.campaign.dto.CampaignSalesReportResponse;
import com.fmcg.campaign.dto.CampaignStatusResponse;
import com.fmcg.campaign.dto.CampaignSummaryResponse;
import com.fmcg.campaign.dto.CampaignUserAssignResponse;
import com.fmcg.campaign.dto.CreateCampaignRequest;
import com.fmcg.campaign.dto.UpdateCampaignStatusRequest;
import com.fmcg.campaign.entity.AppUser;
import com.fmcg.campaign.entity.Campaign;
import com.fmcg.campaign.entity.CampaignBilling;
import com.fmcg.campaign.entity.CampaignProduct;
import com.fmcg.campaign.entity.CampaignStatus;
import com.fmcg.campaign.entity.Product;
import com.fmcg.campaign.exception.BusinessException;
import com.fmcg.campaign.exception.ResourceNotFoundException;
import com.fmcg.campaign.repository.AppUserRepository;
import com.fmcg.campaign.repository.CampaignBillingRepository;
import com.fmcg.campaign.repository.CampaignProductRepository;
import com.fmcg.campaign.repository.CampaignRepository;
import com.fmcg.campaign.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final CampaignProductRepository campaignProductRepository;
    private final CampaignBillingRepository campaignBillingRepository;
    private final AppUserRepository appUserRepository;
    private final ProductRepository productRepository;

    public CampaignResponse createCampaign(CreateCampaignRequest request) {
        AppUser user = getUser(request.userId());
        if (request.toDateTime().isBefore(request.fromDateTime())) {
            throw new BusinessException("INVALID_CAMPAIGN_RANGE", "toDateTime must be greater than fromDateTime");
        }

        Campaign campaign = new Campaign();
        campaign.setId(request.id());
        campaign.setName(request.name());
        campaign.setFromDateTime(request.fromDateTime());
        campaign.setToDateTime(request.toDateTime());
        campaign.setCampaignLocation(request.campaignLocation());
        campaign.setLocation(request.location());
        campaign.setStatus(request.status());
        campaign.setUser(user);

        return toCampaignResponse(campaignRepository.save(campaign));
    }

    @Transactional(readOnly = true)
    public CampaignResponse getCampaign(Long campaignId) {
        return toCampaignResponse(getCampaignEntity(campaignId));
    }

    @Transactional(readOnly = true)
    public List<CampaignSummaryResponse> getCampaigns(String status, String location) {
        List<Campaign> campaigns;

        if (status != null && location != null) {
            CampaignStatus campaignStatus = parseStatus(status);
            campaigns = campaignRepository.findByStatusAndLocationIgnoreCase(campaignStatus, location);
        } else if (status != null) {
            campaigns = campaignRepository.findByStatus(parseStatus(status));
        } else if (location != null) {
            campaigns = campaignRepository.findByLocationIgnoreCase(location);
        } else {
            campaigns = campaignRepository.findAll();
        }

        return campaigns.stream()
                .map(c -> new CampaignSummaryResponse(c.getId(), c.getName(), c.getStatus(), c.getLocation()))
                .toList();
    }

    public CampaignProductResponse addProduct(Long campaignId, AddCampaignProductRequest request) {
        CampaignProduct cp = new CampaignProduct();
        cp.setCampaign(getCampaignEntity(campaignId));
        cp.setProduct(getProduct(request.productId()));
        cp.setDiscount(request.discount());
        cp.setUser(getUser(request.userId()));

        CampaignProduct saved = campaignProductRepository.save(cp);
        return new CampaignProductResponse(saved.getProduct().getId(), saved.getProduct().getName(), saved.getDiscount());
    }

    public CampaignUserAssignResponse assignUser(Long campaignId, com.fmcg.campaign.dto.AssignCampaignUserRequest request) {
        Campaign campaign = getCampaignEntity(campaignId);
        campaign.setUser(getUser(request.userId()));

        List<CampaignProduct> mappings = campaignProductRepository.findByCampaignId(campaignId);
        mappings.forEach(m -> m.setUser(campaign.getUser()));
        campaignProductRepository.saveAll(mappings);
        campaignRepository.save(campaign);

        return new CampaignUserAssignResponse(campaignId, campaign.getUser().getId());
    }

    public CampaignStatusResponse updateStatus(Long campaignId, UpdateCampaignStatusRequest request) {
        Campaign campaign = getCampaignEntity(campaignId);
        campaign.setStatus(request.status());
        campaignRepository.save(campaign);
        return new CampaignStatusResponse(campaignId, campaign.getStatus());
    }

    @Transactional(readOnly = true)
    public CampaignSalesReportResponse getCampaignSalesReport(Long campaignId) {
        getCampaignEntity(campaignId);
        List<CampaignBilling> rows = campaignBillingRepository.findByCampaignId(campaignId);
        BigDecimal grossSales = rows.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQty())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CampaignSalesReportResponse(campaignId, grossSales, (long) rows.size());
    }

    private CampaignResponse toCampaignResponse(Campaign campaign) {
        List<CampaignProductResponse> products = campaignProductRepository.findByCampaignId(campaign.getId()).stream()
                .map(p -> new CampaignProductResponse(p.getProduct().getId(), p.getProduct().getName(), p.getDiscount()))
                .toList();

        return new CampaignResponse(
                campaign.getId(),
                campaign.getName(),
                campaign.getFromDateTime(),
                campaign.getToDateTime(),
                campaign.getCampaignLocation(),
                campaign.getLocation(),
                campaign.getStatus(),
                campaign.getUser().getId(),
                products
        );
    }

    private CampaignStatus parseStatus(String status) {
        try {
            return CampaignStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("INVALID_STATUS", "Invalid campaign status: " + status);
        }
    }

    private Campaign getCampaignEntity(Long campaignId) {
        return campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found: " + campaignId));
    }

    private AppUser getUser(Long userId) {
        return appUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
    }
}
