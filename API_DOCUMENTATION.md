
# FMCG Campaign API Documentation (v1)

## Security
- JWT Bearer Auth (`POST /api/v1/auth/token`)
- Roles: `ROLE_ADMIN`, `ROLE_AGENT`

### Authorization
- `POST /api/v1/products/**` → ADMIN
- `POST /api/v1/campaigns/**` → ADMIN
- `PATCH /api/v1/campaigns/**` → ADMIN
- `POST /api/v1/campaigns/billing` → AGENT or ADMIN
- `/api/v1/reports/**` → ADMIN
- Other endpoints require authentication.

## Controllers
- `ProductController` → `/api/v1/products`
- `CampaignController` → `/api/v1/campaigns`
- `ReportController` → `/api/v1/reports`
- `AuthController` → `/api/v1/auth`

## Product APIs
### Create Product
`POST /api/v1/products`
```json
{"id":1,"name":"Soap","price":35.50}
```

### Get All Products
`GET /api/v1/products`

## Campaign APIs
### Create Campaign
`POST /api/v1/campaigns`
```json
{
  "id":1001,
  "name":"Summer Promo",
  "fromDateTime":"2026-05-01T09:00:00",
  "toDateTime":"2026-05-31T20:00:00",
  "campaignLocation":"Central Mall",
  "location":"Chicago",
  "status":"ACTIVE",
  "userId":101
}
```

### Get Campaign by Id
`GET /api/v1/campaigns/{campaignId}`

### Get Campaigns (with optional filters)
`GET /api/v1/campaigns?status=ACTIVE&location=Chicago`

### Add Product to Campaign
`POST /api/v1/campaigns/{campaignId}/products`
```json
{"productId":1,"discount":5.0,"userId":101}
```

### Assign Campaign User
`PATCH /api/v1/campaigns/{campaignId}/users`
```json
{"userId":102}
```

### Update Campaign Status
`PATCH /api/v1/campaigns/{campaignId}/status`
```json
{"status":"COMPLETE"}
```

### Campaign Billing
`POST /api/v1/campaigns/billing`
```json
{"campaignId":1001,"userId":101,"productId":1,"qty":3}
```

### Get Campaign Billing
`GET /api/v1/campaigns/{campaignId}/billing?userId=101`

## Reports
### Sales Report
`GET /api/v1/reports/campaigns/{campaignId}/sales`

## Standard Error Format
```json
{
  "code": "CAMPAIGN_NOT_ACTIVE",
  "message": "Billing is allowed only for active campaigns",
  "timestamp": "2026-04-16T11:00:00"
}
```
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

> Note: Your original names had spelling variations (`campain`, `campaign`). API paths are kept as requested (`campain`) for compatibility.

