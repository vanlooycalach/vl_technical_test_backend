package com.vanlooy.similarproducts.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.vanlooy.similarproducts.client.ProductApiClient;
import com.vanlooy.similarproducts.dto.ProductDetail;
import com.vanlooy.similarproducts.exception.ErrorMessage;
import com.vanlooy.similarproducts.exception.ProductNotFoundException;

@Service
public class SimilarProductsService {

    private static final Logger log = LoggerFactory.getLogger(SimilarProductsService.class);

    private final ProductApiClient productApiClient;

    public SimilarProductsService(ProductApiClient productApiClient) {
        this.productApiClient = productApiClient;
    }

    public List<ProductDetail> getSimilarProducts(String productId) {
        List<String> similarIds;
        try {
            similarIds = productApiClient.getSimilarIds(productId);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ProductNotFoundException(productId);
        }

        return fetchDetailsInParallel(similarIds);
    }

    private List<ProductDetail> fetchDetailsInParallel(List<String> similarIds) {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<Optional<ProductDetail>>> futures = similarIds.stream()
                    .map(id -> executor.submit(() -> productApiClient.findProductDetail(id)))
                    .toList();

            // recorremos en el mismo orden de los ids para respetar la similitud
            List<ProductDetail> similarProducts = new ArrayList<>(similarIds.size());
            for (int i = 0; i < futures.size(); i++) {
                String id = similarIds.get(i);
                try {
                    futures.get(i).get().ifPresent(similarProducts::add);
                } catch (ExecutionException ex) {
                    log.warn("Skipping similar product {}: {}", id, ex.getCause().toString());
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException(ErrorMessage.FETCH_INTERRUPTED.format(), ex);
                }
            }
            return similarProducts;
        }
    }
}
