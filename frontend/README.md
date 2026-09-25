# Club Administration Frontend

The frontend is a React and TypeScript client for the Club Administration API. It provides registration and login, club membership management, member roles, and club dashboard posts.

## Run locally

The backend should be running on `http://localhost:8080`.

```bash
npm install
npm run dev
```

Vite serves the application on `http://localhost:5173` and proxies `/api` requests to the backend. Set `VITE_API_PROXY_TARGET` to use a different backend address during development.

Set `VITE_API_URL` when the API is exposed under a different base path. Its default value is `/api/v1/`.

## Build

```bash
npm run build
```

The production files are written to `dist`.
