package com.vanlooy.similarproducts.exception;

public enum ErrorMessage {

    PRODUCT_NOT_FOUND("Product not found: %s"),
    UPSTREAM_ERROR("Error calling the products API"),
    UNEXPECTED_ERROR("Unexpected error"),
    FETCH_INTERRUPTED("Interrupted while fetching product details");

    private final String template;

    ErrorMessage(String template) {
        this.template = template;
    }

    public String format(Object... args) {
        return template.formatted(args);
    }
}
