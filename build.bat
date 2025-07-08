@echo off
echo 🚀 Building JWT Authenticator for Render deployment...

REM Clean and build the project
echo 📦 Cleaning and building Maven project...
call mvn clean package -DskipTests -B

REM Check if build was successful
if %ERRORLEVEL% EQU 0 (
    echo ✅ Maven build successful!
    
    REM Build Docker image
    echo 🐳 Building Docker image...
    docker build -t jwt-authenticator:latest .
    
    if %ERRORLEVEL% EQU 0 (
        echo ✅ Docker image built successfully!
        echo 📋 Image details:
        docker images jwt-authenticator:latest
        
        echo.
        echo 🎉 Build completed successfully!
        echo 📝 Next steps:
        echo    1. Test locally: docker run -p 8080:8080 jwt-authenticator:latest
        echo    2. Push to your Git repository
        echo    3. Deploy to Render using the render.yaml configuration
    ) else (
        echo ❌ Docker build failed!
        exit /b 1
    )
) else (
    echo ❌ Maven build failed!
    exit /b 1
)