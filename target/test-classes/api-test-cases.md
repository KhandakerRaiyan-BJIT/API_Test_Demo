# AutomationExercise API Test Cases

This document maps automated API tests to their expected behavior.

## TC01 - Get all products list
- Endpoint: `GET /api/productsList`
- Expected HTTP status: `200`
- Validations:
  - `responseCode = 200`
  - `products` is a non-empty array
  - first product has `id`, `name`, `price`, `brand`

## TC02 - POST to products list is not allowed
- Endpoint: `POST /api/productsList`
- Expected HTTP status: `200`
- Validations:
  - `responseCode = 405`
  - `message = This request method is not supported.`

## TC03 - Get all brands list
- Endpoint: `GET /api/brandsList`
- Expected HTTP status: `200`
- Validations:
  - `responseCode = 200`
  - `brands` is a non-empty array
  - first brand has `id`, `brand`

## TC04 - PUT to brands list is not allowed
- Endpoint: `PUT /api/brandsList`
- Expected HTTP status: `200`
- Validations:
  - `responseCode = 405`
  - `message = This request method is not supported.`

## TC05 - Search products
- Endpoint: `POST /api/searchProduct`
- Form field: `search_product={search.product.term}`
- Expected HTTP status: `200`
- Validations:
  - `responseCode = 200`
  - `products` is a non-empty array
  - first product has `id`, `name`, `price`, `brand`

