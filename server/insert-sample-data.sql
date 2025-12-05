-- ============================================
-- Sample Data Script for Lord Byron's Enterprises
-- ============================================
-- Database: lordbyrons_db
-- ============================================

USE lordbyrons_db;

-- ============================================
-- INSERT CATEGORIES
-- ============================================
INSERT INTO category (name, description) VALUES
('Electronics', 'Electronic devices and accessories'),
('Clothing', 'Apparel and fashion items'),
('Home & Garden', 'Home improvement and garden supplies'),
('Sports', 'Sports equipment and accessories');

-- ============================================
-- INSERT USERS
-- ============================================
INSERT INTO user (first_name, last_name, username, email, password, role) VALUES
-- Pseudo Admin
('John', 'Admin', 'admin', 'admin@lordbyrons.com', '$2a$10$/13yhTxKDTkwDpIf3PibYOQKlUMOXp.Yk.h97JeDfTryxJ2BxeZCm', 'ADMIN'),

-- Pseudo Employee
('Sarah', 'Employee', 'employee1', 'employee1@lordbyrons.com', '$2a$10$/13yhTxKDTkwDpIf3PibYOQKlUMOXp.Yk.h97JeDfTryxJ2BxeZCm', 'EMPLOYEE'),
('Mike', 'Worker', 'employee2', 'employee2@lordbyrons.com', '$2a$10$/13yhTxKDTkwDpIf3PibYOQKlUMOXp.Yk.h97JeDfTryxJ2BxeZCm', 'EMPLOYEE'),

-- Pseudo Customer
('Alice', 'Customer', 'alice', 'alice@example.com', '$2a$10$/13yhTxKDTkwDpIf3PibYOQKlUMOXp.Yk.h97JeDfTryxJ2BxeZCm', 'CUSTOMER'),
('Bob', 'Smith', 'bob', 'bob@example.com', '$2a$10$/13yhTxKDTkwDpIf3PibYOQKlUMOXp.Yk.h97JeDfTryxJ2BxeZCm', 'CUSTOMER'),
('Charlie', 'Brown', 'charlie', 'charlie@example.com', '$2a$10$/13yhTxKDTkwDpIf3PibYOQKlUMOXp.Yk.h97JeDfTryxJ2BxeZCm', 'CUSTOMER'),
('Diana', 'Prince', 'diana', 'diana@example.com', '$2a$10$/13yhTxKDTkwDpIf3PibYOQKlUMOXp.Yk.h97JeDfTryxJ2BxeZCm', 'CUSTOMER'),
('Edward', 'Norton', 'edward', 'edward@example.com', '$2a$10$/13yhTxKDTkwDpIf3PibYOQKlUMOXp.Yk.h97JeDfTryxJ2BxeZCm', 'CUSTOMER');

-- ============================================
-- INSERT PRODUCTS
-- ============================================
-- Category IDs: 1=Electronics, 2=Clothing, 3=Home & Garden, 4=Sports

INSERT INTO product (name, description, price, quantity, category_id) VALUES
-- Electronics (category_id = 1)
('Panasonic TV', '70-inch 4K Ultra HD Smart TV', 1299.99, 25, 1),
('Wireless Mouse', 'Ergonomic wireless mouse with long battery life', 29.99, 150, 1),
('USB-C Hub', '7-in-1 USB-C hub with HDMI, USB 3.0, and SD card reader', 49.99, 80, 1),
('Bluetooth Headphones', 'Noise-cancelling wireless headphones', 199.99, 45, 1),
('Smart Watch', 'Fitness tracking smartwatch with heart rate monitor', 299.99, 60, 1),

-- Clothing (category_id = 2)
('Cotton T-Shirt', '100% organic cotton t-shirt, comfortable fit', 24.99, 200, 2),
('Denim Jeans', 'Classic fit denim jeans, various sizes', 59.99, 120, 2),
('Winter Jacket', 'Warm winter jacket with water-resistant coating', 89.99, 75, 2),
('Running Shoes', 'Lightweight running shoes with cushioned sole', 79.99, 90, 2),
('Baseball Cap', 'Adjustable baseball cap with logo', 19.99, 150, 2),

-- Home & Garden (category_id = 3)
('Garden Tool Set', 'Complete set of 10 essential garden tools', 49.99, 60, 3),
('Indoor Plant Pot', 'Decorative ceramic plant pot, 8 inch', 24.99, 100, 3),
('LED Light Bulbs', 'Energy-efficient LED bulbs, pack of 4', 19.99, 200, 3),
('Kitchen Knife Set', 'Professional 8-piece knife set', 89.99, 45, 3),
('Throw Pillow', 'Soft decorative throw pillow, various colors', 15.99, 120, 3),

-- Sports (category_id = 4)
('Basketball', 'Official size basketball with grip texture', 29.99, 80, 4),
('Yoga Mat', 'Non-slip yoga mat with carrying strap', 34.99, 70, 4),
('Dumbbell Set', 'Adjustable dumbbell set, 5-50 lbs', 149.99, 30, 4),
('Tennis Racket', 'Professional tennis racket with case', 79.99, 50, 4),
('Water Bottle', 'Insulated stainless steel water bottle, 32oz', 24.99, 150, 4);


SELECT COUNT(*) as total_users FROM user;
SELECT COUNT(*) as total_products FROM product;
SELECT COUNT(*) as total_categories FROM category;
SELECT * FROM user;
SELECT * FROM product;
SELECT * FROM category;