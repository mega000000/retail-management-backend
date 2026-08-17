# Retail Management Backend System

A backend REST API for a warehouse distribution company that supports multiple retail stores. Built with **Java**, **Spring Boot 3.3.2**, **Hibernate/JPA**, **MySQL**, and **MongoDB**.

---

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 21 | Programming Language |
| Spring Boot 3.3.2 | Application Framework |
| Hibernate / JPA | ORM for MySQL |
| MySQL (XAMPP) | Relational Database |
| MongoDB | NoSQL for Product Reviews |
| Gradle | Build Tool |

---

## Project Structure

```
src/main/java/com/retail/backend/
├── model/
│   ├── Store.java
│   ├── Product.java
│   ├── Inventory.java
│   ├── Customer.java
│   ├── OrderDetails.java
│   ├── OrderItem.java
│   └── Review.java           ← MongoDB Document
├── repository/
│   ├── StoreRepository.java
│   ├── ProductRepository.java
│   ├── InventoryRepository.java
│   ├── CustomerRepository.java
│   ├── OrderDetailsRepository.java  ← Stored Procedure calls
│   ├── OrderItemRepository.java
│   └── ReviewRepository.java        ← MongoRepository
├── service/
│   ├── StoreService.java
│   ├── ProductService.java
│   ├── InventoryService.java
│   ├── OrderService.java
│   └── ReviewService.java
├── controller/
│   ├── StoreController.java
│   ├── ProductController.java
│   ├── InventoryController.java
│   ├── OrderController.java
│   └── ReviewController.java
├── dto/
│   ├── OrderRequest.java
│   └── OrderItemRequest.java
├── exception/
│   ├── ResourceNotFoundException.java
│   ├── InsufficientStockException.java
│   └── GlobalExceptionHandler.java
└── RetailBackendApplication.java
```

---

## Prerequisites

- Java 21+
- MySQL running on port `3306` (XAMPP recommended)
- MongoDB running on port `27017`
- Gradle (wrapper included)

---

## Setup Instructions

### 1. Clone the repo
```bash
git clone https://github.com/YOUR_USERNAME/retail-management-backend.git
cd retail-management-backend
```

### 2. Create the MySQL database
```sql
CREATE DATABASE retail_db;
```

### 3. Initialize the database (tables, seed data, stored procedures)
```bash
mysql -u root retail_db < src/main/resources/init_db.sql
```

### 4. Configure `application.properties`
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/retail_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
spring.data.mongodb.uri=mongodb://localhost:27017/retail_reviews_db
server.port=8081
```

### 5. Run the application
```bash
./gradlew bootRun
```

Server starts at: `http://localhost:8081`

---

## API Endpoints

### Stores — `/api/stores`
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/stores` | Get all stores |
| GET | `/api/stores/{id}` | Get store by ID |
| POST | `/api/stores` | Create a store |
| PUT | `/api/stores/{id}` | Update a store |
| DELETE | `/api/stores/{id}` | Delete a store |

### Products — `/api/products`
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/products` | Get all products |
| GET | `/api/products/{id}` | Get product by ID |
| POST | `/api/products` | Create a product |
| PUT | `/api/products/{id}` | Update a product |
| DELETE | `/api/products/{id}` | Delete a product |

### Inventory — `/api/inventory`
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/inventory` | Get all inventory |
| GET | `/api/inventory/store/{storeId}` | Get inventory by store |
| PUT | `/api/inventory/store/{sid}/product/{pid}?quantity=N` | Update stock level |

### Orders — `/api/orders`
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/orders` | Get all orders |
| GET | `/api/orders/{id}` | Get order by ID |
| POST | `/api/orders` | Place a new order |

**Place Order Body Example:**
```json
{
  "customerName": "Jane Doe",
  "customerEmail": "jane@example.com",
  "customerPhone": "555-9999",
  "storeId": 1,
  "items": [
    { "productId": 1, "quantity": 2 },
    { "productId": 2, "quantity": 1 }
  ]
}
```

### Reviews — `/api/reviews` (MongoDB)
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/reviews` | Get all reviews |
| GET | `/api/reviews/product/{productId}` | Get reviews by product |
| POST | `/api/reviews` | Submit a review |

### Analytics Reports — Stored Procedures
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/orders/reports/monthly-store-sales?year=2025&month=3` | Sales per store |
| GET | `/api/orders/reports/aggregate-company-sales?year=2025&month=3` | Total company sales |
| GET | `/api/orders/reports/top-selling-products?limit=3&year=2025` | Top products by category |

---

## Stored Procedures (MySQL)

Three stored procedures are defined in `init_db.sql`:

1. `GetMonthlySalesForEachStore(year, month)`
2. `GetAggregateSalesForCompany(year, month)`
3. `GetTopSellingProductsByCategory(limit_num, year)`

---

## Running Tests

```bash
./gradlew test
```
