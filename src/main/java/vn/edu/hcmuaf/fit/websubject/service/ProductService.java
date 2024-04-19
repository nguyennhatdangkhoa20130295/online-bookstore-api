package vn.edu.hcmuaf.fit.websubject.service;

import org.springframework.data.domain.Page;
import vn.edu.hcmuaf.fit.websubject.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> getAllProducts();

    Page<Product> getAllProducts(int page, int perPage, String sort, String filter, String order);

    Optional<Product> getProductById(Integer id);

    List<Product> getProductsByCategory(Integer categoryId);

    List<Product> getThreeLatestProduct();

}
