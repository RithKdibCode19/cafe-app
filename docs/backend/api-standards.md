# Backend API Standards

## Response Wrapper
All API endpoints MUST return data wrapped in a standard `ApiResponse<T>` object to simplify frontend consumption and error handling.

```java
public class ApiResponse<T> {
    private Boolean success;
    private String message;
    private T data;
}
```

### Success Response
```json
{
  "success": true,
  "message": "Order created successfully",
  "data": {
    "orderId": 123,
    "totalAmount": 55.00
  }
}
```

### Error Response
```json
{
  "success": false,
  "message": "Resource not found",
  "data": null
}
```

## Controller Pattern
Controllers should look like this:

```java
@PostMapping
public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@RequestBody OrderRequest request) {
    OrderResponse response = orderService.create(request);
    return ResponseEntity.ok(ApiResponse.success(response, "Created successfully"));
}
```

## Error Handling
Use a global `@ControllerAdvice` to catch exceptions (like `EntityNotFoundException`) and return a standardized `ApiResponse.error()` with the appropriate HTTP status code.
