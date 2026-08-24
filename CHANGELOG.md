# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Vet selector dropdown in visit creation and editing forms
- VisitForm reusable Svelte component with date, description, and veterinarian selection
- Veterinarian field (many-to-one relationship) to Visit entity in backend
- Visit endpoints now require and return veterinarian information
- Database migration to add vet_id foreign key to visits table
- Enhanced visit API responses include full veterinarian details
- Global pets endpoint in PetGlobalController for listing and managing all pets across owners
- Theme switcher component for light/dark mode support
- Pet listing page at `/pets` route with search functionality
- Global pet management endpoints (list, get, create, update, delete)
- Enhanced PetForm component for create and edit operations

### Changed
- Updated visit form handling to include vet selection
- Enhanced VisitController and VisitService to manage veterinarian assignments
- Updated VisitResponse schema to include veterinarian information
- Updated CreateVisitRequest and UpdateVisitRequest to include vetId field
- Updated app styling (app.css) with improved theme support
- Enhanced PetController with case-insensitive pet name search
- Improved PetService with global pet operations
- Enhanced PetRepository with search capabilities
- Updated Header layout with theme switcher integration
- Refactored app.html for better structure

### Fixed
- Pet search now supports partial name matching with case-insensitive queries

## [Previous Releases]

### Added
- OwnerValidator for request validation
- Permit all security configuration for API endpoints
- Updated health check URL to use API endpoint
- Initial project structure with Spring Boot backend and SvelteKit frontend
