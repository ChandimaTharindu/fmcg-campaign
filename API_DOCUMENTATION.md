
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
=======
# FMCG Campaign API Documentation

## 1) Role-wise Journey

### ADMIN journey
1. Create product (`POST /product`).
2. Create campaign (`POST /campain`) and assign responsible agent (`user_id`).
3. Map products into campaign (`POST /campain/product`).
4. Set campaign-product discounts (`POST /campain/discount`).
5. Review campaign-product details (`GET /campain?campainId=...`).
6. View user list (`GET /users`).
7. Re-assign campaign to another user (`PATCH /campain/users`).
8. View single campaign (`GET /campains/{campainId}`) or all campaigns (`GET /campains/list`).
9. Generate sales report (`GET /campains/{campainId}/sales-report`).

### AGENT journey
1. View assigned campaigns (`GET /agent/campain?userId=...`).
2. Raise billing inside active campaign window (`POST /agent/campain/billing`).
3. List billing lines (`GET /agent/campain/billing/{campainId}`).
4. Close campaign when completed (`PATCH /agent/campain/{campainId}/close`).

---

## 2) Request/Response Contract

### Add Product
- **POST** `/product`
- Request:
```json
{
  "id": 1,
  "name": "Soap",
  "price": 35.50
}
```
- Response `201`:
```json
{
  "id": 1,
  "name": "Soap",
  "price": 35.50
}
```

### 3) Setup Campaign (ADMIN)
**POST** `/api/v1/campaigns`
=======
### Setup Campaign
- **POST** `/campain`
- Request:
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
=======
- Response `201`: created campaign object.

### Add Product to Campaign
- **POST** `/campain/product`
- Request:
```json
{
  "campaignId": 1001,
  "productId": 1,
  "discount": 5.00,
  "userId": 101
}
```
- Response `201`: created campaign-product row.

### Campaign Product Discount
- **POST** `/campain/discount`
- Request:
```json
{
  "campaignId": 1001,
  "productId": 1,
  "discount": 7.50
}
```
- Response `201`: created discount record.

### Get Campaign Product List
- **GET** `/campain?campainId=1001`
- Response `200`:
```json
{
  "id": 1001,
  "name": "Summer Promo",
  "fromDateTime": "2026-05-01T09:00:00",
  "toDateTime": "2026-05-31T20:00:00",
  "campaignLocation": "Central Mall",
  "location": "Chicago",
  "status": "ACTIVE",
  "userId": 101,
  "products": [
    {
      "productId": 1,
      "productName": "Soap",
      "discount": 5.00
    }
  ]
}
```

### User Management
- **GET** `/users`
- Response `200`:
```json
[
  {"id": 101, "userName": "agent.ravi", "contactNumber": "+1-202-555-0101"}
]
```

### Campaign User Assignment
- **PATCH** `/campain/users`
- Request:
```json
{
  "userId": 102,
  "campaignId": 1001
}
```
- Response `200`: updated campaign object.

### Campaign Detail APIs
- **GET** `/campains/{campainId}`
- **GET** `/campains/list`

### Campaign Sales Report
- **GET** `/campains/{campainId}/sales-report`
- Response `200`:

```json
{
  "campaignId": 1001,
  "grossSales": 1065.00,
  "billedItems": 15
}
```

---

## Data Model (JPA)
=======
### Agent Billing
- **POST** `/agent/campain/billing`
- Request:
```json
{
  "campaignId": 1001,
  "userId": 101,
  "productId": 1,
  "qty": 3
}
```
- Response `201`:
```json
{
  "campaignId": 1001,
  "userId": 101,
  "productId": 1,
  "qty": 3
}
```

### List Billing Items
- **GET** `/agent/campain/billing/{campainId}`

### Close Campaign
- **PATCH** `/agent/campain/{campainId}/close`
- Response `200`: campaign with status `COMPLETE`.

---

## 3) Implemented Data Model (JPA)
- `product(id, name, price)`
- `app_user(id, user_name, contact_number)`
- `campaign(id, name, from_date_time, to_date_time, campaign_location, location, status, user_id)`
- `campaign_product(id, campaign_id, product_id, discount, user_id)`
- `campaign_product_discount(id, campaign_id, product_id, discount)`
- `campaign_billing(id, campaign_id, user_id, product_id, qty)`
=======

> Note: Your original names had spelling variations (`campain`, `campaign`). API paths are kept as requested (`campain`) for compatibility.

