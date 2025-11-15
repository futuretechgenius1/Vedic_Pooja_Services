# Vedic Pooja Services – Monorepo

Full‑stack monorepo consisting of:
- Next.js (T3 Stack) frontend using tRPC, Prisma, NextAuth, Tailwind
- Spring Boot (Java) backend service for core domain (catalog, bookings, purohit onboarding)

This document provides end‑to‑end project details, setup, and development workflows.

## Repository Structure

- `/src` — Next.js app (App Router)
  - `app/` — pages, layouts, components
  - `server/` — auth, tRPC server, Prisma DB client
  - `trpc/` — tRPC client, server hydration helpers
  - `env.js` — validated environment variables schema
- `/prisma` — Prisma schema and migrations
- `/public` — static assets (favicon)
- `/vedic-pooja-services-backend` — Spring Boot backend
  - Docker + Maven + Flyway, Swagger UI enabled
- Tooling/config:
  - `eslint.config.js`, `prettier.config.js`, `postcss.config.js`, `tsconfig.json`, `next.config.js`
  - `start-database.sh` — starts local Postgres via Docker/Podman using `.env`

## Tech Stack

Frontend (T3):
- Next.js 15, React 19
- tRPC v11 with @tanstack/react-query v5
- Prisma 6 + PostgreSQL
- NextAuth v5 (Discord provider)
- Tailwind CSS v4, Prettier, ESLint (typescript-eslint)

Backend:
- Java 17, Spring Boot 3
- Spring Web, Spring Data JPA, Spring Security (JWT)
- Flyway migrations, MySQL
- OpenAPI (Swagger UI)
- Docker / Docker Compose

## Quick Start

Prerequisites:
- Node.js 20+, npm
- Docker or Podman (for local DB)
- JDK 17 + Maven (if running backend locally)

1) Environment
- Copy `.env.example` to `.env` and fill values (see “Environment Variables” below).

2) Database (Frontend/Prisma)
- Start Postgres: `./start-database.sh`
  - Parses `DATABASE_URL` from `.env` and runs a container named `<db>-postgres`.
- Push schema: `npm run db:push`
  - Alternatively: `npm run db:generate` for dev migrations, `npm run db:migrate` for deploy.

3) Frontend dev
- Install deps: `npm install`
- Start dev server: `npm run dev`
- App runs on http://localhost:3000

4) Backend dev (optional, separate service)
- See `vedic-pooja-services-backend/README.md` for full instructions.
- Docker compose: `docker compose up --build` (inside backend folder)
  - Backend: http://localhost:8080
  - Swagger UI: http://localhost:8080/swagger-ui/index.html

## Environment Variables

Managed and validated in `src/env.js`. Update `.env` and keep `.env.example` in sync.

Required (server-side):
- `AUTH_SECRET` — NextAuth secret (required in production; optional in dev)
- `AUTH_DISCORD_ID` — Discord OAuth Client ID
- `AUTH_DISCORD_SECRET` — Discord OAuth Client Secret
- `DATABASE_URL` — Postgres connection string, e.g.
  `postgresql://postgres:password@localhost:5432/fullstack-app`
- `NODE_ENV` — `development` | `test` | `production` (defaults to `development`)

Notes:
- Generate NextAuth secret: `npx auth secret`
- If deploying on Vercel, ensure environment variables are set in project settings.

## Frontend Architecture

Authentication:
- NextAuth v5 with Discord provider
- Prisma adapter persists sessions/accounts in Postgres
- Augmented session includes `user.id`

API:
- tRPC server in `src/server/api`
  - `trpc.ts` initializes context (session + db), transformers, middlewares
  - `routers/post.ts` sample router: public `hello`, protected `create`, `getLatest`
  - `root.ts` exports `appRouter` and server-side caller
- React Query client wrappers in `src/trpc`
  - `react.tsx` sets up HTTP batch link and logger link
  - `server.ts` provides RSC hydration helpers

Database:
- Prisma schema in `prisma/schema.prisma`
  - Includes NextAuth tables (User, Account, Session, VerificationToken)
  - Sample `Post` model with relation to `User`
- Prisma client in `src/server/db.ts` with dev logging and global singleton

UI:
- App Router (Next.js) in `src/app`
  - `page.tsx` demonstrates SSR tRPC call and auth state
  - `app/_components/post.tsx` shows client component using tRPC hooks
  - Tailwind v4 via `@tailwindcss/postcss`

## Backend Overview

Located at `vedic-pooja-services-backend/`.
- Modules: auth, user, catalog, purohit, schedule, booking, security
- JWT-based auth; roles and restricted endpoints for admin/purohit
- Persistence: MySQL via Spring Data JPA
- Migrations: Flyway on startup
- Documentation: Swagger UI

See `vedic-pooja-services-backend/README.md` for:
- Quick Start (Docker and local)
- Environment vars (DB_URL, DB_USERNAME, DB_PASSWORD, JWT_SECRET)
- API endpoints overview
- Health endpoints
- Notes and licensing

## Scripts

From `package.json`:
- `dev` — Next dev with Turbo
- `build` / `start` — Next production build/start
- `preview` — build then start
- `lint` / `lint:fix` — ESLint (Next config)
- `typecheck` | `check` — TypeScript checks + lint
- `format:check` / `format:write` — Prettier
- `db:push` — Prisma db push
- `db:generate` — Prisma migrate dev
- `db:migrate` — Prisma migrate deploy
- `db:studio` — Prisma Studio
- `postinstall` — Prisma generate

## Deployment

Frontend:
- Vercel, Netlify, or Docker (see T3 Stack docs)
- Ensure env variables are set; use `SKIP_ENV_VALIDATION=1` if validating during Docker builds is undesirable

Backend:
- Docker Compose or containerized deployment
- Provide secure `JWT_SECRET`; configure DB credentials and networking

## Development Tips

- Always run `./start-database.sh` with a correctly configured `.env` before Prisma commands.
- If you see “Port in use” on DB start, change `DATABASE_URL` port or stop the conflicting process.
- Discord OAuth:
  - Set redirect URIs to `http://localhost:3000/api/auth/callback/discord`
- Prisma:
  - Use `npm run db:studio` to explore data
- tRPC:
  - Prefer `protectedProcedure` for authenticated endpoints
  - Middlewares can measure execution time (see `timingMiddleware`)

## Troubleshooting

- Prisma cannot connect:
  - Verify `DATABASE_URL` is correct
  - Container is running: `docker ps` / `podman ps`
- NextAuth errors:
  - Ensure `AUTH_SECRET`, `AUTH_DISCORD_ID`, `AUTH_DISCORD_SECRET` are set
- Type errors:
  - Run `npm run typecheck` and `npm run lint`
- Styling issues:
  - Tailwind v4 is configured via PostCSS plugin; ensure global CSS is imported in `app/layout.tsx`

## References

- T3 Stack: https://create.t3.gg/
- Next.js: https://nextjs.org
- NextAuth: https://next-auth.js.org
- Prisma: https://www.prisma.io
- tRPC: https://trpc.io
- Spring Boot: https://spring.io/projects/spring-boot
- Swagger/OpenAPI: https://swagger.io/tools/swagger-ui/
