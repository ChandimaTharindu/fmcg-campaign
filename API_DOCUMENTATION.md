# FMCG Campaign API Documentation (v1)

## Security Design
- Auth mechanism: **Spring Security + JWT (Bearer Token)**
- Roles:
  - `ROLE_ADMIN`
  - `ROLE_AGENT`
- Token endpoint:
  - `POST /api/v1/auth/token`
  - Request: `{"username":"admin","password":"admin123"}`
  - Response: `{"token":"<jwt>"}`

### Authorization Matrix
- `POST /api/v1/products/**` → ADMIN
- `POST /api/v1/campaigns/**` → ADMIN
- `PATCH /api/v1/campaigns/**` → ADMIN
- `POST /api/v1/campaigns/billing` → AGENT or ADMIN
- `/api/v1/agent/**` → AGENT
- `/api/v1/reports/**` → ADMIN

---

## Role Journey

### ADMIN
1. Add product: `POST /api/v1/products`
2. Setup campaign: `POST /api/v1/campaigns`
3. Map campaign-product: `POST /api/v1/campaigns/products`
4. Save campaign-product discount: `POST /api/v1/campaigns/discount`
5. Get campaign details: `GET /api/v1/campaigns?campainId=...`
6. Get users: `GET /api/v1/users`
7. Assign campaign user: `PATCH /api/v1/campaigns/users`
8. Get campaign by id/list: `GET /api/v1/campaigns/{campainId}`, `GET /api/v1/campaigns/list`
9. Generate report: `GET /api/v1/reports/campaigns/{campainId}/sales`

### AGENT
1. View relevant campaigns: `GET /api/v1/agent/campaigns?userId=...`
2. Billing in active campaign window: `POST /api/v1/campaigns/billing`
3. List billing lines: `GET /api/v1/campaigns/billing/{campainId}`
4. Close campaign: `PATCH /api/v1/agent/campaigns/{campainId}/close`

---

## Core API Contracts

### 1) Login
**POST** `/api/v1/auth/token`
```json
{
  "username": "admin",
  "password": "admin123"
}
```
```json
{
  "token": "<jwt-token>"
}
```

### 2) Add Product (ADMIN)
**POST** `/api/v1/products`
```json
{
  "id": 1,
  "name": "Soap",
  "price": 35.50
}
```

### 3) Setup Campaign (ADMIN)
**POST** `/api/v1/campaigns`
```json
{
  "id": 1001,
  "name": "Summer Promo",
  "fromDateTime": "2026-05-01T09:00:00",
  "toDateTime": "2026-05-31T20:00:00",
  "campaignLocation": "Central Mall",
  "location": "Chicago",
  "status": "ACTIVE",
  "userId": 101
}
```

### 4) Campaign Billing (AGENT/ADMIN)
**POST** `/api/v1/campaigns/billing`
```json
{
  "campaignId": 1001,
  "userId": 101,
  "productId": 1,
  "qty": 3
}
```

### 5) Sales Report (ADMIN)
**GET** `/api/v1/reports/campaigns/1001/sales`
```json
{
  "campaignId": 1001,
  "grossSales": 1065.00,
  "billedItems": 15
}
```

---

## Data Model (JPA)
- `product(id, name, price)`
- `app_user(id, user_name, contact_number)`
- `campaign(id, name, from_date_time, to_date_time, campaign_location, location, status, user_id)`
- `campaign_product(id, campaign_id, product_id, discount, user_id)`
- `campaign_product_discount(id, campaign_id, product_id, discount)`
- `campaign_billing(id, campaign_id, user_id, product_id, qty)`
