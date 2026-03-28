# Fix: Movie Seeding Issues

## Root Causes
1. `@Transactional` self-invocation in `DataSeeder` → Director/Actor entities become detached → Hibernate throws "detached entity passed to persist"
2. `Genre.isDeleted` missing `@Builder.Default` → potential NOT NULL violation

## Tasks

- [x] Analyze root causes
- [x] Create `MovieSeederService.java` with proper `@Transactional` methods
- [x] Update `DataSeeder.java` to inject and call `MovieSeederService`
- [x] Fix `Genre.java` - add `@Builder.Default` for `isDeleted`
