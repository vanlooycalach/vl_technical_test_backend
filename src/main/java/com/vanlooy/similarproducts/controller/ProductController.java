package com.vanlooy.similarproducts.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.vanlooy.similarproducts.dto.ProductDetail;
import com.vanlooy.similarproducts.service.SimilarProductsService;

@RestController
public class ProductController {

    private final SimilarProductsService similarProductsService;

    public ProductController(SimilarProductsService similarProductsService) {
        this.similarProductsService = similarProductsService;
    }

    @GetMapping("/product/{productId}/similar")
    public List<ProductDetail> getSimilarProducts(@PathVariable String productId) {
        return similarProductsService.getSimilarProducts(productId);
    }
}
