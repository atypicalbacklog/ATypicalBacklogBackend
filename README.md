# Atypical Backlog Management Backend

> **LLM assistance disclaimer:** Parts of this codebase were written with LLM assistance (Cursor, various models). Even though this was manually reviewed and tested, generated or assisted code may contain errors, omissions, or security issues. Review, test, and validate all behavior before relying on it in production. Use this software at your own risk.

Spring Boot backend for managing a personal gaming backlog: games, instances, playthroughs, reviews, completions, time bookings, backlog recommendations, and public feed endpoints.

## Tech Stack

- Java 21
- Spring Boot 3
- Spring Web + Spring Data JPA
- PostgreSQL
- Gradle (Kotlin DSL)
- springdoc OpenAPI/Swagger UI (disabled in public profile)

## What This Application Does

Core capabilities include:

- Manage game library entities (games, game instances, DLCs, images)
- Track playthroughs, sessions, time bookings, and completions
- Manage reviews and yearly review data
- Query backlog/game/review/playthrough overview data
- Public backlog recommendation flow (create, upvote, comment, list/detail)
- Public feed endpoints (recent activity, random backlog, random S-tier reviews, random backlog recommendations)

## Runtime Modes

The app supports two modes via `app.mode`:

- `MANAGEMENT`: full internal/management API usage
- `PUBLIC`: hardened mode with restricted write access and blocked admin/discovery surfaces

For public deployments, use:

- `app.mode=PUBLIC`
- `spring.profiles.active=public` (loads `application-public.yml`)

## Setup

### 1) Prerequisites

- JDK 21
- PostgreSQL (local or remote)
- Optional for calendar sync: Google service account JSON credentials
- Optional for IGDB import/search: IGDB/Twitch API credentials

### 2) Run

This project is built with **Gradle** (`./gradlew bootRun`). Adapt the run command if you use another build tool or IDE.

## Database Setup

Current configuration expects PostgreSQL and validates schema on startup (`hibernate.ddl-auto=validate`), so tables must already exist before boot.

### Required

- Create a PostgreSQL database
- Create an app user with privileges for that database
- Configure datasource values (URL, username, password) via environment variables or local config override

### SQL Schema Source

This repository will include the canonical SQL schema file in the future (planned: `final-ERD.sql`).

When available, setup will be:

1. Create empty database
2. Execute `final-ERD.sql`
3. Start application

Until then, use your existing SQL/bootstrap scripts currently used in your local environment.

## Configuration and Secrets

Do **not** commit real secrets. Use environment variables in production (and ideally local dev):

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `IGDB_CLIENT_ID`
- `IGDB_CLIENT_SECRET`
- `GOOGLE_CALENDAR_CREDENTIALS_PATH` (if calendar sync enabled)

Recommended: keep `application.yml` secret-free and inject credentials through env vars or deployment secrets.

## External Features / Integrations

This backend can access the following external systems/features:

1. **PostgreSQL**
   - Primary persistence store for all domain data.

2. **IGDB API (via Twitch credentials)**
   - Used for game search/import related endpoints.
   - Requires client id/secret.

3. **Google Calendar API**
   - Optional sync of time booking data to calendar.
   - Uses service account JSON key.
   - Can be disabled via config.

4. **Filesystem image storage**
   - Stores uploaded game images under configured root path (`app.image.storage.root-path`).

## Public Deployment Notes

For internet-facing deployment:

- Run with `app.mode=PUBLIC`
- Activate `public` profile (`spring.profiles.active=public`)
- Put app behind HTTPS reverse proxy (e.g. Caddy/nginx)
- Apply proxy-level rate limiting
- Keep only `/actuator/health` exposed publicly
- Keep Swagger/OpenAPI docs disabled in public mode/profile

## API Contract

Detailed endpoint contracts are documented in:

- `endpoint-contracts.txt`

## Third Party Libraries

This project depends on open-source libraries. Direct runtime dependencies:

| Library | Artifact | Version | License |
|---------|----------|---------|---------|
| Spring Boot Web | `spring-boot-starter-web` | 3.3.1 (BOM) | Apache-2.0 |
| Spring Boot Actuator | `spring-boot-starter-actuator` | 3.3.1 (BOM) | Apache-2.0 |
| Spring Boot Data JPA | `spring-boot-starter-data-jpa` | 3.3.1 (BOM) | Apache-2.0 |
| PostgreSQL JDBC | `postgresql` | Spring Boot BOM | BSD-2-Clause |
| springdoc OpenAPI UI | `springdoc-openapi-starter-webmvc-ui` | 2.5.0 | Apache-2.0 |
| Apache Commons CSV | `commons-csv` | 1.14.1 | Apache-2.0 |
| TwelveMonkeys ImageIO WebP | `imageio-webp` | 3.12.0 | BSD-3-Clause |
| Google API Client | `google-api-client` | 2.2.0 | Apache-2.0 |
| Google Calendar API | `google-api-services-calendar` | v3-rev20251207-2.0.0 | Apache-2.0 |
| Google Auth (OAuth2 HTTP) | `google-auth-library-oauth2-http` | 1.25.0 | BSD-3-Clause |
| ONNX Runtime (Java) | `onnxruntime` | 1.19.2 | MIT |
| DJL API | `ai.djl:api` | 0.30.0 | Apache-2.0 |
| DJL Hugging Face Tokenizers | `ai.djl.huggingface:tokenizers` | 0.30.0 | Apache-2.0 |

Spring Boot and other libraries also pull in transitive dependencies at build time.

Full copyright and license texts: **[THIRD_PARTY_NOTICES.txt](THIRD_PARTY_NOTICES.txt)**

## License

MIT License

Copyright (c) 2026 Atypical Backlog

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

