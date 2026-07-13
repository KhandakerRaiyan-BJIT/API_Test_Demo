package com.apitesting.demo.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
@DisplayName("Home Tests")
public class HomeTest extends BaseTest {

    // Add home/homepage-related tests here
    // Example test methods can include:
    // - Verify homepage loads successfully
    // - Check homepage elements visibility
    // - Verify banner/carousel functionality
    // - Check footer links
}
