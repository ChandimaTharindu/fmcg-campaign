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
