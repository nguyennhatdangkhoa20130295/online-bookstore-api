package vn.edu.hcmuaf.fit.websubject.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.edu.hcmuaf.fit.websubject.entity.Contact;
import vn.edu.hcmuaf.fit.websubject.entity.Product;
import vn.edu.hcmuaf.fit.websubject.entity.Promotion;
import vn.edu.hcmuaf.fit.websubject.payload.others.CurrentTime;
import vn.edu.hcmuaf.fit.websubject.repository.ProductRepository;
import vn.edu.hcmuaf.fit.websubject.repository.PromotionRepository;
import vn.edu.hcmuaf.fit.websubject.service.PromotionService;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PromotionServiceImpl implements PromotionService {
    final
    PromotionRepository promotionRepository;

    private final ProductRepository productRepository;

    @Autowired
    public PromotionServiceImpl(PromotionRepository promotionRepository, ProductRepository productRepository) {
        this.promotionRepository = promotionRepository;
        this.productRepository = productRepository;
    }

    @Override
    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }

    @Override
    public Page<Promotion> findAllByIsCode(int page, int size, String sort, String order, String filter) {
        Sort.Direction direction = Sort.Direction.ASC;
        if (order.equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }
        Sort sortPa = Sort.by(direction, sort);
        Pageable pageable = PageRequest.of(page, size, sortPa);

        JsonNode jsonFilter;
        try {
            jsonFilter = new ObjectMapper().readTree(java.net.URLDecoder.decode(filter, StandardCharsets.UTF_8));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Specification<Promotion> specification = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (jsonFilter.has("q")) {
                String searchStr = jsonFilter.get("q").asText();
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(criteriaBuilder.lower(root.get("discount")), "%" + searchStr.toLowerCase() + "%"));
            }

            if (jsonFilter.has("isCode")) {
                boolean isCode = jsonFilter.get("isCode").asInt() == 1;
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("isCode"), isCode));
            }

            return predicate;
        };

        return promotionRepository.findAll(specification, pageable);
    }


    @Override
    public Promotion getPromotionById(int id) {
        return promotionRepository.findById(id).orElse(null);
    }

    @Override
    public Promotion getPromotionByCode(String code) {
        Optional<Promotion> promoCode = promotionRepository.findByCode(code);
        return promoCode.orElse(null);
    }

    @Override
    public boolean checkPromoCode(String code) {
        Optional<Promotion> promoCode = promotionRepository.findByCode(code);
        return promoCode.isPresent();
    }


    @Override
    public void addPromotion(Integer idProduct, String code, int discount, Date startDate, Date endDate) {
        Promotion promotion = new Promotion();
        if(idProduct != null){
            Product product = productRepository.findById(idProduct).orElse(null);
            promotion.setProduct(product);
        } else {
            promotion.setProduct(null);
        }
        promotion.setCode(code);
        promotion.setDiscount(discount);
        promotion.setStartDate(startDate);
        promotion.setEndDate(endDate);
        promotion.setIsCode(code != null);
        promotionRepository.save(promotion);
    }

    @Override
    public void updatePromotion(int id, Integer idProduct, String code, int discount, Date startDate, Date endDate) {
        Promotion promotion = promotionRepository.findById(id).orElse(null);
        assert promotion != null;
        if(idProduct != null){
            Product product = productRepository.findById(idProduct).orElse(null);
            promotion.setProduct(product);
        } else {
            promotion.setProduct(null);
        }
        promotion.setCode(code);
        promotion.setDiscount(discount);
        promotion.setStartDate(startDate);
        promotion.setEndDate(endDate);
        promotion.setIsCode(code != null);
        promotionRepository.save(promotion);
    }

    @Override
    public void deletePromotion(int id) {
        promotionRepository.deleteById(id);
    }
}
