# URL Shortener

Small Spring Boot service that creates short links backed by Postgres (Supabase-friendly) with basic click tracking.

## Project layout
- App: `url-shortener/` contains the Spring Boot service and static landing page.
- API: `url-shortener/src/main/java/com/SWE/url_shortener/controller/URLcontroller.java` handles `/api/shorten`, `/r/{key}`, `/api/info/{key}`, `/api/list`, `/api/{key}`.
- Service: `url-shortener/src/main/java/com/SWE/url_shortener/service/Urlservice.java` validates URLs, generates/claims keys, increments click counts.
- Frontend: static landing page under `url-shortener/src/main/resources/static/` (`index.html`, `global.css`).
- Tests: `url-shortener/src/test/java/com/SWE/url_shortener/**` covers controller, service, repository, performance; config in `url-shortener/src/test/resources/application.properties`.

## API snapshot
- `POST /api/shorten` — body `{ "url": "...", "customKey": "optional" }` → `{ "key": "abc123", "shortUrl": "http://localhost:8080/r/abc123" }`.
- `GET /r/{key}` — 302 redirect to original URL; 404 if unknown.
- `GET /api/info/{key}` — metadata including click count and created time.
- `GET /api/list` — recent links (optionally filtered in code by owner).
- `DELETE /api/{key}` — remove a short link.

## Configuration
- Environment variables (see `url-shortener/.env`): `SUPABASE_JDBC_URL`, `SUPABASE_DB_USERNAME`, `SUPABASE_DB_PASSWORD`.
- App runs on `localhost:8080` by default. Point JDBC URL to your Postgres/Supabase; ensure the `short_urls` table exists before running.

## Quick start (local)
1) Prereqs: Java 17+, Maven, and access to Postgres/Supabase.
2) Setup: create or edit `url-shortener/.env` with your `SUPABASE_*` values; create the `short_urls` table per schema above.
3) Run:
   - `cd url-shortener`
   - `./mvnw clean package`
   - `./mvnw spring-boot:run`
   - (Docker alternative) `docker compose up --build`

5) Tests: `./mvnw test`
