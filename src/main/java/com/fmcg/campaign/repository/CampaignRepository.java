package com.fmcg.campaign.repository;

import com.fmcg.campaign.entity.Campaign;
import com.fmcg.campaign.entity.CampaignStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    List<Campaign> findByStatus(CampaignStatus status);

    List<Campaign> findByLocationIgnoreCase(String location);

    List<Campaign> findByStatusAndLocationIgnoreCase(CampaignStatus status, String location);

    }
