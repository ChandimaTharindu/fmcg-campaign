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
import com.fmcg.campaign.entity.CampaignStatus;
import com.fmcg.campaign.entity.Product;
import com.fmcg.campaign.exception.BusinessException;
import com.fmcg.campaign.exception.ResourceNotFoundException;
import com.fmcg.campaign.repository.AppUserRepository;
import com.fmcg.campaign.repository.CampaignBillingRepository;
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
import java.util.List;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
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
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
    }

    private Campaign getCampaign(Long campaignId) {
        return campaignRepository.findById(campaignId)
                .orElseThrow(() -> new NotFoundException("Campaign not found: " + campaignId));
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
                .orElseThrow(() -> new NotFoundException("Product not found: " + productId));
    }
}
