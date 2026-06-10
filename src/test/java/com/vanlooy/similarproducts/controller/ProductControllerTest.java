package com.vanlooy.similarproducts.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.ResourceAccessException;

import com.vanlooy.similarproducts.dto.ProductDetail;
import com.vanlooy.similarproducts.exception.ProductNotFoundException;
import com.vanlooy.similarproducts.service.SimilarProductsService;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SimilarProductsService similarProductsService;

    @Test
    void devuelve200ConLosProductosSimilares() throws Exception {
        when(similarProductsService.getSimilarProducts("1")).thenReturn(List.of(
                new ProductDetail("2", "Dress", new BigDecimal("19.99"), true)));

        mockMvc.perform(get("/product/1/similar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("2"))
                .andExpect(jsonPath("$[0].name").value("Dress"))
                .andExpect(jsonPath("$[0].price").value(19.99))
                .andExpect(jsonPath("$[0].availability").value(true));
    }

    @Test
    void devuelve404SiElProductoNoExiste() throws Exception {
        when(similarProductsService.getSimilarProducts("99"))
                .thenThrow(new ProductNotFoundException("99"));

        mockMvc.perform(get("/product/99/similar"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Product not found: 99"));
    }

    @Test
    void devuelve502SiElApiDeProductosFalla() throws Exception {
        when(similarProductsService.getSimilarProducts("1"))
                .thenThrow(new ResourceAccessException("connection refused"));

        mockMvc.perform(get("/product/1/similar"))
                .andExpect(status().isBadGateway());
    }
}
