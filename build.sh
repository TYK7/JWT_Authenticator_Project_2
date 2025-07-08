#!/bin/bash

# Build script for JWT Authenticator
echo "🚀 Building JWT Authenticator for Render deployment..."

# Clean and build the project
echo "📦 Cleaning and building Maven project..."
mvn clean package -DskipTests -B

# Check if build was successful
if [ $? -eq 0 ]; then
    echo "✅ Maven build successful!"
    
    # Build Docker image
    echo "🐳 Building Docker image..."
    docker build -t jwt-authenticator:latest .
    
    if [ $? -eq 0 ]; then
        echo "✅ Docker image built successfully!"
        echo "📋 Image details:"
        docker images jwt-authenticator:latest
        
        echo ""
        echo "🎉 Build completed successfully!"
        echo "📝 Next steps:"
        echo "   1. Test locally: docker run -p 8080:8080 jwt-authenticator:latest"
        echo "   2. Push to your Git repository"
        echo "   3. Deploy to Render using the render.yaml configuration"
    else
        echo "❌ Docker build failed!"
        exit 1
    fi
else
    echo "❌ Maven build failed!"
    exit 1
fi