# 🛒 ShopAI India

> **AI-Powered E-Commerce Personalization Platform** — Enhanced shopping experience using OpenAI GPT for smart recommendations, chatbot assistance, and intelligent content generation.

---

## 📌 About the Project

**ShopAI India** is a full-stack e-commerce backend built with **Spring Boot 3** and integrated with the **OpenAI GPT-3.5 Turbo API**. It goes beyond a typical online store by using AI to personalize the shopping experience — from product recommendations to promo email generation and user behavior analysis.

This project was built to demonstrate real-world AI integration in a Java Spring Boot application targeting the Indian e-commerce market.

---

## ✨ Features

### 🤖 AI-Powered Features
- **AI Chatbot** — Smart shopping assistant powered by GPT-3.5 Turbo
- **Personalized Recommendations** — AI-generated product suggestions based on purchase history
- **Promo Email Generator** — Automated promotional email content using AI
- **User Behavior Analysis** — AI profiling based on user activity patterns
- **Trending Badge Generator** — Dynamic badges for hot/trending products
- **SEO Content Generation** — AI-written product descriptions and titles

### 🛍️ Core E-Commerce Features
- User Registration & Login with **JWT Authentication**
- Product Management (CRUD)
- Category Management
- Order & Cart Management
- Wishlist
- Promotions & Discounts
- Product Reviews
- Notifications

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2.0 |
| AI Integration | OpenAI GPT-3.5 Turbo API |
| Security | Spring Security + JWT (JJWT 0.11.5) |
| Database | MySQL |
| ORM | Spring Data JPA / Hibernate |
| HTTP Client | Spring WebFlux (WebClient) |
| Build Tool | Maven |
| Other | Lombok, Jackson, Spring Cache |

---

## 📁 Project Structure

```
shopai/backend/
├── src/main/java/com/shopai/
│   ├── config/          # Security & WebClient config
│   ├── controller/      # REST API controllers
│   │   ├── AiController.java
│   │   ├── AuthController.java
│   │   ├── ProductController.java
│   │   ├── OrderController.java
│   │   └── ...
│   ├── model/           # JPA entities
│   ├── repository/      # Spring Data repositories
│   ├── service/         # Business logic & AI services
│   └── dto/             # Request/Response DTOs
└── pom.xml
```

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8+
- OpenAI API Key

### 1. Clone the Repository
```bash
git clone https://github.com/manojkumarr12121-jpg/ShopAI-India.git
cd ShopAI-India/shopai/backend
```

### 2. Configure Database & API Key

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/shopai_db
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password

openai.api.key=your_openai_api_key
```

### 3. Create the Database
```sql
CREATE DATABASE shopai_db;
```

### 4. Run the Application
```bash
mvn spring-boot:run
```

The server starts at `http://localhost:8080`

---

## 📡 API Endpoints

### 🔐 Auth
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login & get JWT token |

### 🤖 AI Features
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/ai/chat` | AI chatbot response |
| POST | `/api/ai/recommend` | Personalized recommendations |
| POST | `/api/ai/promo-email` | Generate promo email content |
| POST | `/api/ai/analyze-behavior` | Analyze user behavior |
| POST | `/api/ai/trending-badge` | Generate trending badge |
| GET | `/api/ai/user-profile/{userId}` | Get AI user profile |
| GET | `/api/ai/health` | AI service health check |

### 🛍️ Products & Orders
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/products` | Get all products |
| POST | `/api/products` | Add new product |
| GET | `/api/orders` | Get orders |
| POST | `/api/orders` | Place new order |

---

## 🔒 Security

- All endpoints (except `/api/auth/**`) are protected with **JWT Bearer Token**
- Passwords are encrypted using **BCrypt**
- CORS enabled for frontend integration

---

## 👨‍💻 Author

**Manoj Kumar R**
- 🎓 B.E. Computer Science — Vinayaka Missions Kirupananda Variyar Engineering College, Salem (2026)
- 💼 Full Stack Java Developer Trainee — Besant Technologies, Bengaluru
- 🐙 GitHub: [@manojkumarr12121-jpg](https://github.com/manojkumarr12121-jpg)

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

---

⭐ **If you found this project helpful, please give it a star!**
