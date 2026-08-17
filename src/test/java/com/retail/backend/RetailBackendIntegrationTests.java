package com.retail.backend;

import com.retail.backend.dto.OrderItemRequest;
import com.retail.backend.dto.OrderRequest;
import com.retail.backend.model.Store;
import com.retail.backend.model.Product;
import com.retail.backend.model.Inventory;
import com.retail.backend.model.Review;
import com.retail.backend.model.OrderDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RetailBackendIntegrationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api";
    }

    @Test
    void testGetStores() {
        ResponseEntity<Store[]> response = restTemplate.getForEntity(getBaseUrl() + "/stores", Store[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
    }

    @Test
    void testGetProducts() {
        ResponseEntity<Product[]> response = restTemplate.getForEntity(getBaseUrl() + "/products", Product[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
    }

    @Test
    void testGetInventoryByStore() {
        ResponseEntity<Inventory[]> response = restTemplate.getForEntity(getBaseUrl() + "/inventory/store/1", Inventory[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
    }

    @Test
    void testPlaceOrder_SuccessAndFailure() {
        // Place an order for 1 Samsung S24 (ID: 8) in Store 1 (ID: 1)
        OrderItemRequest item = new OrderItemRequest(8L, 1);
        OrderRequest request = new OrderRequest("Test Customer", "testcustomer@gmail.com", "123-4567", 1L, Collections.singletonList(item));

        ResponseEntity<OrderDetails> response = restTemplate.postForEntity(getBaseUrl() + "/orders", request, OrderDetails.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals("COMPLETED", response.getBody().getStatus());

        // Try to place an order for a huge quantity of Samsung S24 that exceeds inventory stock
        OrderItemRequest tooManyItems = new OrderItemRequest(8L, 10000);
        OrderRequest failedRequest = new OrderRequest("Test Customer 2", "testcustomer2@gmail.com", "123-4567", 1L, Collections.singletonList(tooManyItems));

        ResponseEntity<Map> failedResponse = restTemplate.postForEntity(getBaseUrl() + "/orders", failedRequest, Map.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode() == HttpStatus.CREATED ? HttpStatus.BAD_REQUEST : HttpStatus.OK);
        assertNotNull(failedResponse.getBody());
        assertTrue(failedResponse.getBody().containsKey("message") || failedResponse.getBody().containsKey("error"));
    }

    @Test
    void testMongoReviewFlow() {
        // Submit review for product ID 1
        Review review = new Review(null, 1L, "Reviewer 1", 5, "Excellent product!", null);
        ResponseEntity<Review> response = restTemplate.postForEntity(getBaseUrl() + "/reviews", review, Review.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals("Reviewer 1", response.getBody().getCustomerName());

        // Get reviews for product ID 1
        ResponseEntity<Review[]> listResponse = restTemplate.getForEntity(getBaseUrl() + "/reviews/product/1", Review[].class);
        assertEquals(HttpStatus.OK, listResponse.getStatusCode());
        assertNotNull(listResponse.getBody());
        assertTrue(listResponse.getBody().length > 0);
    }

    @Test
    void testStoredProcedureReports() {
        // Get Monthly Sales For Store
        ResponseEntity<List> response1 = restTemplate.getForEntity(getBaseUrl() + "/orders/reports/monthly-store-sales?year=2025&month=3", List.class);
        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertNotNull(response1.getBody());
        assertFalse(response1.getBody().isEmpty());

        // Get Aggregate Sales For Company
        ResponseEntity<List> response2 = restTemplate.getForEntity(getBaseUrl() + "/orders/reports/aggregate-company-sales?year=2025&month=3", List.class);
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertNotNull(response2.getBody());
        assertFalse(response2.getBody().isEmpty());

        // Get Top Selling Products
        ResponseEntity<List> response3 = restTemplate.getForEntity(getBaseUrl() + "/orders/reports/top-selling-products?limit=3&year=2025", List.class);
        assertEquals(HttpStatus.OK, response3.getStatusCode());
        assertNotNull(response3.getBody());
        assertFalse(response3.getBody().isEmpty());
    }
}
