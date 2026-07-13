package com.apitesting.demo.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
@DisplayName("Signup Tests")
public class SignupTest extends BaseTest {

    // Add signup-related tests here
    // Example test methods can include:
    // - Register user with valid credentials
    // - Register with duplicate email
    // - Login with valid credentials
    // - Login with invalid credentials
    // - Logout functionality
}
