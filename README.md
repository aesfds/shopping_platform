# Shopping Platform

前后端分离的电商平台示例项目。

- 后端：Spring Boot 4、Spring Web MVC、Spring Data JPA、H2/MySQL
- 前端：Vue 3、Vite、Vue Router、Element Plus
- 数据库：默认可使用 H2 文件数据库，也可以切换到 MySQL

## 项目结构

```text
shopping_platform/
├── database/mysql/        # MySQL 建表和初始化数据脚本
├── frontend/              # Vue/Vite 前端项目
├── src/                   # Spring Boot 后端源码
├── .mvn/wrapper/          # Maven Wrapper 配置
├── mvnw                   # Linux/macOS Maven Wrapper 启动脚本
├── mvnw.cmd               # Windows Maven Wrapper 启动脚本
└── pom.xml                # 后端 Maven 配置
```


## 环境要求

- JDK 21
- Node.js 18 或更高版本
- npm
- MySQL 8.x，可选；默认 H2 模式不需要安装 MySQL


## 后端运行：默认 H2 模式

默认配置使用 H2 文件数据库，适合本地快速启动。

Windows：

```bash
.\mvnw.cmd spring-boot:run
```

Linux/macOS：

```bash
./mvnw spring-boot:run
```

后端默认地址：

```text
http://localhost:8080
```


## 前端运行

打开另一个终端：

```bash
cd frontend
npm install
npm run dev
```

前端默认地址：

```text
http://localhost:5173
```

开发环境下，Vite 会把 `/api` 请求代理到：

```text
http://localhost:8080
```

所以本地开发时需要同时启动后端和前端。

## 使用 MySQL 运行

如果要切换到 MySQL，先创建数据库和初始化数据：

```bash
mysql -u root -p < database/mysql/schema.sql
mysql -u root -p < database/mysql/seed.sql
```

然后创建本地配置文件：

```text
src/main/resources/application-mysql.properties
```

示例内容：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/shopping_platform?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=你的数据库用户名
spring.datasource.password=你的数据库密码

spring.jpa.hibernate.ddl-auto=update
spring.sql.init.mode=never
```

启动 MySQL profile：

Windows：

```bash
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=mysql
```

Linux/macOS：

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=mysql
```


