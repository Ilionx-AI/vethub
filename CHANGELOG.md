# Changelog

All notable changes to VetHub project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- **Visit-Vet Association**: Link veterinarians to individual visit records for better tracking
- **Pet Health Metrics**: Track weight, vaccination dates, allergies, dietary restrictions, and medical notes
- **Global Pet Creation**: Create pets from `/pets/new` route with owner selection
- **Pet Detail Modal**: View complete pet information from visits page in a modal dialog
- **Enhanced Pet Display**: Show health metrics in pet detail, list views, and modal
- **Improved Vet Visibility**: Display veterinarian name and specialties on all visit records
- **Health Metrics UI**: Vaccination status indicators with color-coded badges (current/due soon/overdue)
- **Pet Health Metrics Utility**: Helper functions for age calculation, vaccine status tracking, and date formatting

### Fixed
- **Data Validation**: Added @NotBlank validation to Owner firstName and lastName
- **Date Validation**: Added @Past validation to Pet birthDate to prevent future dates
- **Visit Validation**: Added @NotBlank validation to Visit description
- **Responsive Design**: Fixed button layouts, grid stacking, and modal sizing for 320px-768px mobile viewports
- **Mobile Touch Targets**: Ensured all interactive elements meet 44px minimum height for mobile usability
- **Visits Table**: Improved mobile view with card layout fallback for small screens

### Technical Details
- **Backend**: 3 Liquibase migrations added for new database columns
- **API**: RESTful endpoints for global pet creation and enhanced visit management
- **Frontend**: 7 component updates for health metrics display and responsive improvements
- **Testing**: 322 backend tests passing, 0 TypeScript errors in frontend
- **Database**: H2 in-memory database auto-migrations, backward compatible schema changes

### Breaking Changes
None - all changes are backward compatible

### Migration Notes
- Run backend with `./gradlew bootRun` to auto-apply Liquibase migrations
- Existing visits without assigned vets will have null vetId (preserved)
- Existing pets without health metrics can have them added later via API/UI

## [0.1.0] - 2024-08-19 (Initial Release)

### Added
- Initial VetHub monorepo structure (Spring Boot + SvelteKit)
- Owner management: Create, read, update, delete owners
- Pet management: Create, read, update, delete pets (nested under owners and global)
- Visit tracking: Record, read, update, delete visits for pets
- Veterinarian management: Manage vets and specialties
- OpenAPI/Swagger documentation
- Responsive UI with SvelteKit and TailwindCSS
- Theme switching (light/dark mode)
- Pet search functionality (case-insensitive by name)

### Technical
- Spring Boot 3.x backend with H2 in-memory database
- SvelteKit 2 frontend with TypeScript
- Liquibase database migrations
- MapStruct for entity mapping
- OpenAPI 3.0 documentation auto-generated
- Full test coverage with unit and integration tests
