package com.fmcg.campaign.service;

import com.fmcg.campaign.dto.CreateProductRequest;
import com.fmcg.campaign.dto.ProductResponse;
import com.fmcg.campaign.entity.Product;
import com.fmcg.campaign.repository.ProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = new Product();
        product.setId(request.id());
        product.setName(request.name());
        product.setPrice(request.price());
        Product saved = productRepository.save(product);
        return new ProductResponse(saved.getId(), saved.getName(), saved.getPrice());
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(p -> new ProductResponse(p.getId(), p.getName(), p.getPrice()))
                .toList();
    }
}
