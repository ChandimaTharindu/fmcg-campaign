package com.fmcg.campaign.repository;

import com.fmcg.campaign.entity.CampaignBilling;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignBillingRepository extends JpaRepository<CampaignBilling, Long> {
    List<CampaignBilling> findByCampaignId(Long campaignId);
    List<CampaignBilling> findByCampaignIdAndUserId(Long campaignId, Long userId);
}
