# DTO Folder Structure - POS System

## 📁 Complete Directory Structure

```
dto/
├── common/                         ✅ Created
│   ├── ApiResponse.java           - Generic wrapper for all API responses
│   └── PageResponse.java          - Pagination wrapper
│
├── user/                          ✅ Created
│   ├── UserRequest.java           - Create/update user
│   ├── UserResponse.java          - User data response
│   ├── LoginRequest.java          - Login credentials
│   └── LoginResponse.java         - Login result with token
│
├── order/                         ✅ Created
│   ├── OrderRequest.java          - Create new order
│   ├── OrderResponse.java         - Order details
│   ├── OrderItemRequest.java      - Order line item (nested)
│   └── OrderItemResponse.java     - Order line item response
│
├── menu/                          ✅ Created
│   ├── MenuItemRequest.java       - Create/update menu item
│   └── MenuItemResponse.java      - Menu item details
│
├── category/                      ✅ Created
│   ├── CategoryRequest.java       - Create/update category
│   └── CategoryResponse.java      - Category details
│
├── customer/                      ✅ Created
│   ├── CustomerRequest.java       - Create/update customer
│   └── CustomerResponse.java      - Customer details
│
├── payment/                       ✅ Created
│   ├── PaymentRequest.java        - Process payment
│   └── PaymentResponse.java       - Payment result
│
└── pos/                           ✅ Keep (existing)
    ├── Cashier.java               - POS cashier data
    ├── Product.java               - POS product data
    ├── SaleItem.java              - POS sale item
    └── SalesRecord.java           - POS sales record

```

## 🔄 Data Flow Summary

### 1. **Order Flow** (Most Important for POS)
```
Frontend          Controller              Service                    Repository          Database
   │                   │                     │                           │                  │
   │  POST /orders     │                     │                           │                  │
   ├──────────────────►│  OrderRequest       │                           │                  │
   │                   ├────────────────────►│  Validate                 │                  │
   │                   │                     ├──────────────────────────►│  findById()      │
   │                   │                     │                           ├─────────────────►│
   │                   │                     │  OrderEntity              │  Query           │
   │                   │                     │◄──────────────────────────┤  Result          │
   │                   │                     │                           │◄─────────────────┤
   │                   │                     ├──────────────────────────►│  save()          │
   │                   │                     │                           ├─────────────────►│
   │                   │  OrderResponse      │                           │  Insert          │
   │                   │◄────────────────────┤                           │◄─────────────────┤
   │  OrderResponse    │                     │                           │                  │
   │◄──────────────────┤                     │                           │                  │
   │                   │                     │                           │                  │
```

### 2. **Menu Item Flow**
```
GET /api/menu-items/{id}
Frontend → Controller → Service → Repository → MenuItemEntity
                                           ↓
Frontend ← MenuItemResponse ← Map Entity ← Database Query
```

### 3. **Payment Flow**
```
POST /api/payments
Frontend → PaymentRequest → Service → Create PaymentEntity & Update OrderEntity
                                   ↓
Frontend ← PaymentResponse ← Map Entity ← Save to Database
```

## 📊 DTO Naming Convention

| Type | Pattern | Example |
|------|---------|---------|
| Request DTO | `{Entity}Request` | `OrderRequest` |
| Response DTO | `{Entity}Response` | `OrderResponse` |
| List Response | `{Entity}ListResponse` | `MenuItemListResponse` |
| Nested Request | `{Parent}{Child}Request` | `OrderItemRequest` |
| Login/Auth | `{Action}Request/Response` | `LoginRequest` |

## 🎯 Key Principles Applied

### ✅ DO's
1. **Separate Request and Response DTOs**
   - Request: Only fields needed for creation/update
   - Response: All fields including IDs, timestamps, related data

2. **Use Validation Annotations**
   ```java
   @NotNull, @NotBlank, @NotEmpty
   @Min, @Max, @DecimalMin, @DecimalMax
   @Email, @Pattern, @Size
   ```

3. **Never Expose Entities**
   ```java
   ❌ return orderEntity;  // Bad
   ✅ return orderResponse; // Good
   ```

4. **Flatten Related Data**
   ```java
   ✅ private String categoryName;  // Good - include name
   ❌ private CategoryEntity category; // Bad - don't expose entity
   ```

5. **Use Generic Wrappers**
   ```java
   ApiResponse<OrderResponse>
   PageResponse<MenuItemResponse>
   ```

### ❌ DON'Ts
1. Never include passwords in Response DTOs
2. Never expose entity relationships directly
3. Don't reuse Request DTOs for Response
4. Don't include unnecessary data in responses

## 🔐 Security Considerations

### UserResponse.java
```java
// ❌ Bad
private String password;

// ✅ Good
// No password field at all
```

### Sensitive Fields
- Credit card numbers: mask or exclude
- Passwords: never include
- Internal IDs: carefully consider exposure

## 📝 Example Usage in Controller

```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody OrderRequest request) {
        
        OrderResponse response = orderService.createOrder(request);
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success(response, "Order created"));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(
            @PathVariable Long id) {
        
        OrderResponse response = orderService.getOrderById(id);
        
        return ResponseEntity.ok(
            ApiResponse.success(response));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        PageResponse<OrderResponse> response = 
            orderService.getAllOrders(page, size);
        
        return ResponseEntity.ok(
            ApiResponse.success(response));
    }
}
```

## 🚀 Next Steps

1. **Create Mapper Classes** (Optional but recommended)
   ```java
   @Component
   public class OrderMapper {
       public OrderEntity toEntity(OrderRequest request);
       public OrderResponse toResponse(OrderEntity entity);
   }
   ```

2. **Add More DTOs as needed**
   - Employee DTOs
   - Inventory DTOs
   - Report DTOs
   - Dashboard DTOs

3. **Consider MapStruct** for automatic mapping
   ```xml
   <dependency>
       <groupId>org.mapstruct</groupId>
       <artifactId>mapstruct</artifactId>
   </dependency>
   ```

## 📚 Resources

- Spring Boot Validation: https://spring.io/guides/gs/validating-form-input/
- DTO Best Practices: https://martinfowler.com/eaaCatalog/dataTransferObject.html
- MapStruct: https://mapstruct.org/

---

**Created**: January 21, 2026
**POS System**: Cafe Management Application
**Framework**: Spring Boot + JPA
