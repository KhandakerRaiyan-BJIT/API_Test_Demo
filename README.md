# Playwright Java API Tests for AutomationExercise

This project converts test cases 1 to 5 from the AutomationExercise API list into Playwright Java tests.

## Covered test cases

1. Get All Products List
2. POST To All Products List
3. Get All Brands List
4. PUT To All Brands List
5. POST To Search Product

## Run

```bash
mvn test
```

## Allure report

```bash
mvn clean test
mvn allure:report
```

Generated report location:

- `target/site/allure-maven-plugin/index.html`

## Notes

- Uses Playwright Java `APIRequestContext` for request-level API testing.
- Uses JUnit 5 and Jackson for response parsing.
- Test case reference document: `src/test/resources/api-test-cases.md`
- Execution logs are written to `target/test-logs/api-test.log` and attached to Allure report.
