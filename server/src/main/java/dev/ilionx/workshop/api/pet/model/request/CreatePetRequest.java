package dev.ilionx.workshop.api.pet.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Request DTO for creating a new pet.
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@Schema(description = "Request to create a new pet")
public class CreatePetRequest {

    @Schema(
        description = "Pet's name",
        example = "Leo",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;

    @Schema(
        description = "Pet's date of birth",
        example = "2020-09-07",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDate birthDate;

    @Schema(
        description = "ID of the pet type",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer typeId;

    @NotNull
    @Schema(
        description = "ID of the owner (required for global endpoint)",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer ownerId;

    @Positive
    @Schema(
        description = "Pet's weight in kilograms",
        example = "5.5",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private Double weight;

    @Schema(
        description = "Date of the last vaccine administered",
        example = "2024-08-01",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private LocalDate lastVaccineDate;

    @Schema(
        description = "Comma-separated list or free text describing any allergies",
        example = "Chicken, Dairy",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String allergies;

    @Schema(
        description = "Comma-separated list or free text describing dietary restrictions",
        example = "Low fat, No grains",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String dietaryRestrictions;

    @Schema(
        description = "Medical notes and relevant medical history",
        example = "Sensitive stomach, requires regular check-ups",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String medicalNotes;

}
