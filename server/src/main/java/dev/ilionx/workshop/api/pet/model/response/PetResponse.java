package dev.ilionx.workshop.api.pet.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.List;

/**
 * Response DTO containing pet details including type and visit history.
 */
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Schema(description = "Response containing pet details")
public class PetResponse {

    @Schema(
        description = "The unique identifier of the pet",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer id;

    @Schema(
        description = "The pet's name",
        example = "Leo",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;

    @Schema(
        description = "The pet's birth date",
        example = "2020-09-07",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDate birthDate;

    @Schema(description = "The pet type")
    private PetTypeResponse type;

    @Schema(
        description = "The owner's unique identifier",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer ownerId;

    @Schema(description = "The pet's visits")
    private List<VisitSummaryResponse> visits;

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
