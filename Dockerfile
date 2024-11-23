
# Base image --platform=linux/amd64 引用本地镜像不生效而增加
FROM  --platform=linux/amd64 centos7-java8-chrome110:1.0

# Add application jar
ADD target/goodluck-checkin-service-1.0.0-SNAPSHOT.jar /deployments/app.jar

# Set environment variables
ENV LANG=en_US.UTF-8
ENV TZ=Asia/Shanghai
ENV engine.mode=RUNTIME

# Expose port
EXPOSE 8080

# Define entrypoint
# If you want to use the commented out entrypoint, you can remove the comment
# ENTRYPOINT exec java $APP_OPTS $SKYWALKING_OPTS -cp . -jar /deployments/app.jar
# 启动 Java 应用
CMD ["java", "-jar", "/deployments/app.jar"]
