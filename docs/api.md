# API Documentation

## Authentication

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

---

## Users

Base path: `/api/users`

All endpoints require authentication via session cookie from login.

Notes:

- `ADMIN`/`OWNER` can list, create, and manage any user.
- Regular users can access/update/delete only themselves.
- Role changes are only allowed for `ADMIN`/`OWNER`.
- Delete operations anonymize the user and keep related data.

### GET /api/users

Lists all users (ADMIN/OWNER only).

**Responses:**

- `200 OK`

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

- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - Access denied

---

### GET /api/users/me

Returns the current user.

**Responses:**

- `200 OK`

```json
{
    "id": 1,
    "username": "alice",
    "email": "alice@crusher-test.de",
    "role": "USER"
}
```

- `401 Unauthorized` - Not authenticated

---

### GET /api/users/{id}

Returns a user by ID (ADMIN/OWNER or self).

**Path Parameters:**

- `id` (Long) - The user ID

**Responses:**

- `200 OK` (same shape as `/me`)
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - Access denied
- `404 Not Found` - User not found

---

### POST /api/users

Creates a new user. This endpoint is public for sign-up; authenticated `ADMIN`/`OWNER` can also create users with roles.

**Request Body:**

```json
{
    "username": "new-user",
    "email": "new-user@example.com",
    "password": "strong-password",
    "role": "USER"
}
```

**Fields:**

- `username` (String, required)
- `email` (String, required)
- `password` (String, required)
- `role` (String, optional) - One of: `USER`, `SETTER`, `OWNER`, `ADMIN` (only for `ADMIN`/`OWNER`)

**Responses:**

- `201 Created` (same shape as `/me`)
- `400 Bad Request` - Missing/invalid fields or duplicates
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - Access denied

---

### PUT /api/users/me

Updates the current user.

**Request Body:**

```json
{
    "username": "new-name",
    "email": "new-email@example.com",
    "password": "new-password"
}
```

**Responses:**

- `200 OK` (same shape as `/me`)
- `400 Bad Request` - Invalid fields or duplicates
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - Role change not allowed

---

### PUT /api/users/{id}

Updates a user by ID (ADMIN/OWNER or self).

**Path Parameters:**

- `id` (Long) - The user ID

**Request Body:**

```json
{
    "username": "new-name",
    "email": "new-email@example.com",
    "password": "new-password",
    "role": "SETTER"
}
```

**Responses:**

- `200 OK` (same shape as `/me`)
- `400 Bad Request` - Invalid fields or duplicates
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - Access denied or role change not allowed
- `404 Not Found` - User not found

---

### DELETE /api/users/me

Deletes the current user and logs them out.

**Responses:**

- `204 No Content`
- `401 Unauthorized` - Not authenticated
- `409 Conflict` - User has related data and cannot be deleted

---

### DELETE /api/users/{id}

Deletes a user by ID (ADMIN/OWNER or self).

**Path Parameters:**

- `id` (Long) - The user ID

**Responses:**

- `204 No Content`
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - Access denied
- `404 Not Found` - User not found
- `409 Conflict` - User has related data and cannot be deleted

---

## Goes (Climbing Attempts)

Base path: `/api/sessions/{sessionId}/goes`

All endpoints require authentication via session cookie from login.

### GET /api/sessions/{sessionId}/goes

Lists all climbing attempts (goes) for a specific session.

**Path Parameters:**

- `sessionId` (Long) - The ID of the session

**Responses:**

- `200 OK`

```json
[
    {
        "id": 1,
        "sessionId": 42,
        "boulderId": 15,
        "result": "FINISHED",
        "timestamp": "2026-01-21T14:30:00",
        "progressedHold": null
    },
    {
        "id": 2,
        "sessionId": 42,
        "boulderId": 18,
        "result": "CLOSE_TRY",
        "timestamp": "2026-01-21T14:45:00",
        "progressedHold": 8
    }
]
```

- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - Session belongs to another user
- `404 Not Found` - Session not found

---

### GET /api/sessions/{sessionId}/goes/{goId}

Retrieves details of a specific climbing attempt.

**Path Parameters:**

- `sessionId` (Long) - The ID of the session
- `goId` (Long) - The ID of the go

**Responses:**

- `200 OK`

```json
{
    "id": 1,
    "sessionId": 42,
    "boulderId": 15,
    "result": "FINISHED",
    "timestamp": "2026-01-21T14:30:00",
    "progressedHold": null
}
```

- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - Session belongs to another user
- `404 Not Found` - Session or go not found, or go doesn't belong to session

---

### POST /api/sessions/{sessionId}/goes

Creates a new climbing attempt for a session.

**Path Parameters:**

- `sessionId` (Long) - The ID of the session

**Request Body:**

```json
{
    "boulderId": 15,
    "result": "FINISHED",
    "timestamp": "2026-01-21T14:30:00",
    "progressedHold": null
}
```

**Fields:**

- `boulderId` (Long, required) - The ID of the boulder being attempted
- `result` (String, required) - Must be one of: `DID_NOT_FINISH`, `CLOSE_TRY`, `FINISHED`
- `timestamp` (LocalDateTime, optional) - Defaults to current time if not provided
- `progressedHold` (Integer, optional) - The last hold reached (must be >= 0 and <= boulder's total holds)

**Responses:**

- `201 Created`

```json
{
    "id": 1,
    "sessionId": 42,
    "boulderId": 15,
    "result": "FINISHED",
    "timestamp": "2026-01-21T14:30:00",
    "progressedHold": null
}
```

- `400 Bad Request`

```json
{
    "message": "Boulder is required"
}
```

```json
{
    "message": "Invalid result value. Must be one of: DID_NOT_FINISH, CLOSE_TRY, FINISHED"
}
```

```json
{
    "message": "Progressed hold cannot exceed the boulder's total holds (10)"
}
```

- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - Session belongs to another user
- `404 Not Found` - Session not found

**Notes:**

- If `result` is `FINISHED`, any associated project for this boulder will be automatically removed
- Timestamp defaults to the current time when the request is processed

---

### PUT /api/sessions/{sessionId}/goes/{goId}

Updates an existing climbing attempt.

**Path Parameters:**

- `sessionId` (Long) - The ID of the session
- `goId` (Long) - The ID of the go to update

**Request Body:**

```json
{
    "result": "CLOSE_TRY",
    "progressedHold": 8
}
```

**Fields:**

- `result` (String, required) - Must be one of: `DID_NOT_FINISH`, `CLOSE_TRY`, `FINISHED`
- `progressedHold` (Integer, optional) - The last hold reached (must be >= 0 and <= boulder's total holds)

**Responses:**

- `200 OK`

```json
{
    "id": 1,
    "sessionId": 42,
    "boulderId": 15,
    "result": "CLOSE_TRY",
    "timestamp": "2026-01-21T14:30:00",
    "progressedHold": 8
}
```

- `400 Bad Request`

```json
{
    "message": "Result is required"
}
```

```json
{
    "message": "Progressed hold cannot be negative"
}
```

- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - Session belongs to another user
- `404 Not Found` - Session or go not found, or go doesn't belong to session

**Notes:**

- The original `timestamp` is preserved and not updated
- If `result` is updated to `FINISHED`, any associated project for this boulder will be automatically removed

---

### DELETE /api/sessions/{sessionId}/goes/{goId}

Deletes a climbing attempt.

**Path Parameters:**

- `sessionId` (Long) - The ID of the session
- `goId` (Long) - The ID of the go to delete

**Responses:**

- `204 No Content` - Successfully deleted

- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - Session belongs to another user
- `404 Not Found` - Session or go not found, or go doesn't belong to session

---

### Go Result Values

The `result` field accepts the following enum values:

- `DID_NOT_FINISH` - The climber did not complete the boulder
- `CLOSE_TRY` - The climber came close to finishing
- `FINISHED` - The climber successfully completed the boulder

---

## Statistics

Base path: `/api/statistics`

All endpoints require authentication via session cookie from login.

The statistics API allows users to configure which statistics they want to track and retrieve statistics data since their last fetch. Statistics are calculated based on the user's climbing attempts (goes).

### POST /api/statistics

Creates a new statistics configuration for the authenticated user.

**Request Body:**

```json
{
    "goesPerGradeEnabled": true,
    "finishedGoesPerGradeEnabled": true,
    "resultDistributionEnabled": true,
    "highestFinishedGradeEnabled": true
}
```

**Fields:**

- `goesPerGradeEnabled` (boolean, required) - Track total attempts per grade
- `finishedGoesPerGradeEnabled` (boolean, required) - Track finished attempts per grade
- `resultDistributionEnabled` (boolean, required) - Track distribution of results (FINISHED, CLOSE_TRY, DID_NOT_FINISH)
- `highestFinishedGradeEnabled` (boolean, required) - Track the highest grade completed

**Responses:**

- `201 Created`

```json
{
    "message": "Configuration created successfully",
    "config": {
        "goesPerGradeEnabled": true,
        "finishedGoesPerGradeEnabled": true,
        "resultDistributionEnabled": true,
        "highestFinishedGradeEnabled": true
    }
}
```

- `401 Unauthorized` - Not authenticated

```json
{
    "message": "Not authenticated"
}
```

- `409 Conflict` - Configuration already exists

```json
{
    "message": "Configuration already exists. Use PUT to update."
}
```

**Notes:**

- Each user can only have one statistics configuration
- Use PUT endpoint to update an existing configuration

---

### PUT /api/statistics

Updates the statistics configuration for the authenticated user.

**Request Body:**

```json
{
    "goesPerGradeEnabled": true,
    "finishedGoesPerGradeEnabled": false,
    "resultDistributionEnabled": false,
    "highestFinishedGradeEnabled": true
}
```

**Fields:**

- `goesPerGradeEnabled` (boolean, required) - Track total attempts per grade
- `finishedGoesPerGradeEnabled` (boolean, required) - Track finished attempts per grade
- `resultDistributionEnabled` (boolean, required) - Track distribution of results
- `highestFinishedGradeEnabled` (boolean, required) - Track the highest grade completed

**Responses:**

- `200 OK`

```json
{
    "message": "Configuration updated successfully",
    "config": {
        "goesPerGradeEnabled": true,
        "finishedGoesPerGradeEnabled": false,
        "resultDistributionEnabled": false,
        "highestFinishedGradeEnabled": true
    }
}
```

- `401 Unauthorized` - Not authenticated

```json
{
    "message": "Not authenticated"
}
```

- `404 Not Found` - Configuration doesn't exist

```json
{
    "message": "Configuration not found. Use POST to create."
}
```

**Notes:**

- Updates all configuration fields
- Configuration must already exist (use POST to create first)

---

### GET /api/statistics

Retrieves statistics based on the user's configuration. Returns statistics calculated from goes since the last GET request (or all goes if this is the first request after configuration creation).

**Responses:**

- `200 OK`

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

**Response Fields:**

- `goesPerGrade` (object, nullable) - Count of all attempts per grade (V-scale). Grades are sorted in ascending order. Only present if `goesPerGradeEnabled` is true.
- `finishedGoesPerGrade` (object, nullable) - Count of finished attempts per grade (V-scale). Grades are sorted in ascending order. Only present if `finishedGoesPerGradeEnabled` is true.
- `resultDistribution` (object, nullable) - Count of attempts by result type. Only present if `resultDistributionEnabled` is true.
- `highestFinishedGrade` (string, nullable) - The highest grade (V-scale) successfully finished. Returns `null` if no grades have been finished. Only present if `highestFinishedGradeEnabled` is true.

- `401 Unauthorized` - Not authenticated

```json
{
    "message": "Not authenticated"
}
```

- `404 Not Found` - Configuration doesn't exist

```json
{
    "message": "Configuration not found. Use POST to create one first."
}
```

**Notes:**

- Statistics are calculated only from goes that occurred after the previous GET request
- On the first GET request after configuration creation, all historical goes are included
- The `lastFetchedAt` timestamp is automatically updated after each successful GET request
- Fields in the response are `null` if their corresponding configuration flag is disabled
- Grades in `goesPerGrade` and `finishedGoesPerGrade` are sorted by V-scale value (V0, V1, V2, etc.)
- The `resultDistribution` object always contains all three result types: `FINISHED`, `CLOSE_TRY`, `DID_NOT_FINISH`

---

### Example Workflow

1. **Login** (see Authentication section)

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"password"}' \
  -c cookies.txt
```

2. **Create statistics configuration**

```bash
curl -X POST http://localhost:8080/api/statistics \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{"goesPerGradeEnabled":true,"finishedGoesPerGradeEnabled":true,"resultDistributionEnabled":true,"highestFinishedGradeEnabled":true}'
```

3. **Retrieve statistics**

```bash
curl http://localhost:8080/api/statistics -b cookies.txt
```

4. **Update configuration** (e.g., disable some statistics)

```bash
curl -X PUT http://localhost:8080/api/statistics \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{"goesPerGradeEnabled":true,"finishedGoesPerGradeEnabled":false,"resultDistributionEnabled":false,"highestFinishedGradeEnabled":true}'
```
