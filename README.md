# URL Shortener – API Docs

## Database 

| Column         | Type          | Notes             |
|----------------|---------------|-------------------|
| `id`           | BIGINT (PK)   | Auto-generated    |
| `short_code`   | VARCHAR(16)   | Unique, required  |
| `original_url` | VARCHAR(2500) | Required          |
| `created_time` | TIMESTAMP     | Default: now      |

---

## Endpoints

### `POST /shorten`
- **Description**: Create a short link for a given URL.  
- **Request body**:
  ```json
  { "url": "https://example.com/some/long/path" }
  ```
  - **response**:
  - Short URL: http://localhost:8080/ab12cd3

### `GET /{code}`
Description: Redirect to the original URL.

**Response**:
- 302 Found → Redirects to original_url
- 404 Not Found if code doesn’t exist


### `/testing`:
- just for testing to verfiy the program compiles (remove later for presentation!)

