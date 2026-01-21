# API Documentation

## Base URL and authentication

- All endpoints are served under /api.
- Authentication uses a server-side session. Successful login sets a JSESSIONID cookie.
- For command line testing, use curl -c cookies.txt to store cookies and -b cookies.txt to send them.

## Error format

Most API errors return:

```json
{
    "message": "Human readable message"
}
```

Global behavior for /api/\*\*:

- Unauthenticated requests return 401 Unauthorized.
- Authenticated but unauthorized requests return 403 Forbidden.

---

## Authentication

Base path: /api/auth

### POST /api/auth/login

Request body:

```json
{
    "username": "your-username",
    "password": "your-password"
}
```

Responses:

- 200 OK

```json
{
    "id": 1,
    "username": "your-username",
    "role": "USER"
}
```

- 400 Bad Request

```json
{
    "message": "Username and password are required"
}
```

- 401 Unauthorized

```json
{
    "message": "Invalid username or password"
}
```

Notes:

- Creates an HTTP session and sets the JSESSIONID cookie.

### GET /api/auth/session

Requires the session cookie from login.

Responses:

- 200 OK

```json
{
    "id": 1,
    "username": "your-username",
    "role": "USER"
}
```

- 401 Unauthorized

```json
{
    "message": "Not authenticated"
}
```

### POST /api/auth/logout

Requires the session cookie from login.

Responses:

- 204 No Content

---

## Users

Base path: /api/users

All endpoints require authentication via session cookie from login unless stated otherwise.

Notes:

- ADMIN/OWNER can list, create, and manage any user.
- Regular users can access, update, and delete only themselves.
- Role changes are allowed only for ADMIN/OWNER.
- Delete operations anonymize the user (name/email/password) and keep related data.

### GET /api/users

Lists all users (ADMIN/OWNER only).

Responses:

- 200 OK

```json
[
    {
        "id": 1,
        "username": "alice",
        "email": "alice@crusher-test.de",
        "role": "USER"
    }
]
```

- 401 Unauthorized
- 403 Forbidden

### GET /api/users/me

Returns the current user.

Responses:

- 200 OK

```json
{
    "id": 1,
    "username": "alice",
    "email": "alice@crusher-test.de",
    "role": "USER"
}
```

- 401 Unauthorized

### GET /api/users/{id}

Returns a user by ID (ADMIN/OWNER or self).

Path parameters:

- id (Long) - The user ID.

Responses:

- 200 OK (same shape as /me)
- 401 Unauthorized
- 403 Forbidden
- 404 Not Found

### POST /api/users

Creates a new user. This endpoint is public for sign-up; authenticated ADMIN/OWNER can also
create users with roles. If an authenticated non-admin user calls this endpoint, it returns 403.

Request body:

```json
{
    "username": "new-user",
    "email": "new-user@example.com",
    "password": "strong-password",
    "role": "USER"
}
```

Fields:

- username (String, required)
- email (String, required)
- password (String, required)
- role (String, optional) - One of: USER, SETTER, OWNER, ADMIN (only for ADMIN/OWNER)

Responses:

- 201 Created (same shape as /me)
- 400 Bad Request - Missing/invalid fields or duplicates
- 401 Unauthorized
- 403 Forbidden

### PUT /api/users/me

Updates the current user.

Request body:

```json
{
    "username": "new-name",
    "email": "new-email@example.com",
    "password": "new-password"
}
```

Responses:

- 200 OK (same shape as /me)
- 400 Bad Request - Invalid fields or duplicates
- 401 Unauthorized
- 403 Forbidden - Role change not allowed

### PUT /api/users/{id}

Updates a user by ID (ADMIN/OWNER or self).

Path parameters:

- id (Long) - The user ID

Request body:

```json
{
    "username": "new-name",
    "email": "new-email@example.com",
    "password": "new-password",
    "role": "SETTER"
}
```

Responses:

- 200 OK (same shape as /me)
- 400 Bad Request - Invalid fields or duplicates
- 401 Unauthorized
- 403 Forbidden
- 404 Not Found

### DELETE /api/users/me

Deletes the current user and logs them out.

Responses:

- 204 No Content
- 401 Unauthorized

### DELETE /api/users/{id}

Deletes a user by ID (ADMIN/OWNER or self).

Path parameters:

- id (Long) - The user ID

Responses:

- 204 No Content
- 401 Unauthorized
- 403 Forbidden
- 404 Not Found

---

## Goes (Climbing attempts)

Base path: /api/sessions/{sessionId}/goes

All endpoints require authentication via session cookie from login.

Notes:

- The session must belong to the authenticated user.
- Boulder IDs must reference a non-deleted boulder.
- If result is FINISHED, any related project for that boulder is removed.

### GET /api/sessions/{sessionId}/goes

Lists all goes for a session.

Path parameters:

- sessionId (Long) - The ID of the session.

Responses:

- 200 OK

```json
[
    {
        "id": 1,
        "sessionId": 42,
        "boulderId": 15,
        "result": "FINISHED",
        "timestamp": "2026-01-21T14:30:00",
        "progressedHold": null
    }
]
```

- 401 Unauthorized
- 403 Forbidden
- 404 Not Found

### GET /api/sessions/{sessionId}/goes/{goId}

Retrieves details of a specific go.

Path parameters:

- sessionId (Long) - The ID of the session
- goId (Long) - The ID of the go

Responses:

- 200 OK (same shape as list)
- 401 Unauthorized
- 403 Forbidden
- 404 Not Found

### POST /api/sessions/{sessionId}/goes

Creates a new go for a session.

Request body:

```json
{
    "boulderId": 15,
    "result": "FINISHED",
    "timestamp": "2026-01-21T14:30:00",
    "progressedHold": null
}
```

Fields:

- boulderId (Long, required)
- result (String, required) - DID_NOT_FINISH, CLOSE_TRY, FINISHED
- timestamp (LocalDateTime, optional) - Defaults to current time if not provided
- progressedHold (Integer, optional) - Must be >= 0 and <= boulder holds_count

Responses:

- 201 Created
- 400 Bad Request
- 401 Unauthorized
- 403 Forbidden
- 404 Not Found

### PUT /api/sessions/{sessionId}/goes/{goId}

Updates an existing go.

Request body:

```json
{
    "result": "CLOSE_TRY",
    "progressedHold": 8
}
```

Fields:

- result (String, required) - DID_NOT_FINISH, CLOSE_TRY, FINISHED
- progressedHold (Integer, optional) - Must be >= 0 and <= boulder holds_count

Responses:

- 200 OK
- 400 Bad Request
- 401 Unauthorized
- 403 Forbidden
- 404 Not Found

Notes:

- The original timestamp is preserved on update.

### DELETE /api/sessions/{sessionId}/goes/{goId}

Deletes a go.

Responses:

- 204 No Content
- 401 Unauthorized
- 403 Forbidden
- 404 Not Found

### Go result values

- DID_NOT_FINISH
- CLOSE_TRY
- FINISHED

---

## Statistics

Base path: /api/statistics

All endpoints require authentication via session cookie from login.

The statistics API allows users to configure which statistics they want to track and retrieve
statistics data since their last fetch. Statistics are calculated from the user's goes.

### POST /api/statistics

Creates a new statistics configuration for the authenticated user.

Request body:

```json
{
    "goesPerGradeEnabled": true,
    "finishedGoesPerGradeEnabled": true,
    "resultDistributionEnabled": true,
    "highestFinishedGradeEnabled": true
}
```

Responses:

- 201 Created
- 401 Unauthorized
- 409 Conflict

### PUT /api/statistics

Updates the statistics configuration for the authenticated user.

Responses:

- 200 OK
- 401 Unauthorized
- 404 Not Found

### GET /api/statistics

Retrieves statistics based on the user's configuration.

Responses:

- 200 OK

```json
{
    "goesPerGrade": {
        "V3": 5,
        "V4": 8,
        "V5": 3
    },
    "finishedGoesPerGrade": {
        "V3": 4,
        "V4": 5,
        "V5": 1
    },
    "resultDistribution": {
        "FINISHED": 10,
        "CLOSE_TRY": 4,
        "DID_NOT_FINISH": 2
    },
    "highestFinishedGrade": "V5"
}
```

Notes:

- Only goes after the previous successful GET are included.
- On the first GET after creation, all historical goes are included.
- lastFetchedAt is updated after each successful GET.

---

## Gyms

### GET /api/gyms/{id}/crowd-level

Fetches the current crowd level for a gym (if configured).

Responses:

- 200 OK

```json
{
    "percentage": 62.5,
    "status": "Busy"
}
```

- 404 Not Found (no crowd level URL configured or scraping failed)

Notes:

- Results are cached for 5 minutes.
- This endpoint is used by the gym detail page for lazy loading.

---

## API testing with Bruno

The crusher/ folder contains a Bruno collection (.bru) with requests for Auth, Users, Goes,
and Statistics. Import the folder in Bruno to run authenticated requests quickly.
