package dev.ilionx.workshop.api.visit.model.response;

import dev.ilionx.workshop.api.vet.model.response.VetResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * Response DTO containing visit details.
 */
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Schema(description = "Response containing visit details")
public class VisitResponse {

    @Schema(
        description = "The unique identifier of the visit",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer id;

    @Schema(
        description = "The visit date",
        example = "2023-01-01",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDate date;

    @Schema(
        description = "The visit description",
        example = "Rabies shot",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String description;

    @Schema(
        description = "The pet's unique identifier",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer petId;

    @Schema(
        description = "The veterinarian who performed the visit",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private VetResponse vet;
}
