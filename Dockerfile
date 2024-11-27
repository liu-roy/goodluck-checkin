
FROM  liuroy/centos7-java8-chrome110

# Add application jar
ADD target/goodluck-checkin-1.0.0-SNAPSHOT.jar /deployments/app.jar
COPY run /opt/run-java
# 添加启动脚本
# Set environment variables
ENV LANG=en_US.UTF-8
ENV TZ=Asia/Shanghai
ENV engine.mode=RUNTIME

RUN chmod +x /opt/run-java/run-java.sh
RUN chmod +x /opt/run-java/run-env.sh


# Expose port
EXPOSE 8080

ENTRYPOINT [ "/opt/run-java/run-java.sh" ]
