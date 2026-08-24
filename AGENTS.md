# AGENTS.md

Agent guidance for the VetHub monorepo (Spring Boot + SvelteKit).

## Repository Structure

This is a monorepo with two distinct applications:

- **`/server`** — Spring Boot backend (Java 25, Gradle, H2 in-memory DB)
- **`/client`** — SvelteKit frontend (TypeScript, Vite, Tailwind CSS)
- **`/scripts`** — Shared shell utilities for orchestrating build and development workflows

## Java / Spring Boot Backend

### Quick Commands

- **Start dev server**: `cd server && ./gradlew bootRun`
  - Runs on `http://localhost:8080`
  - H2 database auto-creates on startup
  - Health check endpoint: `http://localhost:8080/api/actuator/health`

- **Run unit tests**: `cd server && ./gradlew test`
  - Uses `@UnitTest` annotation (extends mockito)
  - Located in `src/test/java/**/*Test.java`

- **Run integration tests**: `cd server && ./gradlew test`
  - Uses `@IntegrationTest` annotation (extends WebMvcConfigurator)
  - Auto-cleans database before and after each test
  - Fixtures available in `IntegrationTest` base class

- **Run full checks**: `cd server && ./gradlew check`
  - Runs tests + quality checks (SpotBugs, PMD, Checkstyle)
  - Format check: `./gradlew spotlessCheck`
  - Auto-format: `./gradlew spotlessApply` (must run before commit)

### Code Quality

- **Formatter**: Spotless (Eclipse config in `src/quality/config/spotless/styling.xml`)
  - Must run `spotlessApply` before committing any Java changes
  - Auto-configured as part of the `build` task
  - Git pre-push hook available: `./gradlew spotlessInstallGitPrePushHook`

- **Static Analysis**: SpotBugs, PMD, Checkstyle
  - Configured via quality plugin
  - `checkQualityMain` and `checkQualityTest` for incremental checks

- **Java Version**: Fixed at **Java 25** (see `build.gradle.kts` toolchain)
  - Managed by `mise` tool (`.java` tool installed via `mise.toml`)

### Architecture Notes

- **API Docs**: OpenAPI/Swagger (auto-generated at `/api/v1/public/docs`)
- **Health Endpoint**: `/api/actuator/health` (used by startup scripts)

### DTO Pattern & Mapping

- **Request/Response Models**: Separate `*Request` and `*Response` classes (not DTOs)
  - Keep domain entities clean; isolate API contract from persistence model
  - Use clear naming: `CreateOwnerRequest`, `UpdateOwnerRequest`, `OwnerResponse`, etc.
- **Entity Mapping**: MapStruct for domain ↔ API model mapping
  - Annotation processor generates mappers at compile time
  - Located alongside domain models or in dedicated mapper package
- **Liquibase Migrations**: Database versioning for schema changes
  - Location: `server/src/main/resources/db/changelog/`
  - Auto-applied on application startup
  - Use changelog XML files with incrementing version numbers

### Test Base Classes

**`@UnitTest`** — Fast, isolated, no database
- Extends `Mockito` with `@ExtendWith(MockitoExtension.class)`
- Use for: Service logic, validators, mappers (anything not requiring Spring context)
- Provides builder methods: `aValidOwner()`, `aValidPet()`, `aValidVisit()`, etc.
- All dependencies mocked

**`@IntegrationTest`** — Full Spring context, real H2 database
- Extends `WebMvcConfigurator` with full Spring Boot test context
- Use for: Controllers, repositories, end-to-end API flows (anything requiring database or HTTP layer)
- Auto-cleans database before and after each test
- Provides persistence factories: `aSavedOwner()`, `aSavedPet()`, `aSavedVisit()`, etc.
- Also provides request builders: `aCreateOwnerRequest()`, `aCreatePetRequest()`, etc.
- Database setup is deterministic (seed data with known IDs 1–6; tests can reference these)

## SvelteKit Frontend

### Quick Commands

- **Start dev server**: `cd client && bun run dev`
  - Runs on `http://localhost:5173` (Vite default)
  - Hot reload enabled

- **Type check**: `bun run check` or `bun run check:watch`
  - Runs svelte-check + svelte-kit sync
  - Must pass before commit

- **Build**: `bun run build`
  - Output in `.svelte-kit/` and dist/

- **Sync OpenAPI types**: `bun run sync:api`
  - Orchestrates backend startup, downloads OpenAPI spec, generates TypeScript types
  - Script: `scripts/openapi-sync.sh`
  - Outputs: `server/openapi.json`, `client/src/lib/types/api.d.ts`
  - Uses `openapi-typescript` to generate types from OpenAPI 3.0 spec

### Dependencies

- **Runtime**: openapi-fetch, Svelte 5, shadcn-svelte, TailwindCSS 4, lucide-svelte
- **Dev**: Vite 7, SvelteKit 2, TypeScript 5.9
- **Package manager**: `bun` (1.3.0, managed by `mise`)

### API Integration

- **Endpoint discovery**: `http://localhost:8080/api/v1/public/docs` (OpenAPI spec)
- **Client generation**: `openapi-typescript` creates `src/lib/types/api.d.ts` from OpenAPI 3.0 spec
- **Type-safe fetching**: `openapi-fetch` wrapper for API calls with full TS inference from generated types
  - Auto-complete on requests/responses
  - No need for manual type definitions
- **Updating types after backend changes**: Run `bun run sync:api` to regenerate types and download fresh spec

## Development Workflow

### Setup

```bash
mise install  # Install Java 25, bun, node, etc.
```

### Full-Stack Development

1. **Terminal 1: Start backend**
   ```bash
   cd server
   ./gradlew bootRun
   ```

2. **Terminal 2: Start frontend**
   ```bash
   cd client
   bun run dev
   ```

3. **Backend changes**:
   - Run `./gradlew spotlessApply` before staging Java files
   - Restart backend if needed

4. **OpenAPI spec changed**:
   - Run `bun run sync:api` (orchestrates backend start, spec download, type generation)
   - Commit `server/openapi.json` and `client/src/lib/types/api.d.ts`

### CI/Pre-Commit Checks

- **Backend**: `./gradlew check` (tests + spotless + quality tools)
- **Frontend**: `bun run check` (type checking)
- **Branch creation**: Always start from `main` branch (no `develop` branch)
- **Commit messages**: Use semantic prefixes (`feat:`, `fix:`, `docs:`, etc.)

### Post-Build Procedures

After a successful build (all tests pass and quality checks complete):

1. **Update CHANGELOG.md**
   - Add a new entry summarizing the changes made
   - Format: Use the existing changelog style and structure
   - Include feature additions, bug fixes, and breaking changes
   - Reference the related feature branch or story ID if applicable

2. **Move Completed Stories**
   - Update any associated project management tools (e.g., issue tracker, kanban board, sprint board)
   - Move stories/tasks from "In Progress" to "Done" or "Completed" status
   - Add a comment with the commit SHA and feature branch name for traceability
   - Ensure all acceptance criteria are met before closing

## Debugging & Logs

- **Backend log file**: `/tmp/petclinic-backend.log` (created by startup scripts)
- **Gradle daemon issues**: `cd server && ./gradlew --stop` to clean up stale daemons
- **Vite dev issues**: Delete `client/.svelte-kit` and `client/node_modules` if hot reload breaks

## Git Conventions

- Create feature branches from `main`: `feature/your-feature-name`
- Semantic commits: `feat:`, `fix:`, `docs:`, `refactor:`, etc.
- If `CHANGELOG.md` exists, add entry with change summary
- No Forgejo MCP required (not a kadaster.nl repo)

## Environment

- **Default port**: Backend 8080, Frontend 5173
- **Database**: H2 in-memory (no external config needed, auto-created)
- **Profile**: `SPRING_PROFILES_ACTIVE=dev` (configured in `mise.toml`)
- **Platform**: Development on macOS (see `.gitignore` for OS-specific patterns)
