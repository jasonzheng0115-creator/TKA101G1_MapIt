# ==========================================
# 階段一：建構階段 (Build Stage)
# ==========================================
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# 僅複製 pom.xml 並下載依賴，利用 Docker 快取機制避免每次重新下載依賴
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 複製原始碼並進行編譯打包 (跳過測試以加速封裝)
COPY src ./src
RUN mvn clean package -DskipTests

# ==========================================
# 階段二：運行階段 (Runtime Stage)
# ==========================================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 從建構階段複製打包好的 jar 檔案
# 根據 pom.xml 定義，產出的 jar 應該為 TKA101G1-0.0.1-SNAPSHOT.jar
COPY --from=builder /app/target/TKA101G1-0.0.1-SNAPSHOT.jar app.jar

# 暴露應用程式的埠號 (Spring Boot 預設 8080)
EXPOSE 8080

# 設定預設環境變數（可在運行容器時透過 -e 參數覆寫）
ENV SPRING_PROFILES_ACTIVE=prod

# 啟動應用程式
ENTRYPOINT ["java", "-jar", "app.jar"]
