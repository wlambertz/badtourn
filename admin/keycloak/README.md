# Keycloak Provisioning

This folder contains automation to set up Keycloak for RallyOn.

## Local Bootstrap

Install prerequisites:
- Docker Desktop / Compose v2
- `jq` command-line JSON processor (https://stedolan.github.io/jq/)

1. Start the compose stack:
   ```bash
   docker compose -f infrastructure/local/docker-compose.yml up -d
   ```

2. Export the required secrets:
   ```bash
   export RALLYON_CLIENT_SECRET=super-secret
   # Optional overrides:
   # export RALLYON_REALM=rallyon
   # export RALLYON_CLIENT_ID=rallyon-api
   # export RALLYON_REDIRECT_URIS=http://localhost:8080/*
   # export RALLYON_DEV_PASSWORD=DevOrganizer!1
   ```

3. Apply the provisioning script:
   ```bash
   bash admin/keycloak/provision_keycloak.sh
   ```

This script:
- Creates/updates the `rallyon` realm.
- Adds realm roles (`rallyon-organizer`, `rallyon-participants`, `rallyon-audience`, `rallyon-service`).
- Configures the confidential client `rallyon-api` with the supplied secret.
- Sets up a service account with `rallyon-service`.
- Creates a developer user `dev.organizer` with the organizer role and a `rallyon_user_id` claim (default 42).

## Manual UI configuration (Keycloak 25+)

Keycloak 25 requires a couple of post-provision tweaks in the Admin Console so tokens contain the expected audience and numeric user-id claim.

1. **Expose the custom attribute**
   1. Realm Settings → User profile.
   2. Create attribute `rallyon_user_id` (display name e.g. “RallyOn User Id”).
   3. Save.
   4. Users → `dev.organizer` → Profile tab → Attributes → add `rallyon_user_id = 42` → Save.
2. **Add an audience mapper**
   1. Clients → `rallyon-api` → Client scopes tab.
   2. Protocol mappers → Create.
   3. Mapper type `Audience`, name `rallyon-api-audience`.
   4. Included custom audience = `rallyon-api`, enable “Add to access token”, Save.
3. **Add the user-id mapper**
   1. Staying on `rallyon-api` → Client scopes tab → Protocol mappers → Create.
   2. Mapper type `User Attribute`, name `rallyon-user-id`.
   3. User attribute = `rallyon_user_id`, token claim name = `rallyon_user_id`, JSON type `long`.
   4. Enable “Add to access token” (ID token optional), Save.
4. **Force fresh tokens**
   - Clients → `rallyon-api` → Sessions → Logout all (or Users → `dev.organizer` → Sessions → Logout).

## Requesting a bearer token

Use curl (Git Bash example) to obtain a password grant:

```bash
curl -s -X POST http://localhost:8081/realms/rallyon/protocol/openid-connect/token \
  -d grant_type=password \
  -d client_id=rallyon-api \
  -d client_secret=super-secret \
  -d username=dev.organizer \
  --data-urlencode 'password=DevOrganizer!1'
```

Copy the `access_token` value. The decoded payload should contain:

- `aud`: includes `rallyon-api`
- `rallyon_user_id`: `42`

## Testing via Swagger UI

1. Ensure the stack is running: `docker compose -f infrastructure/local/docker-compose.yml up -d`.
2. Open http://localhost:8080/swagger-ui/index.html.
3. Click **Authorize**, paste `Bearer <access_token>` (complete token with `Bearer` prefix), Authorize.
4. Use the `POST /api/tournamentmgmt/config/drafts` endpoint with `organizerId=42` to create a draft.

If you need to invalidate stale tokens, repeat the “Force fresh tokens” step above.

## Manual Access

- Admin Console: http://localhost:8081/
- Default admin credentials: `admin` / `admin` (change after provisioning).
- Realm user: `dev.organizer` / `DevOrganizer!1`

## Secrets for Services

Populate the following environment variables (or secret manager keys) in each service consuming Keycloak:

| Key | Description |
| --- | --- |
| `KEYCLOAK_ISSUER` | e.g. `http://keycloak:8081/realms/rallyon` |
| `KEYCLOAK_JWKS_URI` | e.g. `http://keycloak:8081/realms/rallyon/protocol/openid-connect/certs` |
| `KEYCLOAK_AUDIENCE` | `rallyon-api` |
| `KEYCLOAK_CLIENT_ID` | `rallyon-api` |
| `KEYCLOAK_CLIENT_SECRET` | value supplied to the script |

Service tokens should include the custom claim `rallyon_user_id` so backend modules can resolve numeric user identifiers.
