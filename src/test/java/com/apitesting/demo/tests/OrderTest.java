package com.apitesting.demo.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
@DisplayName("Order Tests")
public class OrderTest extends BaseTest {

    // Add order-related tests here
    // Example test methods can include:
    // - Create new order
    // - Get order details
    // - Update order status
    // - Get order history
    // - Cancel order
    // - Verify order placement with valid items
}
