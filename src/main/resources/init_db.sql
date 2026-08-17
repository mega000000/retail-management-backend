-- Database initialization script for Retail Management System

USE retail_db;

-- Drop existing procedures to avoid duplication
DROP PROCEDURE IF EXISTS GetMonthlySalesForEachStore;
DROP PROCEDURE IF EXISTS GetAggregateSalesForCompany;
DROP PROCEDURE IF EXISTS GetTopSellingProductsByCategory;

-- Drop existing tables to start fresh (in dependency order)
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS order_details;
DROP TABLE IF EXISTS inventory;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS stores;

-- Create Tables
CREATE TABLE stores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    phone_number VARCHAR(50),
    email VARCHAR(100)
);

CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    description TEXT,
    sku VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT uq_store_product UNIQUE (store_id, product_id)
);

CREATE TABLE customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone_number VARCHAR(50)
);

CREATE TABLE order_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    store_id BIGINT NOT NULL,
    order_date DATETIME NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (store_id) REFERENCES stores(id)
);

CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_details_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_details_id) REFERENCES order_details(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Seed Sample Data

-- 1. Stores
INSERT INTO stores (id, name, address, phone_number, email) VALUES
(1, 'Downtown Tech Store', '123 Main St, Downtown', '555-0101', 'downtown@retail.com'),
(2, 'Suburban Electronics', '456 Oak Ave, Suburbs', '555-0102', 'suburban@retail.com'),
(3, 'Metro Mall Store', '789 Pine Rd, Metro', '555-0103', 'metromall@retail.com'),
(4, 'Westside Retail', '321 Elm St, Westside', '555-0104', 'westside@retail.com'),
(5, 'Eastside Outlet', '654 Maple Dr, Eastside', '555-0105', 'eastside@retail.com');

-- 2. Products
INSERT INTO products (id, name, category, price, description, sku) VALUES
(1, 'T-shirt', 'Clothing', 25.00, '100% cotton casual t-shirt', 'CLO-TSH-001'),
(2, 'Smartwatch', 'Electronics', 200.00, 'Fitness tracking smartwatch', 'ELE-SMW-002'),
(3, 'Dining Table', 'Furniture', 600.00, 'Wooden 6-seater dining table', 'FUR-DNT-003'),
(4, 'Sofa', 'Furniture', 800.00, 'Comfortable 3-seater fabric sofa', 'FUR-SOF-004'),
(5, 'Milk', 'Groceries', 2.50, '1 Gallon whole milk', 'GRO-MLK-005'),
(6, 'Vacuum Cleaner', 'Home Appliances', 150.00, 'Powerful upright vacuum cleaner', 'HOM-VAC-006'),
(7, 'ACER LAPTOP', 'Laptops and Monitors', 1000.00, '15.6 inch gaming laptop', 'LAP-ACE-007'),
(8, 'Samsung S24', 'Mobile', 100.00, 'Flagship Android smartphone', 'MOB-SAM-008');

-- 3. Inventory (Add stock for products in each store)
INSERT INTO inventory (store_id, product_id, quantity) VALUES
(1, 1, 50), (1, 2, 20), (1, 3, 5), (1, 4, 3), (1, 5, 200), (1, 6, 15), (1, 7, 10), (1, 8, 25),
(2, 1, 40), (2, 2, 15), (2, 3, 4), (2, 4, 2), (2, 5, 150), (2, 6, 12), (2, 7, 8),  (2, 8, 20),
(3, 1, 30), (3, 2, 10), (3, 3, 2), (3, 4, 1), (3, 5, 100), (3, 6, 8),  (3, 7, 5),  (3, 8, 15),
(4, 1, 45), (4, 2, 18), (4, 3, 3), (4, 4, 2), (4, 5, 180), (4, 6, 14), (4, 7, 9),  (4, 8, 22),
(5, 1, 35), (5, 2, 12), (5, 3, 2), (5, 4, 1), (5, 5, 120), (5, 6, 10), (5, 7, 6),  (5, 8, 18);

-- 4. Customers
INSERT INTO customers (id, name, email, phone_number) VALUES
(1, 'Alice Smith', 'alice@gmail.com', '555-1111'),
(2, 'Bob Jones', 'bob@yahoo.com', '555-2222'),
(3, 'Charlie Brown', 'charlie@outlook.com', '555-3333');

-- 5. Orders (Order details + Order items to reflect sales matching the prompt examples)
-- March 2025 sales
-- Store 1 total sales: 10100 (e.g. 1 ACER LAPTOP, 10 Smartwatches, 4 Sofas, 1 Dining Table, 4 T-shirts)
-- Store 2 total sales: 2380
-- Store 3 total sales: 2070
-- Store 4 total sales: 1950
-- Store 5 total sales: 1500
-- Let's populate order_details and order_items:

-- March 2025 orders:
-- Store 1:
INSERT INTO order_details (id, customer_id, store_id, order_date, total_amount, status) VALUES
(1, 1, 1, '2025-03-15 10:00:00', 10100.00, 'COMPLETED');
INSERT INTO order_items (order_details_id, product_id, quantity, unit_price) VALUES
(1, 7, 7, 1000.00), -- 7000
(1, 2, 10, 200.00), -- 2000
(1, 4, 1, 800.00),  -- 800
(1, 1, 12, 25.00);  -- 300

-- Store 2:
INSERT INTO order_details (id, customer_id, store_id, order_date, total_amount, status) VALUES
(2, 2, 2, '2025-03-16 11:30:00', 2380.00, 'COMPLETED');
INSERT INTO order_items (order_details_id, product_id, quantity, unit_price) VALUES
(2, 2, 5, 200.00),  -- 1000
(2, 6, 6, 150.00),  -- 900
(2, 8, 4, 100.00),  -- 400
(2, 5, 32, 2.50);   -- 80

-- Store 3:
INSERT INTO order_details (id, customer_id, store_id, order_date, total_amount, status) VALUES
(3, 3, 3, '2025-03-17 14:15:00', 2070.00, 'COMPLETED');
INSERT INTO order_items (order_details_id, product_id, quantity, unit_price) VALUES
(3, 3, 3, 600.00),  -- 1800
(3, 1, 10, 25.00),  -- 250
(3, 5, 8, 2.50);    -- 20

-- Store 4:
INSERT INTO order_details (id, customer_id, store_id, order_date, total_amount, status) VALUES
(4, 1, 4, '2025-03-18 16:00:00', 1950.00, 'COMPLETED');
INSERT INTO order_items (order_details_id, product_id, quantity, unit_price) VALUES
(4, 7, 1, 1000.00), -- 1000
(4, 6, 5, 150.00),  -- 750
(4, 8, 2, 100.00);  -- 200

-- Store 5:
INSERT INTO order_details (id, customer_id, store_id, order_date, total_amount, status) VALUES
(5, 2, 5, '2025-03-19 09:45:00', 1500.00, 'COMPLETED');
INSERT INTO order_items (order_details_id, product_id, quantity, unit_price) VALUES
(5, 4, 1, 800.00),  -- 800
(5, 6, 4, 150.00),  -- 600
(5, 8, 1, 100.00);  -- 100

-- Stored Procedures implementation

DELIMITER //

-- 1. Get Monthly Sales For Each Store
CREATE PROCEDURE GetMonthlySalesForEachStore(IN sale_year INT, IN sale_month INT)
BEGIN
    SELECT 
        store_id, 
        SUM(total_amount) AS total_sales, 
        sale_month AS sale_month, 
        sale_year AS sale_year
    FROM order_details
    WHERE YEAR(order_date) = sale_year AND MONTH(order_date) = sale_month
    GROUP BY store_id;
END //

-- 2. Total Company Sales by Month and Year
CREATE PROCEDURE GetAggregateSalesForCompany(IN sale_year INT, IN sale_month INT)
BEGIN
    SELECT 
        SUM(total_amount) AS total_sales, 
        sale_month AS sale_month, 
        sale_year AS sale_year
    FROM order_details
    WHERE YEAR(order_date) = sale_year AND MONTH(order_date) = sale_month;
END //

-- 3. Get Top-Selling Products By Category
CREATE PROCEDURE GetTopSellingProductsByCategory(IN limit_num INT, IN sale_year INT)
BEGIN
    WITH RankedProducts AS (
        SELECT 
            p.category AS category,
            p.name AS name,
            SUM(oi.quantity) AS total_quantity_sold,
            SUM(oi.quantity * oi.unit_price) AS total_sales,
            ROW_NUMBER() OVER (PARTITION BY p.category ORDER BY SUM(oi.quantity) DESC) AS rn
        FROM order_items oi
        JOIN products p ON oi.product_id = p.id
        JOIN order_details od ON oi.order_details_id = od.id
        WHERE YEAR(od.order_date) = sale_year
        GROUP BY p.category, p.id, p.name
    )
    SELECT category, name, total_quantity_sold, total_sales
    FROM RankedProducts
    WHERE rn <= limit_num
    ORDER BY category, total_quantity_sold DESC;
END //

DELIMITER ;
