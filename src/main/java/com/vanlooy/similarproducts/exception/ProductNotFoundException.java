package com.vanlooy.similarproducts.exception;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String productId) {
        super(ErrorMessage.PRODUCT_NOT_FOUND.format(productId));
    }
}
