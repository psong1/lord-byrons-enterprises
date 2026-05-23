# Lord Byron's Enterprises

A full-stack e-commerce application with an employee portal for order fulfillment and payroll. Customers shop online with guest or registered carts, pay with Stripe, and track orders. Staff and administrators use a separate portal to run the business.

Built as a learning project for modern web development: **React** on the front end, **Spring Boot** on the back end, and **MySQL** for persistence.

---

## Features

### Storefront (customers)

- Browse products and product variants (size, price, SKU)
- Shopping cart for **guests** (session-based) and **logged-in users** (cart merges on login)
- User registration and JWT authentication
- Saved shipping/billing addresses
- Checkout with **Stripe** (Payment Intents)
- Order history

### Staff portal (`ADMIN` and `EMPLOYEE`)

- Dashboard
- **Order fulfillment** — view all orders and update status (New → Paid → Shipped → Delivered, etc.)
- **Payroll** — view your own paychecks

### Admin portal (`ADMIN` only)

- Product/inventory management (CRUD)
- User management (assign roles: Customer, Employee, Admin)
- **Process payroll** — run a pay period and generate paychecks for employees

---

## Tech stack

| Layer         | Technology                                        |
| ------------- | ------------------------------------------------- |
| Frontend      | React 19, Vite 7, React Router 7, Axios           |
| Payments (UI) | Stripe.js, React Stripe Elements                  |
| Backend       | Spring Boot 3.5, Spring Security, Spring Data JPA |
| Database      | MySQL                                             |
| Auth          | JWT (Bearer token) + BCrypt passwords             |
| Build tools   | Maven (server), npm (client)                      |

---

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│  Browser (http://localhost:5173)                            │
│  React SPA — routes, pages, AuthContext, CartContext        │
└──────────────────────────┬──────────────────────────────────┘
                           │ REST / JSON
                           │ Authorization: Bearer <JWT>
                           │ Cookies (guest cart session)
                           ▼
┌─────────────────────────────────────────────────────────────┐
│  Spring Boot API (http://localhost:8080)                    │
│  Controllers → Services → Repositories → JPA entities       │
└──────────────────────────┬──────────────────────────────────┘
                           │
              ┌────────────┴────────────┐
              ▼                         ▼
        ┌──────────┐              ┌──────────┐
        │  MySQL   │              │  Stripe  │
        └──────────┘              └──────────┘
```

The client and server are separate applications in one repository. They communicate over HTTP; the client does not connect to the database directly.

---

## Prerequisites

Before you run the project locally, install:

- **Node.js** 18+ and npm
- **JDK** 24 (as configured in `server/pom.xml`)
- **Maven** 3.9+
- **MySQL** 8+
- A **Stripe** account (test mode keys are enough for checkout)

---

## Getting started

### 1. Clone and open the repository

```bash
git clone <your-repo-url>
cd lord-byrons-enterprises
```

### 2. Configure the database

Create a MySQL database (for example `lordbyrons_db`).

Optional: load sample data — see [HOW_TO_RUN_SQL_SCRIPT.md](HOW_TO_RUN_SQL_SCRIPT.md).

### 3. Configure the server

```bash
cd server
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Edit `application.properties` and set:

| Property                                  | Description                         |
| ----------------------------------------- | ----------------------------------- |
| `spring.datasource.url`                   | JDBC URL to your MySQL database     |
| `spring.datasource.username` / `password` | Database credentials                |
| `application.security.jwt.secret-key`     | Long random secret for signing JWTs |
| `stripe.secret.key`                       | Stripe secret key (`sk_test_...`)   |

Start the API:

```bash
mvn spring-boot:run
```

The server listens on **http://localhost:8080** by default.

On first run, Hibernate can create or update tables (`spring.jpa.hibernate.ddl-auto=update` in the example config).

### 4. Configure and run the client

```bash
cd client
npm install
```

Create `client/.env` (optional if the default API URL works):

```env
VITE_API_BASE_URL=http://localhost:8080
```

Start the dev server:

```bash
npm run dev
```

Open **http://localhost:5173** in your browser.

### 5. Create users and roles

1. Register a customer at **/register**.
2. To access the portal, a user needs role `EMPLOYEE` or `ADMIN`.
3. Log in as an admin, go to **Portal → Manage Users**, and change another user’s role — or update the `role` column in the database.

| Role       | Access                                                             |
| ---------- | ------------------------------------------------------------------ |
| `CUSTOMER` | Shop, cart, checkout, account, orders                              |
| `EMPLOYEE` | Portal: dashboard, order fulfillment, own paychecks                |
| `ADMIN`    | Everything employees can do, plus products, users, process payroll |

---

## Project structure

```
lord-byrons-enterprises/
├── client/                 # React frontend (Vite)
│   ├── src/
│   │   ├── api/            # HTTP calls to the backend
│   │   ├── components/     # Reusable UI (Navbar, ProductCard, CheckoutForm, …)
│   │   ├── context/        # Auth and cart global state
│   │   ├── pages/          # Route-level screens
│   │   └── App.jsx         # Routes and layouts
│   └── package.json
│
├── server/                 # Spring Boot backend
│   ├── src/main/java/.../server/
│   │   ├── config/         # Security, JWT, CORS
│   │   ├── user/           # Users, auth, addresses
│   │   ├── product/        # Products, variants, categories
│   │   ├── cart/           # Guest and user carts
│   │   ├── order/          # Orders and fulfillment
│   │   ├── payment/        # Stripe integration
│   │   ├── inventory/      # Stock reserve/commit/release
│   │   └── payroll/        # Pay runs and paychecks
│   └── pom.xml
│
└── docs (*.md)             # Detailed guides (see below)
```

---

## Main routes (frontend)

| URL                                | Description                |
| ---------------------------------- | -------------------------- |
| `/`                                | Home                       |
| `/products`, `/products/:id`       | Catalog                    |
| `/cart`                            | Shopping cart              |
| `/login`, `/register`              | Authentication             |
| `/account`, `/checkout`, `/orders` | Customer (login required)  |
| `/portal/dashboard`                | Staff portal home          |
| `/portal/fulfillment`              | Order fulfillment          |
| `/portal/payroll`                  | My paychecks               |
| `/portal/inventory`                | Product management (admin) |
| `/portal/process-payroll`          | Run payroll (admin)        |
| `/portal/user-management`          | Manage users (admin)       |

Legacy URLs such as `/admin` redirect to the matching `/portal/...` paths.

---

## API overview (backend)

Base URL: `http://localhost:8080`

| Area     | Examples                                                 |
| -------- | -------------------------------------------------------- |
| Auth     | `POST /auth/login`                                       |
| Users    | `POST /user`, `GET /user`, `PUT /user/{id}/role`         |
| Products | `GET /product`, `POST /product` (admin)                  |
| Cart     | `GET /cart`, `POST /cart/items`, `POST /cart/merge`      |
| Orders   | `POST /orders`, `GET /orders`, `GET /orders/all` (staff) |
| Payroll  | `GET /paychecks/me`, `POST /payroll/process` (admin)     |

Protected endpoints expect `Authorization: Bearer <jwt>`. Guest cart endpoints use session cookies (`withCredentials: true` on the client).

Full API and security details: [server/README.md](server/README.md).

---

## Scripts

### Client (`client/`)

| Command           | Description                 |
| ----------------- | --------------------------- |
| `npm run dev`     | Start Vite dev server       |
| `npm run build`   | Production build to `dist/` |
| `npm run preview` | Preview production build    |
| `npm run lint`    | Run ESLint                  |

### Server (`server/`)

| Command               | Description              |
| --------------------- | ------------------------ |
| `mvn spring-boot:run` | Run the API              |
| `mvn test`            | Run unit and slice tests |
| `mvn compile`         | Compile only             |

---

## Environment variables

### Client (`client/.env`)

| Variable            | Default                 | Purpose                    |
| ------------------- | ----------------------- | -------------------------- |
| `VITE_API_BASE_URL` | `http://localhost:8080` | Backend base URL for Axios |

### Server (`server/src/main/resources/application.properties`)

Not committed to git. Copy from `application.properties.example`. Required: database, JWT secret, Stripe secret.

---

## Testing

```bash
cd server
mvn test
```

Tests include cart, order, and inventory service/controller slices. See `server/src/test/java/`.

---

## Troubleshooting

| Problem                     | What to check                                                                            |
| --------------------------- | ---------------------------------------------------------------------------------------- |
| Client cannot reach API     | Server running on 8080; `VITE_API_BASE_URL`; CORS in `SecurityConfig`                    |
| 401 on protected routes     | Log in again; JWT in browser Local Storage                                               |
| Guest cart lost after login | `POST /cart/merge` and cookies enabled on Axios                                          |
| Checkout fails              | Valid Stripe test keys; card uses [Stripe test numbers](https://docs.stripe.com/testing) |
| Two navbars on screen       | Pages should not render `<Navbar />` — only `App.jsx` layouts do                         |
| Portal page blank           | Browser console for failed lazy import paths in `App.jsx`                                |

---
