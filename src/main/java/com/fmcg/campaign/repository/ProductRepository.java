package com.fmcg.campaign.repository;

import com.fmcg.campaign.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
