package com.fmcg.campaign.service;

import com.fmcg.campaign.dto.BillingRequest;
import com.fmcg.campaign.dto.BillingResponse;
import com.fmcg.campaign.entity.Campaign;
import com.fmcg.campaign.entity.CampaignBilling;
import com.fmcg.campaign.entity.CampaignStatus;
import com.fmcg.campaign.exception.BusinessException;
import com.fmcg.campaign.exception.ResourceNotFoundException;
import com.fmcg.campaign.repository.AppUserRepository;
import com.fmcg.campaign.repository.CampaignBillingRepository;
import com.fmcg.campaign.repository.CampaignRepository;
import com.fmcg.campaign.repository.ProductRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BillingService {

    private final CampaignRepository campaignRepository;
    private final CampaignBillingRepository campaignBillingRepository;
    private final AppUserRepository appUserRepository;
    private final ProductRepository productRepository;

    public BillingResponse createBilling(BillingRequest request) {
        Campaign campaign = getCampaign(request.campaignId());
        if (campaign.getStatus() != CampaignStatus.ACTIVE) {
            throw new BusinessException("CAMPAIGN_NOT_ACTIVE", "Billing is allowed only for active campaigns");
        }

        if (LocalDateTime.now().isBefore(campaign.getFromDateTime()) || LocalDateTime.now().isAfter(campaign.getToDateTime())) {
            throw new BusinessException("CAMPAIGN_WINDOW_CLOSED", "Billing is allowed only within campaign window");
        }

        CampaignBilling billing = new CampaignBilling();
        billing.setCampaign(campaign);
        billing.setUser(appUserRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.userId())));
        billing.setProduct(productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + request.productId())));
        billing.setQty(request.qty());
        campaignBillingRepository.save(billing);

        return new BillingResponse(request.campaignId(), request.userId(), request.productId(), request.qty());
    }

    @Transactional(readOnly = true)
    public List<BillingResponse> getBillingByCampaign(Long campaignId, Long userId) {
        getCampaign(campaignId);
        List<CampaignBilling> rows = userId == null
                ? campaignBillingRepository.findByCampaignId(campaignId)
                : campaignBillingRepository.findByCampaignIdAndUserId(campaignId, userId);

        return rows.stream().map(item -> new BillingResponse(
                item.getCampaign().getId(),
                item.getUser().getId(),
                item.getProduct().getId(),
                item.getQty())).toList();
    }

    private Campaign getCampaign(Long campaignId) {
        return campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found: " + campaignId));
    }
}
