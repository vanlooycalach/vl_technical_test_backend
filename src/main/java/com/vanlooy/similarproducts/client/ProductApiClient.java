package com.vanlooy.similarproducts.client;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.vanlooy.similarproducts.config.CacheConfig;
import com.vanlooy.similarproducts.dto.ProductDetail;

@Component
public class ProductApiClient {

    private static final Logger log = LoggerFactory.getLogger(ProductApiClient.class);

    private static final ParameterizedTypeReference<List<String>> ID_LIST =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;

    public ProductApiClient(RestClient productApiRestClient) {
        this.restClient = productApiRestClient;
    }

    @Cacheable(CacheConfig.SIMILAR_IDS)
    public List<String> getSimilarIds(String productId) {
        return restClient.get()
                .uri("/product/{id}/similarids", productId)
                .retrieve()
                .body(ID_LIST);
    }

    /**
     * Returns empty if the detail does not exist or the API fails (timeout, 5xx...).
     * The result is cached, including failures: this way a slow product does not
     * cost us the full timeout on every request while the cache entry lasts.
     */
    @Cacheable(CacheConfig.PRODUCT_DETAILS)
    public Optional<ProductDetail> findProductDetail(String productId) {
        try {
            return Optional.ofNullable(restClient.get()
                    .uri("/product/{id}", productId)
                    .retrieve()
                    .body(ProductDetail.class));
        } catch (HttpClientErrorException.NotFound ex) {
            log.warn("Product {} has no detail", productId);
            return Optional.empty();
        } catch (RestClientException ex) {
            log.warn("Could not fetch product {}: {}", productId, ex.getMessage());
            return Optional.empty();
        }
    }
}
