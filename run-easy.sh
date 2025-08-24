#!/bin/bash

echo "🐳 Chạy ứng dụng Cathay Bank với Docker..."

# Dọn dẹp container cũ
docker stop cathay-app 2>/dev/null || true
docker rm cathay-app 2>/dev/null || true

# Build image
echo "Đang build Docker image..."
docker build -t cathay-app .

# Chạy container
echo "Đang khởi động container..."
docker run -d -p 8080:8080 --name cathay-app cathay-app

echo ""
echo "THÀNH CÔNG! Ứng dụng đang chạy..."
echo "Ứng dụng chính: http://localhost:8080"
echo "Swagger UI: http://localhost:8080/swagger-ui.html"
echo "H2 Console: http://localhost:8080/h2-console"
echo ""
echo "📋 Xem logs: docker logs cathay-app"
echo "🛑 Dừng app: docker stop cathay-app"
