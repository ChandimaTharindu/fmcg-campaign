package com.fmcg.campaign.repository;

import com.fmcg.campaign.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
}
