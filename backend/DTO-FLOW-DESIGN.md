# POS System DTO Flow Design - Spring Boot

## 📋 Architecture Overview

```
Frontend → Controller → Service → Repository → Database
   ↓          ↓          ↓          ↓            ↓
Request    Validate   Business   Query      Entity
  DTO        DTO      Logic      Data       (Model)
   ↑          ↑          ↑          ↑            ↑
Response   Response   Map to    Entity     Database
  DTO        DTO      Response   Result
```

---

## 🎯 DTO Structure Organization

```
dto/
├── pos/                    # Keep existing POS-specific DTOs
│   ├── Cashier.java
│   ├── Product.java
│   ├── SaleItem.java
│   └── SalesRecord.java
├── user/
│   ├── UserRequest.java
│   ├── UserResponse.java
│   ├── LoginRequest.java
│   └── LoginResponse.java
├── order/
│   ├── OrderRequest.java
│   ├── OrderResponse.java
│   ├── OrderItemRequest.java
│   └── OrderItemResponse.java
├── menu/
│   ├── MenuItemRequest.java
│   ├── MenuItemResponse.java
│   └── MenuItemListResponse.java
├── category/
│   ├── CategoryRequest.java
│   └── CategoryResponse.java
├── customer/
│   ├── CustomerRequest.java
│   └── CustomerResponse.java
├── payment/
│   ├── PaymentRequest.java
│   └── PaymentResponse.java
└── common/
    ├── PageResponse.java
    └── ApiResponse.java
```

---

## 🔄 Complete Data Flow Example

### **Scenario: Creating a New Order in POS System**

#### 1️⃣ **Frontend Request** (JSON)
```json
POST /api/orders
{
  "branchId": 1,
  "cashierUserId": 5,
  "customerId": 10,
  "orderType": "DINE_IN",
  "note": "No sugar",
  "items": [
    {
      "menuItemId": 3,
      "quantity": 2,
      "unitPrice": 5.50,
      "note": "Extra hot"
    },
    {
      "menuItemId": 7,
      "quantity": 1,
      "unitPrice": 3.00
    }
  ]
}
```

#### 2️⃣ **Request DTO** (Controller Layer)
```java
package com.example.backend.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    
    @NotNull(message = "Branch ID is required")
    private Long branchId;
    
    @NotNull(message = "Cashier User ID is required")
    private Long cashierUserId;
    
    private Long customerId; // Optional
    
    @NotNull(message = "Order type is required")
    private String orderType; // "DINE_IN", "TAKEAWAY", "DELIVERY"
    
    private String note;
    
    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemRequest> items;
}
```

```java
package com.example.backend.dto.order;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {
    
    @NotNull(message = "Menu Item ID is required")
    private Long menuItemId;
    
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be positive")
    private Double unitPrice;
    
    private String note;
}
```

#### 3️⃣ **Controller** (Receives and Validates)
```java
package com.example.backend.controller;

import com.example.backend.dto.order.*;
import com.example.backend.dto.common.ApiResponse;
import com.example.backend.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody OrderRequest request) {
        
        OrderResponse response = orderService.createOrder(request);
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success(response, "Order created successfully"));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @PathVariable Long id) {
        
        OrderResponse response = orderService.getOrderById(id);
        
        return ResponseEntity.ok(
            ApiResponse.success(response, "Order retrieved successfully"));
    }
}
```

#### 4️⃣ **Service Layer** (Business Logic)
```java
package com.example.backend.services;

import com.example.backend.dto.order.*;
import com.example.backend.model.*;
import com.example.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final MenuItemRepository menuItemRepository;
    
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        // 1. Validate and fetch related entities
        BranchEntity branch = branchRepository.findById(request.getBranchId())
            .orElseThrow(() -> new RuntimeException("Branch not found"));
            
        UserEntity cashier = userRepository.findById(request.getCashierUserId())
            .orElseThrow(() -> new RuntimeException("Cashier not found"));
            
        CustomerEntity customer = null;
        if (request.getCustomerId() != null) {
            customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        }
        
        // 2. Map Request DTO → Entity
        OrderEntity order = new OrderEntity();
        order.setOrderNo(generateOrderNumber());
        order.setBranch(branch);
        order.setCashierUser(cashier);
        order.setCustomer(customer);
        order.setOrderType(OrderEntity.OrderType.valueOf(request.getOrderType()));
        order.setStatus(OrderEntity.OrderStatus.PENDING);
        order.setNote(request.getNote());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        // 3. Map order items
        List<OrderItemEntity> items = request.getItems().stream()
            .map(itemRequest -> {
                MenuItemEntity menuItem = menuItemRepository
                    .findById(itemRequest.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("Menu item not found"));
                
                OrderItemEntity item = new OrderItemEntity();
                item.setOrder(order);
                item.setMenuItem(menuItem);
                item.setQuantity(itemRequest.getQuantity());
                item.setUnitPrice(itemRequest.getUnitPrice());
                item.setNote(itemRequest.getNote());
                item.setCreatedAt(LocalDateTime.now());
                item.setUpdatedAt(LocalDateTime.now());
                
                return item;
            })
            .collect(Collectors.toList());
        
        order.setItems(items);
        
        // 4. Save to database
        OrderEntity savedOrder = orderRepository.save(order);
        
        // 5. Map Entity → Response DTO
        return mapToResponse(savedOrder);
    }
    
    public OrderResponse getOrderById(Long id) {
        OrderEntity order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        return mapToResponse(order);
    }
    
    // Helper method: Entity → Response DTO
    private OrderResponse mapToResponse(OrderEntity order) {
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getOrderId());
        response.setOrderNo(order.getOrderNo());
        response.setBranchId(order.getBranch().getBranchId());
        response.setBranchName(order.getBranch().getName());
        response.setCashierUserId(order.getCashierUser().getUserId());
        response.setCashierName(order.getCashierUser().getEmployee().getFirstName() 
            + " " + order.getCashierUser().getEmployee().getLastName());
        
        if (order.getCustomer() != null) {
            response.setCustomerId(order.getCustomer().getCustomerId());
            response.setCustomerName(order.getCustomer().getFullName());
        }
        
        response.setOrderType(order.getOrderType().name());
        response.setStatus(order.getStatus().name());
        response.setNote(order.getNote());
        
        List<OrderItemResponse> itemResponses = order.getItems().stream()
            .map(item -> {
                OrderItemResponse itemResponse = new OrderItemResponse();
                itemResponse.setOrderItemId(item.getOrderItemId());
                itemResponse.setMenuItemId(item.getMenuItem().getMenuItemId());
                itemResponse.setMenuItemName(item.getMenuItem().getName());
                itemResponse.setQuantity(item.getQuantity());
                itemResponse.setUnitPrice(item.getUnitPrice());
                itemResponse.setSubtotal(item.getQuantity() * item.getUnitPrice());
                itemResponse.setNote(item.getNote());
                return itemResponse;
            })
            .collect(Collectors.toList());
        
        response.setItems(itemResponses);
        response.setTotalAmount(itemResponses.stream()
            .mapToDouble(OrderItemResponse::getSubtotal)
            .sum());
        
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        
        return response;
    }
    
    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis();
    }
}
```

#### 5️⃣ **Repository** (Data Access Layer)
```java
package com.example.backend.repository;

import com.example.backend.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    
    List<OrderEntity> findByBranch_BranchId(Long branchId);
    
    List<OrderEntity> findByStatus(OrderEntity.OrderStatus status);
    
    List<OrderEntity> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    List<OrderEntity> findByOrderNoContaining(String orderNo);
}
```

#### 6️⃣ **Entity** (Database Model)
```java
// Already exists in your model/OrderEntity.java
// This represents the actual database table structure
```

#### 7️⃣ **Response DTO** (Sent back to Frontend)
```java
package com.example.backend.dto.order;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    
    private Long orderId;
    private String orderNo;
    
    // Branch info
    private Long branchId;
    private String branchName;
    
    // Cashier info
    private Long cashierUserId;
    private String cashierName;
    
    // Customer info (optional)
    private Long customerId;
    private String customerName;
    
    private String orderType;
    private String status;
    private String note;
    
    private List<OrderItemResponse> items;
    private Double totalAmount;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

```java
package com.example.backend.dto.order;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    
    private Long orderItemId;
    private Long menuItemId;
    private String menuItemName;
    private Integer quantity;
    private Double unitPrice;
    private Double subtotal;
    private String note;
}
```

#### 8️⃣ **Frontend Response** (JSON)
```json
{
  "success": true,
  "message": "Order created successfully",
  "data": {
    "orderId": 123,
    "orderNo": "ORD-1737461234567",
    "branchId": 1,
    "branchName": "Main Branch",
    "cashierUserId": 5,
    "cashierName": "John Doe",
    "customerId": 10,
    "customerName": "Jane Smith",
    "orderType": "DINE_IN",
    "status": "PENDING",
    "note": "No sugar",
    "items": [
      {
        "orderItemId": 201,
        "menuItemId": 3,
        "menuItemName": "Cappuccino",
        "quantity": 2,
        "unitPrice": 5.50,
        "subtotal": 11.00,
        "note": "Extra hot"
      },
      {
        "orderItemId": 202,
        "menuItemId": 7,
        "menuItemName": "Croissant",
        "quantity": 1,
        "unitPrice": 3.00,
        "subtotal": 3.00,
        "note": null
      }
    ],
    "totalAmount": 14.00,
    "createdAt": "2026-01-21T10:30:45",
    "updatedAt": "2026-01-21T10:30:45"
  }
}
```

---

## 🎨 Common Response Wrapper
```java
package com.example.backend.dto.common;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    
    private Boolean success;
    private String message;
    private T data;
    
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data);
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}
```

---

## ✅ Best Practices Followed

### 1. **Never Expose Entities to Frontend**
- ❌ Bad: Return `OrderEntity` from Controller
- ✅ Good: Convert `OrderEntity` → `OrderResponse`

### 2. **Use Validation Annotations**
```java
@NotNull, @NotEmpty, @Min, @Max, @Email, @Pattern, @Size
```

### 3. **Separate Request and Response DTOs**
- **Request DTO**: Contains only data needed to create/update
- **Response DTO**: Contains all data including IDs, timestamps, relations

### 4. **Use Meaningful Names**
```
UserRequest, UserResponse  ✅
UserDTO ❌ (too generic)
```

### 5. **Keep DTOs Simple**
- No business logic in DTOs
- Use Lombok for boilerplate (@Data, @NoArgsConstructor, etc.)

### 6. **Handle Relationships Carefully**
```java
// ❌ Bad: Infinite loop risk
private OrderEntity order; // in OrderItemResponse

// ✅ Good: Only include necessary IDs/names
private Long orderId;
private String orderNo;
```

---

## 🔐 Security Notes

1. **Passwords**: Never include in Response DTOs
```java
// UserResponse.java - NO password field
```

2. **Sensitive Data**: Filter before sending to frontend
3. **Role-based**: Return different DTOs based on user role

---

## 📦 Complete Module Example: Menu Items

### Request DTO
```java
@Data
public class MenuItemRequest {
    @NotBlank
    private String name;
    
    @NotNull
    private Long categoryId;
    
    @DecimalMin("0.0")
    private Double basePrice;
    
    private String imageUrl;
}
```

### Response DTO
```java
@Data
public class MenuItemResponse {
    private Long menuItemId;
    private String name;
    private Long categoryId;
    private String categoryName;
    private Double basePrice;
    private Boolean isActive;
    private String imageUrl;
    private LocalDateTime createdAt;
}
```

### Service Mapping
```java
// Request → Entity
MenuItemEntity entity = new MenuItemEntity();
entity.setName(request.getName());
entity.setCategory(categoryRepo.findById(request.getCategoryId())...);
entity.setBasePrice(request.getBasePrice());
entity.setIsActive(true);

// Entity → Response
MenuItemResponse response = new MenuItemResponse();
response.setMenuItemId(entity.getMenuItemId());
response.setName(entity.getName());
response.setCategoryId(entity.getCategory().getCategoryId());
response.setCategoryName(entity.getCategory().getName());
response.setBasePrice(entity.getBasePrice());
```

---

## 🚀 Summary

| Layer | Purpose | Example |
|-------|---------|---------|
| **Frontend** | User interaction | Vue.js form submission |
| **Request DTO** | Receive & validate data | `OrderRequest.java` |
| **Controller** | Route & validate | `@PostMapping("/api/orders")` |
| **Service** | Business logic | Map DTO ↔ Entity |
| **Repository** | Database operations | `orderRepository.save()` |
| **Entity** | Database model | `OrderEntity.java` (with @Entity) |
| **Response DTO** | Return safe data | `OrderResponse.java` |
| **Frontend** | Display data | Vue component shows order |

**Key Principle**: **Never send Entity directly to Frontend. Always use DTOs!**
