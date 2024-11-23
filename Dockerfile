
FROM  liuroy/centos7-java8-chrome110

# Add application jar
ADD target/goodluck-checkin-service-1.0.0-SNAPSHOT.jar /deployments/app.jar

# 添加启动脚本


# Set environment variables
ENV LANG=en_US.UTF-8
ENV TZ=Asia/Shanghai
ENV engine.mode=RUNTIME



# Expose port
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/deployments/app.jar","--spring.config.location=/tmp/application.yml"]
