# Clubr

Clubr is a club administration app for managing memberships, roles, posts, events, and event tickets.

## Requirements

- Docker Desktop

## Run locally

From the project root, build and start the complete application:

```bash
docker compose up --build -d
```

Open `http://localhost:81` in a browser. PostgreSQL, the Java backend, and the frontend start together. Flyway applies the database migrations when the backend starts.

To stop the complete application:

```bash
docker compose down
```

Local database and JWT settings are stored in `.env.docker`. Replace the sample secrets before using the application outside local development.

## Checks

Run the backend tests with Java 21 and Maven installed:

```bash
cd backend
mvn test
```

Check and build the frontend with Node.js 20 or newer and npm installed:

```bash
cd frontend
npm run lint
npm run build
```
