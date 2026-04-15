package com.fmcg.campaign.service;

import com.fmcg.campaign.dto.BillingRequest;
import com.fmcg.campaign.dto.BillingResponse;
import com.fmcg.campaign.dto.CampaignDetailsResponse;
import com.fmcg.campaign.dto.CampaignDiscountRequest;
import com.fmcg.campaign.dto.CampaignProductAssignmentRequest;
import com.fmcg.campaign.dto.CampaignProductResponse;
import com.fmcg.campaign.dto.CampaignRequest;
import com.fmcg.campaign.dto.CampaignSalesReportResponse;
import com.fmcg.campaign.dto.CampaignUserAssignmentRequest;
import com.fmcg.campaign.dto.ProductRequest;
import com.fmcg.campaign.dto.UserResponse;
import com.fmcg.campaign.entity.AppUser;
import com.fmcg.campaign.entity.Campaign;
import com.fmcg.campaign.entity.CampaignBilling;
import com.fmcg.campaign.entity.CampaignProduct;
import com.fmcg.campaign.entity.CampaignProductDiscount;
import com.fmcg.campaign.entity.CampaignStatus;
import com.fmcg.campaign.entity.Product;
import com.fmcg.campaign.exception.BadRequestException;
import com.fmcg.campaign.exception.NotFoundException;
import com.fmcg.campaign.repository.AppUserRepository;
import com.fmcg.campaign.repository.CampaignBillingRepository;
import com.fmcg.campaign.repository.CampaignProductDiscountRepository;
import com.fmcg.campaign.repository.CampaignProductRepository;
import com.fmcg.campaign.repository.CampaignRepository;
import com.fmcg.campaign.repository.ProductRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CampaignService {

    private final ProductRepository productRepository;
    private final CampaignRepository campaignRepository;
    private final CampaignProductRepository campaignProductRepository;
    private final CampaignProductDiscountRepository campaignProductDiscountRepository;
    private final AppUserRepository appUserRepository;
    private final CampaignBillingRepository campaignBillingRepository;

    public CampaignService(ProductRepository productRepository,
                           CampaignRepository campaignRepository,
                           CampaignProductRepository campaignProductRepository,
                           CampaignProductDiscountRepository campaignProductDiscountRepository,
                           AppUserRepository appUserRepository,
                           CampaignBillingRepository campaignBillingRepository) {
        this.productRepository = productRepository;
        this.campaignRepository = campaignRepository;
        this.campaignProductRepository = campaignProductRepository;
        this.campaignProductDiscountRepository = campaignProductDiscountRepository;
        this.appUserRepository = appUserRepository;
        this.campaignBillingRepository = campaignBillingRepository;
    }

    public Product addProduct(ProductRequest request) {
        Product product = new Product();
        product.setId(request.id());
        product.setName(request.name());
        product.setPrice(request.price());
        return productRepository.save(product);
    }

    public Campaign createCampaign(CampaignRequest request) {
        AppUser user = getUser(request.userId());
        if (request.toDateTime().isBefore(request.fromDateTime())) {
            throw new BadRequestException("toDateTime must be greater than fromDateTime");
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
        return campaignRepository.save(campaign);
    }

    public CampaignProduct assignProductToCampaign(CampaignProductAssignmentRequest request) {
        CampaignProduct campaignProduct = new CampaignProduct();
        campaignProduct.setCampaign(getCampaign(request.campaignId()));
        campaignProduct.setProduct(getProduct(request.productId()));
        campaignProduct.setDiscount(request.discount());
        campaignProduct.setUser(getUser(request.userId()));
        return campaignProductRepository.save(campaignProduct);
    }

    public CampaignProductDiscount saveCampaignDiscount(CampaignDiscountRequest request) {
        CampaignProductDiscount discount = new CampaignProductDiscount();
        discount.setCampaign(getCampaign(request.campaignId()));
        discount.setProduct(getProduct(request.productId()));
        discount.setDiscount(request.discount());
        return campaignProductDiscountRepository.save(discount);
    }

    public List<UserResponse> getUsers() {
        return appUserRepository.findAll().stream()
                .map(user -> new UserResponse(user.getId(), user.getUserName(), user.getContactNumber()))
                .toList();
    }

    public Campaign assignUserToCampaign(CampaignUserAssignmentRequest request) {
        Campaign campaign = getCampaign(request.campaignId());
        campaign.setUser(getUser(request.userId()));

        List<CampaignProduct> campaignProducts = campaignProductRepository.findByCampaignId(request.campaignId());
        campaignProducts.forEach(campaignProduct -> campaignProduct.setUser(campaign.getUser()));
        campaignProductRepository.saveAll(campaignProducts);

        return campaignRepository.save(campaign);
    }

    public CampaignDetailsResponse getCampaignDetails(Long campaignId) {
        Campaign campaign = getCampaign(campaignId);
        List<CampaignProductResponse> products = campaignProductRepository.findByCampaignId(campaignId).stream()
                .map(p -> new CampaignProductResponse(p.getProduct().getId(), p.getProduct().getName(), p.getDiscount()))
                .toList();

        return new CampaignDetailsResponse(
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

    public List<CampaignDetailsResponse> getCampaignList() {
        return campaignRepository.findAll().stream()
                .map(campaign -> getCampaignDetails(campaign.getId()))
                .toList();
    }

    public List<CampaignDetailsResponse> getCampaignsForAgent(Long userId) {
        getUser(userId);
        List<Long> campaignIds = campaignProductRepository.findByUserId(userId).stream()
                .map(cp -> cp.getCampaign().getId())
                .distinct()
                .toList();
        return campaignIds.stream().map(this::getCampaignDetails).toList();
    }

    public BillingResponse createBilling(BillingRequest request) {
        Campaign campaign = getCampaign(request.campaignId());
        if (campaign.getStatus() != CampaignStatus.ACTIVE) {
            throw new BadRequestException("Billing allowed only for ACTIVE campaigns");
        }

        if (LocalDateTime.now().isBefore(campaign.getFromDateTime()) || LocalDateTime.now().isAfter(campaign.getToDateTime())) {
            throw new BadRequestException("Billing must happen during campaign active window");
        }

        CampaignBilling billing = new CampaignBilling();
        billing.setCampaign(campaign);
        billing.setUser(getUser(request.userId()));
        billing.setProduct(getProduct(request.productId()));
        billing.setQty(request.qty());
        campaignBillingRepository.save(billing);

        return new BillingResponse(request.campaignId(), request.userId(), request.productId(), request.qty());
    }

    public List<BillingResponse> getBillingItems(Long campaignId) {
        getCampaign(campaignId);
        return campaignBillingRepository.findByCampaignId(campaignId).stream()
                .map(item -> new BillingResponse(
                        item.getCampaign().getId(),
                        item.getUser().getId(),
                        item.getProduct().getId(),
                        item.getQty()))
                .toList();
    }

    public Campaign closeCampaign(Long campaignId) {
        Campaign campaign = getCampaign(campaignId);
        campaign.setStatus(CampaignStatus.COMPLETE);
        return campaignRepository.save(campaign);
    }

    public CampaignSalesReportResponse getCampaignSalesReport(Long campaignId) {
        getCampaign(campaignId);
        List<CampaignBilling> billings = campaignBillingRepository.findByCampaignId(campaignId);
        BigDecimal grossSales = billings.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQty())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CampaignSalesReportResponse(campaignId, grossSales, (long) billings.size());
    }

    private AppUser getUser(Long userId) {
        return appUserRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
    }

    private Campaign getCampaign(Long campaignId) {
        return campaignRepository.findById(campaignId)
                .orElseThrow(() -> new NotFoundException("Campaign not found: " + campaignId));
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found: " + productId));
    }
}
