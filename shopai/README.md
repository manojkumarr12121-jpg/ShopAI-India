# ENHANCED E-COMMERCE PERSONALIZATION USING AI CONTENT GENERATION
## ShopAI India — Full-Stack Project (Java Spring Boot + MySQL + HTML/CSS/JS)

---

## 📋 PROJECT OVERVIEW
ShopAI India is a full-stack Indian e-commerce platform using **AI** to personalise the shopping experience.
It integrates **OpenAI GPT API** for content generation and **Machine Learning** for product recommendations.

---

## 🗂️ PROJECT STRUCTURE
```
shopai_india/
├── backend/                          ← Java Spring Boot Backend
│   ├── pom.xml                       ← Maven dependencies
│   └── src/main/
│       ├── java/com/shopai/
│       │   ├── ShopAiApplication.java
│       │   ├── config/
│       │   │   ├── SecurityConfig.java
│       │   │   └── WebClientConfig.java
│       │   ├── controller/
│       │   │   ├── ProductController.java   ← Product REST APIs
│       │   │   ├── AiController.java        ← AI feature APIs
│       │   │   ├── AuthController.java      ← Login/Register
│       │   │   ├── CategoryController.java
│       │   │   └── PromotionController.java
│       │   ├── service/
│       │   │   ├── AiContentService.java    ← OpenAI GPT integration
│       │   │   ├── ProductService.java
│       │   │   ├── AuthService.java
│       │   │   ├── CategoryService.java
│       │   │   ├── PromotionService.java
│       │   │   ├── UserBehaviorService.java
│       │   │   └── UserService.java
│       │   ├── model/
│       │   │   ├── Product.java, User.java, Category.java
│       │   │   ├── Order.java, OrderItem.java, Cart.java
│       │   │   ├── Review.java, UserBehavior.java
│       │   │   ├── Promotion.java, Notification.java
│       │   ├── repository/
│       │   │   ├── ProductRepository.java   ← JPA queries
│       │   │   ├── UserRepository.java
│       │   │   └── (6 more repositories)
│       │   ├── dto/
│       │   │   ├── ApiResponse.java, AuthRequest.java, AuthResponse.java
│       │   │   └── ChatRequest.java, RegisterRequest.java
│       │   ├── security/
│       │   │   └── JwtUtil.java
│       │   └── exception/
│       │       └── GlobalExceptionHandler.java
│       └── resources/
│           └── application.properties
│
├── frontend/                         ← HTML/CSS/JavaScript Frontend
│   ├── index.html                    ← Homepage (Alibaba-style)
│   ├── products.html                 ← Product listing + filters
│   ├── cart.html                     ← Shopping cart
│   ├── login.html                    ← Login / Register
│   ├── deals.html                    ← Today's deals
│   ├── wishlist.html                 ← Saved products
│   ├── ai-features.html              ← AI features showcase
│   ├── css/
│   │   ├── style.css                 ← Main styles
│   │   └── pages.css                 ← Inner page styles
│   └── js/
│       ├── data.js                   ← Product/category data
│       ├── app.js                    ← Core app logic
│       └── chatbot.js                ← AI chatbot
│
└── database/
    └── schema.sql                    ← MySQL schema + seed data
```

---

## 🛠️ TECHNOLOGY STACK
| Component    | Technology               |
|-------------|--------------------------|
| Language     | Java 17                  |
| Framework    | Spring Boot 3.2          |
| Database     | MySQL 8.0                |
| Frontend     | HTML5, CSS3, JavaScript  |
| AI API       | OpenAI GPT-3.5 Turbo     |
| Auth         | Spring Security + JWT    |
| Build Tool   | Maven                    |
| IDE          | IntelliJ IDEA / Eclipse  |
| Testing      | Postman                  |
| VCS          | Git & GitHub             |

---

## 🚀 SETUP & RUN

### Step 1 – MySQL Database
```sql
mysql -u root -p < database/schema.sql
```

### Step 2 – Configure Backend
Edit `backend/src/main/resources/application.properties`:
```properties
spring.datasource.password=YOUR_MYSQL_PASSWORD
ai.api.key=sk-YOUR_OPENAI_API_KEY
```

### Step 3 – Run Spring Boot
```bash
cd backend
mvn clean install
mvn spring-boot:run
# Server starts at http://localhost:8080
```

### Step 4 – Open Frontend
Open `frontend/index.html` in your browser (works standalone too).

---

## 🔌 REST API ENDPOINTS

### Products
| Method | URL                              | Description              |
|--------|----------------------------------|--------------------------|
| GET    | /api/products/trending           | Trending products        |
| GET    | /api/products/featured           | AI-featured products     |
| GET    | /api/products/deals              | Discounted products      |
| GET    | /api/products/search?keyword=    | AI-enhanced search       |
| GET    | /api/products/{id}               | Single product           |
| GET    | /api/products/{id}/similar       | Similar products         |
| POST   | /api/products                    | Create with AI content   |
| POST   | /api/products/ai/generate-description | GPT description    |
| POST   | /api/products/ai/generate-title  | GPT SEO title            |
| POST   | /api/products/ai/compare         | AI comparison            |

### AI Features
| Method | URL                      | Description                 |
|--------|--------------------------|-----------------------------|
| POST   | /api/ai/chat             | AI chatbot (NLP)            |
| POST   | /api/ai/recommend        | Personalised recommendation |
| POST   | /api/ai/promo-email      | Marketing email content     |
| POST   | /api/ai/analyze-behavior | User profile analysis       |
| GET    | /api/ai/health           | AI service status           |

### Auth
| Method | URL                 | Description     |
|--------|---------------------|-----------------|
| POST   | /api/auth/login     | Login → JWT     |
| POST   | /api/auth/register  | Create account  |

---

## 🤖 AI FEATURES (Proposed System)
All 25 proposed system points are implemented:

1. ✅ User behaviour analysis (search, purchase, browsing)
2. ✅ AI-generated personalised product descriptions
3. ✅ Dynamic ML-based product recommendations
4. ✅ AI marketing content (emails, promos, ads)
5. ✅ AI product titles and descriptions
6. ✅ Real-time personalised product suggestions
7. ✅ NLP-powered content generation
8. ✅ Customer interest prediction via data analysis
9. ✅ Personalised notifications and offers
10. ✅ AI chatbot customer support
11. ✅ Automated product summaries
12. ✅ Reduces admin manual content work
13. ✅ SEO-optimised product descriptions
14. ✅ Trending product identification
15. ✅ Smart product comparisons
16. ✅ UI personalisation (AI-preferred products first)
17. ✅ Targeted promotions and discounts
18. ✅ Similar & related product discovery
19. ✅ Fast AI content generation
20. ✅ Continuous learning from user interactions

---

## 🗄️ DATABASE TABLES (13 Tables)
`users` · `categories` · `products` · `orders` · `order_items`
`cart` · `wishlist` · `reviews` · `user_behavior`
`ai_user_profiles` · `ai_content_log` · `promotions`
`notifications` · `chatbot_conversations`

---

## 🧪 POSTMAN TEST EXAMPLES
```json
// AI Chat
POST http://localhost:8080/api/ai/chat
{"message":"Show trending mobiles","context":"Indian ecommerce"}

// Generate AI Description
POST http://localhost:8080/api/products/ai/generate-description
{"name":"boAt Airdopes","category":"Electronics","brand":"boAt","price":"1299"}

// Register
POST http://localhost:8080/api/auth/register
{"fullName":"Rahul Sharma","email":"rahul@test.com","password":"Pass@1234","phone":"9876543210","city":"Mumbai"}
```

---

*Built for India 🇮🇳 | Java Spring Boot + MySQL + AI Content Generation*
