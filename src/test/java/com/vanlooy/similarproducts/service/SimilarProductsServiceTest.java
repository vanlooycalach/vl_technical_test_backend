package com.vanlooy.similarproducts.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import com.vanlooy.similarproducts.client.ProductApiClient;
import com.vanlooy.similarproducts.dto.ProductDetail;
import com.vanlooy.similarproducts.exception.ProductNotFoundException;

class SimilarProductsServiceTest {

    private ProductApiClient productApiClient;
    private SimilarProductsService service;

    @BeforeEach
    void setUp() {
        productApiClient = mock(ProductApiClient.class);
        service = new SimilarProductsService(productApiClient);
    }

    @Test
    void devuelveLosDetallesEnElOrdenDeSimilitud() {
        when(productApiClient.getSimilarIds("1")).thenReturn(List.of("2", "3", "4"));
        when(productApiClient.findProductDetail("2")).thenReturn(Optional.of(detail("2", "Dress")));
        when(productApiClient.findProductDetail("3")).thenReturn(Optional.of(detail("3", "Blazer")));
        when(productApiClient.findProductDetail("4")).thenReturn(Optional.of(detail("4", "Boots")));

        List<ProductDetail> result = service.getSimilarProducts("1");

        assertThat(result).extracting(ProductDetail::id).containsExactly("2", "3", "4");
    }

    @Test
    void saltaLosSimilaresSinDetalleYDevuelveElResto() {
        when(productApiClient.getSimilarIds("4")).thenReturn(List.of("1", "2", "5"));
        when(productApiClient.findProductDetail("1")).thenReturn(Optional.of(detail("1", "Shirt")));
        when(productApiClient.findProductDetail("2")).thenReturn(Optional.of(detail("2", "Dress")));
        when(productApiClient.findProductDetail("5")).thenReturn(Optional.empty());

        List<ProductDetail> result = service.getSimilarProducts("4");

        assertThat(result).extracting(ProductDetail::id).containsExactly("1", "2");
    }

    @Test
    void lanzaNotFoundSiElProductoBaseNoExiste() {
        when(productApiClient.getSimilarIds("99")).thenThrow(
                HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found",
                        HttpHeaders.EMPTY, new byte[0], null));

        assertThatThrownBy(() -> service.getSimilarProducts("99"))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void devuelveListaVaciaSiNoHaySimilares() {
        when(productApiClient.getSimilarIds("7")).thenReturn(List.of());

        assertThat(service.getSimilarProducts("7")).isEmpty();
    }

    private ProductDetail detail(String id, String name) {
        return new ProductDetail(id, name, new BigDecimal("19.99"), true);
    }
}
