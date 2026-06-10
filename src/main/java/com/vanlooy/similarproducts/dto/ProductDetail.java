package com.vanlooy.similarproducts.dto;

import java.math.BigDecimal;

public record ProductDetail(
        String id,
        String name,
        BigDecimal price,
        boolean availability) {
}
