package dev.ilionx.workshop.api.pet.controller;

import dev.ilionx.workshop.api.pet.model.Pet;
import dev.ilionx.workshop.api.pet.model.mapper.PetMapper;
import dev.ilionx.workshop.api.pet.model.request.CreatePetRequest;
import dev.ilionx.workshop.api.pet.model.request.UpdatePetRequest;
import dev.ilionx.workshop.api.pet.model.response.PetResponse;
import dev.ilionx.workshop.api.pet.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static dev.ilionx.workshop.api.Paths.PETS;
import static dev.ilionx.workshop.api.Paths.PET_BY_ID;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST controller for global pet management operations.
 */
@Tag(
    name = "Pet",
    description = "Global pet management endpoints"
)
@RestController
@RequiredArgsConstructor
public class PetGlobalController {

    private final PetService petService;
    private final PetMapper petMapper;

    @ResponseStatus(OK)
    @Operation(
        summary = "Get all pets or search by name",
        description = "Returns all pets, or filters by name if search parameter is provided (case-insensitive)"
    )
    @GetMapping(
        path = PETS,
        produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<PetResponse>> searchPets(
        @Parameter(
            description = "Search term to filter pets by name (case-insensitive, partial match)",
            required = false,
            example = "Buddy"
        )
        @RequestParam(
            required = false,
            defaultValue = ""
        ) final String name
    ) {
        final List<Pet> pets = name.isEmpty() ? petService.findAll() : petService.searchByName(name);
        return ResponseEntity.status(OK).body(petMapper.toResponseList(pets));
    }

    @ResponseStatus(OK)
    @Operation(
        summary = "Get pet by ID",
        description = "Returns a single pet by its unique identifier"
    )
    @GetMapping(
        path = PET_BY_ID,
        produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<PetResponse> getPetById(@PathVariable final Integer id) {
        final Pet pet = petService.findById(id);
        return ResponseEntity.status(OK).body(petMapper.toResponse(pet));
    }

    @ResponseStatus(CREATED)
    @Operation(
        summary = "Create pet globally",
        description = "Creates a new pet by providing owner ID in request body. The ownerId field is required."
    )
    @ApiResponse(
        responseCode = "201",
        description = "Pet created successfully",
        content = @Content(
            mediaType = APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = PetResponse.class)
        )
    )
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request - missing or null ownerId field"
    )
    @ApiResponse(
        responseCode = "404",
        description = "Owner not found with provided ownerId"
    )
    @PostMapping(
        path = PETS,
        consumes = APPLICATION_JSON_VALUE,
        produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<PetResponse> createPet(@RequestBody final CreatePetRequest request) {
        final Pet pet = petService.create(request.getOwnerId(), request);
        final PetResponse response = petMapper.toResponse(pet);
        return ResponseEntity.created(URI.create(PET_BY_ID.replace("{id}", pet.getId().toString())))
            .body(response);
    }

    @ResponseStatus(OK)
    @Operation(
        summary = "Update pet",
        description = "Updates an existing pet"
    )
    @PutMapping(
        path = PET_BY_ID,
        consumes = APPLICATION_JSON_VALUE,
        produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<PetResponse> updatePet(
        @PathVariable final Integer id,
        @RequestBody final UpdatePetRequest request
    ) {
        final Pet pet = petService.update(id, request);
        return ResponseEntity.status(OK).body(petMapper.toResponse(pet));
    }

    @ResponseStatus(NO_CONTENT)
    @Operation(
        summary = "Delete pet",
        description = "Deletes a pet by its unique identifier"
    )
    @DeleteMapping(path = PET_BY_ID)
    public ResponseEntity<Void> deletePet(@PathVariable final Integer id) {
        petService.delete(id);
        return ResponseEntity.status(NO_CONTENT).build();
    }
}
