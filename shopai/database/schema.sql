-- ============================================================
-- ShopAI India — MySQL Database Schema
-- Enhanced E-Commerce Personalization Using AI Content Generation
-- ============================================================

CREATE DATABASE IF NOT EXISTS shopai_india CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE shopai_india;

-- Categories
CREATE TABLE categories (
  category_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name        VARCHAR(100) NOT NULL,
  slug        VARCHAR(100) UNIQUE NOT NULL,
  description TEXT,
  icon_class  VARCHAR(100),
  parent_id   BIGINT DEFAULT NULL,
  is_active   BOOLEAN DEFAULT TRUE,
  FOREIGN KEY (parent_id) REFERENCES categories(category_id)
);

-- Users
CREATE TABLE users (
  user_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
  full_name     VARCHAR(100) NOT NULL,
  email         VARCHAR(150) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  phone         VARCHAR(15),
  avatar_url    VARCHAR(500),
  city          VARCHAR(100),
  state         VARCHAR(100),
  pincode       VARCHAR(10),
  address       TEXT,
  loyalty_points INT DEFAULT 0,
  is_active     BOOLEAN DEFAULT TRUE,
  created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Products
CREATE TABLE products (
  product_id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  category_id            BIGINT NOT NULL,
  title                  VARCHAR(255) NOT NULL,
  ai_generated_title     VARCHAR(300),
  description            TEXT,
  ai_generated_description TEXT,
  ai_seo_description     TEXT,
  price                  DECIMAL(12,2) NOT NULL,
  discount_percent       DECIMAL(5,2) DEFAULT 0.00,
  stock_qty              INT DEFAULT 0,
  sku                    VARCHAR(100) UNIQUE,
  brand                  VARCHAR(100),
  image_url              VARCHAR(500),
  rating                 DECIMAL(3,2) DEFAULT 0.00,
  review_count           INT DEFAULT 0,
  is_trending            BOOLEAN DEFAULT FALSE,
  is_featured            BOOLEAN DEFAULT FALSE,
  is_active              BOOLEAN DEFAULT TRUE,
  created_at             TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at             TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (category_id) REFERENCES categories(category_id)
);

-- Orders
CREATE TABLE orders (
  order_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id          BIGINT NOT NULL,
  order_number     VARCHAR(50) UNIQUE NOT NULL,
  total_amount     DECIMAL(12,2),
  discount_amount  DECIMAL(12,2) DEFAULT 0,
  shipping_amount  DECIMAL(8,2) DEFAULT 0,
  final_amount     DECIMAL(12,2),
  status           ENUM('PENDING','CONFIRMED','PROCESSING','SHIPPED','DELIVERED','CANCELLED','RETURNED') DEFAULT 'PENDING',
  payment_method   VARCHAR(50),
  payment_status   ENUM('PENDING','PAID','FAILED','REFUNDED') DEFAULT 'PENDING',
  shipping_address TEXT,
  tracking_number  VARCHAR(100),
  promo_code       VARCHAR(50),
  created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Order Items
CREATE TABLE order_items (
  item_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id         BIGINT NOT NULL,
  product_id       BIGINT NOT NULL,
  quantity         INT NOT NULL,
  unit_price       DECIMAL(12,2),
  discount_percent DECIMAL(5,2) DEFAULT 0,
  total_price      DECIMAL(12,2),
  FOREIGN KEY (order_id)   REFERENCES orders(order_id) ON DELETE CASCADE,
  FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- Cart
CREATE TABLE cart (
  cart_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id    BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  quantity   INT DEFAULT 1,
  added_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uq_cart (user_id, product_id),
  FOREIGN KEY (user_id)    REFERENCES users(user_id) ON DELETE CASCADE,
  FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- Wishlist
CREATE TABLE wishlist (
  wishlist_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id     BIGINT NOT NULL,
  product_id  BIGINT NOT NULL,
  added_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uq_wish (user_id, product_id),
  FOREIGN KEY (user_id)    REFERENCES users(user_id) ON DELETE CASCADE,
  FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- Reviews
CREATE TABLE reviews (
  review_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  product_id         BIGINT NOT NULL,
  user_id            BIGINT NOT NULL,
  rating             INT CHECK (rating BETWEEN 1 AND 5),
  title              VARCHAR(200),
  comment            TEXT,
  ai_summary         TEXT,
  is_verified_purchase BOOLEAN DEFAULT FALSE,
  helpful_count      INT DEFAULT 0,
  created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (product_id) REFERENCES products(product_id),
  FOREIGN KEY (user_id)    REFERENCES users(user_id)
);

-- User Behavior (AI training data)
CREATE TABLE user_behavior (
  behavior_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id         BIGINT,
  session_id      VARCHAR(100),
  product_id      BIGINT,
  category_id     BIGINT,
  action_type     ENUM('VIEW','SEARCH','ADD_CART','PURCHASE','WISHLIST','COMPARE','REVIEW') NOT NULL,
  search_query    VARCHAR(255),
  duration_seconds INT,
  device_type     VARCHAR(50),
  created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id)    REFERENCES users(user_id)    ON DELETE SET NULL,
  FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE SET NULL
);

-- AI Personalization Profiles
CREATE TABLE ai_user_profiles (
  profile_id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id              BIGINT UNIQUE NOT NULL,
  preferred_categories JSON,
  preferred_brands     JSON,
  price_range_min      DECIMAL(10,2),
  price_range_max      DECIMAL(10,2),
  interest_keywords    JSON,
  last_updated         TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- AI Content Log
CREATE TABLE ai_content_log (
  log_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  product_id      BIGINT,
  content_type    ENUM('title','description','seo','email','promo','comparison','chatbot') NOT NULL,
  generated_content TEXT,
  model_used      VARCHAR(100),
  tokens_used     INT,
  created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE SET NULL
);

-- Promotions
CREATE TABLE promotions (
  promo_id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  code                 VARCHAR(50) UNIQUE NOT NULL,
  title                VARCHAR(200),
  ai_generated_content TEXT,
  discount_type        ENUM('PERCENT','FLAT') NOT NULL,
  discount_value       DECIMAL(10,2),
  min_order_amount     DECIMAL(10,2) DEFAULT 0,
  max_uses             INT,
  used_count           INT DEFAULT 0,
  start_date           DATE,
  end_date             DATE,
  is_active            BOOLEAN DEFAULT TRUE
);

-- Notifications
CREATE TABLE notifications (
  notif_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id    BIGINT NOT NULL,
  title      VARCHAR(200),
  message    TEXT,
  type       ENUM('PROMO','ORDER','RESTOCK','AI_SUGGESTION','SYSTEM') DEFAULT 'SYSTEM',
  is_read    BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Chatbot Conversations
CREATE TABLE chatbot_conversations (
  conv_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id    BIGINT,
  session_id VARCHAR(100) NOT NULL,
  message    TEXT NOT NULL,
  response   TEXT,
  intent     VARCHAR(100),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- SEED DATA
-- ============================================================
INSERT INTO categories (name, slug, description, icon_class) VALUES
('Electronics',  'electronics',  'Mobiles, Laptops, Tablets and Gadgets',     'fas fa-mobile-alt'),
('Fashion',      'fashion',      'Clothing, Footwear and Accessories',         'fas fa-tshirt'),
('Home & Living','home-living',  'Furniture, Decor and Appliances',            'fas fa-home'),
('Books',        'books',        'Educational and Fiction Books',              'fas fa-book'),
('Sports',       'sports',       'Gym Equipment and Sportswear',               'fas fa-dumbbell'),
('Beauty',       'beauty',       'Skincare, Haircare and Personal Care',       'fas fa-spa'),
('Groceries',    'groceries',    'Fresh Produce and Packaged Foods',           'fas fa-shopping-basket'),
('Toys & Kids',  'toys',         'Toys, Games and Educational Kits',           'fas fa-gamepad');

INSERT INTO products (category_id,title,ai_generated_title,description,ai_generated_description,ai_seo_description,price,discount_percent,stock_qty,sku,brand,rating,review_count,is_trending,is_featured,image_url) VALUES
(1,'Samsung Galaxy S24 Ultra','Samsung Galaxy S24 Ultra – AI-Powered Flagship for India','Latest Samsung flagship with S Pen and 200MP camera','Experience mobile innovation with 200MP AI camera, titanium frame, Snapdragon 8 Gen 3. Perfect for Indian professionals.','Buy Samsung Galaxy S24 Ultra India | 200MP AI Camera Best Price Online',124999.00,10,50,'SAM-S24U','Samsung',4.7,1243,TRUE,TRUE,'https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=400'),
(1,'Apple MacBook Air M3','MacBook Air M3 – Silence Meets Speed for Indian Professionals','Ultra-thin laptop with M3 chip and 18hr battery','Extraordinary performance with 18-hour battery and fanless design. Ideal for students, designers, professionals.','Buy Apple MacBook Air M3 India | Best Ultrabook 2024 Lowest Price',114900.00,5,30,'APL-MBA-M3','Apple',4.8,876,FALSE,TRUE,'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=400'),
(2,"Levi's 511 Slim Fit Jeans","Levi's 511 – Slim Fit Comfort for Every Indian Occasion",'Classic slim fit jeans in dark wash denim','Superior denim quality with all-day comfort. Perfect for casual outings, office wear, or festive occasions.','Buy Levis 511 Slim Fit Jeans India | Best Denim for Men Online',3499.00,30,200,"LEV-511","Levi's",4.5,3421,TRUE,FALSE,'https://images.unsplash.com/photo-1542272604-787c3835535d?w=400'),
(3,'Nilkamal Chair Set of 4','Nilkamal Comfort Chair – Durable Choice for Indian Homes','Strong UV-resistant plastic chairs','UV-resistant, stackable, easy-clean chairs perfect for dining rooms and offices across India.','Buy Nilkamal Chair Set of 4 India | Best Home Furniture Online',3299.00,15,500,'NIL-CHR4','Nilkamal',4.2,5621,FALSE,FALSE,'https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=400'),
(5,'Boldfit Pro Gym Gloves','Boldfit Pro Gym Gloves – Maximum Grip Maximum Gains','Anti-slip gym gloves with wrist support','Superior wrist support, anti-slip grip, breathable mesh. Trusted by 50,000+ Indian gym-goers.','Buy Boldfit Gym Gloves India | Best Anti-slip Workout Gloves Online',599.00,20,350,'BLD-GYM','Boldfit',4.4,2341,TRUE,FALSE,'https://images.unsplash.com/photo-1517836357463-d25dfeac3438?w=400'),
(6,'Mamaearth Vitamin C Serum','Mamaearth Vitamin C Serum – Glow Naturally the Indian Way','Natural vitamin C serum with Niacinamide','15% Vitamin C and Niacinamide reduces dark spots in 4 weeks. Dermatologically tested for Indian skin.','Buy Mamaearth Vitamin C Face Serum India | Best Skin Brightening Serum',599.00,25,400,'MME-VTC','Mamaearth',4.6,8765,TRUE,TRUE,'https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=400'),
(1,'boAt Airdopes 141 TWS',"boAt Airdopes 141 – True Wireless at India's Favourite Price",'TWS earbuds with 42Hr battery and BEAST Mode','42 hours total playback, BEAST Mode gaming, IPX4 water resistance. India #1 selling earbuds.','Buy boAt Airdopes 141 TWS Earbuds India | Best Budget Wireless Earphones',1299.00,35,800,'BOA-141','boAt',4.3,15432,TRUE,TRUE,'https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=400'),
(4,'Rich Dad Poor Dad','Rich Dad Poor Dad – The Finance Bible Every Indian Needs','Personal finance bestseller by Robert Kiyosaki','Timeless masterpiece on financial literacy and investing. A must-read for every aspiring Indian entrepreneur.','Buy Rich Dad Poor Dad India | Best Personal Finance Book Lowest Price',299.00,10,1000,'RDP-BOOK','Manjul Publishing',4.7,22341,FALSE,FALSE,'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400'),
(1,'OnePlus Nord CE 3 Lite 5G','OnePlus Nord CE 3 Lite – Smart 5G at India Budget','108MP camera 5G phone with 67W charging','108MP camera, 67W SUPERVOOC, Snapdragon 695. Smartest budget 5G phone for Indians in 2024.','Buy OnePlus Nord CE 3 Lite 5G India | Best Budget 5G Phone Online',19999.00,12,300,'OPL-CE3','OnePlus',4.4,4532,TRUE,FALSE,'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400'),
(2,'Fabindia Handloom Kurta Set','Fabindia Handloom Kurta – Desi Elegance Redefined','Traditional handloom cotton kurta set for women','Crafted by skilled artisans with 100% pure cotton. Perfect for festivals, office and Indian occasions.','Buy Fabindia Handloom Kurta Set India | Best Cotton Ethnic Wear Online',2499.00,20,150,'FBI-KRT','Fabindia',4.6,1876,FALSE,TRUE,'https://images.unsplash.com/photo-1594938298603-c8148c4dae35?w=400'),
(6,'Himalaya Neem Face Wash','Himalaya Neem Face Wash – Pure Ayurvedic Glow for India','Purifying neem and turmeric face wash for oily skin','Natural neem and turmeric extracts deeply cleanse pores. Trusted by millions of Indian families for 30+ years.','Buy Himalaya Neem Face Wash India | Best Ayurvedic Cleanser Online',185.00,5,2000,'HIM-NM','Himalaya',4.5,34521,FALSE,FALSE,'https://images.unsplash.com/photo-1556228578-8c89e6adf883?w=400'),
(7,'Tata Salt Lite Low Sodium','Tata Salt Lite – Healthy Choice for Every Indian Kitchen','Low sodium iodized salt for health-conscious families','15% less sodium than regular salt. Helps manage blood pressure while keeping food delicious.','Buy Tata Salt Lite Low Sodium India | Best Healthy Salt Online Grocery',32.00,0,5000,'TTA-SLT','Tata',4.3,8932,FALSE,FALSE,'https://images.unsplash.com/photo-1518110925495-5fe2fda0442c?w=400');

INSERT INTO users (full_name, email, password_hash, phone, city, state, pincode) VALUES
('Rahul Sharma', 'rahul@example.com', '$2a$10$exPass1', '9876543210', 'Mumbai',    'Maharashtra', '400001'),
('Priya Patel',  'priya@example.com', '$2a$10$exPass2', '9876543211', 'Bengaluru', 'Karnataka',   '560001'),
('Amit Kumar',   'amit@example.com',  '$2a$10$exPass3', '9876543212', 'Delhi',     'Delhi',       '110001'),
('Sneha Reddy',  'sneha@example.com', '$2a$10$exPass4', '9876543213', 'Hyderabad', 'Telangana',   '500001');

INSERT INTO promotions (code,title,ai_generated_content,discount_type,discount_value,min_order_amount,start_date,end_date) VALUES
('AISHIP50',  'AI Flash Sale – 50% OFF',          'Our AI unlocked 50% OFF just for you! Limited time — shop before this personalised offer expires.',                      'PERCENT',50, 499, CURDATE(), DATE_ADD(CURDATE(),INTERVAL 7 DAY)),
('INDIA200',  'India Pride Sale – Flat Rs.200 OFF','Celebrate Incredible India with Rs.200 OFF on orders above Rs.999. AI-curated deals across 1000+ products.',           'FLAT',  200, 999, CURDATE(), DATE_ADD(CURDATE(),INTERVAL 3 DAY)),
('NEWUSER30', 'Welcome – 30% OFF for New Members', 'Welcome to ShopAI India! As a new member enjoy 30% OFF your first order. AI has your best deals ready.',               'PERCENT',30, 199, CURDATE(), DATE_ADD(CURDATE(),INTERVAL 30 DAY));
