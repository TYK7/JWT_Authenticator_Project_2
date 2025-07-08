# 🚀 Render Deployment Guide for JWT Authenticator

## 📋 **Prerequisites**

✅ **Render Account** - Sign up at [render.com](https://render.com)  
✅ **GitHub Repository** - Push your code to GitHub  
✅ **PostgreSQL Database** - Will be created on Render  
✅ **Email SMTP Settings** - Gmail or other email provider  
✅ **Google OAuth2 Credentials** - From Google Cloud Console  

---

## 🐳 **Docker Configuration**

### **1. Dockerfile** (✅ Already Created)
```dockerfile
# Multi-stage build for Java 8
FROM openjdk:8-jdk-alpine AS builder
# ... (optimized for Render's build environment)
```

### **2. .dockerignore** (✅ Already Created)
```
target/
.idea/
*.log
# ... (excludes unnecessary files)
```

---

## ⚙️ **Environment Configuration**

### **1. Application Properties for Render**
File: `application-render.properties` (✅ Already Created)
```properties
# Uses environment variables from Render
app.base-url=${APP_BASE_URL:https://your-app-name.onrender.com}
app.frontend-url=${APP_FRONTEND_URL:https://your-frontend-name.onrender.com}
```

### **2. Render Blueprint**
File: `render.yaml` (✅ Already Created)
```yaml
services:
  - type: web
    name: jwt-authenticator
    env: docker
    # ... (complete Render configuration)
```

---

## 🚀 **Step-by-Step Deployment**

### **Step 1: Prepare Your Code**

1. **Build and Test Locally**
   ```bash
   # Make build script executable (Linux/Mac)
   chmod +x build.sh
   ./build.sh
   
   # Or run manually
   ./mvnw clean package -DskipTests
   docker build -t jwt-authenticator .
   docker run -p 8080:8080 jwt-authenticator
   ```

2. **Test Docker Image**
   ```bash
   # Test the Docker container
   docker run -p 8080:8080 \
     -e APP_BASE_URL=http://localhost:8080 \
     -e APP_FRONTEND_URL=http://localhost:3000 \
     jwt-authenticator
   ```

3. **Push to GitHub**
   ```bash
   git add .
   git commit -m "Add Render deployment configuration"
   git push origin main
   ```

### **Step 2: Create PostgreSQL Database on Render**

1. **Go to Render Dashboard** → **New** → **PostgreSQL**
2. **Configure Database:**
   - **Name:** `jwt-postgres`
   - **Database:** `jwt_auth_db`
   - **User:** `jwt_user`
   - **Region:** Choose closest to your users
   - **Plan:** Free (or paid for production)

3. **Note the Connection Details:**
   - **Internal Database URL:** `postgresql://jwt_user:password@jwt-postgres:5432/jwt_auth_db`
   - **External Database URL:** Will be provided by Render

### **Step 3: Create Web Service on Render**

1. **Go to Render Dashboard** → **New** → **Web Service**
2. **Connect Repository:** Select your GitHub repository
3. **Configure Service:**
   - **Name:** `jwt-authenticator`
   - **Environment:** `Docker`
   - **Region:** Same as your database
   - **Branch:** `main`
   - **Dockerfile Path:** `./Dockerfile`

### **Step 4: Configure Environment Variables**

Add these environment variables in Render:

#### **🔗 Application URLs**
```
APP_BASE_URL=https://jwt-authenticator.onrender.com
APP_FRONTEND_URL=https://your-frontend.onrender.com
```

#### **🗄️ Database Configuration**
```
DATABASE_URL=postgresql://jwt_user:password@jwt-postgres:5432/jwt_auth_db
SPRING_PROFILES_ACTIVE=render
```

#### **🔐 JWT Configuration**
```
JWT_SECRET=your-super-secret-jwt-key-minimum-32-characters
JWT_EXPIRATION=86400000
```

#### **📧 Email Configuration**
```
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=your-email@gmail.com
SPRING_MAIL_PASSWORD=your-app-password
```

#### **🔑 Google OAuth2**
```
GOOGLE_CLIENT_ID=your-google-client-id.apps.googleusercontent.com
```

#### **⚡ Performance Settings**
```
JAVA_OPTS=-Xmx450m -Xms200m -XX:+UseG1GC -XX:MaxGCPauseMillis=200
```

### **Step 5: Deploy**

1. **Click "Create Web Service"**
2. **Monitor Build Logs** - Check for any errors
3. **Wait for Deployment** - Usually takes 5-10 minutes
4. **Test Your Application** - Visit your Render URL

---

## 🧪 **Testing Your Deployment**

### **1. Health Check**
```bash
curl https://your-app-name.onrender.com/actuator/health
```
**Expected Response:**
```json
{"status":"UP"}
```

### **2. API Status**
```bash
curl https://your-app-name.onrender.com/test/status
```
**Expected Response:**
```json
{
  "service": "JWT Authenticator",
  "baseUrl": "https://your-app-name.onrender.com",
  "environment": "production"
}
```

### **3. Google Sign-In Demo**
Visit: `https://your-app-name.onrender.com/test/google-signin-demo`

### **4. Swagger Documentation**
Visit: `https://your-app-name.onrender.com/swagger-ui.html`

---

## 🔧 **Troubleshooting**

### **Common Issues & Solutions**

#### **❌ Build Fails**
```bash
# Check build logs in Render dashboard
# Common fixes:
1. Ensure Java 8 compatibility
2. Check Maven dependencies
3. Verify Dockerfile syntax
```

#### **❌ Database Connection Error**
```bash
# Check environment variables:
1. DATABASE_URL is correctly set
2. Database service is running
3. Network connectivity between services
```

#### **❌ Application Won't Start**
```bash
# Check application logs:
1. Memory limits (adjust JAVA_OPTS)
2. Port configuration (should be 8080)
3. Environment variables are set
```

#### **❌ Email Not Working**
```bash
# Verify email configuration:
1. SMTP settings are correct
2. App password (not regular password) for Gmail
3. Less secure app access enabled (if using Gmail)
```

#### **❌ Google OAuth2 Issues**
```bash
# Update Google Cloud Console:
1. Add Render URL to authorized origins
2. Add callback URLs
3. Verify client ID is correct
```

---

## 🔄 **Updating Your Deployment**

### **Method 1: Auto-Deploy (Recommended)**
```bash
# Just push to GitHub
git add .
git commit -m "Update application"
git push origin main
# Render will automatically rebuild and deploy
```

### **Method 2: Manual Deploy**
1. Go to Render Dashboard
2. Select your service
3. Click "Manual Deploy" → "Deploy latest commit"

---

## 📊 **Monitoring & Logs**

### **1. Application Logs**
- **Render Dashboard** → **Your Service** → **Logs**
- Real-time log streaming
- Filter by log level

### **2. Health Monitoring**
- **Built-in Health Checks** - `/actuator/health`
- **Custom Monitoring** - Add your monitoring service
- **Uptime Monitoring** - Use external services like UptimeRobot

### **3. Performance Metrics**
- **Render Metrics** - CPU, Memory, Network usage
- **Application Metrics** - Custom metrics via Actuator
- **Database Metrics** - PostgreSQL performance

---

## 💰 **Cost Optimization**

### **Free Tier Limits**
- **Web Service:** 750 hours/month
- **PostgreSQL:** 1GB storage, 1 month retention
- **Bandwidth:** 100GB/month

### **Optimization Tips**
1. **Use efficient Docker image** (Alpine-based)
2. **Optimize JVM settings** for memory usage
3. **Enable compression** for responses
4. **Use CDN** for static assets
5. **Database connection pooling**

---

## 🔒 **Security Best Practices**

### **1. Environment Variables**
- ✅ Never commit secrets to Git
- ✅ Use Render's environment variable encryption
- ✅ Rotate secrets regularly

### **2. Database Security**
- ✅ Use strong passwords
- ✅ Enable SSL connections
- ✅ Regular backups

### **3. Application Security**
- ✅ HTTPS only (enforced by Render)
- ✅ Secure JWT secrets
- ✅ Input validation
- ✅ Rate limiting

---

## 🎯 **Production Checklist**

### **Before Going Live**
- [ ] Update all placeholder URLs
- [ ] Configure real email SMTP
- [ ] Set up Google OAuth2 properly
- [ ] Test all API endpoints
- [ ] Verify database migrations
- [ ] Set up monitoring
- [ ] Configure custom domain (optional)
- [ ] SSL certificate (automatic with Render)

### **After Deployment**
- [ ] Test user registration flow
- [ ] Test email verification
- [ ] Test password reset
- [ ] Test Google Sign-In
- [ ] Monitor application logs
- [ ] Set up alerts for errors
- [ ] Document API endpoints
- [ ] Create user documentation

---

## 🆘 **Support & Resources**

### **Render Documentation**
- [Render Docs](https://render.com/docs)
- [Docker on Render](https://render.com/docs/docker)
- [Environment Variables](https://render.com/docs/environment-variables)

### **Spring Boot on Render**
- [Spring Boot Deployment Guide](https://render.com/docs/deploy-spring-boot)
- [Database Configuration](https://render.com/docs/databases)

### **Troubleshooting**
- **Render Community:** [community.render.com](https://community.render.com)
- **Support:** [render.com/support](https://render.com/support)

---

## 🎉 **Congratulations!**

Your JWT Authenticator is now deployed on Render with:
- ✅ **Centralized URL Configuration**
- ✅ **Docker Containerization**
- ✅ **PostgreSQL Database**
- ✅ **Auto-deployment from Git**
- ✅ **Production-ready Configuration**
- ✅ **Health Monitoring**
- ✅ **SSL/HTTPS Security**

**🚀 Your application is ready for production use!**