package com.vanlooy.similarproducts.client;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.vanlooy.similarproducts.dto.ProductDetail;

@Component
public class ProductApiClient {

    private static final ParameterizedTypeReference<List<String>> ID_LIST =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;

    public ProductApiClient(RestClient productApiRestClient) {
        this.restClient = productApiRestClient;
    }

    public List<String> getSimilarIds(String productId) {
        return restClient.get()
                .uri("/product/{id}/similarids", productId)
                .retrieve()
                .body(ID_LIST);
    }

    public ProductDetail getProductDetail(String productId) {
        return restClient.get()
                .uri("/product/{id}", productId)
                .retrieve()
                .body(ProductDetail.class);
    }
}
