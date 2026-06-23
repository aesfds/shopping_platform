# Shopping Platform

前后端分离的电商首页骨架：

- 后端：Spring Boot 4、Spring Web MVC、Spring Data JPA、H2/MySQL
- 前端：Vue 3、Vite、Vue Router、Element Plus
- 当前实现：首页 API、首页 UI、分类/商品/横幅数据、登录/商品/购物车/订单路由预留

## 后端启动

默认使用 H2 文件数据库，首次启动会在项目根目录生成 `data/`：

```bash
./mvnw spring-boot:run
```

首页接口：

```text
GET http://localhost:8080/api/home
```

H2 控制台：

```text
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:file:./data/shopping-platform
User: sa
Password:
```

## 切换 MySQL

先执行：

```sql
database/mysql/schema.sql
database/mysql/seed.sql
```

再按本机账号修改 `src/main/resources/application-mysql.properties`，启动时启用 profile：

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=mysql
```

## 前端启动

```bash
cd frontend
npm install
npm run dev
```

前端默认端口 `5173`，Vite 会把 `/api` 代理到 `http://localhost:8080`。


