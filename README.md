
# goodluck-checkin
每日签到程序，可以对某个网站自动登录签到，传统的接口调用方式会被cloudflare拦截，进行人机识别，本程序采用selenium自动化测试工具开发，模拟人为点击进行签到。

## 功能

- 自动登录并签到特定网站
- 支持的登录站点：
  - Bugutv
  - Shaoshuren

## 项目结构

- `goodluck-checkin-api`: API模块，包含接口定义和数据模型
- `goodluck-checkin-service`: 服务模块，包含业务逻辑和服务实现

## 快速开始

### 先决条件

- [Docker](https://docs.docker.com/get-docker/)
- [Maven](https://maven.apache.org/install.html)
- [Java 8](https://adoptopenjdk.net/)

### 克隆项目

```bash
git clone https://github.com/liu-roy/goodluck-checkin.git
cd goodluck-checkin
```

### 构建项目

使用 Maven 进行构建：

```bash
mvn clean package -DskipTests
```

### 构建 Docker 镜像

```bash
docker build --tag liuroy/goodluck-checkin --file ./Dockerfile .
```

### 运行服务

```bash
docker run -d -p 8080:8080 -v /volume1/docker/goodluck-checkin/application.yml:/deployments/application.yml -e JAVA_OPTIONS="-Dspring.config.location=/deployments/application.yml" liuroy/goodluck-checkin
```

### 配置文件

项目使用 `application.yml`  配置文件进行配置。你需要配置application.yml中相应网站的用户名和密码。

## GitHub Actions 工作流

项目包含两个 GitHub Actions 工作流用于 CI/CD 流程：

- `docker-checkin.yml`: 构建并推送 Docker 镜像
- `docker-image.yml`: 构建并推送自定义 Docker 镜像

## 常见问题

### 如何修改登录信息？

在 `application.yml`文件中修改 `bugutv` 和 `shaoshuren` 部分的用户名和密码。

### 如何查看健康检查信息？

服务启动后，可以通过访问 `/actuator/health` 查看健康检查信息。

## 贡献

欢迎提交 issue 和 pull request 来贡献代码。

## 许可证

该项目采用 MIT 许可证，详情请参阅 [LICENSE](LICENSE) 文件。
```

