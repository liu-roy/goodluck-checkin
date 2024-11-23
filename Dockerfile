
# Base image --platform=linux/amd64 引用本地镜像不生效而增加
FROM  --platform=linux/amd64 centos7-java8-chrome110:3.0

# Add application jar
ADD target/goodluck-checkin-service-1.0.0-SNAPSHOT.jar /deployments/app.jar

# 添加启动脚本

# Set environment variables
ENV LANG=en_US.UTF-8
ENV TZ=Asia/Shanghai
ENV engine.mode=RUNTIME



# Expose port
EXPOSE 8080

CMD ["java", "-jar", "/deployments/app.jar"]
