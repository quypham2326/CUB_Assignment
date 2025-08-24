# Cathay United Bank - Currency & Exchange Rate Management System

A comprehensive Spring Boot application for managing currencies and exchange rates with internationalization support (English & Vietnamese).

---

## 📥 **Getting Started - Clone & Setup**

### **Clone the Repository**

```bash
# Clone the project repository
git clone https://github.com/quypham2326/CUB_Assignment.git

# Navigate to project directory
cd CUB_Assignment

# Checkout develop branch for latest development code
git checkout develop

# Verify you're on the develop branch
git branch -v
```

### **Alternative Clone Methods**

```bash
# Method 1: Standard HTTPS (Recommended) ✅
git clone https://github.com/quypham2326/CUB_Assignment.git
cd CUB_Assignment
git checkout develop

# Method 2: SSH (if you have SSH keys configured)
git clone git@github.com:quypham2326/CUB_Assignment.git
cd CUB_Assignment  
git checkout develop

# Method 3: Download ZIP (manual)
# Visit: https://github.com/quypham2326/CUB_Assignment/archive/refs/heads/develop.zip
```

### **Branch Information**

| Branch | Purpose | Status |
|--------|---------|---------|
| `main` | Production-ready stable code | ✅ Stable |
| `develop` | Latest development features | 🚧 Development |

**⚠️ Important**: Use the `develop` branch to review the latest code and features.

### **Repository Status Check**

**🔍 Repository Confirmed: ✅ PUBLIC & ACCESSIBLE**

```bash
# Quick public access test
curl -s https://api.github.com/repos/quypham2326/CUB_Assignment | grep -o '"private":[^,]*'
# Output: "private": false ✅

# Or use our automated checker:
chmod +x check-repo-access.sh && ./check-repo-access.sh
```

### **Quick Verification**

```bash
# Check repository structure
ls -la

# View commit history (should show implementation commits)
git log --oneline -5

# Expected output:
# 81ee2c5 implement
# 708a613 init spring project  
# c725006 Initial commit

# Check current branch and available branches
git branch -a

# Verify you're on develop branch
git status
```

### **Repository Access Notes**

| Scenario | Status | Solution |
|----------|--------|----------|
| **Public Repository** | ✅ **CONFIRMED** | Use standard HTTPS clone (no authentication needed) |
| **Clone Access** | ✅ **VERIFIED** | Repository is fully accessible to everyone |
| **Branch Access** | ✅ **AVAILABLE** | Both `main` and `develop` branches are public |

**📋 Repository Information:**
- **URL**: https://github.com/quypham2326/CUB_Assignment.git
- **Visibility**: 🌐 **PUBLIC** (confirmed via API)
- **Main Branch**: `main` (stable release)
- **Development Branch**: `develop` (latest features) ⭐
- **Latest Commit**: `81ee2c5 implement`
- **Available Branches**: `main`, `develop`

---

## ✅ **Project Features Checklist**

| Feature | Status | Implementation Details |
|---------|--------|------------------------|
| **1. Request/Response Logging** | ✅ **INCLUDED** | Custom Feign Logger + Application DEBUG logging |
| **2. Swagger UI** | ✅ **INCLUDED** | Available at `/swagger-ui.html` |
| **3. i18n Design** | ✅ **INCLUDED** | English & Vietnamese language support |
| **4. Docker Support** | ✅ **INCLUDED** | Dockerfile + Docker Compose + Easy run script |
| **5. Error Handling** | ✅ **INCLUDED** | Global Exception Handler with decorated responses |

---

## 📋 **Feature Details**

### 🔍 **1. Request/Response Logging**
- **External API Logs**: Custom Feign Logger captures full request/response for external exchange rate API
- **Application Logs**: DEBUG level logging for internal operations
- **SQL Logs**: Database query logging with parameter binding
- **Configuration**: 
  ```yaml
  logging:
    level:
      cathay.united.bank: DEBUG
      feign: DEBUG
      org.hibernate.SQL: INFO
  feign:
    client:
      config:
        default:
          loggerLevel: BASIC
  ```

### 📚 **2. Swagger UI Documentation**
- **URL**: `http://localhost:8080/swagger-ui.html`
- **API Documentation**: Complete interactive API documentation
- **Try It Out**: Test APIs directly from browser
- **Schema Validation**: Request/response models with validation rules

### 🌍 **3. Internationalization (i18n)**
- **Languages**: English (`en`) & Vietnamese (`vi`)
- **Error Messages**: Fully localized error responses
- **Auto-Detection**: Locale detection from Accept-Language header
- **Message Files**:
  - `messages_en.properties` - English messages
  - `messages_vi.properties` - Vietnamese messages

### 🐳 **4. Docker Support**
- **Dockerfile**: Multi-stage build with Maven & OpenJDK 17
- **Docker Compose**: One-command deployment
- **Easy Script**: `run-easy.sh` for quick startup
- **Port Mapping**: Application runs on port 8080

### ⚠️ **5. Comprehensive Error Handling**
- **Global Exception Handler**: Centralized error processing
- **Decorated Responses**: Consistent error response format
- **Validation Errors**: Field-level validation with i18n messages
- **HTTP Status Codes**: Proper status code mapping
- **Business Logic Errors**: Custom exception handling

---

## 🚀 **Quick Start**

### **Option 1: Easy Script (Recommended)**
```bash
chmod +x run-easy.sh && ./run-easy.sh
```

### **Option 2: Docker Compose**
```bash
docker-compose up --build
```

### **Option 3: Manual Docker**
```bash
# Build image
docker build -t cathay-app .

# Run container
docker run -d -p 8080:8080 --name cathay-app cathay-app
```

---

## 📱 **Application URLs**

| Service | URL | Description |
|---------|-----|-------------|
| **Main Application** | http://localhost:8080 | API endpoints |
| **Swagger UI** | http://localhost:8080/swagger-ui.html | Interactive API docs |
| **H2 Database Console** | http://localhost:8080/h2-console | Database management |
| **Health Check** | http://localhost:8080/actuator/health | Application health |
| **API Documentation** | http://localhost:8080/v3/api-docs | OpenAPI 3.0 JSON |

---

## 🗄️ **Database Configuration**

### **H2 Database Console**
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: *(empty)*
- **Driver**: `org.h2.Driver`

### **Initial Data**
- Pre-loaded currencies: EUR, GBP, VND
- Sample exchange rate data from external API
- Automatic schema and data initialization

---

## 🔧 **API Endpoints**

### **Currency Management**
```bash
GET    /currency/list              # Get all currencies
GET    /currency/{id}              # Get currency by ID
GET    /currency?code={code}       # Get currency by code
POST   /currency                   # Create new currency
PUT    /currency/{id}              # Update currency
DELETE /currency/{id}              # Delete currency
```

### **Exchange Rates**
```bash
GET    /exchange-rate                    # Get paginated exchange rates
GET    /exchange-rate/currency/{code}    # Get rates by base currency
```

### **System APIs**
```bash
GET    /actuator/health                  # Health check
GET    /swagger-ui.html                  # API documentation
GET    /h2-console                       # Database console
```

---

## 🧪 **Testing & Verification**

### **Repository Access Verification**
```bash
# Check repository accessibility and get clone instructions
./check-repo-access.sh
```

### **Automated API Testing Scripts**
```bash
# Comprehensive API testing
./test-all-apis.sh

# Python-based testing
python3 python-api-test.py

# Node.js testing
node javascript-api-test.js
```

### **Manual Testing**
```bash
# Health check
curl -X GET http://localhost:8080/actuator/health

# Get currencies
curl -X GET http://localhost:8080/currency/list

# Create currency
curl -X POST http://localhost:8080/currency \
  -H "Content-Type: application/json" \
  -d '{"currency_code":"USD","currency_name":"US Dollar","country":"USA","symbol":"$"}'
```

---

## 🔍 **Logging Examples**

### **External API Request Log**
```
➡️ Feign Request: method=GET, url=https://fxds-public-exchange-rates-api.oanda.com/cc-api/currencies
```

### **External API Response Log**
```
⬅️ Feign Response: status=200, duration=245ms, body={"currencies":[...]}
```

### **Application Debug Log**
```
13:45:23.123 [http-nio-8080-exec-1] DEBUG c.u.b.service.CurrencyServiceImpl - Finding currency by code: USD
```

---

## 🌐 **Internationalization Examples**

### **English Error Response**
```json
{
  "code": 400,
  "message": "Currency not found with code: XYZ",
  "data": []
}
```

### **Vietnamese Error Response** (with Accept-Language: vi)
```json
{
  "code": 400,
  "message": "Không tìm thấy loại tiền tệ với mã: XYZ", 
  "data": []
}
```

---

## 📋 **Docker Management**

### **Useful Commands**
```bash
# View application logs
docker logs cathay-app

# Follow logs in real-time
docker logs -f cathay-app

# Check container status
docker ps

# Stop and remove
docker stop cathay-app && docker rm cathay-app

# With Docker Compose
docker-compose down
docker-compose logs -f
```

### **Environment Configuration**
- **Java**: OpenJDK 17
- **Maven**: 3.8.4
- **Spring Boot**: 3.x
- **Database**: H2 in-memory
- **Port**: 8080

---

## ⚙️ **Configuration Files**

| File | Purpose |
|------|---------|
| `Dockerfile` | Container build configuration |
| `docker-compose.yml` | Multi-container deployment |
| `run-easy.sh` | Quick start script |
| `application.yml` | Spring Boot configuration |
| `messages_*.properties` | i18n message files |

---

## 🛠️ **Development Setup**

### **Prerequisites**
- Docker & Docker Compose
- Java 17+ (for local development)
- Maven 3.6+ (for local development)

### **Local Development**
```bash
# Without Docker
mvn spring-boot:run

# With Docker (recommended)
./run-easy.sh
```

---

## 📊 **Error Response Format**

All API responses follow a consistent format:

### **Success Response**
```json
{
  "code": 200,
  "message": "OK",
  "data": {...}
}
```

### **Error Response**
```json
{
  "code": 400,
  "message": "Validation Error",
  "data": {
    "currency_code": "Currency code is required"
  }
}
```

### **Validation Error Example**
```json
{
  "code": 400,
  "message": "Validation Error", 
  "data": {
    "currency_code": "Currency code must be 3 uppercase letters",
    "currency_name": "Currency name is required"
  }
}
```

---

## 🎯 **Business Logic Features**

- ✅ **Currency validation**: 3-letter ISO codes only
- ✅ **Duplicate prevention**: Cannot create duplicate currency codes
- ✅ **Referential integrity**: Cannot delete currencies with active rates
- ✅ **Data consistency**: Automatic exchange rate updates via scheduled job
- ✅ **Pagination support**: Efficient data retrieval for large datasets

---

## 🚨 **Troubleshooting**

### **Port 8080 in use**
```bash
# Find and kill process
lsof -ti:8080 | xargs kill -9

# Or use different port
docker run -d -p 8081:8080 --name cathay-app cathay-app
```

### **Container not starting**
```bash
# Check logs
docker logs cathay-app

# Rebuild image
docker-compose up --build
```

### **Database issues**
```bash
# Reset database (restart container)
docker stop cathay-app && docker rm cathay-app
./run-easy.sh
```

---

## 🎉 **Success Indicators**

✅ **Application Started**: Swagger UI accessible  
✅ **Database Initialized**: H2 console shows tables with data  
✅ **APIs Working**: Currency endpoints return 200 status  
✅ **Logging Active**: Console shows request/response logs  
✅ **i18n Working**: Error messages in multiple languages  

---

**🚀 Ready to go! Open http://localhost:8080/swagger-ui.html and start testing!**
