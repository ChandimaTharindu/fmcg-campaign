package com.fmcg.campaign.repository;

import com.fmcg.campaign.entity.CampaignProduct;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignProductRepository extends JpaRepository<CampaignProduct, Long> {
    List<CampaignProduct> findByCampaignId(Long campaignId);

    List<CampaignProduct> findByUserId(Long userId);
}
