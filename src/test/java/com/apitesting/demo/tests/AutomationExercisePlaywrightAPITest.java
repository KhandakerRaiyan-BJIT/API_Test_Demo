package com.apitesting.demo.tests;

import com.apitesting.demo.models.Brand;
import com.apitesting.demo.models.Product;
import com.apitesting.demo.utils.APIConstants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.FormData;
import com.microsoft.playwright.options.RequestOptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
public class AutomationExercisePlaywrightAPITest extends BaseTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonNode readBody(String responseBody) throws Exception {
        return MAPPER.readTree(responseBody);
    }

    private List<Product> readProducts(JsonNode body) throws Exception {
        return MAPPER.readerForListOf(Product.class).readValue(body.path("products"));
    }

    private List<Brand> readBrands(JsonNode body) throws Exception {
        return MAPPER.readerForListOf(Brand.class).readValue(body.path("brands"));
    }

    @Test
    @Order(1)
    @DisplayName("TC01 - Get all products list")
    void testCase1_getAllProductsList() throws Exception {
        APIResponse response = request.get(APIConstants.PRODUCTS_ENDPOINT);
        String responseBody = response.text();
        JsonNode body = readBody(responseBody);
        logResponse(APIConstants.PRODUCTS_ENDPOINT, response.status(), responseBody);

        assertEquals(APIConstants.OK, response.status());
        assertEquals(APIConstants.OK, body.path("responseCode").asInt());
        assertTrue(body.path("products").isArray());
        assertTrue(body.path("products").size() > 0);

        List<Product> products = readProducts(body);
        assertFalse(products.isEmpty());

        Product firstProduct = products.get(0);
        assertTrue(firstProduct.getId() > 0);
        assertFalse(firstProduct.getName().isBlank());
        assertFalse(firstProduct.getPrice().isBlank());
        assertFalse(firstProduct.getBrand().isBlank());
    }

    @Test
    @Order(2)
    @DisplayName("TC02 - POST to products list is not allowed")
    void testCase2_postToAllProductsList() throws Exception {
        APIResponse response = request.post(APIConstants.PRODUCTS_ENDPOINT);
        String responseBody = response.text();
        logResponse(APIConstants.PRODUCTS_ENDPOINT, response.status(), responseBody);

        assertEquals(APIConstants.OK, response.status());

        JsonNode json = readBody(responseBody);
        assertEquals(APIConstants.METHOD_NOT_ALLOWED, json.path("responseCode").asInt());
        assertEquals(APIConstants.NOT_SUPPORTED_MESSAGE, json.path("message").asText());
    }

    @Test
    @Order(3)
    @DisplayName("TC03 - Get all brands list")
    void testCase3_getAllBrandsList() throws Exception {
        APIResponse response = request.get(APIConstants.BRANDS_ENDPOINT);
        String responseBody = response.text();
        JsonNode body = readBody(responseBody);
        logResponse(APIConstants.BRANDS_ENDPOINT, response.status(), responseBody);

        assertEquals(APIConstants.OK, response.status());
        assertEquals(APIConstants.OK, body.path("responseCode").asInt());
        assertTrue(body.path("brands").isArray());
        assertTrue(body.path("brands").size() > 0);

        List<Brand> brands = readBrands(body);
        assertFalse(brands.isEmpty());

        Brand firstBrand = brands.get(0);
        assertTrue(firstBrand.getId() > 0);
        assertFalse(firstBrand.getBrand().isBlank());
    }

    @Test
    @Order(4)
    @DisplayName("TC04 - PUT to brands list is not allowed")
    void testCase4_putToAllBrandsList() throws Exception {
        APIResponse response = request.put(APIConstants.BRANDS_ENDPOINT);
        String responseBody = response.text();
        logResponse(APIConstants.BRANDS_ENDPOINT, response.status(), responseBody);

        assertEquals(APIConstants.OK, response.status());

        JsonNode json = readBody(responseBody);
        assertEquals(APIConstants.METHOD_NOT_ALLOWED, json.path("responseCode").asInt());
        assertEquals(APIConstants.NOT_SUPPORTED_MESSAGE, json.path("message").asText());
    }

    @Test
    @Order(5)
    @DisplayName("TC05 - Search products")
    void testCase5_postToSearchProduct() throws Exception {
        FormData form = FormData.create().set(APIConstants.SEARCH_PRODUCT_KEY, APIConstants.SEARCH_TERM);
        APIResponse response = request.post(
                APIConstants.SEARCH_PRODUCT_ENDPOINT,
                RequestOptions.create().setForm(form)
        );
        String responseBody = response.text();
        JsonNode body = readBody(responseBody);
        logResponse(APIConstants.SEARCH_PRODUCT_ENDPOINT, response.status(), responseBody);

        assertEquals(APIConstants.OK, response.status());
        assertEquals(APIConstants.OK, body.path("responseCode").asInt());
        assertTrue(body.path("products").isArray());
        assertTrue(body.path("products").size() > 0);

        List<Product> products = readProducts(body);
        assertFalse(products.isEmpty());

        Product firstProduct = products.get(0);
        assertTrue(firstProduct.getId() > 0);
        assertFalse(firstProduct.getName().isBlank());
        assertFalse(firstProduct.getPrice().isBlank());
        assertFalse(firstProduct.getBrand().isBlank());
    }
}
