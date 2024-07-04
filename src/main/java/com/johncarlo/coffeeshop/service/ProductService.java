package com.johncarlo.coffeeshop.service;

import com.johncarlo.coffeeshop.model.Product;
import com.johncarlo.coffeeshop.model.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    public Product getProduct(Long productId) {
        return productRepository.findById(productId).orElse(null);
    }
}
