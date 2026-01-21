# API Auth

Base path: `/api/auth`

## POST /api/auth/login

Request body:

```json
{
    "username": "your-username",
    "password": "your-password"
}
```

Responses:

- `200 OK`

```json
{
    "id": 1,
    "username": "your-username",
    "role": "USER"
}
```

- `400 Bad Request`

```json
{
    "message": "Username and password are required"
}
```

- `401 Unauthorized`

```json
{
    "message": "Invalid username or password"
}
```

Notes:

- Creates an HTTP session and sets the `JSESSIONID` cookie.

## GET /api/auth/session

Requires the session cookie from login.

Responses:

- `200 OK`

```json
{
    "id": 1,
    "username": "your-username",
    "role": "USER"
}
```

- `401 Unauthorized`

```json
{
    "message": "Not authenticated"
}
```

## POST /api/auth/logout

Requires the session cookie from login.

Responses:

- `204 No Content`

## Error handling for /api/\*\*

- Unauthenticated requests receive `401 Unauthorized`.
- Authenticated but unauthorized requests receive `403 Forbidden`.
