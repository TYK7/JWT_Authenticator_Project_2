# 🐳 Docker Usage Guide - Updated with Your Configuration

## ✅ **Your Configuration Applied!**

I've updated the Docker Compose files with your actual configuration from `application.properties` and `application-postgres.properties`.

---

## 📁 **Docker Files Overview**

### **1. `docker-compose.yml` - Local Development**
- **PostgreSQL Container:** Local database for development
- **Your Settings:** Database name `myprojectdb`, user `postgres`
- **Your Email:** `tyedukondalu@stratapps.com` with app password
- **Your Google OAuth2:** Client ID `333815600502-fcfheqik99ceft5sq5nk4f8ae5aialec`

### **2. `docker-compose.prod.yml` - Production**
- **AWS RDS Database:** Your actual production database
- **Database URL:** `database-1.ctoysco66obu.eu-north-1.rds.amazonaws.com`
- **Same Email & OAuth2:** Your actual working configuration

### **3. `Dockerfile` - Container Build**
- **Java 8 Compatible:** Optimized for your Spring Boot 2.7.18
- **Production Ready:** Multi-stage build, security best practices

---

## 🚀 **How to Use**

### **Option 1: Local Development (with local PostgreSQL)**
```bash
# Build and run with local PostgreSQL container
docker-compose up --build

# Your app will be available at:
# http://localhost:8080

# Database: Local PostgreSQL container
# Email: Your actual Gmail configuration
# Google OAuth2: Your actual client ID
```

### **Option 2: Production (with AWS RDS)**
```bash
# Build and run with your AWS RDS database
docker-compose -f docker-compose.prod.yml up --build

# Your app will connect to:
# Database: Your AWS RDS instance
# Email: Your actual Gmail configuration
# Google OAuth2: Your actual client ID
```

### **Option 3: Render Deployment (Recommended)**
```bash
# Just push to GitHub - Render will use the Dockerfile
git add .
git commit -m "Updated Docker configuration"
git push origin main

# Render will:
# - Build using Dockerfile
# - Use environment variables you set in Render dashboard
# - Connect to your AWS RDS or Render PostgreSQL
```

---

## ⚙️ **Configuration Details**

### **✅ Database Configuration**

#### **Local Development (docker-compose.yml):**
```yaml
SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/myprojectdb
SPRING_DATASOURCE_USERNAME: postgres
SPRING_DATASOURCE_PASSWORD: srikar045
```

#### **Production (docker-compose.prod.yml):**
```yaml
SPRING_DATASOURCE_URL: jdbc:postgresql://database-1.ctoysco66obu.eu-north-1.rds.amazonaws.com:5432/myprojectdb
SPRING_DATASOURCE_USERNAME: postgres
SPRING_DATASOURCE_PASSWORD: srikar045
```

### **✅ Email Configuration (Both files):**
```yaml
SPRING_MAIL_HOST: smtp.gmail.com
SPRING_MAIL_PORT: 587
SPRING_MAIL_USERNAME: tyedukondalu@stratapps.com
SPRING_MAIL_PASSWORD: whesvjdtjmyhgwwt
```

### **✅ Google OAuth2 (Both files):**
```yaml
GOOGLE_CLIENT_ID: 333815600502-fcfheqik99ceft5sq5nk4f8ae5aialec.apps.googleusercontent.com
```

### **✅ JWT Configuration (Both files):**
```yaml
JWT_SECRET: yourSuperSecretKeyThatIsAtLeast256BitsLongAndShouldBeStoredSecurely
JWT_EXPIRATION: 86400000
```

---

## 🧪 **Testing Your Docker Setup**

### **1. Local Development Test:**
```bash
# Start local development
docker-compose up --build

# Test endpoints:
curl http://localhost:8080/actuator/health
curl http://localhost:8080/test/status

# Check logs:
docker-compose logs jwt-app
```

### **2. Production Test:**
```bash
# Start production setup
docker-compose -f docker-compose.prod.yml up --build

# Test with your AWS RDS:
curl http://localhost:8080/actuator/health
curl http://localhost:8080/test/status

# Check database connection:
docker-compose -f docker-compose.prod.yml logs jwt-app
```

---

## 🔧 **Customization Options**

### **Change URLs for Different Environments:**

#### **For Staging:**
```yaml
APP_BASE_URL: https://staging-api.yourdomain.com
APP_FRONTEND_URL: https://staging.yourdomain.com
```

#### **For Production:**
```yaml
APP_BASE_URL: https://api.yourdomain.com
APP_FRONTEND_URL: https://yourdomain.com
```

### **Memory Optimization:**
```yaml
# For low-memory environments:
JAVA_OPTS: -Xmx256m -Xms128m

# For high-performance:
JAVA_OPTS: -Xmx1g -Xms512m -XX:+UseG1GC
```

---

## 🚨 **Important Notes**

### **1. Security Considerations:**
- ⚠️ **Passwords in docker-compose.yml** are visible in plain text
- ✅ **For production:** Use environment variables or Docker secrets
- ✅ **For Render:** Use environment variables in dashboard

### **2. Database Considerations:**
- **Local Development:** Uses PostgreSQL container (data persists in Docker volume)
- **Production:** Uses your AWS RDS (existing data preserved)
- **Render Deployment:** Can use Render PostgreSQL or your AWS RDS

### **3. Network Considerations:**
- **Local:** App accessible at `http://localhost:8080`
- **Production:** Update `APP_BASE_URL` to your actual domain
- **Render:** Automatically gets HTTPS domain

---

## 🔄 **Environment-Specific Commands**

### **Development:**
```bash
# Start development environment
docker-compose up -d

# View logs
docker-compose logs -f jwt-app

# Stop and remove
docker-compose down
```

### **Production:**
```bash
# Start production environment
docker-compose -f docker-compose.prod.yml up -d

# View logs
docker-compose -f docker-compose.prod.yml logs -f jwt-app

# Stop and remove
docker-compose -f docker-compose.prod.yml down
```

### **Build Only:**
```bash
# Build Docker image without running
docker build -t jwt-authenticator .

# Run built image manually
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://your-db:5432/myprojectdb \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=srikar045 \
  jwt-authenticator
```

---

## 📊 **Monitoring & Debugging**

### **Health Checks:**
```bash
# Check application health
curl http://localhost:8080/actuator/health

# Check detailed status
curl http://localhost:8080/test/status
```

### **Database Connection Test:**
```bash
# Check if database is accessible
docker-compose exec postgres psql -U postgres -d myprojectdb -c "SELECT version();"
```

### **Container Logs:**
```bash
# Application logs
docker-compose logs jwt-app

# Database logs
docker-compose logs postgres

# Follow logs in real-time
docker-compose logs -f jwt-app
```

---

## 🎯 **Recommended Workflow**

### **1. Local Development:**
```bash
# Use local PostgreSQL container
docker-compose up --build
# Test your changes locally
```

### **2. Production Testing:**
```bash
# Test with your AWS RDS
docker-compose -f docker-compose.prod.yml up --build
# Verify production database connectivity
```

### **3. Render Deployment:**
```bash
# Push to GitHub
git add .
git commit -m "Ready for deployment"
git push origin main
# Deploy using Render's Docker support
```

---

## ✅ **Your Configuration Summary**

- **✅ Database:** AWS RDS PostgreSQL (`myprojectdb`)
- **✅ Email:** Gmail SMTP (`tyedukondalu@stratapps.com`)
- **✅ OAuth2:** Google Client ID configured
- **✅ JWT:** Secure secret key configured
- **✅ Docker:** Multi-stage build optimized
- **✅ Environments:** Local development + Production ready

**🎉 Your Docker setup is now configured with your actual working settings!**