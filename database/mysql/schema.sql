CREATE DATABASE IF NOT EXISTS shopping_platform
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE shopping_platform;

CREATE TABLE IF NOT EXISTS home_banners (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(80) NOT NULL,
  subtitle VARCHAR(140) NOT NULL,
  cta_text VARCHAR(32),
  background_color VARCHAR(32),
  sort_order INT DEFAULT 0,
  enabled BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS product_categories (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(64) NOT NULL,
  icon VARCHAR(32),
  target_route VARCHAR(120) NOT NULL,
  sort_order INT DEFAULT 0,
  enabled BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS products (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  subtitle VARCHAR(160) NOT NULL,
  description VARCHAR(800),
  price DECIMAL(10, 2) NOT NULL,
  original_price DECIMAL(10, 2),
  sales BIGINT DEFAULT 0,
  tag VARCHAR(32),
  image_url VARCHAR(255),
  stock INT DEFAULT 99,
  specs VARCHAR(500),
  category_id BIGINT NOT NULL,
  sort_order INT DEFAULT 0,
  available BOOLEAN NOT NULL DEFAULT TRUE,
  created_at DATETIME NOT NULL,
  INDEX idx_products_category_id (category_id),
  INDEX idx_products_available_sort (available, sort_order)
);

CREATE TABLE IF NOT EXISTS users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(40) NOT NULL UNIQUE,
  email VARCHAR(120) NOT NULL UNIQUE,
  phone VARCHAR(20) NOT NULL UNIQUE,
  avatar_url VARCHAR(255),
  balance DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
  password_hash VARCHAR(220) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS auth_sessions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  token_hash VARCHAR(64) NOT NULL UNIQUE,
  expires_at DATETIME NOT NULL,
  revoked BOOLEAN NOT NULL DEFAULT FALSE,
  created_at DATETIME NOT NULL,
  INDEX idx_auth_sessions_user_id (user_id),
  INDEX idx_auth_sessions_token_hash (token_hash),
  CONSTRAINT fk_auth_sessions_user
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS cart_items (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  quantity INT NOT NULL,
  selected BOOLEAN NOT NULL DEFAULT TRUE,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  UNIQUE KEY uk_cart_user_product (user_id, product_id),
  INDEX idx_cart_items_user_id (user_id),
  CONSTRAINT fk_cart_items_user
    FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_cart_items_product
    FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE IF NOT EXISTS user_orders (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_no VARCHAR(40) NOT NULL UNIQUE,
  user_id BIGINT NOT NULL,
  status VARCHAR(24) NOT NULL,
  total_amount DECIMAL(10, 2) NOT NULL,
  receiver_name VARCHAR(40) NOT NULL,
  created_at DATETIME NOT NULL,
  INDEX idx_user_orders_user_id (user_id),
  INDEX idx_user_orders_status (status),
  CONSTRAINT fk_user_orders_user
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS order_items (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  product_name VARCHAR(100) NOT NULL,
  price DECIMAL(10, 2) NOT NULL,
  quantity INT NOT NULL,
  subtotal DECIMAL(10, 2) NOT NULL,
  INDEX idx_order_items_order_id (order_id),
  CONSTRAINT fk_order_items_order
    FOREIGN KEY (order_id) REFERENCES user_orders(id)
);
