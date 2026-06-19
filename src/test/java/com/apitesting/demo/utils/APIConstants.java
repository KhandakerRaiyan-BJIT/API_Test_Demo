package com.apitesting.demo.utils;

public final class APIConstants {
    public static final String BASE_URL = ConfigReader.getString("base.url");
    public static final String PRODUCTS_ENDPOINT = ConfigReader.getString("endpoint.products");
    public static final String BRANDS_ENDPOINT = ConfigReader.getString("endpoint.brands");
    public static final String SEARCH_PRODUCT_ENDPOINT = ConfigReader.getString("endpoint.searchProduct");
    public static final String SEARCH_TERM = ConfigReader.getString("search.product.term");
    public static final String SEARCH_PRODUCT_KEY = ConfigReader.getString("search.product.key");

    public static final int OK = ConfigReader.getInt("api.ok.status");
    public static final int METHOD_NOT_ALLOWED = ConfigReader.getInt("api.method.not.allowed.status");

    public static final String NOT_SUPPORTED_MESSAGE = ConfigReader.getString("api.not.supported.message");

    public static final String LOG_FILE_PATH = ConfigReader.getString("log.file.path");

    private APIConstants() {
    }
}
